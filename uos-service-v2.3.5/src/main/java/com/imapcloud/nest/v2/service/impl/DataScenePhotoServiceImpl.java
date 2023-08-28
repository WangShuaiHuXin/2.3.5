package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.core.util.DateUtils;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.mapper.*;
import com.imapcloud.nest.model.*;
import com.imapcloud.nest.v2.common.enums.DataAnalysisPicSourceEnum;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.dao.entity.DataScenePhotoEntity;
import com.imapcloud.nest.v2.dao.mapper.DataScenePhotoMapper;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestOutDO;
import com.imapcloud.nest.v2.manager.sql.BaseNestManager;
import com.imapcloud.nest.v2.service.DataAnalysisBaseService;
import com.imapcloud.nest.v2.service.DataScenePhotoService;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisAggSaveInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisDetailSaveInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataAnalysisMarkSaveInDTO;
import com.imapcloud.nest.v2.service.dto.in.DataScenePhotoDTO;
import com.imapcloud.nest.v2.service.dto.out.DataScenePhotoOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 现场取证
 *
 * @author boluo
 * @date 2022-07-13
 */
@Slf4j
@Service
public class DataScenePhotoServiceImpl implements DataScenePhotoService {

    @Resource
    private DataAnalysisBaseService dataAnalysisBaseService;

    @Resource
    private DataScenePhotoMapper dataScenePhotoMapper;

    @Resource
    private MissionRecordsMapper missionRecordsMapper;

    @Resource
    private MissionMapper missionMapper;

    @Resource
    private TaskMapper taskMapper;

    @Resource
    private SysTagMapper sysTagMapper;

    @Resource
    private BaseNestManager baseNestManager;

    @Resource
    private MissionPhotoMapper missionPhotoMapper;

    @Resource
    private SysTaskTagMapper sysTaskTagMapper;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

//    @Override
//    public Long create(MultipartFile fileData, String fileName, String accountId) {
//        // 上传原图
//        String newFileName = null;
//        try (InputStream in = fileData.getInputStream()) {
//            newFileName = String.format("%s.%s", BizIdUtils.snowflakeIdStr(), MinIoUnit.getFileType(in));
//        } catch (Exception e) {
//            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_GET_THE_FILE_STREAM.getContent()));
//        }
//
//        String imagePath = String.format("%s%s", UploadTypeEnum.DATA_SCENE_VIDEO.getPath(), newFileName);
//        try (InputStream image = fileData.getInputStream()) {
//            if (!MinIoUnit.putObject(imagePath, image)) {
//                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_UPLOAD_FILE.getContent()));
//            }
//        } catch (Exception e) {
//            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_GET_THE_FILE_STREAM.getContent()));
//        }
//        String thumbnailImagePath = String.format("%s%s", UploadTypeEnum.DATA_SCENE_VIDEO_THUMBNAIL.getPath(), newFileName);
//        try (InputStream thumbnailIn = fileData.getInputStream()) {
//            if (!DrawImageUtils.thumbnailImage(thumbnailIn, thumbnailImagePath, new BigDecimal("0.5"))) {
//                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_GENERATE_IMAGE_THUMBNAIL.getContent()));
//            }
//        } catch (IOException e) {
//            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_GET_THE_FILE_STREAM.getContent()));
//        }
//
//        imagePath = String.format("%s%s", geoaiUosProperties.getStore().getOriginPath(), imagePath);
//        thumbnailImagePath = String.format("%s%s", geoaiUosProperties.getStore().getOriginPath(), thumbnailImagePath);
//        DataScenePhotoEntity dataScenePhotoEntity = new DataScenePhotoEntity();
//        dataScenePhotoEntity.setScenePhotoId(BizIdUtils.snowflakeId());
//        dataScenePhotoEntity.setSrcImagePath(imagePath);
//        dataScenePhotoEntity.setThumbnailImagePath(thumbnailImagePath);
//        dataScenePhotoEntity.setScenePhotoName(fileName);
//        dataScenePhotoEntity.setCreatorId(accountId);
//        dataScenePhotoEntity.setModifierId(accountId);
//        dataScenePhotoMapper.insertEntity(dataScenePhotoEntity);
//        return dataScenePhotoEntity.getScenePhotoId();
//    }

