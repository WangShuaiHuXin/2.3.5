package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.GridManageEntity;
import com.imapcloud.nest.v2.dao.entity.GridRegionEntity;
import com.imapcloud.nest.v2.dao.po.in.GridRegionQueryCriteriaInPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Classname GridRegionMapper
 * @Description 网络区域管理 Mapper 接口
 * @Date 2022/12/9 15:25
 * @Author Carnival
 */
public interface GridRegionMapper extends BaseMapper<GridRegionEntity>,
        IPageMapper<GridRegionEntity, GridRegionQueryCriteriaInPO, PagingRestrictDo> {

    List<GridRegionEntity> selectGridRegion(@Param("orgCode")String orgCode);

    List<GridRegionEntity> selectGridRegionByGridManageIds(@Param("list") List<String> gridManageIds);
}
