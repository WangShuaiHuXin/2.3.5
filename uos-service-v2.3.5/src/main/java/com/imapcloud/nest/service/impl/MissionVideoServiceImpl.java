package com.imapcloud.nest.service.impl;

import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.CharsetUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.async.PartUploadFileAsync;
import com.imapcloud.nest.common.constant.DataConstant;
import com.imapcloud.nest.common.constant.MissionConstant;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.common.validator.Assert;
import com.imapcloud.nest.mapper.MissionVideoMapper;
import com.imapcloud.nest.model.*;
import com.imapcloud.nest.pojo.vo.DownLoadMediaVO;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.utils.*;
import com.imapcloud.nest.utils.mongo.pojo.NestAndAirEntity;
import com.imapcloud.nest.utils.mongo.service.MongoNestAndAirService;
import com.imapcloud.nest.utils.nms.NmsRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.common.properties.IptvConfig;
import com.imapcloud.nest.v2.common.properties.StoreConfig;
import com.imapcloud.nest.v2.manager.dataobj.FileUrlMappingDO;
import com.imapcloud.nest.v2.service.dto.in.RecordTaskVideoInDTO;
import com.imapcloud.nest.v2.manager.sql.MissionVideoManager;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.dto.out.CreateRelayParamOutDTO;
import com.imapcloud.nest.v2.service.dto.out.DJIAircraftInfoOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2020-08-14
 */
@Service
@Slf4j
@EnableAsync
public class MissionVideoServiceImpl extends ServiceImpl<MissionVideoMapper, MissionVideoEntity> implements MissionVideoService {

    @Resource
    private MissionRecordsService missionRecordsService;

    @Resource
    private MissionService missionService;

    @Resource
    private DownLoadZipUtil downLoadZipUtil;

    @Autowired
    private DataCenterService dataCenterService;

    @Autowired
    private NodeMediaUtil nodeMediaUtil;

//    @Autowired
//    private SysTagService sysTagService;

    @Autowired
    private MongoNestAndAirService mongoNestAndAirService;

    @Autowired
    private ExecutorService executorService;

//    @Autowired
//    private MediaRelayService mediaRelayService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

//    @Resource
//    private BaseNestService baseNestService;

    @Resource
    private FileInfoSrtService fileInfoSrtService;

    @Resource
    private MissionVideoManager missionVideoManager;
    @Override
    public PageUtils getVideoPage(Map<String, Object> params, Integer missionRecordId) {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        IPage<MissionVideoEntity> totalPage = baseMapper.getVideoByRecordId(new Query<MissionPhotoEntity>().getPage(params), missionRecordId, orgCode);
        setVideoDetail(totalPage);
        return new PageUtils(totalPage);
    }


