package com.imapcloud.nest.service;

import com.imapcloud.nest.model.BackLandFunEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 备降功能表 服务类
 * </p>
 *
 * @author wmin
 * @since 2021-06-30
 */
public interface BackLandFunService extends IService<BackLandFunEntity> {

    /**
     * 获取所有备降点
     * @param nestId
     * @return
     */
    List<BackLandFunEntity> getNestAllBackLandPoint(String nestId);
}
