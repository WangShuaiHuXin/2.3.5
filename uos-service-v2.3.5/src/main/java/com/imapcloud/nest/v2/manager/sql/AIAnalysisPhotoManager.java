package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.dao.entity.AIAnalysisPhotoEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * AI分析图片数据管理器
 * @author Vastfy
 * @date 2022/11/3 16:16
 * @since 2.1.4
 */
public interface AIAnalysisPhotoManager {

    AIAnalysisPhotoEntity findAIAnalysisPhotoEntity(String recordId);

    /**
     * 根据分析图片ID查询分析图片信息
     * @param taskId    任务ID
     * @return  分析任务记录
     */
    List<AIAnalysisPhotoEntity> getAnalysisPhotos(String taskId);

    /**
     * 根据分析图片详情ID查询分析图片信息
     * @param centerDetailIds    基础数据详情ID列表
     * @param states    图片识别状态
     * @return  分析任务记录
     */
    List<AIAnalysisPhotoEntity> fetchAnalysisPhotos(Collection<Long> centerDetailIds, Integer... states);

    /**
     * 根据分析图片详情ID查询分析图片信息
     * @param recordIds    分析图片记录ID列表
     * @return  分析任务记录
     */
    List<AIAnalysisPhotoEntity> fetchAnalysisPhotos(Collection<String> recordIds);

    /**
     * 根据分析图片记录ID列表查询分析图片信息
     * @param recordIds    分析图片记录ID列表
     * @return  分析图片记录
     */
    List<AIAnalysisPhotoEntity> fetchIncompleteAnalysisPhotos(Collection<String> recordIds);

    /**
     * 根据分析图片记录ID列表查询分析图片信息
     * @return  分析图片记录
     */
    int getIncompleteAnalysisPhotoCounts(String taskId);

    /**
     * 乐观锁更新状态
     * @param updateInfo  更新数据
     * @return  是否更新成功
     */
    @Transactional(rollbackFor = Exception.class)
    boolean updateAIAnalysisPhotoEntity(AIAnalysisPhotoEntity updateInfo);

    /**
     * 乐观锁更新状态
     */
    @Transactional(rollbackFor = Exception.class)
    void updateAutoAIAnalysisPhoto(Integer expectState, String aiTaskId, String taskId, List<Integer> states);

    /**
     * 乐观锁更新状态
     */
    @Transactional(rollbackFor = Exception.class)
    void updateAIAnalysisPhotoState(Integer expectState, String taskId, List<Integer> states);

}