    @Override
    public void getInspectLatLng(Long videoId) {
        log.info("===============开始给" + videoId + "写经纬度=====================");
        try {
            MissionVideoEntity missionVideoEntity = this.getById(videoId);
            LocalDateTime createTime = missionVideoEntity.getCreateTime();
            Integer missionRecordsId = missionVideoEntity.getMissionRecordsId();
            String videoUrl = missionVideoEntity.getVideoUrl();
            List<NestAndAirEntity> nestAndAirEntities = mongoNestAndAirService.findByMissionRecordsId(missionRecordsId);
            log.info("===============从mongo获取经纬度数据=====================");
            if (nestAndAirEntities.size() > 0) {
                JSONObject jsonObject = new JSONObject();
                for (NestAndAirEntity nestAndAirEntity : nestAndAirEntities) {
                    Map<String, Object> msg = nestAndAirEntity.getMsg();
                    LocalDateTime saveDateTime = nestAndAirEntity.getSaveDateTime();
                    Duration duration = Duration.between(createTime, saveDateTime);
                    long seconds = duration.getSeconds();
                    Object object = msg.get("aircraftMsg");
                    String latLngStr = getObjectValue(object, "lng") + "," + getObjectValue(object, "lat") + "," + getObjectValue(object, "alt");
                    jsonObject.put(String.valueOf(seconds), latLngStr);
                }
                String srtJsonName = videoUrl.substring(0, videoUrl.lastIndexOf("."));
                String srtJsonFile = PartUploadFileAsync.writeFileContext(jsonObject, srtJsonName);
                missionVideoEntity.setSrtUrl(srtJsonFile);
                updateById(missionVideoEntity);
                log.info("===============录屏写经纬度成功=====================");
            }
        } catch (Exception e) {
            //e.printStackTrace();
            log.info("===============录屏写经纬度失败=====================" + e.getMessage());
        }
    }


//    @Override
//    public void checkCaptureStop(Integer missionId, String execMissionID, String nestId, Integer uavWhich) {
//        String token = nodeMediaUtil.getToken();
//        String host = geoaiUosProperties.getIptv().getUrl();
////        NestEntity nest = nestService.getNestByIdIsCache(nestId);
//        CreateRelayParamOutDTO createRelayParam = baseNestService.getCreateRelayParamCache(nestId, uavWhich);
//        if (Objects.isNull(createRelayParam)) {
//            log.error("未查询到基站相关信息");
//            return;
//        }
//        String appName = nodeMediaUtil.getAppName(createRelayParam.getUavStreamUrl());
//        String videoCodec = nodeMediaUtil.getVideoCodec(appName, token, host);
//        log.debug("【{}】结束录像检查:{}", createRelayParam.getNestName(), videoCodec);
//        if (null == execMissionID) {
//            log.error("【{}】任务未关联到execId", missionId);
//            mediaRelayService.delRelay(nestId, token, host, uavWhich);
//            return;
//        }
//        if (nodeMediaUtil.CODEC_ERR.equals(videoCodec)) {
//            log.error("【{}】获取流编码异常,准备删除转发", createRelayParam.getNestName());
//            mediaRelayService.delRelay(nestId, token, host, uavWhich);
//            return;
//        }
//        // 先判断是否在录制，如果正在录制，则结束录制任务
//        NmsRes recRes = nodeMediaUtil.getRecord(appName, token, host);
//        MissionEntity mission = missionService.getById(missionId);
//        if (Objects.equals(nodeMediaUtil.CODE_SUCCESS, recRes.getCode())) {
//            NmsRes finishRes = nodeMediaUtil.finishRecord(appName, token, host);
//            if (Objects.equals(finishRes.getCode(), nodeMediaUtil.CODE_SUCCESS)) {
//                log.info("架次【{}】录像任务结束成功", mission.getName());
//
//                //删除relay
//                mediaRelayService.delRelay(nestId, token, host, uavWhich);
//                log.info("relay删除成功，关联基站:{}", createRelayParam.getNestName());
//                MissionRecordsEntity missionRecord = missionRecordsService.getOne(new QueryWrapper<MissionRecordsEntity>().eq("exec_id", execMissionID).eq("deleted", 0));
//                if (null == missionRecord) {
//                    log.info("execId【{}】无法关联到架次记录", execMissionID);
//                    return;
//                }
//                List<MissionVideoEntity> videoEntityList = baseMapper.selectList(new QueryWrapper<MissionVideoEntity>().eq("mission_records_id", missionRecord.getId()).eq("deleted", 0));
//                if (null != videoEntityList && !videoEntityList.isEmpty()) {
//                    videoEntityList.stream().forEach(v -> {
//                        //更新视频记录状态
//                        v.setRecordStatus(MissionConstant.MissionVideoRecordStatus.FINISH_RECORD);
//                        //写经纬度信息
////                        executorService.execute(() -> getInspectLatLng(v));
//                    });
//                    this.updateBatchById(videoEntityList);
//                    return;
//                }
//            }
//            log.error("架次【{}】录像任务结束失败", mission.getName());
//            return;
//        }
//        log.info("{}录像任务已停止", mission.getName());
//        //录像停止有可能存在提前于架次任务进度的情况，此处单独再执行一次relay删除操作
//        mediaRelayService.delRelay(nestId, token, host, uavWhich);
//        return;
//    }


//    /**
//     * 创建录像任务
//     */
//    @Override
//    public void createVideoCapture(String appName, String token, String host, String nestId, Integer missionId, Integer recordId, String execMissionID) {
//        MissionEntity missionEntity = missionService.getById(missionId);
//        String missionName = missionEntity.getName();
//        String filename = "CAPTURE_" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
//        String videoName = filename + ".flv";
//        String screenName = filename + ".jpg";
////        NestEntity nest = nestService.getNestByIdIsCache(nestId);
//        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
//        String missionPath = String.format("%s/%s/%s/", nestUuid, missionId, recordId);
//        //查询该架次任务已记录的录像文件数
//        Integer count = this.baseMapper.countByRecordId(recordId);
//        if (null == count) {
//            count = 0;
//        }
//        //默认最大35分钟录像时长
//        log.info("开始异步创建录像任务");
//        nodeMediaUtil.creatRecordAsync(geoaiUosProperties.getIptv().getPath() + "/" + missionPath, videoName, 35 * 60, appName, token, host);
//        // 已存在录像任务--400，流不存在--404
//        log.info("架次【{}】第{}个录像创建成功，路径:{},名称:{}", missionName, count + 1, missionPath, videoName);
//        // 创建录屏成功，截图做视频封面
////        nodeMediaUtil.screenshotAsync(nodeMediaConfig.getPath() + "/" + missionPath, screenName, appName, token, host);
////        log.info("架次【{}】第{}个封面创建成功,路径:{},名称:{}", missionName, count + 1, missionPath, screenName);
////        String videoThumbnail = globalConfig.getVideoPath() + missionPath + screenName;
//        //保存录像实体入库
//        MissionVideoEntity missionVideoEntity = new MissionVideoEntity();
//        missionVideoEntity.setMissionId(missionId);
//        missionVideoEntity.setMissionRecordsId(recordId);
//        missionVideoEntity.setExecId(execMissionID);
//        missionVideoEntity.setOrgCode(missionEntity.getOrgCode());
//        missionVideoEntity.setName(videoName);
//        missionVideoEntity.setVideoUrl(geoaiUosProperties.getStore().getVideoPath() + missionPath + videoName);
////        missionVideoEntity.setVideoThumbnail(videoThumbnail);
//        missionVideoEntity.setType(MissionConstant.MissionVideoType.RECORD_VIDEO);
//        missionVideoEntity.setRecordStatus(MissionConstant.MissionVideoRecordStatus.NO_FINISH_RECORD);
//        this.save(missionVideoEntity);
//        log.info("第{}个录像记录入库", count + 1);
//        //TODO 创建录频成功的时候去保存消息的日志
////        } else {
////            log.error("架次【{}】第{}个录像创建失败,原因:{}", missionName, count + 1, recRes.getError());
////            //删除转发并重新转发
////            log.info("录像任务创建失败，删除转发");
////            mediaRelayService.delRelay(nestId, token, host);
////            log.info("录像任务创建失败，重新创建转发");
////            mediaRelayService.createRelay(nestId);
////        }
//    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String saveVideoGpsInfo(Integer videoId, String srtFilename, String gpsJsonStorageUrl) {
        FileInfoSrtEntity fileInfoSrtEntity = new FileInfoSrtEntity();
        fileInfoSrtEntity.setSrtUrl(gpsJsonStorageUrl);
        // since 2.2.3，由于源srt文件不会进行存储，故设置成gps信息文件地址
        fileInfoSrtEntity.setFileUrl(gpsJsonStorageUrl);
        fileInfoSrtEntity.setName(srtFilename);
        fileInfoSrtService.save(fileInfoSrtEntity);
        // 查询视频ID
        MissionVideoEntity missionVideoEntity = this.getById(videoId);
        if(Objects.nonNull(missionVideoEntity)){
            missionVideoEntity.setSrtId(fileInfoSrtEntity.getId());
            // srt源文件解析GPS信息JSON文件地址
            missionVideoEntity.setSrtUrl(fileInfoSrtEntity.getSrtUrl());
            // srt源文件地址
            missionVideoEntity.setSrtFileUrl(null);
            this.updateById(missionVideoEntity);
        }
        return fileInfoSrtEntity.getId().toString();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveMissionRecordVideo(String recordTaskId, String execMissionId) {
        QueryWrapper<MissionRecordsEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("exec_id", execMissionId).eq("deleted", 0);
        MissionRecordsEntity missionRecord = missionRecordsService.getOne(queryWrapper);
        if (missionRecord == null) {
            log.warn("execId:{}未关联架次记录，无需录像视频记录", execMissionId);
            return null;
        }
        MissionEntity missionEntity = missionService.getById(missionRecord.getMissionId());
        MissionVideoEntity missionVideoEntity = new MissionVideoEntity();
        missionVideoEntity.setMissionId(missionRecord.getMissionId());
        missionVideoEntity.setMissionRecordsId(missionRecord.getId());
        missionVideoEntity.setExecId(execMissionId);
        missionVideoEntity.setOrgCode(missionEntity.getOrgCode());
        missionVideoEntity.setName("未知录像名称.mp4");
        missionVideoEntity.setVideoUrl(null);
        missionVideoEntity.setRecordTaskId(recordTaskId);
        missionVideoEntity.setType(MissionConstant.MissionVideoType.RECORD_VIDEO);
        missionVideoEntity.setRecordStatus(MissionConstant.MissionVideoRecordStatus.NO_FINISH_RECORD);
        missionVideoEntity.setRecordIndex(0);
        this.save(missionVideoEntity);
        return missionVideoEntity.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void saveMissionRecordVideo(RecordTaskVideoInDTO recordTaskVideoInDTO) {
        MissionVideoMapper baseMapper = getBaseMapper();
        LambdaQueryWrapper<MissionVideoEntity> con = Wrappers.lambdaQuery(MissionVideoEntity.class)
                .eq(MissionVideoEntity::getRecordTaskId, recordTaskVideoInDTO.getRecordTaskId())
                .eq(MissionVideoEntity::getDeleted, false);
        List<MissionVideoEntity> missionVideoEntities = baseMapper.selectList(con);
        if(!CollectionUtils.isEmpty(missionVideoEntities)){
            Optional<MissionVideoEntity> optional = missionVideoEntities.stream().filter(r -> Objects.equals(r.getRecordIndex(), recordTaskVideoInDTO.getIndex())).findFirst();
            if(optional.isPresent()){
                MissionVideoEntity missionVideoEntity = optional.get();
                MissionVideoEntity update = new MissionVideoEntity();
                update.setId(missionVideoEntity.getId());
                update.setVideoUrl(recordTaskVideoInDTO.getVideoUri());
                update.setName(recordTaskVideoInDTO.getVideoName());
                update.setRecordIndex(recordTaskVideoInDTO.getIndex());
                update.setVideoSize(recordTaskVideoInDTO.getVideoSize());
                update.setRecordStatus(MissionConstant.MissionVideoRecordStatus.FINISH_RECORD);
                int affect = baseMapper.updateById(update);
                if(log.isDebugEnabled()){
                    log.debug("更新任务视频[{}]录像信息[{}]，结果：{}", missionVideoEntity.getId(), recordTaskVideoInDTO.getRecordTaskId(), affect > 0);
                }
            }else{
                MissionVideoEntity missionVideoEntity = missionVideoEntities.get(0);
                MissionVideoEntity insert = new MissionVideoEntity();
                insert.setMissionId(missionVideoEntity.getMissionId());
                insert.setMissionRecordsId(missionVideoEntity.getMissionRecordsId());
                insert.setExecId(missionVideoEntity.getExecId());
                insert.setOrgCode(missionVideoEntity.getOrgCode());
                insert.setName(recordTaskVideoInDTO.getVideoName());
                insert.setVideoUrl(recordTaskVideoInDTO.getVideoUri());
                insert.setRecordTaskId(missionVideoEntity.getRecordTaskId());
                insert.setType(MissionConstant.MissionVideoType.RECORD_VIDEO);
                insert.setRecordStatus(MissionConstant.MissionVideoRecordStatus.FINISH_RECORD);
                int affect = baseMapper.insert(insert);
                if(log.isDebugEnabled()){
                    log.debug("新增任务视频[{}]录像信息[{}]，结果：{}", missionVideoEntity.getId(), recordTaskVideoInDTO.getRecordTaskId(), affect > 0);
                }
            }
        }
    }

    @Override
    public String findRecordingTaskIdByExecId(String execMissionId) {
        if(StringUtils.hasText(execMissionId)){
            MissionVideoMapper baseMapper = getBaseMapper();
            LambdaQueryWrapper<MissionVideoEntity> con = Wrappers.lambdaQuery(MissionVideoEntity.class)
                    .eq(MissionVideoEntity::getExecId, execMissionId)
                    .eq(MissionVideoEntity::getDeleted, false);
            List<MissionVideoEntity> missionVideoEntities = baseMapper.selectList(con);
            if(!CollectionUtils.isEmpty(missionVideoEntities)){
                return missionVideoEntities.get(0).getRecordTaskId();
            }
        }
        return null;
    }

    public Object getObjectValue(Object object, String name) {
        try {
            Field declaredField = object.getClass().getDeclaredField(name);
            declaredField.setAccessible(true);
            return declaredField.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0;
    }


    @Override
    public void appCreateVideo(Integer missionId, Integer recordId, String deviceId) {
        IptvConfig iptv = geoaiUosProperties.getIptv();
        String token = nodeMediaUtil.getToken();
        String host = iptv.getUrl();
        String pullHttpUrl = missionRecordsService.getAppPullHttpUrl(recordId);
        String appName = nodeMediaUtil.getAppName(pullHttpUrl);
        // 判断是否已经在录制，是-则停止当前录屏后重新录制新的视频；否-直接创建录屏；
        NmsRes recordRes = nodeMediaUtil.getRecord(appName, token, iptv.getUrl());
        if (recordRes.getCode().equals(nodeMediaUtil.CODE_SUCCESS)) {
            // 结束录像
            log.info("即将停止先前的录像任务");
            nodeMediaUtil.finishRecord(appName, token, iptv.getUrl());
        }
        // 先判断是否有流
        String codec = nodeMediaUtil.getVideoCodec(appName, token, host);
        if (codec.equals(nodeMediaUtil.CODEC_ERR)) {
            log.info("开始异步创建app录像任务");
            createAppCapture(appName, host, token, missionId, recordId, deviceId);
            return;
        }
        log.info("【{}】流不存在，无法创建录像任务", appName);
    }

    @Override
    public void appFinishVideo(Integer recordId) {
        // 通过架次id获取是否已有视频，有则结束录像
        Long videoId = baseMapper.getIdByRecordId(recordId);
        if (ToolUtil.isNotEmpty(videoId)) {
            String pullHttpUrl = missionRecordsService.getAppPullHttpUrl(recordId);
            String appName = nodeMediaUtil.getAppName(pullHttpUrl);
            appFinishCapture(videoId, appName);
        }
        log.info("没有录屏任务，无需结束录屏");
    }


    /**
     * @param videoId
     */
    @Override
    public void updateMissionVideoLatLng(Integer videoId) {
        MissionVideoEntity missionVideoEntity = this.getById(videoId);
        Integer missionId = missionVideoEntity.getMissionId();
        String srtUrl = missionVideoEntity.getSrtUrl();
        if (srtUrl == null) {
            MissionVideoEntity one = this.getOne(new QueryWrapper<MissionVideoEntity>().eq("mission_id", missionId).isNotNull("srt_url").eq("deleted", 0).last("limit 1"));
            if (one != null) {
                missionVideoEntity.setSrtUrl(one.getSrtUrl());
                updateById(missionVideoEntity);
            } else {
                Assert.failure(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_NEW_TRACK_FOR_THIS_SORTIE.getContent()));
            }
        } else {
            Assert.failure(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_VIDEO_ALREADY_EXISTS.getContent()));
        }
    }

