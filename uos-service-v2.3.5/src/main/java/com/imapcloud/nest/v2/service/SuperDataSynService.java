package com.imapcloud.nest.v2.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.constant.MissionConstant;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.model.MissionEntity;
import com.imapcloud.nest.model.MissionPhotoEntity;
import com.imapcloud.nest.model.MissionRecordsEntity;
import com.imapcloud.nest.model.TaskEntity;
import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.utils.ToolUtil;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.exception.SynDataValidateException;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.common.properties.UploadConfig;
import com.imapcloud.nest.v2.manager.dataobj.in.DataSynInDO;
import com.imapcloud.nest.v2.manager.event.DataSynEvent;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.base.BaseManager;
import com.imapcloud.sdk.manager.media.entity.CpsUploadFileEntity;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.Constant;
import com.imapcloud.sdk.pojo.constant.DataSynEnum;
import com.imapcloud.sdk.pojo.constant.MediaStatusV2Enum;
import com.imapcloud.sdk.pojo.entity.MediaFile;
import com.imapcloud.sdk.pojo.entity.MediaStateV2;
import com.imapcloud.sdk.pojo.entity.MissionMediaErrStatus;
import com.imapcloud.sdk.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Slf4j
@Component
public class SuperDataSynService {
    @Resource
    private MissionRecordsService missionRecordsService;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private CommonNestStateService commonNestStateService;

    @Resource
    private MissionService missionService;

    @Resource
    private MissionPhotoService missionPhotoService;

    @Resource
    private MissionVideoService missionVideoService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private MissionMediaErrLogService missionMediaErrLogService;
    @Resource
    private TaskService taskService;

    public static Map<String, List<String>> fileErrMap = new ConcurrentHashMap<>();

    public static Map<String, List<String>> dataSynErrMap = new ConcurrentHashMap<>();


