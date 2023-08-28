package com.imapcloud.nest.v2.manager.ai;

import com.geoai.common.core.bean.DefaultTrustedAccessInformation;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.imapcloud.nest.v2.common.properties.AnalysisConfig;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.service.AIAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 系统AI识别分析任务监听器
 * @author Vastfy
 * @date 2022/12/2 13:55
 * @since 2.1.5
 */
@Slf4j
@Component
public class SystemAIAnalysisTaskExecuteListener implements SmartApplicationListener {

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Lazy
    @Resource
    private AIAnalysisService aiAnalysisService;

    @Override
    public boolean supportsEventType(@NonNull Class<? extends ApplicationEvent> eventType) {
        return SystemAIAnalysisTaskInitializedEvent.class.isAssignableFrom(eventType)
                || AIAnalysisTaskHandleCompletedEvent.class.isAssignableFrom(eventType);
    }

    @Async("bizAsyncExecutor")
    @Override
    public void onApplicationEvent(@NonNull ApplicationEvent event) {
        if (event instanceof AIAnalysisTaskHandleCompletedEvent) {
            handle((AIAnalysisTaskHandleCompletedEvent) event);
        }
        else if (event instanceof SystemAIAnalysisTaskInitializedEvent) {
            handle((SystemAIAnalysisTaskInitializedEvent) event);
        }
    }

    private void handle(AIAnalysisTaskHandleCompletedEvent event){
        log.info("监听到允许中队列AI识别任务处理完成，尝试获取待运行队列任务");
        pollAiTaskExecuting();
    }

    private void handle(SystemAIAnalysisTaskInitializedEvent event){
        log.info("监听到系统创建AI识别任务初始化完成，尝试获取待运行队列任务");
        pollAiTaskExecuting();
    }

    private synchronized void pollAiTaskExecuting() {
        AnalysisConfig analysisConfig = geoaiUosProperties.getAnalysis();
        // a. 全量识别任务数量限制
        if(AIAnalysisTasks.getAllTaskCounts() >= analysisConfig.getAiTaskCount()){
            log.info("当前系统AI分析任务数量超过最大限制，忽略本次操作");
            return;
        }
        // b. 单个单位识别任务数量限制
        WaitingTaskElement waitingTask = SystemAIAnalysisTask.pollWaitingTask(orgCode -> {
            int taskCounts = AIAnalysisTasks.getTaskCounts(orgCode, true);
            Integer orgCapacity = analysisConfig.getOrgAiTaskCount();
            boolean hit = taskCounts < orgCapacity;
            if(log.isDebugEnabled()){
                log.debug("单位[{}]运行中队列任务数量：{}，系统配置容量：{}，命中队列：{} ", orgCode, taskCounts, orgCapacity, hit);
            }
            return hit;
        });
        if(Objects.isNull(waitingTask)){
            log.info("未获取到符合条件的可运行任务数据");
            return;
        }
        // 创建自动AI识别分析任务
        try {
            DefaultTrustedAccessInformation defaultTrustedAccessInformation = new DefaultTrustedAccessInformation();
            defaultTrustedAccessInformation.setOrgCode(waitingTask.getOrgCode());
            defaultTrustedAccessInformation.setUsername("SYSTEM");
            defaultTrustedAccessInformation.setAccountId("0");
            TrustedAccessTracerHolder.set(defaultTrustedAccessInformation);
            aiAnalysisService.execAutoAIAnalysisTask(waitingTask);
        }finally {
            TrustedAccessTracerHolder.clear();
        }
    }

}
