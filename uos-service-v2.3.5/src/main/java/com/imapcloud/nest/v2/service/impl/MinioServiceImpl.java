package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.json.JSONUtil;
import com.geoai.common.core.util.DateUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.enums.MinioEventTypeEnum;
import com.imapcloud.nest.enums.MinioObjectTypeEnum;
import com.imapcloud.nest.enums.UploadTypeEnum;
import com.imapcloud.nest.utils.MinIoUnit;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.manager.dataobj.MinioDO;
import com.imapcloud.nest.v2.manager.dataobj.in.*;
import com.imapcloud.nest.v2.manager.dataobj.out.*;
import com.imapcloud.nest.v2.manager.rest.DingTalkManager;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.manager.sql.*;
import com.imapcloud.nest.v2.service.MinioService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * minio服务
 *
 * @author boluo
 * @date 2022-10-26
 */
@Slf4j
@Service
public class MinioServiceImpl implements MinioService {

    public static final String TASK_LOCK_KEY = "minio:taskLock";

    @Resource
    private MissionPhotoManager missionPhotoManager;

    @Resource
    private FileEventInfoManager fileEventInfoManager;

    @Resource
    private FileDetailInfoManager fileDetailInfoManager;

    @Resource
    private FileReportInfoManager fileReportInfoManager;

    @Resource
    private MissionVideoManager missionVideoManager;

    @Resource
    private MissionManager missionManager;

    @Resource
    private MissionVideoPhotoManager missionVideoPhotoManager;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private DingTalkManager dingTalkManager;

    @Resource
    private FileManager fileManager;

    @Value("${spring.profiles.active}")
    private String active;

    @Override
    public int missionPhoto(int tagVersion) {

        List<MissionPhotoOutDO> missionPhotoOutDOList = missionPhotoManager.queryListByTagVersion(tagVersion);
        if (CollUtil.isEmpty(missionPhotoOutDOList)) {
            return 0;
        }

        Set<Integer> missionIdSet = missionPhotoOutDOList.stream().map(MissionPhotoOutDO::getMissionId).collect(Collectors.toSet());
        List<MissionOutDO.TaskMissionOutDO> taskMissionOutDOList = missionManager.selectTaskMissionListByMissionIdList(missionIdSet);
        Map<Integer, MissionOutDO.TaskMissionOutDO> integerTaskMissionOutDOMap = taskMissionOutDOList.stream()
                .collect(Collectors.toMap(MissionOutDO.TaskMissionOutDO::getMissionId, bean -> bean, (key1, key2) -> key1));

        List<Long> successIdList = Lists.newLinkedList();
        List<Long> failedIdList = Lists.newLinkedList();
        for (MissionPhotoOutDO missionPhotoOutDO : missionPhotoOutDOList) {

            // 原图
            try {
                MinioDO.TagDO tagDO = new MinioDO.TagDO();

                MissionOutDO.TaskMissionOutDO taskMissionOutDO = integerTaskMissionOutDOMap.get(missionPhotoOutDO.getMissionId());
                if (taskMissionOutDO == null) {
                    tagDO.setOrgCode(MinioDO.getOrgCode(null));
                    tagDO.setNestId(MinioDO.getNestId(null));
                } else {
                    tagDO.setOrgCode(MinioDO.getOrgCode(taskMissionOutDO.getOrgCode()));
                    tagDO.setNestId(MinioDO.getNestId(taskMissionOutDO.getNestId()));
                }

                tagDO.setObjectTime(MinioDO.getObjectTime(missionPhotoOutDO.getTimeCreated() != null ? missionPhotoOutDO.getTimeCreated() : missionPhotoOutDO.getCreateTime()));
                tagDO.setObject(missionPhotoOutDO.getPhotoUrl());
                tagDO.setObjectType(MinioObjectTypeEnum.PHOTO.getCode());
                tagDO.setTagVersion(String.valueOf(tagVersion));
                fileManager.setFileTags(missionPhotoOutDO.getPhotoUrl(), JSONUtil.toBean(JSONUtil.parseObj(tagDO), Map.class));
//                MinIoUnit.setObjectTags(missionPhotoOutDO.getPhotoUrl(), JSONUtil.toBean(JSONUtil.parseObj(tagDO), Map.class));

                // 缩略图
                tagDO.setObject(missionPhotoOutDO.getThumbnailUrl());
                fileManager.setFileTags(missionPhotoOutDO.getThumbnailUrl(), JSONUtil.toBean(JSONUtil.parseObj(tagDO), Map.class));
//                MinIoUnit.setObjectTags(missionPhotoOutDO.getPhotoUrl(), JSONUtil.toBean(JSONUtil.parseObj(tagDO), Map.class));
                successIdList.add(missionPhotoOutDO.getId());
            } catch (Exception e) {
                String message = e.getMessage();
                if (message.contains("The specified key does not exist")) {
                    // object不存在
                    failedIdList.add(missionPhotoOutDO.getId());
                } else {
                    log.error("#MinioServiceImpl.missionPhoto# error:", e);
                }
            }
        }
        // 处理异常的数据，tagVersion改成-100
        missionPhotoManager.updateTagVersionByIdList(MinioDO.DEFAULT_INVALID_OBJECT, failedIdList);
        // 更新tag版本信息
        missionPhotoManager.updateTagVersionByIdList(tagVersion, successIdList);
        return 1;
    }

