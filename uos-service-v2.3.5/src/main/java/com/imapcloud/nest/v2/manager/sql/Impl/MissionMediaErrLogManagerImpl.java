package com.imapcloud.nest.v2.manager.sql.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imapcloud.nest.v2.dao.entity.MissionMediaErrLogEntity;
import com.imapcloud.nest.v2.dao.mapper.MissionMediaErrLogMapper;
import com.imapcloud.nest.v2.dao.po.out.MissionMediaErrLogOutPO;
import com.imapcloud.nest.v2.manager.sql.MissionMediaErrLogManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class MissionMediaErrLogManagerImpl implements MissionMediaErrLogManager {

    @Resource
    private MissionMediaErrLogMapper missionMediaErrLogMapper;

    @Override
    public void insert(MissionMediaErrLogEntity errLogEntity) {
        missionMediaErrLogMapper.insert(errLogEntity);
    }

    @Override
    public void update(Integer recordId) {
        LambdaUpdateWrapper<MissionMediaErrLogEntity> wrapper = Wrappers.<MissionMediaErrLogEntity>lambdaUpdate().eq(MissionMediaErrLogEntity::getMissionRecordId, recordId)
                .eq(MissionMediaErrLogEntity::getDeleted, false)
                .set(MissionMediaErrLogEntity::getDeleted, true)
                .set(MissionMediaErrLogEntity::getModifiedTime, LocalDateTime.now());
        missionMediaErrLogMapper.update(null, wrapper);
    }

    @Override
    public List<MissionMediaErrLogOutPO> selectByMissionId(List<Integer> missionId) {
        LambdaQueryWrapper<MissionMediaErrLogEntity> wrapper = Wrappers.<MissionMediaErrLogEntity>lambdaQuery()
                .in(MissionMediaErrLogEntity::getMissionId, missionId)
                .eq(MissionMediaErrLogEntity::getDeleted, false);
        List<MissionMediaErrLogEntity> missionMediaErrLogEntities = missionMediaErrLogMapper.selectList(wrapper);
        List<MissionMediaErrLogOutPO> outPOS = new ArrayList<>();
        for (MissionMediaErrLogEntity missionMediaErrLogEntity : missionMediaErrLogEntities) {
            MissionMediaErrLogOutPO outPO = new MissionMediaErrLogOutPO();
            outPO.setId(missionMediaErrLogEntity.getId());
            outPO.setMissionId(missionMediaErrLogEntity.getMissionId());
            outPO.setMissionRecordId(missionMediaErrLogEntity.getMissionRecordId());
            outPO.setNestUuid(missionMediaErrLogEntity.getNestUuid());
            outPO.setErrorStep(missionMediaErrLogEntity.getErrorStep());
            outPO.setErrorFile(missionMediaErrLogEntity.getErrorFile());
            outPO.setErrorInfo(missionMediaErrLogEntity.getErrorInfo());
            outPO.setErrorSolution(missionMediaErrLogEntity.getErrorSolution());
            outPO.setErrorCode(missionMediaErrLogEntity.getErrorCode());
            outPOS.add(outPO);
        }
        return outPOS;
    }
}
