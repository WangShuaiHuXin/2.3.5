package com.imapcloud.nest.service.impl;

import com.imapcloud.nest.model.NestMaintenanceMessageEntity;
import com.imapcloud.nest.mapper.NestMaintenanceMessageMapper;
import com.imapcloud.nest.service.NestMaintenanceMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 机巢维保表与维保项目的关联表 服务实现类
 * </p>
 *
 * @author hc
 * @since 2021-05-17
 */
@Service
public class NestMaintenanceMessageServiceImpl extends ServiceImpl<NestMaintenanceMessageMapper, NestMaintenanceMessageEntity> implements NestMaintenanceMessageService {

}
