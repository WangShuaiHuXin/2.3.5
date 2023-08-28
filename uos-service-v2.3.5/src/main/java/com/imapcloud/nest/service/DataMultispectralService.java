package com.imapcloud.nest.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.DataMultispectralEntity;
import com.imapcloud.nest.model.MissionRecordsEntity;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 正射数据表 服务类
 * </p>
 *
 * @author hc
 * @since 2021-06-16
 */
public interface DataMultispectralService extends IService<DataMultispectralEntity> {

    List<MissionRecordsEntity> getMissionRecords(String startTime, String endTime);

    Map getAllMap(Integer id);
}
