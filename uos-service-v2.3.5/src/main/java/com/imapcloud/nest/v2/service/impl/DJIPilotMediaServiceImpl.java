package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.lang.UUID;
import com.alibaba.nacos.common.utils.MD5Utils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.imapcloud.nest.enums.UploadTypeEnum;
import com.imapcloud.nest.model.MissionEntity;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.model.TaskEntity;
import com.imapcloud.nest.service.MissionRecordsService;
import com.imapcloud.nest.service.MissionService;
import com.imapcloud.nest.service.TaskService;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.manager.dataobj.in.DjiFileUploadCallBackDO;
import com.imapcloud.nest.v2.manager.dataobj.out.SessionSecurityTokenOutDO;
import com.imapcloud.nest.v2.manager.event.DJIFileUploadCallBackEvent;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.DJIPilotMediaService;
import com.imapcloud.nest.v2.service.converter.DJIPilotConverter;
import com.imapcloud.nest.v2.service.dto.in.PilotFileUploadInDTO;
import com.imapcloud.nest.v2.service.dto.out.PilotStsCredentialsOutDTO;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Slf4j
@Service
public class DJIPilotMediaServiceImpl implements DJIPilotMediaService {

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private MissionRecordsService missionRecordsService;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private TaskService taskService;

    @Resource
    private MissionService missionService;

    @Resource
    private RedisService redisService;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private FileManager fileManager;

    /**
     * 获取授权信息
     *
     * @return
     */
    @Override
    public PilotStsCredentialsOutDTO getSts() {
        SessionSecurityTokenOutDO outDO = this.fileManager.getSecurityToken(3600 , Boolean.TRUE);
        return PilotStsCredentialsOutDTO.builder()
                .bucket(this.geoaiUosProperties.getMinio().getBucketName())
                .credentials(PilotStsCredentialsOutDTO.CredentialsOutDTO.builder()
                        .accessKeyId(outDO.getAccessKey())
                        .accessKeySecret(outDO.getSecretKey())
                        .expire(3600L - 300L)
                        .securityToken(outDO.getSessionToken())
                        .build())
                .endpoint(outDO.getEndpoint())
                .objectKeyPrefix(UploadTypeEnum.DJI_PILOT_AUTO_UPLOAD.getPath())
                .provider("minio")
                .region("")
                .build();
    }

