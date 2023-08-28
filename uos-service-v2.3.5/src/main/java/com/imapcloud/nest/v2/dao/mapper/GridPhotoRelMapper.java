package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.GridPhotoRelEntity;
import com.imapcloud.nest.v2.dao.po.in.GridPhotoRelQueryCriteriaInPO;
import com.imapcloud.nest.v2.dao.po.out.GridManageHasDataPO;
import com.imapcloud.nest.v2.service.dto.in.GridHistoryInDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Classname GridPhotoRelMapper
 * @Description 照片与网格关联类 Mapper 接口
 * @Date 2022/12/9 15:25
 * @Author Carnival
 */
public interface GridPhotoRelMapper extends BaseMapper<GridPhotoRelEntity>,
        IPageMapper<GridPhotoRelEntity, GridPhotoRelQueryCriteriaInPO, PagingRestrictDo> {

    int batchInsert(@Param("entityList") List<GridPhotoRelEntity> entities);

    Integer queryLatestMission(@Param("gridManageId") String gridManageId);

    List<Long> queryPhotoIds(@Param("criteria") GridPhotoRelQueryCriteriaInPO po);

    List<GridManageHasDataPO> gridManageHasData(@Param("gridManageIds")List<String> gridManageIds, @Param("startTime")String startTime, @Param("endTime")String endTime);

}
