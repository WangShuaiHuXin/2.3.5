package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.dao.po.out.MissionMediaErrLogOutPO;
import com.imapcloud.nest.v2.service.dto.out.MissionMediaErrLogOutDTO;
import com.imapcloud.sdk.pojo.entity.MissionMediaErrStatus;

import java.util.List;

public interface MissionMediaErrLogService {
    void saveErrLog(MissionMediaErrStatus missionMediaErrStatus, Integer recordId, Integer missionId, String nestUUid);

    void deleteErrLog(Integer recordId);

    List<MissionMediaErrLogOutDTO> selectList(List<Integer> missionId);
}
