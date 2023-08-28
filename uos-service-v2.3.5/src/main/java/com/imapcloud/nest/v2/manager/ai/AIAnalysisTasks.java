package com.imapcloud.nest.v2.manager.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * AI分析任务上下文
 * @author Vastfy
 * @date 2022/11/30 17:26
 * @since 2.1.5
 */
@Slf4j
@Component
public final class AIAnalysisTasks implements ApplicationEventPublisherAware {

    private static ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationEventPublisher(@NonNull ApplicationEventPublisher applicationEventPublisher) {
        AIAnalysisTasks.applicationEventPublisher = applicationEventPublisher;
    }

    public static IAIAnalysisTask createIfAbsent(String taskId, Supplier<IAITaskProcess> supplier) {
        if(!StringUtils.hasText(taskId)){
            throw new IllegalArgumentException("taskId is null");
        }
        IAIAnalysisTask instance = AbstractAIAnalysisTask.getInstance(taskId);
        if(Objects.isNull(instance)){
            synchronized (taskId.intern()) {
                //noinspection ConstantConditions
                if(Objects.isNull(instance)){
                    instance = init(supplier);
                }
            }
        }
        return instance;
    }

    /**
     * 获取AI分析任务总数量
     * @return  AI分析任务总数量
     */
    public static int getAllTaskCounts(){
        return AbstractAIAnalysisTask.getAllTaskCounts();
    }

    /**
     * 获取指定账号的AI分析任务数量
     * @param subjectId    账号ID/单位编码
     * @return  账号的AI分析任务数量
     */
    public static int getTaskCounts(String subjectId, boolean manualTask){
        if(manualTask){
            return ManualAIAnalysisTask.getManualTaskCounts(subjectId);
        }
        return SystemAIAnalysisTask.getSystemTaskCounts(subjectId);
    }

    private static IAIAnalysisTask init(Supplier<IAITaskProcess> supplier) {
        if(Objects.isNull(supplier)){
            throw new IllegalArgumentException("AITaskProcess supplier is null");
        }
        IAITaskProcess aiTaskProcess = supplier.get();
        IAIAnalysisTask instance;
        if(aiTaskProcess instanceof ISystemAITaskProcess){
            instance = new SystemAIAnalysisTask(aiTaskProcess, applicationEventPublisher);
        }else {
            instance = new ManualAIAnalysisTask(aiTaskProcess);
        }
        instance.initialize();
        return instance;
    }

}
