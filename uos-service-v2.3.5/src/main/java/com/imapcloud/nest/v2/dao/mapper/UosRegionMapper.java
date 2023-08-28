package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.mp.mapper.IPageMapper;
import com.imapcloud.nest.v2.dao.entity.UosRegionEntity;
import com.imapcloud.nest.v2.dao.po.UosRegionQueryCriteriaPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Classname UosRegionMapper
 * @Description 区域管理 Mapper 接口
 * @Date 2022/8/11 11:16
 * @Author Carnival
 */
@Mapper
public interface UosRegionMapper extends BaseMapper<UosRegionEntity> ,
        IPageMapper<UosRegionEntity, UosRegionQueryCriteriaPO, PagingRestrictDo> {

    int queryRegionUsed(String regionId);
}
