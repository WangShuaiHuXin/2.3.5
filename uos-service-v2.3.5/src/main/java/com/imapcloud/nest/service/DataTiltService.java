package com.imapcloud.nest.service;

import com.imapcloud.nest.model.DataTiltEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.MissionRecordsEntity;

import java.util.List;

/**
 * <p>
 * 倾斜数据表 服务类
 * </p>
 *
 * @author hc
 * @since 2021-06-16
 */
public interface DataTiltService extends IService<DataTiltEntity> {

    List<MissionRecordsEntity> getMissionRecords(String startTime, String endTime);
}
