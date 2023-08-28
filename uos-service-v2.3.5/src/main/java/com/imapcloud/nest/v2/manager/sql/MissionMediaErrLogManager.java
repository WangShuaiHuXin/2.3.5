package com.imapcloud.nest.v2.manager.sql;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.imapcloud.nest.v2.dao.entity.MissionMediaErrLogEntity;
import com.imapcloud.nest.v2.dao.po.out.MissionMediaErrLogOutPO;

import java.util.List;

public interface MissionMediaErrLogManager {
    void insert(MissionMediaErrLogEntity errLogEntity);

    void update(Integer recordId);

    List<MissionMediaErrLogOutPO> selectByMissionId(List<Integer> missionId);
}
