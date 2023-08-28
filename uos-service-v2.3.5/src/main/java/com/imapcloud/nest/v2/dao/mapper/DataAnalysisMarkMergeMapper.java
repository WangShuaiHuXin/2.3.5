package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisMarkMergeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * data_analysis_mark_merge
 *
 * @author boluo
 * @date 2022-10-11
 */
@Mapper
public interface DataAnalysisMarkMergeMapper extends BaseMapper<DataAnalysisMarkMergeEntity> {

    /**
     * 批量插入
     *
     * @param dataAnalysisMarkMergeEntityList 数据分析马克合并实体列表
     * @return int
     */
    int batchInsert(@Param("entityList") List<DataAnalysisMarkMergeEntity> dataAnalysisMarkMergeEntityList);

    /**
     * 根据标注ID查找当前标注是否已分组
     *
     * @param markId
     * @return
     */
    String selectIsMergeGroup(Long markId);

    /**
     * 根据标注ID批量查询标注merge结果
     * @param markIds
     * @return
     */
    List<DataAnalysisMarkMergeEntity> queryDataByMarkId(List<Long> markIds);


    void queryDataByGroupId();

    /**
     * 通过标注ID查询分组ID
     *
     * @param markIdCollection 马克id集合
     * @return {@link List}<{@link DataAnalysisMarkMergeEntity}>
     */
    List<DataAnalysisMarkMergeEntity> selectAllByMarkIdList(@Param("markIdCollection") Collection<Long> markIdCollection);

    List<DataAnalysisMarkMergeEntity> selectAllByGroupIdList(@Param("groupIdCollection") Collection<String> resultGroupIdCollection);
}
