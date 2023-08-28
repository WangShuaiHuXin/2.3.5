package com.imapcloud.nest.service;

import com.imapcloud.nest.model.StationInfraredThresholdEntity;
import com.imapcloud.nest.utils.RestRes;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author zheng
 * @since 2021-04-13
 */
public interface StationInfraredThresholdService extends IService<StationInfraredThresholdEntity> {

    void setTemperature(Double temperature);

	RestRes getList(StationInfraredThresholdEntity stationInfraredThresholdEntity);

	RestRes updateData(StationInfraredThresholdEntity stationInfraredThresholdEntity);
}