    @Override
    public String uploadCallback(PilotFileUploadInDTO pilotFileUploadInDTO) {
        log.info("【DJIPilotMediaServiceImpl】【uploadCallback】-> {}" , pilotFileUploadInDTO.toString());
        String objectKey = "";
        //查找有没有对应的架次记录，如果有则将其归到一个架次记录里，否则生成新的架次记录数据
        String path = pilotFileUploadInDTO.getPath();
        String md5Key = MD5Utils.md5Hex( path, "UTF-8");

        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(MissionRecordsEntity.class)
                .eq(MissionRecordsEntity::getExecId, md5Key);
        MissionRecordsEntity missionRecordsEntity = (MissionRecordsEntity) missionRecordsService.list(queryWrapper)
                .stream()
                .findFirst()
                .orElseGet(() -> new MissionRecordsEntity());

        if(missionRecordsEntity.getId() == null){
            //空的情况下，需要新增records
            //新增task、Mission、MissionRecords
            String uavSn = pilotFileUploadInDTO.getExt().getSn();
            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.DJI_PILOT_UAV_BIND , uavSn);
            String nestSnValue = (String) this.redisService.get(redisKey);
            String nestId = this.baseNestService.getNestIdByNestUuid(nestSnValue);
            Integer taskId = this.saveTask(nestId , pilotFileUploadInDTO.getPath());
            Integer missionId = this.saveMission(taskId);
            Integer missionRecordsId = this.saveMissionRecords(nestId,String.valueOf(taskId), missionId , md5Key);
        }
        pilotFileUploadInDTO.getExt().setFlightId(md5Key);
        this.applicationContext.publishEvent(new DJIFileUploadCallBackEvent(new DjiFileUploadCallBackDO()
                .setNestTypeEnum(NestTypeEnum.DJI_PILOT)
                .setDjiPilotFileUploadCallBackDO(DJIPilotConverter.INSTANCES.convert(pilotFileUploadInDTO))
        ));
        return objectKey;
    }

    /**
     * 保存task任务
     * @return
     */
    private Integer saveTask(String nestId , String name){
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setName(name);
        taskEntity.setDescription("");
        taskEntity.setType(0);
        taskEntity.setNestId(0);
        taskEntity.setBaseNestId(nestId);
        taskEntity.setCopyCount(0);
        taskEntity.setMold(0);
        taskEntity.setUnitId("");
        taskEntity.setOrgCode(TrustedAccessTracerHolder.get().getOrgCode());
//        taskEntity.setSubType(0);
        taskEntity.setCreatorId(0L);
        taskEntity.setCreateTime(LocalDateTime.now());
        taskEntity.setModifyTime(LocalDateTime.now());
        taskEntity.setDeleted(false);
//        taskEntity.setDataType(0);
//        taskEntity.setTagId(0);
        this.taskService.save(taskEntity);
        return taskEntity.getId();
    }

    /**
     * 保存架次信息
     * @return
     */
    private Integer saveMission( Integer taskId){
        MissionEntity missionEntity = new MissionEntity();
        missionEntity.setName("");
        missionEntity.setUuid(UUID.randomUUID().toString());
        missionEntity.setSeqId(0);
        missionEntity.setAirLineId(-1);
        missionEntity.setTaskId(taskId);
        missionEntity.setMissionParamId(-1);
        missionEntity.setCopyCount(0);
        missionEntity.setLastFlightStrategy(0);
        missionEntity.setCreateUserId(0);
        missionEntity.setCreateTime(LocalDateTime.now());
        missionEntity.setModifyTime(LocalDateTime.now());
        missionEntity.setDeleted(false);
        missionEntity.setOrgCode(TrustedAccessTracerHolder.get().getOrgCode());
        this.missionService.save(missionEntity);
        return missionEntity.getId();
    }

    /**
     * 保存任务架次记录信息
     * @return
     */
    private Integer saveMissionRecords( String nestId , String taskId , Integer missionId , String flightId ){
        MissionRecordsEntity missionRecordsEntity = new MissionRecordsEntity();
        missionRecordsEntity.setMissionId(missionId);
        missionRecordsEntity.setExecId(flightId);
        missionRecordsEntity.setAppId(0);
        missionRecordsEntity.setBaseAppId(nestId);
        missionRecordsEntity.setStatus(0);
        missionRecordsEntity.setUploadTime(LocalDateTime.now());
        missionRecordsEntity.setStartTime(LocalDateTime.now());
        missionRecordsEntity.setEndTime(LocalDateTime.now());
        missionRecordsEntity.setDataStatus(0);
        missionRecordsEntity.setDataPath("");
        missionRecordsEntity.setVideoPath("");
        missionRecordsEntity.setDataSize(0);
        missionRecordsEntity.setMiles(0.0D);
        missionRecordsEntity.setSeconds(0L);
        missionRecordsEntity.setReachIndex(0);
        missionRecordsEntity.setBackUpStatus(false);
        missionRecordsEntity.setGainDataMode(0);
        missionRecordsEntity.setGainVideoData(0);
        missionRecordsEntity.setGainVideo(0);
        missionRecordsEntity.setFlyIndex(0);
        missionRecordsEntity.setCreateUserId(Long.valueOf(TrustedAccessTracerHolder.get().getAccountId()));
        missionRecordsEntity.setCreateTime(LocalDateTime.now());
        missionRecordsEntity.setCreateTimeStr("");
        missionRecordsEntity.setModifyTime(LocalDateTime.now());
        missionRecordsEntity.setTaskName("");
        missionRecordsEntity.setTaskId(Integer.valueOf(taskId));
        missionRecordsEntity.setSysTagName("");
        missionRecordsEntity.setSysTagId(0);
        missionRecordsEntity.setNestId("");
        missionRecordsEntity.setDeleted(false);
        missionRecordsEntity.setRecordStatus(0);
        missionRecordsEntity.setUnitId("");
        missionRecordsEntity.setOrgCode(TrustedAccessTracerHolder.get().getOrgCode());
        missionRecordsEntity.setUavWhich(0);
        this.missionRecordsService.save(missionRecordsEntity);
        return missionRecordsEntity.getId();
    }

}
