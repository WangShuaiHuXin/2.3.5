package com.imapcloud.nest.v2.manager.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.imapcloud.nest.common.constant.MissionConstant;
import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.enums.DataTypeEnum;
import com.imapcloud.nest.model.*;
import com.imapcloud.nest.pojo.dto.reqDto.IdenDataDto;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.DJIActionTypeEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.exception.ValidateException;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.manager.dataobj.in.DjiFileUploadCallBackDO;
import com.imapcloud.nest.v2.manager.event.DJIFileUploadCallBackEvent;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.service.DJITaskFileService;
import com.imapcloud.nest.v2.service.GridMissionService;
import com.imapcloud.nest.v2.service.dto.in.DjiAirLineDTO;
import com.imapcloud.nest.v2.service.dto.out.DJITaskOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DataScenePhotoOutDTO;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import com.imapcloud.sdk.pojo.djido.DjiCommonDO;
import com.imapcloud.sdk.pojo.djido.FileUploadCallBackDO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJIFileUploadCallBackListener.java
 * @Description DJIFileUploadCallBackListener
 * @createTime 2022年03月23日 09:12:00
 */
@Slf4j
@Service
public class DJIFileUploadCallBackListener extends AbstractEventListener<DJIFileUploadCallBackEvent> {

    @Resource
    private MissionPhotoService missionPhotoService;

    @Resource
    private MissionVideoService missionVideoService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private MissionRecordsService missionRecordsService;

    @Resource
    private MissionService missionService;

    @Resource
    private TaskService taskService;

    @Resource
    private DataCenterService dataCenterService;

    @Resource
    private RedisService redisService;

    @Resource
    private FileManager fileManager;

    @Resource
    private GridMissionService gridMissionService;

    @Resource
    private DJITaskFileService djiTaskFileService;