    private Pair<Long, String> getTagInfoPair(Integer taskId) {

        if (taskId == null) {
            return new Pair<>(0L, "");
        }
        LambdaQueryWrapper<SysTaskTagEntity> eq = Wrappers.lambdaQuery(SysTaskTagEntity.class)
                .eq(SysTaskTagEntity::getTaskId, taskId);
        List<SysTaskTagEntity> sysTaskTagEntityList = sysTaskTagMapper.selectList(eq);
        if (CollUtil.isEmpty(sysTaskTagEntityList)) {
            return new Pair<>(0L, "");
        }

        LambdaQueryWrapper<SysTagEntity> sysTagEntityLambdaQueryWrapper = Wrappers.lambdaQuery(SysTagEntity.class)
                .eq(SysTagEntity::getId, sysTaskTagEntityList.get(0).getTagId());
        List<SysTagEntity> sysTagEntityList = sysTagMapper.selectList(sysTagEntityLambdaQueryWrapper);
        if (CollUtil.isEmpty(sysTagEntityList)) {
            return new Pair<>(0L, "");
        }
        SysTagEntity sysTagEntity = sysTagEntityList.get(0);
        return new Pair<>(sysTagEntity.getId(), sysTagEntity.getName());
    }

    private BaseNestOutDO.BaseNestEntityOutDO getNestInfo(String nestId) {

        if (nestId == null) {
            return null;
        }
        List<BaseNestOutDO.BaseNestEntityOutDO> baseNestEntityOutDOList = baseNestManager.selectByNestId(nestId);
        if (CollUtil.isEmpty(baseNestEntityOutDOList)) {
            return null;
        }
        return baseNestEntityOutDOList.get(0);
    }

    private MissionEntity getMissionEntity(Integer missionId) {
        LambdaQueryWrapper<MissionEntity> missionEntityLambdaQueryWrapper = Wrappers.lambdaQuery(MissionEntity.class)
                .eq(MissionEntity::getId, missionId);
        List<MissionEntity> missionEntityList = missionMapper.selectList(missionEntityLambdaQueryWrapper);
        if (CollUtil.isEmpty(missionEntityList)) {
            return null;
        }
        return missionEntityList.get(0);
    }

    private TaskEntity getTaskEntity(Integer taskId) {
        LambdaQueryWrapper<TaskEntity> taskEntityLambdaQueryWrapper = Wrappers.lambdaQuery(TaskEntity.class)
                .eq(TaskEntity::getId, taskId);
        List<TaskEntity> taskEntityList = taskMapper.selectList(taskEntityLambdaQueryWrapper);
        if (CollUtil.isEmpty(taskEntityList)) {
            return null;
        }
        return taskEntityList.get(0);
    }

