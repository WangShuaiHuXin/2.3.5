package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.mapper.SysUnitFenceMapper;
import com.imapcloud.nest.model.SysUnitFenceEntity;
import com.imapcloud.nest.service.SysUnitFenceService;
import com.imapcloud.nest.utils.RestRes;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 单位-电子围栏缓冲范围表 服务实现类
 * </p>
 *
 * @author zheng
 * @since 2021-09-26
 */
@Service
public class SysUnitFenceServiceImpl extends ServiceImpl<SysUnitFenceMapper, SysUnitFenceEntity> implements SysUnitFenceService {

    @Override
    public RestRes getFenceRange(String unitId) {
        LambdaQueryWrapper<SysUnitFenceEntity> condition = Wrappers.lambdaQuery(SysUnitFenceEntity.class)
                .eq(SysUnitFenceEntity::getOrgCode, unitId)
                .eq(SysUnitFenceEntity::getDeleted, false);
        SysUnitFenceEntity sysUnitFenceEntity = this.getOne(condition);
        Map map = new HashMap();
        map.put("data", sysUnitFenceEntity);
        return RestRes.ok(map);
    }

    @Override
    public RestRes setFenceRange(SysUnitFenceEntity sysUnitFenceEntity) {
        this.saveOrUpdate(sysUnitFenceEntity);
        return RestRes.ok();
    }
}
