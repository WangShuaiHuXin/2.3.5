package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.imapcloud.nest.v2.dao.entity.MissionMediaErrLogEntity;
import com.imapcloud.nest.v2.dao.mapper.MissionMediaErrLogMapper;
import com.imapcloud.nest.v2.dao.po.out.MissionMediaErrLogOutPO;
import com.imapcloud.nest.v2.manager.sql.MissionMediaErrLogManager;
import com.imapcloud.nest.v2.service.MissionMediaErrLogService;
import com.imapcloud.nest.v2.service.dto.out.MissionMediaErrLogOutDTO;
import com.imapcloud.sdk.pojo.entity.MissionMediaErrStatus;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MissionMediaErrLogServiceImpl implements MissionMediaErrLogService {

    @Resource
    private MissionMediaErrLogManager missionMediaErrLogManager;

    @Override
    public void saveErrLog(MissionMediaErrStatus missionMediaErrStatus, Integer recordId, Integer missionId, String nestUUid) {
        MissionMediaErrLogEntity errLogEntity = new MissionMediaErrLogEntity();
        errLogEntity.setMissionId(missionId);
        errLogEntity.setMissionRecordId(recordId);
        errLogEntity.setNestUuid(nestUUid);
        errLogEntity.setErrorStep(missionMediaErrStatus.errorStep);
        errLogEntity.setErrorFile(missionMediaErrStatus.errorFile);
        errLogEntity.setErrorInfo(missionMediaErrStatus.errorInfo);
        errLogEntity.setErrorCode(missionMediaErrStatus.errorCode);
        errLogEntity.setErrorSolution(missionMediaErrStatus.errorSolution);
        errLogEntity.setDeleted(false);
        errLogEntity.setCreatedTime(LocalDateTime.now());
        errLogEntity.setModifiedTime(LocalDateTime.now());
        missionMediaErrLogManager.insert(errLogEntity);
    }

    @Override
    public void deleteErrLog(Integer recordId) {

        missionMediaErrLogManager.update(recordId);
    }

    @Override
    public List<MissionMediaErrLogOutDTO> selectList(List<Integer> missionId) {
        List<MissionMediaErrLogOutPO> outPOS = missionMediaErrLogManager.selectByMissionId(missionId);
        List<MissionMediaErrLogOutDTO> outDTOS = new ArrayList<>();
        if (CollectionUtil.isEmpty(outPOS)) {
            return outDTOS;
        }
        outDTOS = outPOS.stream().map(outPO -> {
            MissionMediaErrLogOutDTO dto = new MissionMediaErrLogOutDTO();
            BeanUtils.copyProperties(outPO, dto);
            return dto;
        }).collect(Collectors.toList());
        return outDTOS;
    }
}
