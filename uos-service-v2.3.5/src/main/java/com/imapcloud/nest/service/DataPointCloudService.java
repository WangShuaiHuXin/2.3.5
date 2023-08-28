package com.imapcloud.nest.service;

import com.imapcloud.nest.model.DataPointCloudEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.MissionRecordsEntity;

import java.util.List;

/**
 * <p>
 * 点云数据表 服务类
 * </p>
 *
 * @author hc
 * @since 2021-06-16
 */
public interface DataPointCloudService extends IService<DataPointCloudEntity> {

    List<MissionRecordsEntity> getMissionRecords(String startTime, String endTime);

    String getLasUrlById(Integer id);

    /**
     * 根据id修改state为0
     * @param beforeId
     * @param afterId
     * @param state
     */
    void updateStateById(Integer beforeId, Integer afterId, Integer state);
}
