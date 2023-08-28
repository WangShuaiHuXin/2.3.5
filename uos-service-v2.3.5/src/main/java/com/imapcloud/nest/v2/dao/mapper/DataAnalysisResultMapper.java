package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisResultEntity;
import com.imapcloud.nest.v2.dao.po.in.DataAnalysisResultInPO;
import com.imapcloud.nest.v2.dao.po.out.DataAnalysisResultOutPO;
import com.imapcloud.nest.v2.dao.po.out.GridProblemsOutPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 数据分析结果
 *
 * @author boluo
 * @date 2022-07-15
 */
@Mapper
public interface DataAnalysisResultMapper extends BaseMapper<DataAnalysisResultEntity>,  IPageMapper<DataAnalysisResultEntity, DataAnalysisResultInPO.ProblemIn, PagingRestrictDo> {
    /**
     * 批量插入
     *
     * @param dataAnalysisResultEntityList 数据分析结果实体列表
     * @return int
     */
    int batchInsert(@Param("entityList") List<DataAnalysisResultEntity> dataAnalysisResultEntityList);

    /**
     * 查询各问题级别的数量
     *
     * @param problemInPo 收集和
     * @return {@link DataAnalysisResultOutPO.CollectSumOut}
     */
    List<DataAnalysisResultOutPO.CollectSumOut> collectSum(@Param("criteria") DataAnalysisResultInPO.ProblemIn problemInPo);

    /**
     * 问题趋势
     *
     * @param problemInPo 问题趋势
     * @return {@link List}<{@link DataAnalysisResultOutPO.ProblemTrendOut}>
     */
    List<DataAnalysisResultOutPO.ProblemTrendOut> problemTrend(@Param("criteria") DataAnalysisResultInPO.ProblemIn problemInPo);

    /**
     * 批量逻辑删除
     *
     * @param markIdList 马克id列表
     * @return int
     */
    int updateDeleteByMarkIdList(@Param("markIdList") List<Long> markIdList);

    void selectByGroupIdCondition();

    List<DataAnalysisResultOutPO.GroupInfoOutPO> selectByResultGroupId(@Param("groupIdList") List<String> groupIdList);

    /**
     * 通过标注ID查询所有问题
     *
     * @param markIdList 马克id列表
     * @return {@link List}<{@link DataAnalysisResultEntity}>
     */
    List<DataAnalysisResultEntity> selectAllByMarkIdList(@Param("markIdList") List<Long> markIdList);

    List<DataAnalysisResultEntity> selectAllByResultGroupId(@Param("groupIdList") List<String> groupIdList);

    List<GridProblemsOutPO> selectGroupByGridManageIds(@Param("gridManageIds") List<String> gridManageIds, @Param("orgCode")String orgCode);

    void batchUpdateGridManage(@Param("entityList") List<DataAnalysisResultEntity> entityList);
}
