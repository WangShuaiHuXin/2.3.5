package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.GridDataEntity;
import com.imapcloud.nest.v2.dao.po.in.GridDataQueryCriteriaInPO;
import com.imapcloud.nest.v2.service.dto.in.GridInDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Classname GridManageMapper
 * @Description 管理网格 Mapper 接口
 * @Date 2022/12/9 15:25
 * @Author Carnival
 */
public interface GridDataMapper extends BaseMapper<GridDataEntity>,
        IPageMapper<GridDataEntity, GridDataQueryCriteriaInPO, PagingRestrictDo> {

    int batchInsert(@Param("entityList") List<GridDataEntity> entities);

    void batchDelete(@Param("entityList") List<GridInDTO.GridManageOrgCodeDTO> gridManageOrgCodeList);

    void batchUpdateOrgCode(@Param("entityList") List<GridDataEntity> gridDataEntityList);
}