    @Override
    public void delSrt(Integer videoId) {
        MissionVideoEntity missionVideoEntity = this.getById(videoId);
        missionVideoEntity.setSrtUrl(null);
        missionVideoEntity.setSrtId(null);
        missionVideoEntity.setSrtFileUrl(null);
        this.updateById(missionVideoEntity);
    }

    @Override
    public Long getIdByRecordId(Integer recordId) {
        return baseMapper.getIdByRecordId(recordId);
    }

    @Override
    public MissionVideoEntity getVideoByRecordId(Integer recordId) {
        return baseMapper.getVideoByRecordsId(recordId);
    }

    @Override
    public void deleteVideo(List<Integer> missionRecordIds) {
        baseMapper.deleteByRecordId(missionRecordIds);
    }

    @Override
    public void downlandVideo(String ids, boolean isAll, Integer missionRecordsId, HttpServletResponse response) {
        if (ToolUtil.isEmpty(ids) && !isAll) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DOWNLOAD_FAILED_PLEASE_SELECT_THE_CORRECT_VIDEO.getContent()));
        }
        if (ToolUtil.isNotEmpty(ids) && isAll) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DOWNLOAD_FAILED_PLEASE_SELECT_THE_CORRECT_VIDEO.getContent()));
        }

        // 压缩包名称
        String missionName = missionRecordsService.getMissionName(missionRecordsId);
        Integer flyIndex = missionRecordsService.getFlyIndex(missionRecordsId);
        String fileName = missionName + "_第" + flyIndex + "次执行_成果视频压缩包_" + System.currentTimeMillis() + ".zip";
        Integer missionId = missionRecordsService.getMissionIdByRecordId(missionRecordsId);
        // 原视频的路径--modify reason 由拼接改为从数据库获取地址。
        String originPath = "";
        // 录屏的路径