    /**
     * 消息监听-处理
     *
     * @param djiFileUploadCallBackEvent 消息事件
     */
    @Override
    @Async("djiExecutor")
    @EventListener
    public void eventListener(DJIFileUploadCallBackEvent djiFileUploadCallBackEvent) {
        log.info("【DJIFileUploadCallBackEvent】-【DJIFileUploadCallBackListener】事件:{}", djiFileUploadCallBackEvent.toString());
        DjiFileUploadCallBackDO djiFileUploadCallBackDO = djiFileUploadCallBackEvent.getSource();
        if (Objects.isNull(djiFileUploadCallBackDO) || djiFileUploadCallBackDO.getSuccess() == null) {
            return;
        }
        if(!NestTypeEnum.DJI_DOCK.equals(djiFileUploadCallBackDO.getNestTypeEnum())){
            if(log.isDebugEnabled()){
                log.debug("【DJIFileUploadCallBackEvent】-【DJIFileUploadCallBackListener】非大疆基站类型数据过滤");
            }
            return;
        }
        if (!djiFileUploadCallBackDO.getSuccess().booleanValue()) {
            log.info("同步失败，失败原因：{}", djiFileUploadCallBackDO.getErrMsg());
            return;
        }
        DjiCommonDO<FileUploadCallBackDO> doDjiCommonDO = djiFileUploadCallBackDO.getDoDjiCommonDO();

        Optional.ofNullable(doDjiCommonDO).map(DjiCommonDO::getData).map(FileUploadCallBackDO::getFile)
                .ifPresent(fileUpload -> {
                    String redisKey = "", uuid = "", name = "";
                    try {
                        name = fileUpload.getName();
                        redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.DJI_AUTO_UPLOAD, String.valueOf(name));
                        uuid = UUID.randomUUID().toString();
                        if (StringUtils.isEmpty(name)) {
                            return;
                        }
                        boolean bol = this.redisService.tryLock(redisKey, uuid, 3, TimeUnit.MINUTES);
                        if (!bol) {
                            throw new ValidateException("当前已有相同数据在同步，过滤去重");
                        }
                        if (name.endsWith(".jpeg") || name.endsWith(".JPEG")
                                || name.endsWith(".jpg") || name.endsWith(".JPG")) {
                            this.photoHandler(fileUpload);
                        } else if (name.endsWith(".mp4") || name.endsWith("MP4")) {
                            this.videoHandler(fileUpload);
                        }
                    } catch (ValidateException ve) {
                        log.info("【DJIFileUploadCallBackListener】:{},{}", ve.getMsg(), name);
                        throw new BusinessException(ve.getMessage());
                    } catch (Exception e) {
                        throw new BusinessException(e.getMessage());
                    } finally {
                        this.redisService.releaseLock(redisKey, uuid);
                    }
                });
    }

    /**
     * 处理照片
     *
     * @param fileUpload
     */
    private void photoHandler(FileUploadCallBackDO.FileUpload fileUpload) {
        //对象key
        String key = fileUpload.getObjectKey(),
                name = fileUpload.getName(),
                path = fileUpload.getPath(),
                wholePath = String.format("%s%s", geoaiUosProperties.getStore().getOriginPath(), key),
                flightId = fileUpload.getExt() == null ? "" : fileUpload.getExt().getFlightId();
        BigDecimal longitude = fileUpload.getMetadata().getShootPosition().getLng(), latitude = fileUpload.getMetadata().getShootPosition().getLat();
        //DJI_20221224152821_0001_Z.jpeg
        String[] djiNames = name.split("_");
        String wayPointIndexStr = "0001";
        String suffix = ".JPG";
        String lenDjiName = djiNames[djiNames.length - 1];
        String[] split = lenDjiName.split("\\.");
        Integer wayPointIndex = Integer.parseInt(split[0]);
        log.info("【DJIFileUploadCallBackListener】wayPointIndex:{}", wayPointIndex);
        List<Integer> identifyTypeList = new ArrayList<>();

        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(MissionRecordsEntity.class)
                .eq(MissionRecordsEntity::getExecId, flightId);
        MissionRecordsEntity missionRecordsEntity = (MissionRecordsEntity) missionRecordsService.list(queryWrapper)
                .stream()
                .findFirst()
                .orElseGet(() -> new MissionRecordsEntity());
        MissionEntity mission = missionService.getById(missionRecordsEntity.getMissionId());
        if(mission == null){
            log.info("【DJIFileUploadCallBackListener】 不是当前环境飞行数据 missionId -> {}",missionRecordsEntity.getMissionId());
            return;
        }
        TaskEntity taskEntity = taskService.getById(mission.getTaskId());
        if(taskEntity == null){
            log.info("【DJIFileUploadCallBackListener】 不是当前环境飞行数据 taskId -> {}",mission.getTaskId());
            return;
        }
        AtomicReference<String> byName = new AtomicReference<>();
        //幂等处理
        if (isExist(DataTypeEnum.PHOTO.getValue(), name)) {
            log.info("【DJIFileUploadCallBackListener】 重复数据过滤 name ->{}" , name);
            return;
        }
        //生成缩略图
//        String thumbnailPath = String.format("thumbnail/%s", key)
//                ,wholeThumbnailPath = String.format("%s%s",geoaiUosProperties.getStore().getOriginPath() , thumbnailPath);
//        try {
//            DrawImageUtils.thumbnailImage(key, thumbnailPath);
//        } catch (IOException e) {
//            log.error("#DJIFileUploadCallBackListener.photoHandler# 生成缩略图失败，key-> {} , thumbnailPath -> {}", key , thumbnailPath , e);
//        }
        String thumbnailUrl = fileManager.generateThumbnail(wholePath, 0.54D, false);

        MissionPhotoEntity missionPhotoEntity = MissionPhotoEntity.builder()
                .name(name)
                .OriginalFilename(name)
                .photoUrl(wholePath)
                //文件大小 -MARK
                .photoSize(Long.parseLong("1"))
                .missionId(mission.getId())
                .missionRecordsId(missionRecordsEntity.getId())
                .thumbnailName(name)
                .thumbnailUrl(thumbnailUrl)
                .timeCreated(Optional.ofNullable(fileUpload)
                        .map(FileUploadCallBackDO.FileUpload::getMetadata)
                        .map(FileUploadCallBackDO.Metadata::getCreateTime)
                        .map(LocalDateTime::parse)
                        .orElseGet(() -> LocalDateTime.now()))
                .downloaded(0)
                .orgCode(mission.getOrgCode())
                .waypointIndex(1)
                .photoType(0)
                .execId(flightId)
                .longitude(longitude == null ? null : longitude.doubleValue())
                .latitude(latitude == null ? null : latitude.doubleValue())
                .build();
        //0-普通可见光镜头 1-广角可见光镜头 2-变焦可见光镜头 3-热红外镜头
        Integer lenType = 0;
        String photoType = "VIS";
        if (name.contains("_W")) {
            lenType = 1;
            photoType = "WIDE";
        } else if (name.contains("_Z")) {
            lenType = 2;
            photoType = "ZOOM";
        } else if (name.contains("_T")) {
            lenType = 3;
            photoType = "THUM";
        }
        missionPhotoEntity.setLenType(lenType);
        int photoTypeInt = missionPhotoEntity.getLenType() == 3 ? 1 : 0;
        missionPhotoEntity.setPhotoType(photoTypeInt);
        //重命名
        DJITaskOutDTO.DJITaskInfoOutDTO djiTaskInfoOutDTO = this.djiTaskFileService.queryDJIAirLine(String.valueOf(mission.getTaskId()));
        String airLineJson = djiTaskInfoOutDTO.getDjiAirLineMap().get(mission.getId());
        DjiAirLineDTO djiAirLineDTO = JSON.parseObject(airLineJson, DjiAirLineDTO.class);
        List<DjiAirLineDTO.Points> pointsList = Optional.ofNullable(djiAirLineDTO)
                .map(DjiAirLineDTO::getMapConfigs)
                .map(DjiAirLineDTO.MapConfigs::getPoints)
                .orElseGet(CollectionUtil::newArrayList);

        if (CollectionUtil.isNotEmpty(pointsList)) {
            List<DjiAirLineDTO.CustomActions> customActions = pointsList.stream().map(DjiAirLineDTO.Points::getCustomActions)
                    .flatMap(Collection::stream)
                    .filter(action -> DJIActionTypeEnum.TAKE_PHOTO.getActionType().equals(action.getActionType()))
                    .collect(Collectors.toList());
            if (customActions.size() >= wayPointIndex) {
                DjiAirLineDTO.CustomActions customAction = customActions.get(wayPointIndex);
                if (Objects.nonNull(customAction)) {
                    byName.set(customAction.getByname());
                    identifyTypeList.addAll(CollectionUtil.newArrayList(customAction.getIdentifyType()));
                }
            }
        }

        /**
         * 有重命名
         */
        if (StringUtils.hasLength(byName.get())) {
            missionPhotoEntity.setName(byName.get() + suffix);
        } else {
            String rename = String.format("%s_%s_%s%s", taskEntity.getName(), photoType, wayPointIndexStr, suffix);
            missionPhotoEntity.setName(rename);
        }

        missionPhotoEntity.setWaypointIndex(wayPointIndex);


        this.missionPhotoService.save(missionPhotoEntity);
        log.info("identifyTypeList:{}", JSON.toJSONString(identifyTypeList));
        //推送分析统计
        this.pushDataAnalysis(identifyTypeList, missionPhotoEntity.getId().intValue(), taskEntity.getId());

        //推送网格
        this.pushGrid(CollectionUtil.newArrayList(missionPhotoEntity.getId()) , taskEntity.getId(),  missionRecordsEntity.getId(), taskEntity.getOrgCode());

        log.info("同步照片成功 -> {} , {}", missionPhotoEntity.getId(), name);
    }

    /**
     * 推送识别分析 -由于缺少照片航点挂钩信息暂时无法推送
     */
    private void pushDataAnalysis(List<Integer> identifyTypeList, Integer photoId, Integer tagId) {
        if(log.isDebugEnabled()){
            log.debug("【DJIFileUploadCallBackListener】大疆图片推送识别分析 {} ， {} , {} ", identifyTypeList , photoId , tagId);
        }
        IdenDataDto dto = new IdenDataDto();
        dto.setTagId(tagId);
        dto.setIdenIds(identifyTypeList);
        dto.setPhotoIds(Lists.newArrayList(photoId));
        dto.setDataIds(Lists.newArrayList());
        dto.setDataType(DataTypeEnum.PHOTO.getValue());
//        dto.setInfraredType();
        DataScenePhotoOutDTO.PushOut out = this.dataCenterService.push(dto);
        if(log.isDebugEnabled()){
            log.debug("【DJIFileUploadCallBackListener】大疆图片推送识别分析 {} ， {} , {} 结果 -> {} ", identifyTypeList , photoId , tagId , out.toString());
        }
    }

    /**
     * 推送网格
     */
    private void pushGrid(List<Long> photos , Integer taskId , Integer missionRecordId, String orgCode){
        if(log.isDebugEnabled()){
            log.debug("【DJIFileUploadCallBackListener】大疆图片推送网格数据 {} ， {} , {} , {}", photos , taskId , missionRecordId, orgCode);
        }
        Boolean gridRelRes = gridMissionService.relPhotoToGrid(photos, taskId, missionRecordId, orgCode);
        if(log.isDebugEnabled()){
            log.debug("【DJIFileUploadCallBackListener】大疆图片推送网格数据 {} ， {} , {} 结果 -> {} ", photos , taskId , missionRecordId , gridRelRes);
        }
    }


    /**
     * 处理视频
     *
     * @param fileUpload
     */
    public void videoHandler(FileUploadCallBackDO.FileUpload fileUpload) {
        //对象key
        String key = fileUpload.getObjectKey(),
                name = fileUpload.getName(),
                path = fileUpload.getPath(),
                wholePath = String.format("%s%s", geoaiUosProperties.getStore().getOriginPath(), key),
                flightId = fileUpload.getExt() == null ? "" : fileUpload.getExt().getFlightId();
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(MissionRecordsEntity.class)
                .eq(MissionRecordsEntity::getExecId, flightId);
        MissionRecordsEntity missionRecordsEntity = (MissionRecordsEntity) missionRecordsService.list(queryWrapper)
                .stream()
                .findFirst()
                .orElseGet(() -> new MissionRecordsEntity());
        MissionEntity mission = missionService.getById(missionRecordsEntity.getMissionId());
        if(mission == null){
            log.info("【DJIFileUploadCallBackListener】 不是当前环境飞行数据 missionId -> {}" , missionRecordsEntity.getMissionId());
            return;
        }
        //幂等处理
        if (isExist(DataTypeEnum.VIDEO.getValue(), name)) {
            log.info("【DJIFileUploadCallBackListener】重复数据过滤 name -> {}",name);
            return;
        }
        MissionVideoEntity missionVideoEntity = MissionVideoEntity.builder()
                .name(name)
                .fileName(name)
                .videoUrl(wholePath)
                //文件大小 -MARK
                .videoSize(Long.parseLong("1"))
                .missionId(mission.getId())
                .missionRecordsId(missionRecordsEntity.getId())
                .orgCode(mission.getOrgCode())
                .timeCreated(Optional.ofNullable(fileUpload)
                        .map(FileUploadCallBackDO.FileUpload::getMetadata)
                        .map(FileUploadCallBackDO.Metadata::getCreateTime)
                        .map(LocalDateTime::parse)
                        .orElseGet(() -> LocalDateTime.now()))
                .execId(flightId)
                .downloaded(0)
                .build();
        if (name.contains("_W")) {
            missionVideoEntity.setType(MissionConstant.MissionVideoType.WIDE_VIDEO);
        } else if (name.contains("_Z")) {
            missionVideoEntity.setType(MissionConstant.MissionVideoType.ZOOM_VIDEO);
        } else if (name.contains("_T")) {
            missionVideoEntity.setType(MissionConstant.MissionVideoType.THRM_VIDEO);
        } else if (name.contains("_S")) {
            missionVideoEntity.setType(MissionConstant.MissionVideoType.SCRN_VIDEO);
        } else {
            missionVideoEntity.setType(MissionConstant.MissionVideoType.DOWNLOAD_VIDEO);
        }
        if (missionVideoEntity.getType().equals(MissionConstant.MissionVideoType.DOWNLOAD_VIDEO)) {
            missionVideoEntity.setMediaCreateTime(missionVideoEntity.getTimeCreated());
        }
        missionVideoService.save(missionVideoEntity);
        log.info("【DJIFileUploadCallBackListener】 同步视频成功 -> {} , {}", missionVideoEntity.getId(), name);
    }

    /**
     * 重复文件过滤
     *
     * @param type
     * @param originName
     * @return
     */
    private boolean isExist(Integer type, String originName) {
        if (DataTypeEnum.PHOTO.getValue() == type) {
            LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(MissionPhotoEntity.class)
                    .eq(MissionPhotoEntity::getOriginalFilename, originName);
            return this.missionPhotoService.list(queryWrapper).stream().count() > 0;
        } else if (DataTypeEnum.VIDEO.getValue() == type) {
            LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(MissionVideoEntity.class)
                    .eq(MissionVideoEntity::getFileName, originName);
            return this.missionVideoService.list(queryWrapper).stream().count() > 0;
        }
        return false;
    }

}
