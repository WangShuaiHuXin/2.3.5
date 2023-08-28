package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisResultInDTO;
import com.imapcloud.nest.v2.service.dto.out.DataAnalysisResultOutDTO;
import com.imapcloud.nest.v2.service.dto.out.GridOutDTO;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 数据分析结果服务
 *
 * @author boluo
 * @date 2022-07-15
 */
public interface DataAnalysisResultService {

    /**
     * 批量插入
     *
     * @param insertInfoInList 在列表中插入信息
     * @param accountId        帐户id
     */
    void batchInsert(List<DataAnalysisResultInDTO.InsertInfoIn> insertInfoInList, String accountId);

    /**
     * 按问题级别查询问题数量
     *
     * @param problemIn 参数
     * @return {@link DataAnalysisResultOutDTO.CollectSumOut}
     */
    DataAnalysisResultOutDTO.CollectSumOut collectSum(DataAnalysisResultInDTO.ProblemIn problemIn);

    /**
     * 问题趋势
     *
     * @param problemIn 参数
     * @return {@link DataAnalysisResultOutDTO.ProblemTrendOut}
     */
    List<DataAnalysisResultOutDTO.ProblemTrendOut> problemTrend(DataAnalysisResultInDTO.ProblemIn problemIn);

    /**
     * 问题列表
     *
     * @param problemIn 问题
     * @return {@link PageResultInfo}<{@link DataAnalysisResultOutDTO.ProblemOut}>
     */
    PageResultInfo<DataAnalysisResultOutDTO.ProblemOut> problemList(DataAnalysisResultInDTO.ProblemIn problemIn);

    /**
     * 问题列表
     *
     * @param resultIdList 结果id列表
     * @return {@link List}<{@link DataAnalysisResultOutDTO.ProblemOut}>
     */
    List<DataAnalysisResultOutDTO.ProblemOut> problemList(List<Long> resultIdList);

    /**
     * 批量逻辑删除结果
     *
     * @param markIdList 标注id列表
     * @return int
     */
    int deleteByMarkIdList(List<Long> markIdList, Boolean flag);

    /**
     * 问题删除、取消合并操作，问题组和合并关系是否删除逻辑
     *
     * @param resultGroupIdCollection 问题组id集合
     */
    void balance(Collection<String> resultGroupIdCollection);

    /**
     * 查询同组下的所有问题信息
     *
     * @param resultGroupId 结果组id
     * @return {@link List}<{@link DataAnalysisResultOutDTO.ProblemOut}>
     */
    List<DataAnalysisResultOutDTO.ProblemOut> selectResultByGroupId(String resultGroupId);

    /**
     * 查询问题列表
     *
     * @param gridManageId
     * @return
     */
    List<GridOutDTO.ProblemDTO> listProblemsByGridId(String gridManageId);

    /**
     * 统计各个网格的问题个数
     * @param gridManageIdList
     * @return
     */
    Map<String, Integer> statisticsGridProblems(List<String> gridManageIdList);
}
