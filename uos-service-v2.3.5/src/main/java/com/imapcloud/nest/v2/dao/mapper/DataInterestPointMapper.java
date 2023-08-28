package com.imapcloud.nest.v2.dao.mapper;

import com.geoai.common.mp.entity.PagingRestrictDo;
import com.imapcloud.nest.v2.dao.entity.DataInterestPointEntity;
import com.geoai.common.mp.mapper.IPageMapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.po.DataInterestPointQueryCriteriaPO;

/**
 * @Classname DataInterestPointMapper
 * @Description 全景兴趣点Mapper
 * @Date 2022/9/26 11:32
 * @Author Carnival
 */
public interface DataInterestPointMapper extends BaseMapper<DataInterestPointEntity>,
        IPageMapper<DataInterestPointEntity, DataInterestPointQueryCriteriaPO, PagingRestrictDo>{
}
