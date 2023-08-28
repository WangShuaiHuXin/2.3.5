package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.AirspaceManageEntity;
import com.imapcloud.nest.v2.dao.po.in.AirspaceManageCriteriaPO;

/**
 * @Classname AirspaceManageMapper
 * @Description 空域管理 Mapper
 * @Date 2023/3/8 18:24
 * @Author Carnival
 */
public interface AirspaceManageMapper extends BaseMapper<AirspaceManageEntity>,
        IPageMapper<AirspaceManageEntity, AirspaceManageCriteriaPO, PagingRestrictDo> {

}