    private String getImageType(String imagePath) {
        if (imagePath.contains(".")) {
            String[] split = imagePath.split("\\.");
            return split[split.length - 1];
        }
        return "png";
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean update(DataScenePhotoDTO.UpdateInfo updateInfo) {

        // 查询架次和任务信息
        LambdaQueryWrapper<MissionRecordsEntity> missionRecordsEntityLambdaQueryWrapper = Wrappers.lambdaQuery(MissionRecordsEntity.class)
                .eq(MissionRecordsEntity::getExecId, updateInfo.getExecMissionId());
        List<MissionRecordsEntity> missionRecordsEntityList = missionRecordsMapper.selectList(missionRecordsEntityLambdaQueryWrapper);
        if (CollUtil.isEmpty(missionRecordsEntityList)) {
            return false;
        }
        MissionRecordsEntity missionRecordsEntity = missionRecordsEntityList.get(0);
        // 查询架次
        MissionEntity missionEntity = getMissionEntity(missionRecordsEntity.getMissionId());
        if (missionEntity == null) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SORTIE_INFORMATION_DOES_NOT_EXIST.getContent()));
        }

        // 查询任务
        TaskEntity taskEntity = getTaskEntity(missionEntity.getTaskId());
        if (taskEntity == null) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TASK_INFORMATION_DOES_NOT_EXIST.getContent()));
        }
        // 基站信息
        BaseNestOutDO.BaseNestEntityOutDO nestInfo = getNestInfo(taskEntity.getBaseNestId());
        if (nestInfo == null) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BTS_INFORMATION_DOES_NOT_EXIST.getContent()));
        }

        // tag信息
        Pair<Long, String> tagInfoPair = getTagInfoPair(missionEntity.getTaskId());

        DataScenePhotoEntity dataScenePhotoEntity = new DataScenePhotoEntity();
        dataScenePhotoEntity.setScenePhotoId(BizIdUtils.snowflakeId());
        dataScenePhotoEntity.setSrcImagePath(updateInfo.getImagePath());
        dataScenePhotoEntity.setThumbnailImagePath(updateInfo.getImagePath());
        dataScenePhotoEntity.setAddr(updateInfo.getAddr());
        dataScenePhotoEntity.setTaskId(taskEntity.getId().longValue());
        dataScenePhotoEntity.setMissionId(missionRecordsEntity.getMissionId().longValue());
        dataScenePhotoEntity.setMissionRecordsId(missionRecordsEntity.getId().longValue());
        dataScenePhotoEntity.setLongitude(updateInfo.getLongitude());
        dataScenePhotoEntity.setLatitude(updateInfo.getLatitude());
        dataScenePhotoEntity.setTopicLevelId(updateInfo.getTopicLevelId());
        dataScenePhotoEntity.setIndustryType(updateInfo.getIndustryType());
        dataScenePhotoEntity.setTopicProblemId(updateInfo.getTopicProblemId());
        dataScenePhotoEntity.setCreatorId(updateInfo.getAccountId());
        dataScenePhotoEntity.setModifierId(updateInfo.getAccountId());

        String str = taskEntity.getName() == null ? "" : taskEntity.getName();
        String name = String.format("%s_snap_%s.%s", str.substring(0, Math.min(str.length(), 6))
                , LocalDateTime.now().format(DateUtils.DATE_TIME_FORMATTER), getImageType(dataScenePhotoEntity.getSrcImagePath()));
        dataScenePhotoEntity.setScenePhotoName(name);
        int i = dataScenePhotoMapper.insertEntity(dataScenePhotoEntity);

        // 推送到数据统计
        DataAnalysisAggSaveInDTO dataAnalysisAggSaveInDTO = new DataAnalysisAggSaveInDTO();
        dataAnalysisAggSaveInDTO.setTagId(tagInfoPair.getKey());
        dataAnalysisAggSaveInDTO.setTagName(tagInfoPair.getValue());
        dataAnalysisAggSaveInDTO.setTaskId(taskEntity.getId().longValue());
        dataAnalysisAggSaveInDTO.setTaskName(taskEntity.getName());
        dataAnalysisAggSaveInDTO.setMissionId(missionRecordsEntity.getMissionId().longValue());
        dataAnalysisAggSaveInDTO.setMissionRecordId(missionRecordsEntity.getId().longValue());
        dataAnalysisAggSaveInDTO.setNestId(nestInfo.getNestId());
        dataAnalysisAggSaveInDTO.setNestName(nestInfo.getName());
        dataAnalysisAggSaveInDTO.setOrgId(taskEntity.getOrgCode());
        dataAnalysisAggSaveInDTO.setMissionRecordTime(missionRecordsEntity.getCreateTime());
        dataAnalysisAggSaveInDTO.setTaskType(taskEntity.getType());
        dataAnalysisAggSaveInDTO.setMissionSeqId(missionEntity.getSeqId());

        dataAnalysisAggSaveInDTO.setSubType(taskEntity.getSubType() != null && taskEntity.getSubType() == 1);

        DataAnalysisDetailSaveInDTO dataAnalysisDetailSaveInDTO = new DataAnalysisDetailSaveInDTO();
        dataAnalysisAggSaveInDTO.setDataAnalysisDetailSaveInDTOS(Lists.newArrayList(dataAnalysisDetailSaveInDTO));

        dataAnalysisDetailSaveInDTO.setPhotoName(dataScenePhotoEntity.getScenePhotoName());
        dataAnalysisDetailSaveInDTO.setPhotoId(dataScenePhotoEntity.getScenePhotoId());
        dataAnalysisDetailSaveInDTO.setThumImagePath(dataScenePhotoEntity.getThumbnailImagePath());
        dataAnalysisDetailSaveInDTO.setImagePath(dataScenePhotoEntity.getSrcImagePath());
        dataAnalysisDetailSaveInDTO.setLongitude(dataScenePhotoEntity.getLongitude());
        dataAnalysisDetailSaveInDTO.setLatitude(dataScenePhotoEntity.getLatitude());
        dataAnalysisDetailSaveInDTO.setPhotoCreateTime(dataScenePhotoEntity.getCreatedTime());
        dataAnalysisDetailSaveInDTO.setPicType(0);

        dataAnalysisDetailSaveInDTO.setOriginalWidth(0);
        dataAnalysisDetailSaveInDTO.setOriginalHeight(0);
        dataAnalysisDetailSaveInDTO.setSrcDataType(DataAnalysisPicSourceEnum.DATA_SCENE.getType());
        dataAnalysisDetailSaveInDTO.setDataAnalysisMarkSaveInDTOList(Lists.newArrayList(getDataAnalysisMarkSaveInDTO(updateInfo)));

        log.info("#DataScenePhotoServiceImpl.update# dataAnalysisAggSaveInDTO={}", dataAnalysisAggSaveInDTO);
        Long pushCenterAggMain = dataAnalysisBaseService.pushCenterAggAndMarkMain(dataAnalysisAggSaveInDTO);
        log.info("#DataScenePhotoServiceImpl.update# dataAnalysisAggSaveInDTO={}, pushCenterAggMain={}", dataAnalysisAggSaveInDTO, pushCenterAggMain);
        return i > 0;
    }

    private DataAnalysisMarkSaveInDTO getDataAnalysisMarkSaveInDTO(DataScenePhotoDTO.UpdateInfo updateInfo) {
        DataAnalysisMarkSaveInDTO dataAnalysisMarkSaveInDTO = new DataAnalysisMarkSaveInDTO();
        dataAnalysisMarkSaveInDTO.setRecX(updateInfo.getRecX());
        dataAnalysisMarkSaveInDTO.setRecY(updateInfo.getRecY());
        dataAnalysisMarkSaveInDTO.setRecWidth(updateInfo.getRecWidth());
        dataAnalysisMarkSaveInDTO.setRecHeight(updateInfo.getRecHeight());
        dataAnalysisMarkSaveInDTO.setRelX(updateInfo.getRelX());
        dataAnalysisMarkSaveInDTO.setRelY(updateInfo.getRelY());
        dataAnalysisMarkSaveInDTO.setCutWidth(updateInfo.getCutWidth());
        dataAnalysisMarkSaveInDTO.setCutHeight(updateInfo.getCutHeight());
        dataAnalysisMarkSaveInDTO.setPicScale(updateInfo.getPicScale());
        dataAnalysisMarkSaveInDTO.setAiMark(false);
        dataAnalysisMarkSaveInDTO.setAddr(updateInfo.getAddr());
        dataAnalysisMarkSaveInDTO.setTopicLevelId(updateInfo.getTopicLevelId());
        dataAnalysisMarkSaveInDTO.setIndustryType(updateInfo.getIndustryType());
        dataAnalysisMarkSaveInDTO.setTopicProblemId(updateInfo.getTopicProblemId());
        return dataAnalysisMarkSaveInDTO;
    }

    @Override
    public DataScenePhotoOutDTO.PushOut push(List<Integer> photoIds) {

        // 查询mission_photo
        LambdaQueryWrapper<MissionPhotoEntity> queryWrapper = Wrappers.lambdaQuery(MissionPhotoEntity.class)
                .in(MissionPhotoEntity::getId, photoIds);
        List<MissionPhotoEntity> missionPhotoEntityList = missionPhotoMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(missionPhotoEntityList)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_EXIST_IMAGE.getContent()));
        }

        Integer missionRecordsId = missionPhotoEntityList.get(0).getMissionRecordsId();

        // 查询架次和任务信息
        LambdaQueryWrapper<MissionRecordsEntity> missionRecordsEntityLambdaQueryWrapper = Wrappers.lambdaQuery(MissionRecordsEntity.class)
                .eq(MissionRecordsEntity::getId, missionRecordsId);
        List<MissionRecordsEntity> missionRecordsEntityList = missionRecordsMapper.selectList(missionRecordsEntityLambdaQueryWrapper);
        if (CollUtil.isEmpty(missionRecordsEntityList)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_RECORD_OF_SORTIES_IN_THE_PICTURE.getContent()));
        }
        MissionRecordsEntity missionRecordsEntity = missionRecordsEntityList.get(0);

        // 查询架次
        MissionEntity missionEntity = getMissionEntity(missionRecordsEntity.getMissionId());
        if (missionEntity == null) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PICTURE_DOES_NOT_BELONG_TO_ANY_SORTIES.getContent()));
        }

        // 查询任务
        TaskEntity taskEntity = getTaskEntity(missionEntity.getTaskId());
        if (taskEntity == null) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_IMAGE_DOES_NOT_BELONG_TO_THE_TASK.getContent()));
        }

        // 基站信息
        BaseNestOutDO.BaseNestEntityOutDO nestEntity = getNestInfo(taskEntity.getBaseNestId());

        // tag信息
        Pair<Long, String> tagInfoPair = getTagInfoPair(missionEntity.getTaskId());

        DataAnalysisAggSaveInDTO dataAnalysisAggSaveInDTO = new DataAnalysisAggSaveInDTO();
        dataAnalysisAggSaveInDTO.setTagId(tagInfoPair.getKey());
        dataAnalysisAggSaveInDTO.setTagName(tagInfoPair.getValue());
        dataAnalysisAggSaveInDTO.setTaskId(taskEntity.getId().longValue());
        dataAnalysisAggSaveInDTO.setTaskName(taskEntity.getName());
        dataAnalysisAggSaveInDTO.setMissionId(missionRecordsEntity.getMissionId().longValue());
        dataAnalysisAggSaveInDTO.setMissionRecordId(missionRecordsEntity.getId().longValue());
        dataAnalysisAggSaveInDTO.setNestId("0");
        dataAnalysisAggSaveInDTO.setNestName("");
        if (nestEntity != null) {
            dataAnalysisAggSaveInDTO.setNestId(nestEntity.getNestId());
            dataAnalysisAggSaveInDTO.setNestName(nestEntity.getName());
        }
        dataAnalysisAggSaveInDTO.setOrgId(taskEntity.getOrgCode());

        dataAnalysisAggSaveInDTO.setMissionRecordTime(missionRecordsEntity.getCreateTime());
        dataAnalysisAggSaveInDTO.setTaskType(taskEntity.getType());
        dataAnalysisAggSaveInDTO.setMissionSeqId(missionEntity.getSeqId());
        dataAnalysisAggSaveInDTO.setSubType(taskEntity.getSubType() != null && taskEntity.getSubType() == 1);
        List<DataAnalysisDetailSaveInDTO> dataAnalysisDetailSaveInDTOList = Lists.newArrayList();
        dataAnalysisAggSaveInDTO.setDataAnalysisDetailSaveInDTOS(dataAnalysisDetailSaveInDTOList);
        List<String> failedList = Lists.newLinkedList();
        int successNum = 0;
        // 推送到数据统计
        for (MissionPhotoEntity missionPhotoEntity : missionPhotoEntityList) {
            // 获取经纬度
            double longitude, latitude;
            if (missionPhotoEntity.getLongitude() != null && missionPhotoEntity.getLongitude() != 0) {
                longitude = missionPhotoEntity.getLongitude();
            } else if (nestEntity != null) {
                longitude = nestEntity.getLongitude();
            } else {
                failedList.add(missionPhotoEntity.getName());
                continue;
            }

            if (missionPhotoEntity.getLatitude() != null && missionPhotoEntity.getLatitude() != 0) {
                latitude = missionPhotoEntity.getLatitude();
            } else if (nestEntity != null) {
                latitude = nestEntity.getLatitude();
            } else {
                failedList.add(missionPhotoEntity.getName());
                continue;
            }

            DataAnalysisDetailSaveInDTO dataAnalysisDetailSaveInDTO = new DataAnalysisDetailSaveInDTO();
            dataAnalysisDetailSaveInDTO.setPhotoId(missionPhotoEntity.getId());
            dataAnalysisDetailSaveInDTO.setThumImagePath(missionPhotoEntity.getThumbnailUrl());
            dataAnalysisDetailSaveInDTO.setImagePath(missionPhotoEntity.getPhotoUrl());

            dataAnalysisDetailSaveInDTO.setLongitude(BigDecimal.valueOf(longitude));
            dataAnalysisDetailSaveInDTO.setLatitude(BigDecimal.valueOf(latitude));
            dataAnalysisDetailSaveInDTO.setPhotoName(missionPhotoEntity.getName());
            if (missionPhotoEntity.getTimeCreated() != null) {
                dataAnalysisDetailSaveInDTO.setPhotoCreateTime(missionPhotoEntity.getTimeCreated());
            } else {
                dataAnalysisDetailSaveInDTO.setPhotoCreateTime(missionPhotoEntity.getCreateTime());
            }

            // 红外判定-以lenType为主，但需兼容历史代码原因
            int photoType = missionPhotoEntity.getLenType() == 3 ? 1 : 0;
            dataAnalysisDetailSaveInDTO.setPicType(photoType);

            dataAnalysisDetailSaveInDTO.setOriginalWidth(0);
            dataAnalysisDetailSaveInDTO.setOriginalHeight(0);
            dataAnalysisDetailSaveInDTO.setSrcDataType(DataAnalysisPicSourceEnum.PHOTO.getType());
            dataAnalysisDetailSaveInDTOList.add(dataAnalysisDetailSaveInDTO);
            successNum ++;
        }

        log.info("#DataScenePhotoServiceImpl.update# dataAnalysisAggSaveInDTO={}", dataAnalysisAggSaveInDTO);
        if (successNum > 0) {
            Long pushCenterAggMain = dataAnalysisBaseService.pushCenterAggMain(dataAnalysisAggSaveInDTO);
            log.info("#DataScenePhotoServiceImpl.update# dataAnalysisAggSaveInDTO={}, pushCenterAggMain={}", dataAnalysisAggSaveInDTO, pushCenterAggMain);
        }
        DataScenePhotoOutDTO.PushOut pushOut = new DataScenePhotoOutDTO.PushOut();
        pushOut.setFailedList(failedList);
        pushOut.setSuccessNum(successNum);
        return pushOut;
    }

    @Override
    public String getOrgIdByExecMissionId(String execMissionId) {
        LambdaQueryWrapper<MissionRecordsEntity> missionRecordsEntityLambdaQueryWrapper = Wrappers.lambdaQuery(MissionRecordsEntity.class)
                .eq(MissionRecordsEntity::getExecId, execMissionId);
        List<MissionRecordsEntity> missionRecordsEntityList = missionRecordsMapper.selectList(missionRecordsEntityLambdaQueryWrapper);
        if (CollUtil.isEmpty(missionRecordsEntityList)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SORTIE_EXECUTION_ID_DOES_NOT_EXIST.getContent()));
        }
        MissionRecordsEntity missionRecordsEntity = missionRecordsEntityList.get(0);
        if (missionRecordsEntity.getStatus() != null && missionRecordsEntity.getStatus() == 2) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXECUTION_OF_THE_SORTIE_ID_HAS_BEEN_COMPLETED.getContent()));
        }
        // 架次
        LambdaQueryWrapper<MissionEntity> missionEntityLambdaQueryWrapper = Wrappers.lambdaQuery(MissionEntity.class)
                .eq(MissionEntity::getId, missionRecordsEntity.getMissionId());
        List<MissionEntity> missionEntityList = missionMapper.selectList(missionEntityLambdaQueryWrapper);
        if (CollUtil.isEmpty(missionEntityList)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SORTIE_CORRESPONDING_TO_THE_SORTIE_EXECUTION_ID_DOES_NOT_EXIST.getContent()));
        }
        MissionEntity missionEntity = missionEntityList.get(0);
        // 任务
        LambdaQueryWrapper<TaskEntity> taskEntityLambdaQueryWrapper = Wrappers.lambdaQuery(TaskEntity.class)
                .eq(TaskEntity::getId, missionEntity.getTaskId());
        List<TaskEntity> taskEntityList = taskMapper.selectList(taskEntityLambdaQueryWrapper);
        if (CollUtil.isEmpty(taskEntityList)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TASK_CORRESPONDING_TO_THE_SORTIE_EXECUTION_ID_DOES_NOT_EXIST.getContent()));
        }
        return taskEntityList.get(0).getOrgCode();
    }
}
