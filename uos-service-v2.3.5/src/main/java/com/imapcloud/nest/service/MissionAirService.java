package com.imapcloud.nest.service;

import com.imapcloud.nest.model.MissionAirEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imapcloud.nest.model.MissionRecordsEntity;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hc
 * @since 2021-04-21
 */
public interface MissionAirService extends IService<MissionAirEntity> {

    Map getDataByMissionId(Integer missionRecordsId);

    List<MissionRecordsEntity> getMissionRecords(String startTime, String endTime);

    int selectNum(Integer missionRecordsId, String orgCode);
}
