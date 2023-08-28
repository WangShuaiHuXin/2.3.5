package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.GridMissionRecordEntity;
import com.imapcloud.nest.v2.dao.po.in.GridHistoryPhotoPO;
import com.imapcloud.nest.v2.dao.po.in.GridMissionRecordCriteriaInPO;
import com.imapcloud.nest.v2.dao.po.out.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Classname GridMissionRecordMapper
 * @Description 网格任务记录Mapper
 * @Date 2022/12/9 15:25
 * @Author Carnival
 */
public interface GridMissionRecordMapper extends BaseMapper<GridMissionRecordEntity>,
        IPageMapper<GridMissionRecordEntity, GridMissionRecordCriteriaInPO, PagingRestrictDo> {

    List<GridStatisticsPO> getMissionRecordsIdByGridManageIds(@Param("gridManageIds") List<String> gridManageIds);

    List<GridMissionRecordsPO> getMissionRecordsIdByGridManageIdsToPhoto(@Param("gridManageIds") List<String> gridManageIds);

    List<GridDataStatusPO> getMissionRecordsDataStatus(@Param("missionRecordsIds") List<Integer> missionRecordsIds);

    List<GridMissionNamePO> getMissionNameAndTaskName(@Param("missionIds") List<Integer> missionIds);

    List<GridHistoryPhotoPO> getHistoryPhoto(@Param("photoIds") List<Long> photoIds, @Param("orgCode")String orgCode);

    List<GridMissionRecordsPO> selectGridMissionRecords(@Param("gridManageIds") List<String> gridManageIds);

    List<GridMissionRecordsPO> selectGridMissionRecordsWithOrgCode(@Param("gridManageIds") List<String> gridManageIds, @Param("orgCode")String orgCode);

    List<GridManageIdAndMaxPO> queryGridManageIdAndMax(@Param("gridManageIds") List<String> gridManageIds);

    List<GridCenterDetailIdOutPO> queryCenterDetailId(@Param("photoIds") List<Long> photoIds);
}
