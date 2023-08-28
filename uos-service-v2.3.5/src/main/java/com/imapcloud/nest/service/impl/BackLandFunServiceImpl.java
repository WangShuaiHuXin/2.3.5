package com.imapcloud.nest.service.impl;

import com.imapcloud.nest.model.BackLandFunEntity;
import com.imapcloud.nest.mapper.BackLandFunMapper;
import com.imapcloud.nest.service.BackLandFunService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 备降功能表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2021-06-30
 */
@Service
public class BackLandFunServiceImpl extends ServiceImpl<BackLandFunMapper, BackLandFunEntity> implements BackLandFunService {

    @Override
    public List<BackLandFunEntity> getNestAllBackLandPoint(String nestId) {
        return this.lambdaQuery().eq(BackLandFunEntity::getBaseNestId,nestId).list();
    }
}
