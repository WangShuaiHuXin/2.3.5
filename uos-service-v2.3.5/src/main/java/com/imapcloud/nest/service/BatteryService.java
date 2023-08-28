package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.BatteryEntity;

import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 电池信息表 服务类
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
public interface BatteryService extends IService<BatteryEntity> {

    void exportBatteryUseNums(String nestId, HttpServletResponse response);
}
