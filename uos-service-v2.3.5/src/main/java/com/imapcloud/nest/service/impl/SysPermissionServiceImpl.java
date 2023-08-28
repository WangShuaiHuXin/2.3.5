package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.imapcloud.nest.model.SysPermissionEntity;
import com.imapcloud.nest.mapper.SysPermissionMapper;
import com.imapcloud.nest.service.SysPermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户权限表 服务实现类
 * </p>
 *
 * @author root
 * @since 2020-08-25
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermissionEntity> implements SysPermissionService {


    @Override
    public SysPermissionEntity queryByPermissionName(String permissionName) {
        return this.getOne(new QueryWrapper<SysPermissionEntity>().eq("name",permissionName));
    }
}
