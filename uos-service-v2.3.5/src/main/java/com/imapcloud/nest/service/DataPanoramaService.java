package com.imapcloud.nest.service;

import com.imapcloud.nest.model.DataPanoramaEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.MissionRecordsEntity;

import java.util.List;

/**
 * <p>
 * 全景数据表 服务类
 * </p>
 *
 * @author hc
 * @since 2021-06-16
 */
public interface DataPanoramaService extends IService<DataPanoramaEntity> {

    List<MissionRecordsEntity> getMissionRecords(String startTime, String endTime);
}
