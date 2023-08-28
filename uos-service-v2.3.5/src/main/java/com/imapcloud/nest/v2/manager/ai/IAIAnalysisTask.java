package com.imapcloud.nest.v2.manager.ai;

import com.imapcloud.nest.v2.service.dto.out.AIAnalysisPicResultDataOutDTO;

import java.util.function.Consumer;

/**
 * AI分析任务接口
 * @author Vastfy
 * @date 2022/11/30 15:43
 * @since 2.1.5
 */
public interface IAIAnalysisTask {

    /**
     * 获取AI任务进行信息
     * @return  AI任务进度信息
     */
    IAITaskProcess getTaskProcess();

    /**
     * AI任务初始化
     */
    void initialize();

    /**
     * AI任务销毁
     */
    void destroy();

    /**
     * 暂停AI分析任务
     * @param postOperation 后置操作
     */
    void paused(Consumer<IAITaskProcess> postOperation);

    /**
     * 继续AI分析任务
     * @param postOperation 后置操作
     */
    void resume(Consumer<IAITaskProcess> postOperation);

    /**
     * 终止AI分析任务
     * @param postOperation 后置操作
     */
    void terminated(Consumer<IAITaskProcess> postOperation);

    /**
     * 刷新AI分析任务
     * @param postOperation 后置操作
     */
    void refreshing(AIAnalysisPicResultDataOutDTO aiData, Consumer<IAITaskProcess> postOperation);

    /**
     * 完成AI分析任务
     * @param postOperation 后置操作
     */
    void completed(Consumer<IAITaskProcess> postOperation);

}
