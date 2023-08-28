package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.manager.ai.WaitingTaskElement;
import com.imapcloud.nest.v2.service.dto.in.AIRecognitionTaskInDTO;
import com.imapcloud.nest.v2.service.dto.in.AutoAIRecognitionTaskInDTO;
import com.imapcloud.nest.v2.service.dto.out.AIAnalysisPicResultDataOutDTO;
import com.imapcloud.nest.v2.service.dto.out.AIAnalysisRepoOutDTO;

import java.util.List;

/**
 * AI分析业务接口
 * @author Vastfy
 * @date 2022/11/2 13:41
 * @since 2.1.4
 */
public interface AIAnalysisService {

    /**
     * 获取已授权的识别功能列表
     * @return  识别功能列表
     */
    List<AIAnalysisRepoOutDTO> getGrantedRecFunctions(String orgCode);

    /**
     * 创建AI分析任务
     * @param data  任务数据
     * @return  任务ID
     */
    String createManualAIAnalysisTask(AIRecognitionTaskInDTO data);

    /**
     * 创建自动AI分析任务
     * @param autoAiTask  自动AI任务数据
     * @return  任务ID
     */
    String createAutoAIAnalysisTask(AutoAIRecognitionTaskInDTO autoAiTask);

    /**
     * 创建自动AI分析任务
     * @param waitingTask  等待执行任务信息
     */
    void execAutoAIAnalysisTask(WaitingTaskElement waitingTask);

    /**
     * 暂停AI分析任务
     * @param taskId 任务ID
     */
    void pauseAIRecognitionTask(String taskId);

    /**
     * 继续AI分析任务
     * @param taskId    任务ID
     */
    void resumeAIRecognitionTask(String taskId);

    /**
     * 终止AI分析任务
     * @param taskId    任务ID
     */
    void stopAIRecognitionTask(String taskId);

    /**
     * 处理图片分析结果
     * @param aiData   AI分析结果
     */
    void handlePhotoAnalysisResults(AIAnalysisPicResultDataOutDTO aiData);

    /**
     * 处理任务分析结果
     * @param aiTaskId   AI分析任务ID
     */
    void handleTaskAnalysisResult(String aiTaskId);

    /**
     * 加载指定账号进行中的AI分析任务信息
     * @param accountId 账号ID
     */
    void loadProcessingTasks(String accountId);

    /**
     * 加载所有进行中的AI分析任务信息
     */
    void loadProcessingTasks();

    /**
     * 加载所有等待中（未处理）的AI分析任务信息
     */
    void loadWaitingTasks();

    /**
     * AI分析任务超时处理
     * @param taskId   任务ID
     */
    void handleIfAITaskTimeout(String taskId);
}
