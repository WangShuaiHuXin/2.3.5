package com.imapcloud.nest.service;

import com.imapcloud.nest.model.SysWhiteListEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 系统白名单表 服务类
 * </p>
 *
 * @author hc
 * @since 2022-03-09
 */
public interface SysWhiteListService extends IService<SysWhiteListEntity> {

    List<String> listMqttBrokerWhiteListsCache();
}