    @Override
    public int analysisFileEvent() {

        List<FileEventInfoOutDO> fileEventInfoOutDOList = fileEventInfoManager.selectNotAnalysisList();
        if (CollUtil.isEmpty(fileEventInfoOutDOList)) {
            return 0;
        }

        List<FileEventInfoInDO> inDOList = Lists.newLinkedList();
        for (FileEventInfoOutDO fileEventInfoOutDO : fileEventInfoOutDOList) {

            FileEventInfoOutDO.EventDataInfoDO eventDataInfoDO = fileEventInfoOutDO.getEventDataInfoDO();

            FileEventInfoInDO fileEventInfoInDO = new FileEventInfoInDO();
            fileEventInfoInDO.setId(fileEventInfoOutDO.getId());
            fileEventInfoInDO.setEventStatus(1);
            fileEventInfoInDO.setSynStatus(0);
            fileEventInfoInDO.setEventType(eventDataInfoDO.getEventType());
            fileEventInfoInDO.setBucket(eventDataInfoDO.getBucket());
            fileEventInfoInDO.setObject(eventDataInfoDO.getObject());
            inDOList.add(fileEventInfoInDO);
        }
        fileEventInfoManager.batchUpdate(inDOList);
        return 1;
    }

    private MinioDO.TagDO getTagDO(String bucket, String object) {

        try {
            Map<String, String> objectTags = MinIoUnit.getObjectTags(bucket, object);
            return JSONUtil.toBean(JSONUtil.parseObj(objectTags), MinioDO.TagDO.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private int analysisTag(MinioEventTypeEnum eventType) {
        List<FileEventInfoOutDO> fileEventInfoOutDOList = fileEventInfoManager.selectNotSynList(eventType);
        if (CollUtil.isEmpty(fileEventInfoOutDOList)) {
            return 0;
        }
        List<FileDetailInfoInDO> fileDetailInfoInDOList = Lists.newLinkedList();
        // 根据bucket object event_type 去重
        List<String> boeList = Lists.newLinkedList();
        for (FileEventInfoOutDO fileEventInfoOutDO : fileEventInfoOutDOList) {

            try {

                // 查询object的tag
                MinioDO.TagDO tagDO = getTagDO(fileEventInfoOutDO.getBucket(), fileEventInfoOutDO.getObject());
                String key = String.format("%s-%s-%s", fileEventInfoOutDO.getBucket(), fileEventInfoOutDO.getObject(), tagDO.getTagVersion());
                if (boeList.contains(key)) {
                    continue;
                }
                boeList.add(key);

                FileDetailInfoInDO infoInDO = new FileDetailInfoInDO();
                infoInDO.setBucket(fileEventInfoOutDO.getBucket());
                infoInDO.setObject(fileEventInfoOutDO.getObject());
                infoInDO.setEventType(fileEventInfoOutDO.getEventType());
                infoInDO.setObjectType(tagDO.getObjectType());
                infoInDO.setApp(tagDO.getApp());
                infoInDO.setOrgCode(tagDO.getOrgCode());
                infoInDO.setNestId(tagDO.getNestId());

                infoInDO.setObjectTime(LocalDateTime.parse(tagDO.getObjectTime(), DateUtils.DATE_TIME_FORMATTER_OF_CN));
                infoInDO.setTagVersion(Integer.parseInt(tagDO.getTagVersion()));
                infoInDO.setSynStatus(0);

                infoInDO.setObjectSize(MinIoUnit.getObjectSize(fileEventInfoOutDO.getBucket(), fileEventInfoOutDO.getObject()));
                fileDetailInfoInDOList.add(infoInDO);
            } catch (Exception e) {
                log.error("#MinioServiceImpl.analysisTag# error:", e);
            }
        }
        fileDetailInfoManager.batchInsert(fileDetailInfoInDOList);
        // 更新fileEventInfo同步状态
        fileEventInfoManager.updateSysStatusByIdList(fileEventInfoOutDOList.stream().map(FileEventInfoOutDO::getId).collect(Collectors.toList()));
        return 1;
    }

    private int analysisDelete(MinioEventTypeEnum eventType) {
        List<FileEventInfoOutDO> fileEventInfoOutDOList = fileEventInfoManager.selectNotSynList(eventType);
        if (CollUtil.isEmpty(fileEventInfoOutDOList)) {
            return 0;
        }
        List<FileDetailInfoInDO> fileDetailInfoInDOList = Lists.newLinkedList();

        for (FileEventInfoOutDO fileEventInfoOutDO : fileEventInfoOutDOList) {

            try {

                FileDetailInfoInDO infoInDO = new FileDetailInfoInDO();
                infoInDO.setBucket(fileEventInfoOutDO.getBucket());
                infoInDO.setObject(fileEventInfoOutDO.getObject());
                infoInDO.setEventType(fileEventInfoOutDO.getEventType());
                infoInDO.setSynStatus(0);
                fileDetailInfoInDOList.add(infoInDO);
            } catch (Exception e) {
                log.error("#MinioServiceImpl.analysisTag# error:", e);
            }
        }
        fileDetailInfoManager.batchUpdate(fileDetailInfoInDOList);
        // 更新fileEventInfo同步状态
        fileEventInfoManager.updateSysStatusByIdList(fileEventInfoOutDOList.stream().map(FileEventInfoOutDO::getId).collect(Collectors.toList()));
        return 1;
    }

    @Override
    public int analysisEvent(MinioEventTypeEnum eventType) {

        if (eventType == MinioEventTypeEnum.PUT_TAGGING) {
            return analysisTag(eventType);
        } else if (eventType == MinioEventTypeEnum.DELETE) {
            return analysisDelete(eventType);
        }
        return 0;
    }

    @Override
    public int report(int tagVersion) {

        List<FileDetailInfoOutDO.NotSynInfoOutDO> notSynInfoOutDOList = fileDetailInfoManager.selectNotSynListByTagVersion(tagVersion);
        if (CollUtil.isEmpty(notSynInfoOutDOList)) {
            return 0;
        }

        List<FileReportInfoInDO> fileReportInfoInDOList = Lists.newLinkedList();
        for (FileDetailInfoOutDO.NotSynInfoOutDO notSynInfoOutDO : notSynInfoOutDOList) {

            FileDetailInfoInDO.DayReportInDO dayReportInDO = new FileDetailInfoInDO.DayReportInDO();
            dayReportInDO.setOrgCode(notSynInfoOutDO.getOrgCode());
            dayReportInDO.setNestId(notSynInfoOutDO.getNestId());
            dayReportInDO.setTagVersion(tagVersion);
            dayReportInDO.setReportDay(notSynInfoOutDO.getReportDay());
            List<FileDetailInfoOutDO.DayReportOutDO> dayReportOutDOList = fileDetailInfoManager.dayReport(dayReportInDO);

            FileReportInfoInDO fileReportInfoInDO = new FileReportInfoInDO();
            fileReportInfoInDO.setApp(notSynInfoOutDO.getApp());
            fileReportInfoInDO.setOrgCode(notSynInfoOutDO.getOrgCode());
            fileReportInfoInDO.setNestId(notSynInfoOutDO.getNestId());
            long picture = 0, video = 0, videoPicture = 0;
            for (FileDetailInfoOutDO.DayReportOutDO reportOutDO : dayReportOutDOList) {
                if (MinioObjectTypeEnum.PHOTO.getCode().equals(reportOutDO.getObjectType())) {
                    picture = reportOutDO.getObjectSize();
                } else if (MinioObjectTypeEnum.VIDEO.getCode().equals(reportOutDO.getObjectType())) {
                    video = reportOutDO.getObjectSize();
                } else if (MinioObjectTypeEnum.VIDEO_PICTURE.getCode().equals(reportOutDO.getObjectType())) {
                    videoPicture = reportOutDO.getObjectSize();
                }
            }
            fileReportInfoInDO.setPictureSize(picture);
            fileReportInfoInDO.setVideoSize(video);
            fileReportInfoInDO.setVideoPictureSize(videoPicture);
            fileReportInfoInDO.setTotalSize(picture + video + videoPicture);
            fileReportInfoInDO.setReportDay(notSynInfoOutDO.getReportDay());
            fileReportInfoInDO.setTagVersion(tagVersion);
            fileReportInfoInDOList.add(fileReportInfoInDO);
        }
        fileReportInfoManager.batchInsert(fileReportInfoInDOList);
        for (FileDetailInfoOutDO.NotSynInfoOutDO notSynInfoOutDO : notSynInfoOutDOList) {
            FileDetailInfoInDO.SynInDO synInDO = new FileDetailInfoInDO.SynInDO();
            synInDO.setOrgCode(notSynInfoOutDO.getOrgCode());
            synInDO.setNestId(notSynInfoOutDO.getNestId());
            synInDO.setTagVersion(tagVersion);
            synInDO.setReportDay(notSynInfoOutDO.getReportDay());
            fileDetailInfoManager.updateSyn(synInDO);
        }
        return 1;
    }

    @Override
    public int missionVideo(int tagVersion) {

        List<MissionVideoOutDO> missionVideoOutDOList = missionVideoManager.queryListByTagVersion(tagVersion);
        if (CollUtil.isEmpty(missionVideoOutDOList)) {
            return 0;
        }
        // 查询视频所属单位和基站
        Set<Integer> missionIdSet = missionVideoOutDOList.stream().map(MissionVideoOutDO::getMissionId).collect(Collectors.toSet());
        List<MissionOutDO.TaskMissionOutDO> taskMissionOutDOList = missionManager.selectTaskMissionListByMissionIdList(missionIdSet);
        Map<Integer, MissionOutDO.TaskMissionOutDO> integerTaskMissionOutDOMap = taskMissionOutDOList.stream()
                .collect(Collectors.toMap(MissionOutDO.TaskMissionOutDO::getMissionId, bean -> bean, (key1, key2) -> key1));

        List<Long> successIdList = Lists.newLinkedList();
        List<Long> failedIdList = Lists.newLinkedList();

        List<MissionVideoOutDO> updateList = Lists.newLinkedList();
        for (MissionVideoOutDO missionVideoOutDO : missionVideoOutDOList) {

            // 原图
            try {
                // 判断是否是录屏文件
                if (checkVideo(missionVideoOutDO.getVideoUrl())
                        || checkVideo(missionVideoOutDO.getSrtUrl())
                        || checkVideo(missionVideoOutDO.getVideoThumbnail())) {
                    updateList.add(missionVideoOutDO);
                    continue;
                }
                MinioDO.TagDO tagDO = new MinioDO.TagDO();
                MissionOutDO.TaskMissionOutDO taskMissionOutDO = integerTaskMissionOutDOMap.get(missionVideoOutDO.getMissionId());
                if (taskMissionOutDO == null) {
                    tagDO.setOrgCode(MinioDO.getOrgCode(null));
                    tagDO.setNestId(MinioDO.getNestId(null));
                } else {
                    tagDO.setOrgCode(MinioDO.getOrgCode(taskMissionOutDO.getOrgCode()));
                    tagDO.setNestId(MinioDO.getNestId(taskMissionOutDO.getNestId()));
                }

                tagDO.setObjectTime(MinioDO.getObjectTime(missionVideoOutDO.getTimeCreated() != null ? missionVideoOutDO.getTimeCreated() : missionVideoOutDO.getCreateTime()));
                tagDO.setObjectType(MinioObjectTypeEnum.VIDEO.getCode());
                tagDO.setTagVersion(String.valueOf(tagVersion));

                // 视频
                setTag(missionVideoOutDO.getVideoUrl(), tagDO);

                // 视频轨迹
                setTag(missionVideoOutDO.getSrtUrl(), tagDO);

                // 视频缩略图
                setTag(missionVideoOutDO.getVideoThumbnail(), tagDO);

                successIdList.add(missionVideoOutDO.getId());
            } catch (Exception e) {
                String message = e.getMessage();
                if (message.contains("The specified key does not exist")) {
                    // object不存在
                    failedIdList.add(missionVideoOutDO.getId());
                } else {
                    log.error("#MinioServiceImpl.missionVideo# error:", e);
                }
            }
        }

        // 处理异常的数据，tagVersion改成-100
        missionVideoManager.updateTagVersionByIdList(MinioDO.DEFAULT_INVALID_OBJECT, failedIdList);
        // 更新tag版本信息
        missionVideoManager.updateTagVersionByIdList(tagVersion, successIdList);
        // 将录屏视频迁移到minio
        List<MissionVideoInDO> missionVideoInDOList = Lists.newLinkedList();
        for (MissionVideoOutDO missionVideoOutDO : updateList) {
            MissionVideoInDO missionVideoInDO = new MissionVideoInDO();
            missionVideoInDO.setId(missionVideoOutDO.getId());
            missionVideoInDO.setVideoUrl(getMinioUrl(missionVideoOutDO.getVideoUrl()));
            missionVideoInDO.setSrtUrl(getMinioUrl(missionVideoOutDO.getSrtUrl()));
            missionVideoInDO.setVideoThumbnail(getMinioUrl(missionVideoOutDO.getVideoThumbnail()));
            missionVideoInDOList.add(missionVideoInDO);
        }
        missionVideoManager.updateVideoUrl(missionVideoInDOList);
        return 1;
    }

    private String getMinioUrl(String url) {
        if (!checkVideo(url)) {
           return url;
        }
        String newUrl = url.replace(geoaiUosProperties.getStore().getVideoPath(), "");
        if (newUrl.startsWith("/")) {
            newUrl = newUrl.substring(1);
        }
        String minioVideoUrl = String.format("%s%s", UploadTypeEnum.MISSION_VIDEO_PATH.getPath(), newUrl);
        try {
            File file = Paths.get(url).toFile();
            if (file.exists() && file.isFile()) {
                boolean flag = MinIoUnit.putObject(minioVideoUrl, Files.newInputStream(file.toPath()), FileTypeUtil.getType(file));
                if(flag){
                    boolean delete = file.delete();
                    if (!delete) {
                        log.error("#getMinioUrl# delete error url = {}", url);
                    }
                }
            }
        } catch (Exception e) {
            log.error("#getMinioUrl# url = {}", url);
        }
        return String.format("%s%s", geoaiUosProperties.getStore().getOriginPath(), minioVideoUrl);
    }

    private boolean checkVideo(String url) {
        String videoPath = geoaiUosProperties.getStore().getVideoPath();
        return url != null && url.startsWith(videoPath);
    }

    private void setTag(String url, MinioDO.TagDO tagDO) {
        if (StringUtils.isNotBlank(url)) {
            tagDO.setObject(url);
//            MinIoUnit.setObjectTags(url, JSONUtil.toBean(JSONUtil.parseObj(tagDO), Map.class));
            fileManager.setFileTags(url, JSONUtil.toBean(JSONUtil.parseObj(tagDO), Map.class));
        }
    }

    @Override
    public int missionVideoPhoto(int tagVersion) {

        List<MissionVideoPhotoOutDO> missionVideoPhotoOutDOList = missionVideoPhotoManager.queryListByTagVersion(tagVersion);
        if (CollUtil.isEmpty(missionVideoPhotoOutDOList)) {
            return 0;
        }
        // 查询视频所属单位和基站
        Set<Integer> missionIdSet = missionVideoPhotoOutDOList.stream().map(MissionVideoPhotoOutDO::getMissionId).collect(Collectors.toSet());
        List<MissionOutDO.TaskMissionOutDO> taskMissionOutDOList = missionManager.selectTaskMissionListByMissionIdList(missionIdSet);
        Map<Integer, MissionOutDO.TaskMissionOutDO> integerTaskMissionOutDOMap = taskMissionOutDOList.stream()
                .collect(Collectors.toMap(MissionOutDO.TaskMissionOutDO::getMissionId, bean -> bean, (key1, key2) -> key1));

        List<Long> successIdList = Lists.newLinkedList();
        List<Long> failedIdList = Lists.newLinkedList();
        for (MissionVideoPhotoOutDO missionVideoPhotoOutDO : missionVideoPhotoOutDOList) {

            // 原图
            try {
                MinioDO.TagDO tagDO = new MinioDO.TagDO();
                MissionOutDO.TaskMissionOutDO taskMissionOutDO = integerTaskMissionOutDOMap.get(missionVideoPhotoOutDO.getMissionId());
                if (taskMissionOutDO == null) {
                    tagDO.setOrgCode(MinioDO.getOrgCode(null));
                    tagDO.setNestId(MinioDO.getNestId(null));
                } else {
                    tagDO.setOrgCode(MinioDO.getOrgCode(taskMissionOutDO.getOrgCode()));
                    tagDO.setNestId(MinioDO.getNestId(taskMissionOutDO.getNestId()));
                }

                tagDO.setObjectTime(MinioDO.getObjectTime(missionVideoPhotoOutDO.getExtractTime() != null ? missionVideoPhotoOutDO.getExtractTime() : missionVideoPhotoOutDO.getCreateTime()));
                tagDO.setObjectType(MinioObjectTypeEnum.VIDEO_PICTURE.getCode());
                tagDO.setTagVersion(String.valueOf(tagVersion));

                // 原图
                if (StringUtils.isNotBlank(missionVideoPhotoOutDO.getPhotoUrl())) {
                    tagDO.setObject(missionVideoPhotoOutDO.getPhotoUrl());
//                    MinIoUnit.setObjectTags(missionVideoPhotoOutDO.getPhotoUrl(), JSONUtil.toBean(JSONUtil.parseObj(tagDO), Map.class));
                    fileManager.setFileTags(missionVideoPhotoOutDO.getPhotoUrl(), JSONUtil.toBean(JSONUtil.parseObj(tagDO), Map.class));
                }

                // 缩略图
                if (StringUtils.isNotBlank(missionVideoPhotoOutDO.getThumbnailUrl())) {
                    tagDO.setObject(missionVideoPhotoOutDO.getThumbnailUrl());
//                    MinIoUnit.setObjectTags(missionVideoPhotoOutDO.getThumbnailUrl(), JSONUtil.toBean(JSONUtil.parseObj(tagDO), Map.class));
                    fileManager.setFileTags(missionVideoPhotoOutDO.getThumbnailUrl(), JSONUtil.toBean(JSONUtil.parseObj(tagDO), Map.class));
                }

                successIdList.add(missionVideoPhotoOutDO.getId());
            } catch (Exception e) {
                String message = e.getMessage();
                if (message.contains("The specified key does not exist")) {
                    // object不存在
                    failedIdList.add(missionVideoPhotoOutDO.getId());
                } else {
                    log.error("#MinioServiceImpl.missionVideo# error:", e);
                }
            }
        }
        // 处理异常的数据，tagVersion改成-100
        missionVideoPhotoManager.updateTagVersionByIdList(MinioDO.DEFAULT_INVALID_OBJECT, failedIdList);
        // 更新tag版本信息
        missionVideoPhotoManager.updateTagVersionByIdList(tagVersion, successIdList);
        return 1;
    }

    private void delete(List<String> fileList) {

        for (String url : fileList) {
            if (StringUtils.isBlank(url)) {
                continue;
            }
            File file = Paths.get(url).toFile();
            if (file.exists()) {
                boolean delete = file.delete();
                if (!delete) {
                    log.error("#delete# error, url={}", url);
                }
            }
        }
    }

    @Override
    public int physicsDeleteMissionPhoto(int tagVersion) {

        List<MissionPhotoOutDO> missionPhotoOutDOList = missionPhotoManager.queryDeleteList(tagVersion);
        if (CollUtil.isEmpty(missionPhotoOutDOList)) {
            return 0;
        }

        List<String> objectList = Lists.newLinkedList();
        List<String> fileList = Lists.newLinkedList();
        for (MissionPhotoOutDO missionPhotoOutDO : missionPhotoOutDOList) {

            if (MinIoUnit.objectExists(missionPhotoOutDO.getPhotoUrl())) {
                objectList.add(missionPhotoOutDO.getPhotoUrl());
            }
            if (MinIoUnit.objectExists(missionPhotoOutDO.getThumbnailUrl())) {
                objectList.add(missionPhotoOutDO.getThumbnailUrl());
            }
            fileList.add(missionPhotoOutDO.getPhotoUrl());
            fileList.add(missionPhotoOutDO.getThumbnailUrl());
        }
//        MinIoUnit.rmObjects(objectList);
        fileManager.deleteFiles(objectList);
        delete(fileList);
        missionPhotoManager.updatePhysicsDeleteByIdList(missionPhotoOutDOList.stream().map(MissionPhotoOutDO::getId).collect(Collectors.toSet()));
        return 1;
    }

    @Override
    public int physicsDeleteMissionVideo(int tagVersion) {

        List<MissionVideoOutDO> missionVideoOutDOList = missionVideoManager.queryDeleteList(tagVersion);
        if (CollUtil.isEmpty(missionVideoOutDOList)) {
            return 0;
        }

        List<String> objectList = Lists.newLinkedList();
        List<String> fileList = Lists.newLinkedList();
        for (MissionVideoOutDO missionVideoOutDO : missionVideoOutDOList) {

            if (MinIoUnit.objectExists(missionVideoOutDO.getVideoUrl())) {
                objectList.add(missionVideoOutDO.getVideoUrl());
            }
            if (MinIoUnit.objectExists(missionVideoOutDO.getSrtUrl())) {
                objectList.add(missionVideoOutDO.getSrtUrl());
            }
            if (MinIoUnit.objectExists(missionVideoOutDO.getVideoThumbnail())) {
                objectList.add(missionVideoOutDO.getVideoThumbnail());
            }
            fileList.add(missionVideoOutDO.getVideoUrl());
            fileList.add(missionVideoOutDO.getSrtUrl());
            fileList.add(missionVideoOutDO.getVideoThumbnail());
        }
//        MinIoUnit.rmObjects(objectList);
        fileManager.deleteFiles(objectList);
        delete(fileList);
        missionVideoManager.updatePhysicsDeleteByIdList(missionVideoOutDOList.stream().map(MissionVideoOutDO::getId).collect(Collectors.toSet()));
        return 1;
    }

    @Override
    public int physicsDeleteMissionVideoPhoto(int tagVersion) {
        List<MissionVideoPhotoOutDO> missionVideoPhotoOutDOList = missionVideoPhotoManager.queryDeleteList(tagVersion);
        if (CollUtil.isEmpty(missionVideoPhotoOutDOList)) {
            return 0;
        }

        List<String> objectList = Lists.newLinkedList();
        List<String> fileList = Lists.newLinkedList();
        for (MissionVideoPhotoOutDO missionVideoPhotoOutDO : missionVideoPhotoOutDOList) {

            if (MinIoUnit.objectExists(missionVideoPhotoOutDO.getPhotoUrl())) {
                objectList.add(missionVideoPhotoOutDO.getPhotoUrl());
            }
            if (MinIoUnit.objectExists(missionVideoPhotoOutDO.getThumbnailUrl())) {
                objectList.add(missionVideoPhotoOutDO.getThumbnailUrl());
            }
            fileList.add(missionVideoPhotoOutDO.getPhotoUrl());
            fileList.add(missionVideoPhotoOutDO.getThumbnailUrl());
        }
//        MinIoUnit.rmObjects(objectList);
        fileManager.deleteFiles(objectList);
        delete(fileList);
        missionVideoPhotoManager.updatePhysicsDeleteByIdList(missionVideoPhotoOutDOList.stream().map(MissionVideoPhotoOutDO::getId).collect(Collectors.toSet()));
        return 1;
    }

    @Override
    public void send(int tagVersion) {

        FileReportInfoOutDO.FileOutDO totalReport = fileReportInfoManager.totalReport(tagVersion, null);

        String today = LocalDateTime.now().minusDays(1).format(DateUtils.DATE_FORMATTER_OF_CN);

        FileReportInfoOutDO.FileOutDO dayReport = fileReportInfoManager.totalReport(tagVersion, today);

        String markDown = String.format("## UOS存储空间 \n " +
                        "### 环境：%s \n " +
                        "### 时间：%s \n " +
                        "--------------- \n " +
                        "| **数据类型&emsp;**  |  **&emsp;&emsp;%s**|  **&emsp;&emsp;&emsp;&emsp;全部** | \n " +
                        "|  :--------  |  --------:  | --------:  | \n " +
                        "| 照片 |  %s  |  %s  |  \n  " +
                        "| 视频 |  %s  |  %s  |  \n  " +
                        "| 视频抽帧 |  %s  |  %s  |  \n  " +
                        "| 总计 |  %s  |  %s  |  \n"
                , active
                , LocalDateTime.now().format(DateUtils.DATE_TIME_FORMATTER_OF_CN)
                , today
                , toMemorySize(dayReport.getPictureSize())
                , toMemorySize(totalReport.getPictureSize())
                , toMemorySize(dayReport.getVideoSize())
                , toMemorySize(totalReport.getVideoSize())
                , toMemorySize(dayReport.getVideoPictureSize())
                , toMemorySize(totalReport.getVideoPictureSize())
                , toMemorySize(dayReport.getTotalSize())
                , toMemorySize(totalReport.getTotalSize())
        );

        DingTalkInDO.SendInDO sendInDO = new DingTalkInDO.SendInDO();
        sendInDO.setMsgType(DingTalkInDO.MARKDOWN);
        sendInDO.setTitle("UOS存储空间使用情况");
        sendInDO.setText(markDown);
        sendInDO.setAt(false);

        dingTalkManager.send(sendInDO);
    }

    private String toMemorySize(long size) {
        BigDecimal bigDecimal = new BigDecimal(size);
        if (size < 102400) {
            BigDecimal divide = bigDecimal.divide(new BigDecimal(1024), 2, RoundingMode.HALF_UP);
            return String.format("%sK", divide.toString());
        }
        if (size < 102400 * 1024) {
            BigDecimal divide = bigDecimal.divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP);
            return String.format("%sM", divide.toString());
        }

        BigDecimal divide = bigDecimal.divide(new BigDecimal(1024 * 1024 * 1024), 2, RoundingMode.HALF_UP);
        return String.format("%sG", divide.toString());
    }

    @Override
    public void deleteFileEvent() {

        fileEventInfoManager.delete();
    }
}
