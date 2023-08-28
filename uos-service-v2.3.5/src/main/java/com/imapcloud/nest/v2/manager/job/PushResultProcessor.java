package com.imapcloud.nest.v2.manager.job;

import cn.hutool.core.collection.CollectionUtil;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.utils.AuditUtils;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisMarkMapper;
import com.imapcloud.nest.v2.dao.po.out.DataAnalysisDetailMarkOutPO;
import com.imapcloud.nest.v2.service.DataAnalysisDetailService;
import com.imapcloud.nest.v2.service.DataAnalysisResultService;
import com.imapcloud.nest.v2.service.converter.DataAnalysisMarkConverter;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisMarkDrawInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisResultInDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName PushResultProcessor.java
 * @Description PushResultProcessor
 * @createTime 2022年07月26日 09:11:00
 */
@Slf4j
@Component
public class PushResultProcessor implements BasicProcessor {

    @Resource
    private DataAnalysisMarkMapper dataAnalysisMarkMapper;

    @Resource
    private DataAnalysisDetailService dataAnalysisDetailService;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private DataAnalysisResultService dataAnalysisResultService;

    @Override
    public ProcessResult process(TaskContext context) throws Exception {

        // 在线日志功能，可以直接在控制台查看任务日志，非常便捷
        OmsLogger omsLogger = context.getOmsLogger();
        omsLogger.info("BasicProcessorDemo start to process, current JobParams is {}.", context.getJobParams());

        List<DataAnalysisDetailMarkOutPO> dataAnalysisDetailMarkOutPOS = this.dataAnalysisMarkMapper.queryDetailMarkForResult();
        this.dataAnalysisDetailService.fillTopicData(dataAnalysisDetailMarkOutPOS);
        List<DataAnalysisMarkDrawInDTO> dataAnalysisMarkDrawInDTOS = dataAnalysisDetailMarkOutPOS.stream().map(DataAnalysisMarkConverter.INSTANCES::convertDraw).collect(Collectors.toList());

        List<DataAnalysisResultInDTO.InsertInfoIn> insertInfoInList = new ArrayList<>();

        if (CollectionUtil.isNotEmpty(dataAnalysisMarkDrawInDTOS)) {
            insertInfoInList = dataAnalysisMarkDrawInDTOS.stream()
                    .map(DataAnalysisMarkConverter.INSTANCES::convertForResult)
                    .collect(Collectors.toList());
            insertInfoInList = insertInfoInList.stream()
                    .filter(x->this.filterData(x))
                    .collect(Collectors.toList());
            omsLogger.info("【PushResultProcessor】-【process】：{}",insertInfoInList.stream().map(DataAnalysisResultInDTO.InsertInfoIn::getMarkId).collect(Collectors.toList()).toString());
            try{
                this.dataAnalysisResultService.batchInsert(insertInfoInList, AuditUtils.getAudit());
            }catch (BusinessException e){
                log.error("【分析中心】推送【结果】，失败：",e);
                omsLogger.error("【分析中心】推送【结果】",e);
            }
        }

        return new ProcessResult(true, MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PUSH_ANALYSIS_RESULT_SUCCESS.getContent()));
    }

    /**
     *
     * @return
     */
    public boolean filterData(DataAnalysisResultInDTO.InsertInfoIn insertInfoIns){
        if(StringUtils.isEmpty(insertInfoIns.getImagePath())){
            return false;
        }
        if(StringUtils.isEmpty(insertInfoIns.getThumImagePath())){
            return false;
        }
        if(StringUtils.isEmpty(insertInfoIns.getResultImagePath())){
            return false;
        }
        if(StringUtils.isEmpty(insertInfoIns.getLatitude())){
            return false;
        }
        if(StringUtils.isEmpty(insertInfoIns.getLongitude())){
            return false;
        }
        return true;
    }
}