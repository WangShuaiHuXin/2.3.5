package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.mapper.MapParamMapper;
import com.imapcloud.nest.model.MapParamEntity;
import com.imapcloud.nest.service.MapParamService;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zheng
 * @since 2021-08-17
 */
@Service
public class MapParamServiceImpl extends ServiceImpl<MapParamMapper, MapParamEntity> implements MapParamService {

    @Override
    public RestRes saveMapParam(MapParamEntity mapParamEntity) {
        String orgCode = mapParamEntity.getOrgCode();
        QueryWrapper<MapParamEntity> con = new QueryWrapper<MapParamEntity>().eq("deleted", 0).eq("org_code", orgCode);
        MapParamEntity one = this.getOne(con);
        boolean res = one == null ? this.save(mapParamEntity) : this.update(mapParamEntity, con);

        return RestRes.ok();
    }

    @Override
    public MapParamEntity getMapParam(String orgCode) {
        MapParamEntity mapParamEntity = this.getOne(new QueryWrapper<MapParamEntity>().eq("deleted", 0).eq("org_code", orgCode));
        return mapParamEntity;
    }
}