    /**
     * 检查基站状态
     *
     * @param nestUuid
     * @param recordId
     */
    public void checkStatusPub(String nestUuid, Integer recordId) {
        //检测基站是否在传输文件
        MediaStateV2 mediaState = commonNestStateService.getMediaStateV2(nestUuid);
        if (MediaStatusV2Enum.UPLOADING.getValue().equals(mediaState.getCurrentState()) ||
                MediaStatusV2Enum.DOWNLOADING.getValue().equals(mediaState.getCurrentState()) ||
                MediaStatusV2Enum.DOWNLOAD_UPLOAD_MEANWHILE.getValue().equals(mediaState.getCurrentState())) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_CURRENT_BASE_STATION_IS_SYNCHRONIZING_DATA_THE_STATUS_IS.getContent())
                    + "【" + mediaState.getCurrentState() + "】");
        }
    }

    /**
     * 获取媒体数据
     *
     * @param nestUuid
     * @param recordId
     * @return
     */
    public List<String> getMediaDataPub(String nestUuid, Integer recordId) {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        // 判断机巢是否连接
        List<String> fileIdList = new ArrayList<>();
        if (ToolUtil.isNotEmpty(cm)) {
            // 查询数据库，获取图片List或视频List，为空则重新全部保存，不为空则比较缺失的图片或视频，补充拿缺失的图片或视频
            // 获取图片
            LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(MissionPhotoEntity.class)
                    .eq(MissionPhotoEntity::getMissionRecordsId, recordId)
                    .select(MissionPhotoEntity::getFileId);
            List<String> photoFileIdList = ((List<MissionPhotoEntity>) this.missionPhotoService.getBaseMapper()
                    .selectList(queryWrapper))
                    .stream()
                    .map(MissionPhotoEntity::getFileId)
                    .collect(Collectors.toList());
            if (photoFileIdList != null) {
                fileIdList.addAll(photoFileIdList);
            }

            // 获取视频
            List<String> videoFileIdList = missionVideoService.getFileIdListByRecordId(recordId);
            if (videoFileIdList != null) {
                fileIdList.addAll(videoFileIdList);
            }

        }
        return fileIdList;
    }

    /**
     * 获取媒体列表
     *
     * @param nestUuid
     * @param recordId
     * @return
     */
    public List handleSynPub(String nestUuid, Integer recordId) {
        MissionRecordsEntity missionRecordsEntity = missionRecordsService.getById(recordId);
        if (missionRecordsEntity == null) {
            log.info("检测不到架次记录，recordId={}", recordId);
            throw new BusinessException("检测不到架次记录");
        }
        Integer missionId = missionRecordsEntity.getMissionId();
        MissionEntity me = missionService.lambdaQuery()
                .eq(MissionEntity::getId, missionId)
                .select(MissionEntity::getName)
                .one();
        MissionRecordsEntity mre = missionRecordsService.lambdaQuery()
                .eq(MissionRecordsEntity::getId, recordId)
                .select(MissionRecordsEntity::getFlyIndex)
                .one();
        String missionName = me.getName() + "#" + mre.getFlyIndex() + ",";
        String execId = missionRecordsEntity.getExecId();
        // 获取无人机标识
        Integer uavWhich = missionRecordsEntity.getUavWhich();
        // 图片或视频列表
        log.info("execId: " + execId);
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm == null) {
            throw new BusinessException("机巢离线");
        }
        MqttResult<MediaFile> mqttResult = cm.getMediaManagerCf().getMediaInfoList(execId, AirIndexEnum.getInstance(uavWhich));
        if (!mqttResult.isSuccess()) {
            log.info("同步源数据失败，获取多媒体列表请求发送失败 recordId={}", recordId);
            throw new BusinessException(String.format("获取多媒体列表请求失败:%s", mqttResult.getMsg()));
        }
        List<String> resList = mqttResult.getResList().stream().map(MediaFile::getFileId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(resList)) {
            log.info("同步源数据失败，查询不到媒体列表 recordId={}", recordId);
            throw new BusinessException(String.format("获取多媒体列表请求失败: 查询不到媒体列表"));
        }
        return resList;
    }

    /**
     * @param nestUuid
     * @param recordId
     * @param fileIdList
     * @param mediaList
     * @return
     */
    public String sendCommandPub(String nestUuid, Integer recordId, List fileIdList, List mediaList) {
        String code = "";
        MissionRecordsEntity missionRecordsEntity = missionRecordsService.getById(recordId);
        String execId = missionRecordsEntity.getExecId();
        Integer uavWhich = missionRecordsEntity.getUavWhich();
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm == null) {
            throw new BusinessException("机巢离线");
        }
        List<String> lostFileIdList = ListUtils.subtract(mediaList, fileIdList);
        CpsUploadFileEntity cpsUploadFileEntity = null;
        //如果缺失文件列表为空，且已同步文件列表不为空，代表没有数据需要同步
        if (CollectionUtil.isEmpty(lostFileIdList) && CollectionUtil.isNotEmpty(fileIdList)) {
            throw new SynDataValidateException(MessageUtils.getMessageByLang(MessageEnum.GEOAI_UOS_ALL_DATA_HAS_BEEN_SYNCHRONIZED_NO_NEED_TO_REPEAT_SYNCHRONIZATION.getContent(), ""));
        }

        //缺失文件有值的时候，走按文件id同步流程；否则走架次同步
        if (CollectionUtil.isNotEmpty(lostFileIdList)) {
            cpsUploadFileEntity = this.initCpsUploadFile(null, lostFileIdList, uavWhich);
        }
        //没有文件的时候
        if (CollectionUtil.isEmpty(fileIdList)) {
            cpsUploadFileEntity = this.initCpsUploadFile(execId, null, uavWhich);
        }
        log.info("调用同步指令 cpsUploadFileEntity-> {} recordId={}", cpsUploadFileEntity, recordId);
        // 上传媒体文件到服务器
        cm.getMediaManagerV2().downloadToServer(cpsUploadFileEntity, (baseResult3, isSuccess, errMsg) -> {
            // 回调处理
            try {
                log.info("调用同步指令回调，记录异常信息 ：{}", baseResult3.getCode());
                if (isSuccess) {
                    if (ObjectUtils.isNotEmpty(baseResult3.getParam()) && DataSynEnum.isContaionCode(baseResult3.getCode())) {
                        MissionMediaErrStatus missionMediaErrStatus = JSONUtil.parseObject(baseResult3.getParam(), MissionMediaErrStatus.class);
                        missionMediaErrLogService.saveErrLog(missionMediaErrStatus, recordId, missionRecordsEntity.getMissionId(), nestUuid);
                    }
                }
            } catch (Exception e) {
                log.error("mqttException",e);
            }
        });
        if (CollectionUtil.isNotEmpty(lostFileIdList)) {
            code = Constant.MEDIA_MANAGER_2_C7;
        } else {
            code = Constant.MEDIA_MANAGER_2_C2;
        }
        return code;
    }

    /**
     * 回调数据
     *
     * @param nestUuid
     * @param recordId
     */
    public void callbackPub(String nestUuid, Integer recordId, CountDownLatch countDownLatch) {
        String key = String.format("%s-%d", nestUuid, recordId);
        String threadName = Thread.currentThread().getName();
        fileErrMap.put(key, CollectionUtil.newArrayList());
        dataSynErrMap.put(key, CollectionUtil.newArrayList());
        MissionRecordsEntity missionRecordsEntity = missionRecordsService.getById(recordId);
        Integer uavWhich = 0;
        if (Objects.nonNull(missionRecordsEntity)) {
            uavWhich = missionRecordsEntity.getUavWhich();
        }
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm == null) {
            throw new BusinessException("基站离线");
        }
        BaseManager baseManager = cm.getBaseManager();
        //监听媒体进度条
        Integer finalUavWhich = uavWhich;
        baseManager.listenMediaStateV2((mediaState, isSuccess, errMsg) -> {
            if (isSuccess) {
                if (!MediaStatusV2Enum.IDLE.getValue().equals(mediaState.getCurrentState()) && !MediaStatusV2Enum.UNKNOWN.getValue().equals(mediaState.getCurrentState())) {
                    Map<String, Object> map = new HashMap<>(2);
                    map.put("missionRecordId", recordId);
                    map.put("mediaState", mediaState);
                    map.put("uavWhich", finalUavWhich);
                    String message = WebSocketRes.ok().topic(WebSocketTopicEnum.UPLOAD_MEDIA_TO_SERVER).data(map).toJSONString();
                    // 推流（成果页面、中台页面进度条）
                    pushPhotoTransMsgByWs(nestUuid, message);
                }
            }
        }, AirIndexEnum.getInstance(0));
        TaskEntity taskEntity = taskService.getByMissionId(missionRecordsEntity.getMissionId());
        Integer airLineId = missionService.getAirLineIdByMissionId(missionRecordsEntity.getMissionId());
        // 数据保存到服务器的回调
        // 异步线程监听
        baseManager.listenMediaManualState((result, isSuccess, errMsg) -> {
            // 失败时返回的数据格式
            log.info("callbackPub recordsId -> {} , isSuccess -> {} , errMsg -> {} , result -> {} ", recordId, isSuccess, errMsg, result);
            /*if (DataSynEnum.FILE_NOT_FIND.getCode().equals(result.getCode())
                    || DataSynEnum.FILE_NOT_EXIST.getCode().equals(result.getCode()))*/
            if (DataSynEnum.isContaionCode(result.getCode())) {
                //是否需要全部返回，还是记录起来
                if (log.isDebugEnabled()) {
                    log.debug("4000/4001 callbackPub 【{}】recordsId -> {} , message -> {} , result -> {} ", threadName, recordId, result.getMsg(), result);
                }
                String param = result.getParam();
                if (StringUtils.isNotEmpty(param)) {
                    missionRecordsService.updateDataStatusById(MissionConstant.MissionExecDataStatus.SERVER_ERROR, recordId);
                    missionMediaErrLogService.saveErrLog(JSONUtil.parseObject(param, MissionMediaErrStatus.class), recordId, missionRecordsEntity.getMissionId(), nestUuid);
                }
                fileErrMap.get(key).add(result.getMsg());
                //更新记录同步状态，记录异常信息
            } else if ("1001".equals(result.getCode())) {
                //异步处理架次成功后事件
                this.applicationContext.publishEvent(new DataSynEvent(new DataSynInDO()
                        .setAirLineId(airLineId)
                        .setTaskId(taskEntity.getId())
                        .setTaskType(taskEntity.getType())
                        .setNestUuid(nestUuid)
                        .setMissionRecordId(missionRecordsEntity.getId())));
                //释放阻塞
                countDownLatch.countDown();
                log.info("1001 callbackPub 【{}】 recordsId -> {} ，回调消息 -> countDownLatch减1,当前架次同步结束 ，countDown -> {} ， result -> {}", threadName, recordId, countDownLatch, result);
                //发起事件回调
                baseManager.removeListMediaManualState();
            } else {
                dataSynErrMap.get(key).add(result.getMsg());
            }
        });
    }

    /**
     * 该对象后面需要改成dto
     *
     * @param execId
     * @param lostFileIdList
     * @param uavWhich
     * @return
     */
    public CpsUploadFileEntity initCpsUploadFile(String execId, List<String> lostFileIdList, Integer uavWhich) {
        String nginxDomain = geoaiUosProperties.getDomain().getNginx();
        UploadConfig uploadConfig = geoaiUosProperties.getUpload();
        String chunkInitUrl = String.format("%s%s", nginxDomain, uploadConfig.getChunkInitPath()),
                chunkCombineUrl = String.format("%s%s", nginxDomain, uploadConfig.getChunkCombinePath()),
                chunkSyncUrl = String.format("%s%s", nginxDomain, uploadConfig.getChunkSyncPath());
        return CpsUploadFileEntity.builder()
                .chunkInitUrl(chunkInitUrl)
                .chunkCombineUrl(chunkCombineUrl)
                .chunkSyncUrl(chunkSyncUrl)
                .url(uploadConfig.getUrl())
                .execId(execId)
                .fileIdList(lostFileIdList)
                .autoStartUp(true)
                .uploadByChunks(true)
                .uavWhich(uavWhich)
                .ossConfig(geoaiUosProperties.getOss())
                .build();

    }

    /**
     * 推送
     *
     * @param uuid
     * @param message
     */
    private void pushPhotoTransMsgByWs(String uuid, String message) {
        ChannelService.sendMessageByType3Channel(uuid, message);
        ChannelService.sendMessageByType4Channel(uuid, message);
    }

}