//        String baseNestId = taskService.getBaseNestIdByRecordsId(missionRecordsId);
//        String nestUuid = baseNestService.getNestUuidByNestId(baseNestId);
//        NestEntity nestEntity = missionRecordsService.getNestUuidById(missionRecordsId);
//        String nestUuid = "";
//        if (nestEntity != null) {
//            nestUuid = nestEntity.getUuid();
//        }
//        String appUuid = missionRecordsService.getNestUuidByAppId(missionRecordsId);
//        if(appUuid != null){
//            nestUuid = appUuid;
//        }
        // 录屏的路径--modify reason 由拼接改为从数据库获取地址。
        String recordPath = "";

        Integer[] syncTypes = {
                MissionConstant.MissionVideoType.DOWNLOAD_VIDEO,
                MissionConstant.MissionVideoType.WIDE_VIDEO,
                MissionConstant.MissionVideoType.ZOOM_VIDEO,
                MissionConstant.MissionVideoType.THRM_VIDEO,
                MissionConstant.MissionVideoType.SCRN_VIDEO
        };
        Integer[] recordTypes = {MissionConstant.MissionVideoType.RECORD_VIDEO};
        List<String> minIoNameList = new ArrayList<>();
        List<String> nmsNameList = new ArrayList<>();
        // 全部视频下载
        if (isAll) {
            // 同步源数据得到的视频List
            List<Map> minIoMapList = baseMapper.getVideoNameAndUrlByRecordId(missionRecordsId, Arrays.asList(syncTypes));
            // 录屏得到的视频List
            List<Map> nmsMapList = baseMapper.getVideoNameAndUrlByRecordId(missionRecordsId, Arrays.asList(recordTypes));
            if (ToolUtil.isEmpty(minIoMapList) && ToolUtil.isEmpty(nmsMapList)) {
                throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DOWNLOAD_FAILED_THERE_IS_NO_VIDEO_FOR_THIS_SORTIE.getContent()));
            }
            DownLoadMediaVO downLoadMediaVO = this.preHandlerMedia(minIoMapList, nmsMapList);
            // 将录屏视频，原视频从MinIO下载，并打包到压缩包里下载到浏览器
            downLoadZipUtil.downloadFileAndZip(fileName, downLoadMediaVO.getOriginVideos(), downLoadMediaVO.getRecordVideos(), response);
        } else {
            // 选择部分视频下载
            List<String> photoIdStr = Arrays.asList(ids.split(","));
            List<Integer> idList = photoIdStr.stream().map(e -> Integer.parseInt(e)).collect(Collectors.toList());

            if (ToolUtil.isNotEmpty(idList)) {
                Integer videoType = baseMapper.getVideoType(idList.get(0));
                if (MissionConstant.MissionVideoType.UPLOAD_VIDEO.equals(videoType)) {
                    // 人工上传
                    fileName = "人工上传视频压缩包_" + System.currentTimeMillis() + ".zip";
                    List<FileUrlMappingDO> videoUrlList = baseMapper.getVideoUrlByIdList(idList);
                    DownLoadZipUtil.downloadUploadPhotoOrVideo(fileName, videoUrlList, response);
                } else {
                    // 巡检同步回来的视频或录屏
                    List<Map> minIoMapList = baseMapper.getNameAndUrlByIdList(idList, Arrays.asList(syncTypes));
                    List<Map> nmsMapList = baseMapper.getNameAndUrlByIdList(idList, Arrays.asList(recordTypes));
                    if (ToolUtil.isEmpty(minIoMapList) && ToolUtil.isEmpty(nmsMapList)) {
                        throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DOWNLOAD_FAILED_NO_CORRESPONDING_VIDEO_FOUND.getContent()));
                    }
                    DownLoadMediaVO downLoadMediaVO = this.preHandlerMedia(minIoMapList, nmsMapList);
                    // 将视频从MinIO下载，并打包到压缩包里
                    downLoadZipUtil.downloadFileAndZip(fileName, downLoadMediaVO.getOriginVideos(), downLoadMediaVO.getRecordVideos(), response);
                }
            } else {
                throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DOWNLOAD_FAILED_NO_CORRESPONDING_VIDEO_FOUND.getContent()));
            }
        }

    }

    @Override
    public int selectNum(Integer videoId, String orgCode) {
        return missionVideoManager.selectNum(videoId,orgCode);
    }

    private DownLoadMediaVO preHandlerMedia(List<Map> minIoMapList, List<Map> nmsMapList) {
        List<FileUrlMappingDO> originVideos = minIoMapList.stream()
                .filter(x -> x.get("video_url") != null)
                .map(x -> {
                    FileUrlMappingDO fileUrlMappingDO = new FileUrlMappingDO();
                    fileUrlMappingDO.setStorageUri(x.get("video_url").toString());
                    fileUrlMappingDO.setOriginName(x.get("name").toString());
                    return fileUrlMappingDO;
                })
                .collect(Collectors.toList());
        List<FileUrlMappingDO> recordVideos = nmsMapList.stream()
                .filter(x -> x.get("video_url") != null)
                .map(x -> {
                    FileUrlMappingDO fileUrlMappingDO = new FileUrlMappingDO();
                    fileUrlMappingDO.setStorageUri(x.get("video_url").toString());
                    fileUrlMappingDO.setOriginName(x.get("name").toString());
                    return fileUrlMappingDO;
                })
                .collect(Collectors.toList());
        return DownLoadMediaVO.builder()
                .originVideos(originVideos)
                .recordVideos(recordVideos)
                .build();
    }

    @Override
    public List<String> getFileIdListByRecordId(Integer recordId) {
        return baseMapper.getFileIdListByRecordId(recordId);
    }

    @Override
    public Integer getVideoNum(Integer recordId) {
        return baseMapper.getVideoNum(recordId);
    }

    @Override
    public RestRes updateVideoName(Long id, String videoName) {
        MissionVideoEntity missionVideoEntity = new MissionVideoEntity();
        missionVideoEntity.setName(videoName);
        this.update(missionVideoEntity, new QueryWrapper<MissionVideoEntity>().eq("id", id));
        return RestRes.ok();
    }

    @Override
    public List<MissionRecordsEntity> getMissionRecords(String startTime, String endTime) {
        return baseMapper.getMissionRecords(startTime, endTime);
    }

    @Override
    public List<Map> getInspect(List<Integer> missionRecordsIds) {
        return baseMapper.getInspect(missionRecordsIds);
    }

    @Override
    public RestRes createRelay(Integer mode, String inUrl, String outUrl, Boolean disableAudio, String host, String comment) {
        String token = nodeMediaUtil.getToken();
        NmsRes res = nodeMediaUtil.createRelay(mode, inUrl, outUrl, disableAudio, token, geoaiUosProperties.getIptv().getUrl(), comment);
        if (res.getCode() == 200) {
            //保存或更新relay到数据库

            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_CREATE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_CREATE.getContent()));
    }


    /**
     * 获取视频流的appName
     *
     * @param rtmpUrl
     * @return
     */
    @Deprecated
    private String getAppName(String rtmpUrl) {
//        String rtmpUrl = nestService.getRtmpUrlByMissionId(missionId);
        String appName = "";
        if (ToolUtil.isNotEmpty(rtmpUrl)) {
            if (rtmpUrl.startsWith(DataConstant.HTTP)) {
//                            appName = rtmpUrl.substring(rtmpUrl.lastIndexOf("/",rtmpUrl.lastIndexOf("/")-1));
                //腾讯云推流，通过nms转推到42服务器，appName采用tencent标识
                if (rtmpUrl.contains("iflyer360") || rtmpUrl.contains("guihuao")) {
                    appName = "/tencent" + rtmpUrl.substring(rtmpUrl.lastIndexOf("/"), rtmpUrl.lastIndexOf("."));
                } else {
                    appName = rtmpUrl.substring(rtmpUrl.lastIndexOf("/", rtmpUrl.lastIndexOf("/") - 1), rtmpUrl.lastIndexOf("."));
                }
            } else if (rtmpUrl.startsWith(DataConstant.RTMP)) {
                appName = rtmpUrl.substring(rtmpUrl.lastIndexOf("/", rtmpUrl.lastIndexOf("/") - 1), rtmpUrl.lastIndexOf("?"));
            } else {
                throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PLEASE_CHANGE_THE_ADDRESS_TO_THE_CORRECT_ONE.getContent()));
            }
        }
        return appName;
    }

    @Override
    public String getInspectLatLng(MissionVideoEntity missionVideo) {
        log.debug("给录像文件【{}】写经纬度信息", missionVideo.getName());
        try {
            LocalDateTime createTime = missionVideo.getCreateTime();
            Integer missionRecordsId = missionVideo.getMissionRecordsId();
            String videoUrl = missionVideo.getVideoUrl();
            // UrlDecode
            videoUrl = URLDecoder.decode(videoUrl, CharsetUtil.CHARSET_UTF_8);
            missionVideo.setVideoUrl(videoUrl);
            List<NestAndAirEntity> nestAndAirEntities = mongoNestAndAirService.findByMissionRecordsId(missionRecordsId);
            if (nestAndAirEntities.size() > 0) {
                JSONObject jsonObject = new JSONObject();
                for (NestAndAirEntity nestAndAirEntity : nestAndAirEntities) {
                    Map<String, Object> msg = nestAndAirEntity.getMsg();
                    LocalDateTime saveDateTime = nestAndAirEntity.getSaveDateTime();
                    Duration duration = Duration.between(createTime, saveDateTime);
                    long seconds = duration.getSeconds();
                    Object object = msg.get("aircraftMsg");
                    //适配大疆航线
                    String latLngStr = "";
                    if(object instanceof DJIAircraftInfoOutDTO){
                        latLngStr = getObjectValue(object, "longitude") + "," + getObjectValue(object, "latitude") + "," + getObjectValue(object, "elevation");
                    }else{
                        latLngStr = getObjectValue(object, "lng") + "," + getObjectValue(object, "lat") + "," + getObjectValue(object, "alt");
                    }
                    jsonObject.put(String.valueOf(seconds), latLngStr);
                }
                String srtJsonName = videoUrl.substring(0, videoUrl.lastIndexOf(".")) + ".json";
                String srtJsonFile = PartUploadFileAsync.writeFileContext(jsonObject, srtJsonName);
                missionVideo.setSrtUrl(srtJsonFile);
                updateById(missionVideo);
                log.info("录像文件【{}】写经纬度信息成功", missionVideo.getName());
                return srtJsonFile;
            } else {
                log.info("mongo中未匹配到相关数据,录像文件【{}】未写入经纬度信息", missionVideo.getName());
            }
        } catch (Exception e) {
            log.error("给录像文件【{}】写经纬度信息失败:{}", missionVideo.getName(), e.getMessage());
        }
        return null;
    }


    private void setVideoDetail(IPage<MissionVideoEntity> totalPage) {
        List<MissionVideoEntity> missionVideoEntities = totalPage.getRecords();
        for (MissionVideoEntity missionVideoEntity : missionVideoEntities) {
            try {
                if (ToolUtil.isNotEmpty(missionVideoEntity.getVideoThumbnail())) {
                    missionVideoEntity.setVideoThumbnail(URLEncoder.encode(missionVideoEntity.getVideoThumbnail(), "utf-8").replace("+", "%20"));
                }
                if(StringUtils.hasText(missionVideoEntity.getVideoUrl())){
                    missionVideoEntity.setVideoUrl(URLEncoder.encode(missionVideoEntity.getVideoUrl(), "utf-8").replace("+", "%20"));
                }
                dataCenterService.setFirstPoint(missionVideoEntity);
                dataCenterService.setSrtJson(missionVideoEntity);
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }
        totalPage.setRecords(missionVideoEntities);
    }


    /**
     * 结束录屏操作
     *
     * @param videoId
     * @param appName
     * @return
     */
    private void appFinishCapture(Long videoId, String appName) {
        log.info("准备结束录像");
        String token = nodeMediaUtil.getToken();
        // 结束录像
        NmsRes res = nodeMediaUtil.finishRecord(appName, token, geoaiUosProperties.getIptv().getUrl());
        if (res.getCode().equals(nodeMediaUtil.CODE_SUCCESS)) {
            MissionVideoEntity missionVideoEntity = new MissionVideoEntity();
            missionVideoEntity.setId(videoId);
            missionVideoEntity.setRecordStatus(MissionConstant.MissionVideoRecordStatus.FINISH_RECORD);
            this.updateById(missionVideoEntity);
            log.info("【{}】结束录屏,写入坐标", appName);
            executorService.execute(() -> getInspectLatLng(videoId));
        }
        log.info("【{}】结束录屏失败,视频id:{}", appName, videoId);
    }


    /**
     * app创建录像任务
     *
     * @param appName
     * @param host
     * @param token
     * @param missionId
     * @param recordId
     * @param deviceId
     */
    private void createAppCapture(String appName, String host, String token, Integer missionId, Integer recordId, String deviceId) {
        MissionEntity missionEntity = missionService.getById(missionId);
        String missionName = missionEntity.getName();
        String filename = "CAPTURE_" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        String videoName = filename + ".flv";
        String screenName = filename + ".jpg";
        String missionPath = String.format("%s/%s/%s/", deviceId, missionId, recordId);
        //查询该架次任务已记录的录像文件数
        Integer count = this.baseMapper.countByRecordId(recordId);
        if (null == count) {
            count = 0;
        }
        //默认最大35分钟录像时长
        log.info("开始异步创建录像任务");
        IptvConfig iptv = geoaiUosProperties.getIptv();
        nodeMediaUtil.creatRecordReact(iptv.getPath() + "/" + missionPath, videoName, 35 * 60, appName, token, host);
        // 已存在录像任务--400，流不存在--404
        log.info("架次【{}】第{}个录像创建成功，路径:{},名称:{}", missionName, count + 1, missionPath, videoName);
        // 创建录屏成功，截图做视频封面
        String videoThumbnail = null;
        NmsRes resScreen = nodeMediaUtil.screenshot(iptv.getPath() + "/" + missionPath, screenName, appName, token, host);
        StoreConfig store = geoaiUosProperties.getStore();
        if (Objects.equals(resScreen.getCode(), nodeMediaUtil.CODE_SUCCESS)) {
            log.info("架次【{}】第{}个封面创建成功,路径:{},名称:{}", missionName, count + 1, missionPath, screenName);
            videoThumbnail = store.getVideoPath() + missionPath + screenName;
        } else {
            log.error("架次【{}】第{}个封面创建失败，原因:", missionName, count + 1, resScreen.getError());
        }
        //保存录像实体入库
        MissionVideoEntity missionVideoEntity = new MissionVideoEntity();
        missionVideoEntity.setMissionId(missionId);
        missionVideoEntity.setMissionRecordsId(recordId);
        missionVideoEntity.setOrgCode(missionEntity.getOrgCode());
        missionVideoEntity.setName(videoName);
        missionVideoEntity.setVideoUrl(store.getVideoPath() + missionPath + videoName);
        missionVideoEntity.setVideoThumbnail(videoThumbnail);
        missionVideoEntity.setType(MissionConstant.MissionVideoType.RECORD_VIDEO);
        missionVideoEntity.setRecordStatus(MissionConstant.MissionVideoRecordStatus.NO_FINISH_RECORD);
        this.save(missionVideoEntity);
        log.info("第{}个录像记录入库", count + 1);
        //TODO 创建录频成功的时候去保存消息的日志
    }

}
