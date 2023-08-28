package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.in.DataAnalysisMarkMergeInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.DataAnalysisMarkMergeOutDO;

import java.util.Collection;
import java.util.List;

/**
 * data_analysis_mark_merge防腐层
 *
 * @author boluo
 * @date 2022-10-11
 */
public interface DataAnalysisMarkMergeManager {

    /**
     * 批量插入
     *
     * @param dataAnalysisMarkMergeInDOList 数据分析在dolist马克合并
     * @return int
     */
    int batchInsert(List<DataAnalysisMarkMergeInDO> dataAnalysisMarkMergeInDOList);

    /**
     * 批量查询同组下的合并关系
     *
     * @param resultGroupIdCollection 结果组id列表
     * @return {@link List}<{@link DataAnalysisMarkMergeOutDO}>
     */
    List<DataAnalysisMarkMergeOutDO> selectListByGroupIdList(Collection<String> resultGroupIdCollection);

    /**
     * 逻辑删除标注的合并关系
     *
     * @param markIdCollection 标注ID
     * @return int
     */
    int deleteByMarkIdList(Collection<Long> markIdCollection);

    /**
     * 逻辑删除
     *
     * @param resultGroupIdCollection 结果组id列表
     * @return int
     */
    int deleteByResultGroupIdList(Collection<String> resultGroupIdCollection);

    /**
     * 批量标注所在的问题组 包括已删除的数据
     *
     * @param markIdCollection 结果组id列表
     * @return {@link List}<{@link DataAnalysisMarkMergeOutDO}>
     */
    List<DataAnalysisMarkMergeOutDO> selectAllListByMarkIdList(Collection<Long> markIdCollection);

    /**
     * 查询所有标注，不管是否是已删除的
     * @param resultGroupIdCollection
     * @return
     */
    List<DataAnalysisMarkMergeOutDO> selectAllListByGroupIdList(Collection<String> resultGroupIdCollection);
}
