package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.dao.entity.AIAnalysisPhotoEntity;
import com.imapcloud.nest.v2.dao.entity.AIAnalysisTaskEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * AI分析任务数据管理器
 *
 * @author Vastfy
 * @date 2022/11/2 16:16
 * @since 2.1.4
 */
public interface AIAnalysisTaskManager {

    /**
     * 根据分析任务ID查询分析任务信息
     * @param taskId    任务ID
     * @return  分析任务记录
     */
    AIAnalysisTaskEntity findAIAnalysisTaskEntity(String taskId);

    /**
     * 根据第三方AI分析任务ID查询任务信息
     * @param aiTaskIds AI分析任务ID列表
     * @return  分析任务记录
     */
    List<AIAnalysisTaskEntity> fetchAIAnalysisTasks(Collection<String> aiTaskIds);

    /**
     * 查询AI分析任务信息
     * @param aiTaskStates AI任务状态
     * @return  分析任务记录
     */
    List<AIAnalysisTaskEntity> fetchAIAnalysisTasks(Integer... aiTaskStates);

    /**
     * 查询账号的AI分析任务信息
     * @param accountId 账号ID
     * @return  分析任务记录
     */
    List<AIAnalysisTaskEntity> fetchIncompleteTasks(String accountId);

    /**
     * 查询单位下的AI分析任务信息
     * @param orgCode 单位编码
     * @return  分析任务记录
     */
    List<AIAnalysisTaskEntity> fetchOrgIncompleteTasks(String orgCode);

    /**
     * 新增分析任务记录
     * @param taskEntity    分析任务信息
     * @return  true：插入成功
     */
    @Transactional(rollbackFor = Exception.class)
    boolean insertAnalysisTask(AIAnalysisTaskEntity taskEntity, Collection<? extends AIAnalysisPhotoEntity> photos);

    /**
     * 更新分析任务记录
     * @param taskEntity    最新任务信息
     * @return  true：更新成功
     */
    @Transactional(rollbackFor = Exception.class)
    boolean updateAIAnalysisTaskEntity(AIAnalysisTaskEntity taskEntity);

}
