package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.PersonManageEntity;
import com.imapcloud.nest.v2.dao.po.in.PersonManageCriteriaPO;

/**
 * @Classname PersonManageMapper
 * @Description 人员管理 Mapper
 * @Date 2023/3/28 14:08
 * @Author Carnival
 */
public interface PersonManageMapper extends BaseMapper<PersonManageEntity>,
        IPageMapper<PersonManageEntity, PersonManageCriteriaPO, PagingRestrictDo> {
}
