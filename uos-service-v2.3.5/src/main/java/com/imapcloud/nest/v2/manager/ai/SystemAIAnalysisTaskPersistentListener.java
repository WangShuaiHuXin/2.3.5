package com.imapcloud.nest.v2.manager.ai;

import com.imapcloud.nest.v2.common.enums.AIAnalysisTaskTypeEnum;
import com.imapcloud.nest.v2.common.utils.AsyncBusinessUtils;
import com.imapcloud.nest.v2.service.AIAnalysisService;
import com.imapcloud.nest.v2.service.PowerDataService;
import com.imapcloud.nest.v2.service.PowerMeterDataService;
import com.imapcloud.nest.v2.service.dto.in.AutoAIRecognitionTaskInDTO;
import com.imapcloud.nest.v2.service.dto.out.AIAutoDiscernSettingsOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DiscernFunctionSettingOutDTO;
import com.imapcloud.nest.v2.service.dto.out.PowerMeterFlightDataOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 系统AI分析识别任务持久化监听器
 * @author Vastfy
 * @date 2022/12/5 14:07
 * @since 2.1.5
 */
@Slf4j
@Component
public class SystemAIAnalysisTaskPersistentListener implements ApplicationListener<PowerMeterDataPushedEvent> {

    @Resource
    private PowerMeterDataService powerMeterDataService;

    @Resource
    private AIAnalysisService aiAnalysisService;

    @Resource
    private PowerDataService powerDataService;

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    @Async("bizAsyncExecutor")
    @Override
    public void onApplicationEvent(@NonNull PowerMeterDataPushedEvent event) {
        String orgCode = event.getOrgCode();
        List<String> meterDetailIds = event.getMeterDetailIds();
        log.info("监听单位{}下新增表计读数详情数据 ==> {}", orgCode, meterDetailIds);
        // 判断是否开启自动识别
        AIAutoDiscernSettingsOutDTO settings = powerDataService.getOrgAIAutoDiscernSettings(orgCode);
        if(!settings.isEnabled()){
            log.info("当前表计数据所属单位[{}]未开启自动创建AI识别任务，忽略本次事件", orgCode);
            return;
        }
        // 判断是否有设置识别功能
        List<DiscernFunctionSettingOutDTO.DiscernFunctionInfoOutDTO> discernFunctionInfos = powerDataService.getDiscernFunctionInfos(orgCode, AIAnalysisTaskTypeEnum.DIALS.covert().getIdenValue());
        if(CollectionUtils.isEmpty(discernFunctionInfos)){
            log.info("当前表计数据所属单位[{}]未配置识别功能，忽略本次事件", orgCode);
            return;
        }

        if(CollectionUtils.isEmpty(meterDetailIds)){
            return;
        }
        log.info("开始尝试自动初始化AI分析任务...");
        PowerMeterFlightDataOutDTO data = null;
        int count  = 0;
        while(Objects.isNull(data) && count < 10){
            count ++;
            log.info("尝试第[{}]次查询表计详情数据", count);
            data = powerMeterDataService.fetchPowerMeterData(meterDetailIds);
            if(Objects.isNull(data)){
                log.info("未查询到表计数据信息，等待500ms重试 ==> {}", meterDetailIds);
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    log.error("线程休眠异常", e);
                }
            }
        }
        if(Objects.isNull(data)){
            log.warn("已重试[{}]次，仍为未查询到表计数据信息 ==> {}", count, meterDetailIds);
            return;
        }

        // 创建自动AI识别分析任务
        AutoAIRecognitionTaskInDTO autoAiTask = new AutoAIRecognitionTaskInDTO();
        autoAiTask.setBaseDataId(data.getDataId());
        autoAiTask.setAiTaskName(data.getAiTaskName());
        autoAiTask.setAiTaskType(AIAnalysisTaskTypeEnum.DIALS.getType());
        autoAiTask.setFlightTaskId(data.getFlightTaskId());
        autoAiTask.setFlightTaskTag(data.getFlightTaskTag());
        autoAiTask.setTaskName(data.getTaskName());
        autoAiTask.setOrgCode(orgCode);
        List<AutoAIRecognitionTaskInDTO.PhotoInfo> photoInfos = data.getDetailInfos()
                .stream()
                .map(r -> {
                    AutoAIRecognitionTaskInDTO.PhotoInfo photoInfo = new AutoAIRecognitionTaskInDTO.PhotoInfo();
                    photoInfo.setDetailId(r.getDetailId());
                    photoInfo.setPhotoName(r.getPhotoName());
                    photoInfo.setPhotoPath(r.getOriginalPicUrl());
                    if(!StringUtils.hasText(r.getComponentId())){
                        photoInfo.setFailedReason("图片数据未关联部件信息");
                    }
                    return photoInfo;
                })
                .collect(Collectors.toList());
        autoAiTask.setPhotoInfos(photoInfos);
        String autoAiTaskId = AsyncBusinessUtils.executeBusiness(() -> aiAnalysisService.createAutoAIAnalysisTask(autoAiTask));
        WaitingTaskElement waitingTask = WaitingTaskElementSupport.builder()
                .taskId(autoAiTaskId)
                .orgCode(orgCode)
                .build();
        boolean success = SystemAIAnalysisTask.addWaitingTask(waitingTask);
        if(success){
            applicationEventPublisher.publishEvent(new SystemAIAnalysisTaskInitializedEvent(this));
        }
    }

}
