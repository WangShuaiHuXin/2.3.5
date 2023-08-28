package com.imapcloud.nest.service;

import com.imapcloud.nest.model.SysPermissionEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户权限表 服务类
 * </p>
 *
 * @author root
 * @since 2020-08-25
 */
public interface SysPermissionService extends IService<SysPermissionEntity> {


    SysPermissionEntity queryByPermissionName(String sysPermissionName);
}
