package com.imapcloud.nest.service;

import com.imapcloud.nest.model.SysUnitFenceEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.utils.RestRes;

/**
 * <p>
 * 单位-电子围栏缓冲范围表 服务类
 * </p>
 *
 * @author zheng
 * @since 2021-09-26
 */
public interface SysUnitFenceService extends IService<SysUnitFenceEntity> {
    RestRes getFenceRange(String unitId);
    RestRes setFenceRange(SysUnitFenceEntity sysUnitFenceEntity);
}
