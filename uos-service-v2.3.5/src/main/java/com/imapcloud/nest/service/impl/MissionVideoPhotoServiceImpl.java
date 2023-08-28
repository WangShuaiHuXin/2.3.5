package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geoai.common.core.constant.SymbolConstants;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.core.util.DateUtils;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.async.PartUploadFileAsync;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import com.imapcloud.nest.mapper.MissionVideoPhotoMapper;
import com.imapcloud.nest.model.DataProblemEntity;
import com.imapcloud.nest.model.MissionPhotoEntity;
import com.imapcloud.nest.model.MissionVideoEntity;
import com.imapcloud.nest.model.MissionVideoPhotoEntity;
import com.imapcloud.nest.service.DataProblemService;
import com.imapcloud.nest.service.MissionPhotoService;
import com.imapcloud.nest.service.MissionVideoPhotoService;
import com.imapcloud.nest.service.MissionVideoService;
import com.imapcloud.nest.utils.DownLoadZipUtil;
import com.imapcloud.nest.utils.SSHHelper;
import com.imapcloud.nest.utils.ToolUtil;
import com.imapcloud.nest.utils.WebSocketRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.manager.dataobj.in.VideoFrameExtractionInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.VideoExtractionStartOutDO;
import com.imapcloud.nest.v2.manager.feign.VideoServiceClient;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.manager.sql.MissionPhotoManager;
import com.imapcloud.nest.v2.manager.sql.MissionVideoPhotoManager;
import com.imapcloud.nest.v2.service.impl.FileCallbackHandleServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * <p>
 * 视屏抽帧的图片 服务实现类
 * </p>
 *
 * @author hc
 * @since 2021-06-30
 */
@Slf4j
@Service
public class MissionVideoPhotoServiceImpl extends ServiceImpl<MissionVideoPhotoMapper, MissionVideoPhotoEntity> implements MissionVideoPhotoService {
    @Resource
    private SSHHelper sshHelper;
    @Autowired
    @Lazy
    private MissionVideoService missionVideoService;
    //    private Map<String,Integer> missionVideoPhotoMap = new HashMap<>();
    @Autowired
    private MissionPhotoService missionPhotoService;
    @Autowired
    private DataProblemService dataProblemService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private FileManager fileManager;

    @Resource
    private VideoServiceClient videoServiceClient;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private MissionVideoPhotoManager missionVideoPhotoManager;

    private static final Map<String, VideoFrameExtraction> VIDEO_FRAME_EXTRACTION_MAP = new ConcurrentHashMap<>();

    enum ExtractionState {
        /**
         * 已激活
         */
        ACTIVATED,
        /**
         * 已完成
         */
        COMPLETING,
        /**
         * 已取消
         */
        CANCELING
    }

    @Data
    static class VideoFrameExtraction {
        private String extractionId;
        private int totalCount;
        // 状态：0 ==> 运行中；1 ==> 已完成；2 ==> 已取消；
        private volatile int state;
        // 以下是缓存信息
        private String notifyChannelId;
        private Integer missionVideoId;
        private String videoName;
        private Integer seconds;
        private LocalDateTime extractTime;
        private String orgCode;
        private Integer missionId;
        private Integer missionRecordsId;
        private Map<String, String> srtGps;
        /**
         * 计数用
         */
        private final AtomicInteger counts = new AtomicInteger(0);
    }

    private void doStartExtractPhoto(MissionVideoEntity missionVideoEntity, Integer seconds, String extractTime, String videoName) {
        Integer id = missionVideoEntity.getId().intValue();
        LocalDateTime localDateTime = LocalDateTime.parse(extractTime, DateUtils.DATE_TIME_FORMATTER_OF_CN);
        log.info("#MissionVideoPhotoServiceImpl.putPhotoAnsy# missionVideoId={}, extractTimeStr={}", id, extractTime);
        Integer missionId = missionVideoEntity.getMissionId();
        Integer missionRecordsId = missionVideoEntity.getMissionRecordsId();
        String videoUrl = missionVideoEntity.getVideoUrl();
        String orgCode = missionVideoEntity.getOrgCode();
        VideoFrameExtractionInDO body = new VideoFrameExtractionInDO();
        body.setVideoUri(videoUrl);
        body.setAutoThumbnail(true);
        body.setIntervalMills(seconds * 1000);

        // 防止抽帧过快，此时map中还无缓存信息
        body.setDelayMills(2000);
        body.setStartTime(0);
        Result<VideoExtractionStartOutDO> result = videoServiceClient.startVideoFrameExtraction(body);
        log.info("创建抽帧任务信息 ==> {}", result);
        if (!result.isOk()) {
            if (Objects.equals(result.getCode(), "10714")) {
                // TODO 国际化
                throw new BizException("视频抽帧超出系统限制，请稍后重试！");
            }
            // TODO 国际化
            throw new BizException("视频抽帧任务开启失败");
        }
        // 正常抽帧
        VideoExtractionStartOutDO data = result.getData();
        VideoFrameExtraction videoFrameExtraction = new VideoFrameExtraction();
        videoFrameExtraction.setExtractionId(data.getExtractionId());
        videoFrameExtraction.setTotalCount(data.getEstimatedCounts());
        videoFrameExtraction.setMissionVideoId(id);
        videoFrameExtraction.setVideoName(videoName);
        videoFrameExtraction.setSeconds(seconds);
        videoFrameExtraction.setExtractTime(localDateTime);
        videoFrameExtraction.setOrgCode(orgCode);
        videoFrameExtraction.setMissionId(missionId);
        videoFrameExtraction.setMissionRecordsId(missionRecordsId);
        videoFrameExtraction.setSrtGps(getSrtGpsInfo(missionVideoEntity.getSrtUrl()));
        String notifyChannelId = videoFrameExtraction.getMissionVideoId() + SymbolConstants.MINUS +
                videoFrameExtraction.getExtractTime().format(DateUtils.DATE_TIME_FORMATTER);
        videoFrameExtraction.setNotifyChannelId(notifyChannelId);
        VIDEO_FRAME_EXTRACTION_MAP.put(videoFrameExtraction.getExtractionId(), videoFrameExtraction);
    }

    private Map<String, String> getSrtGpsInfo(String srtUrl) {
        log.info("#MissionVideoPhotoServiceImpl.doStartExtractPhoto# srtUrl={}", srtUrl);
        if (StringUtils.hasText(srtUrl)) {
            String srtJson = this.getSrtJson(srtUrl);
            log.info("#MissionVideoPhotoServiceImpl.doStartExtractPhoto# srtJson={}", srtJson);
            if (StringUtils.hasText(srtJson)) {
                try {
                    return objectMapper.readValue(srtJson, new TypeReference<Map<String, String>>() {
                    });
                } catch (JsonProcessingException e) {
                    log.error("解析srt位置信息错误", e);
                }
            }
        }
        return Collections.emptyMap();
    }

    @Override
    public void handleExtractingResult(FileCallbackHandleServiceImpl.VideoFrameResult videoFrameResult) {
        String notifyId = videoFrameResult.getNotifyId();
        VideoFrameExtraction videoFrameExtraction = VIDEO_FRAME_EXTRACTION_MAP.get(notifyId);
        if (Objects.isNull(videoFrameExtraction)) {
            log.warn("抽帧任务缓存信息为空，忽略本次通知信息 ==> {}", videoFrameResult);
            return;
        }
        // 已取消时，忽略本次推送
        if (ExtractionState.CANCELING.ordinal() == videoFrameExtraction.getState()) {
            log.warn("当前抽帧任务已取消，忽略本次通知消息 ==> {}", videoFrameResult);
            return;
        }
        int imageCounts = videoFrameExtraction.getCounts().incrementAndGet();
        // 数量一致，标识抽帧完成
        if (imageCounts == videoFrameExtraction.getTotalCount()) {
            videoFrameExtraction.setState(ExtractionState.COMPLETING.ordinal());
        }
        // 记录数据库
        MissionVideoPhotoEntity missionVideoPhotoEntity = new MissionVideoPhotoEntity();
        missionVideoPhotoEntity.setMissionId(videoFrameExtraction.getMissionId());
        missionVideoPhotoEntity.setMissionRecordsId(videoFrameExtraction.getMissionRecordsId());
        missionVideoPhotoEntity.setExtractTime(videoFrameExtraction.getExtractTime());
        missionVideoPhotoEntity.setSpaceTime(videoFrameExtraction.getSeconds());
        missionVideoPhotoEntity.setOrgCode(videoFrameExtraction.getOrgCode());
        missionVideoPhotoEntity.setSeconds(videoFrameResult.getTimestamp() / 1000);
        missionVideoPhotoEntity.setPhotoUrl(videoFrameResult.getImageUri());
        missionVideoPhotoEntity.setThumbnailUrl(videoFrameResult.getThumbUri());
        missionVideoPhotoEntity.setName(videoFrameResult.getImageName());
        //设置经纬度
        if (!CollectionUtils.isEmpty(videoFrameExtraction.getSrtGps())) {
            String gps = videoFrameExtraction.getSrtGps().get(missionVideoPhotoEntity.getSeconds().toString());
            if (StringUtils.hasText(gps)) {
                String[] split = gps.split(",");
                if (split.length > 1) {
                    double lng = Double.parseDouble(split[0]);
                    double lat = Double.parseDouble(split[1]);
                    missionVideoPhotoEntity.setLatitude(lat);
                    missionVideoPhotoEntity.setLongitude(lng);
                }
            }
        }
        this.save(missionVideoPhotoEntity);
        // websocket通知
        doNotifyProgress(videoFrameExtraction);
        // 任务已完成或已取消
        if (ExtractionState.ACTIVATED.ordinal() != videoFrameExtraction.getState()) {
            VIDEO_FRAME_EXTRACTION_MAP.remove(notifyId);
        }
    }

    private void doCancelExtracting(@NonNull VideoFrameExtraction videoFrameExtraction) {
        if (ExtractionState.COMPLETING.ordinal() == videoFrameExtraction.getState()) {
            log.warn("视频抽帧已完成，忽略取消操作");
            return;
        }
        videoFrameExtraction.setState(ExtractionState.CANCELING.ordinal());
        log.info("#MissionVideoPhotoServiceImpl.doCancelExtracting# missionVideoId={}, 抽帧已取消", videoFrameExtraction.getMissionVideoId());
        // websocket通知
        doNotifyProgress(videoFrameExtraction);
        // 删除数据库数据
        LambdaQueryWrapper<MissionVideoPhotoEntity> queryWrapper = Wrappers.lambdaQuery(MissionVideoPhotoEntity.class).eq(MissionVideoPhotoEntity::getExtractTime, videoFrameExtraction.getExtractTime());
        List<MissionVideoPhotoEntity> list = this.list(queryWrapper);
        if (!CollectionUtils.isEmpty(list)) {
            List<String> collect = list.stream().map(MissionVideoPhotoEntity::getPhotoUrl).collect(Collectors.toList());
            fileManager.deleteFiles(collect);
        }
        this.remove(queryWrapper);
        // 任务已完成或已取消
        if (ExtractionState.ACTIVATED.ordinal() != videoFrameExtraction.getState()) {
            VIDEO_FRAME_EXTRACTION_MAP.remove(videoFrameExtraction.getExtractionId());
        }

    }

    private void doNotifyProgress(VideoFrameExtraction videoFrameExtraction) {
        Map<String, Object> map = new HashMap<>(6);
        map.put("id", videoFrameExtraction.getMissionVideoId());
        map.put("extractTime", videoFrameExtraction.getExtractTime().format(DateUtils.DATE_TIME_FORMATTER_OF_CN));
        map.put("videoName", videoFrameExtraction.getVideoName());
        map.put("count", videoFrameExtraction.getCounts().get());
        map.put("state", videoFrameExtraction.getState());
        map.put("totalCount", videoFrameExtraction.getTotalCount());
        String message = WebSocketRes.ok().topic(WebSocketTopicEnum.VIDEO_PHOTO).data(map).toJSONString();
        ChannelService.sendMessageByType4Channel(videoFrameExtraction.getNotifyChannelId(), message);
    }

    @Override
    public boolean extractPhoto(Integer id, Integer seconds, String extractTime, String videoName) {
        MissionVideoEntity missionVideoEntity = missionVideoService.getById(id);
        if (missionVideoEntity == null) {
            return false;
        }
        Boolean checkFileExists = fileManager.checkFileExists(missionVideoEntity.getVideoUrl());
        if (!Objects.equals(checkFileExists, Boolean.TRUE)) {
            // TODO 国际化
            throw new BizException("当前视频文件无法进行抽帧处理");
        }
        doStartExtractPhoto(missionVideoEntity, seconds, extractTime, videoName);
//        executorService.execute(() -> putPhotoAnsy(missionVideoEntity, seconds, extractTime,videoName));
        return true;
    }

//    private void putPhotoAnsy(MissionVideoEntity missionVideoEntity, Integer seconds, String extractTime,String videoName) {
//        Integer id = missionVideoEntity.getId().intValue();
//
//        LocalDateTime localDateTime = LocalDateTime.parse(extractTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        String extractTimeStr = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
//        log.info("#MissionVideoPhotoServiceImpl.putPhotoAnsy# missionVideoId={}, extractTimeStr={}", id, extractTimeStr);
//        Integer missionId = missionVideoEntity.getMissionId();
//        Integer missionRecordsId = missionVideoEntity.getMissionRecordsId();
//        String videoUrl = missionVideoEntity.getVideoUrl();
//        String srtUrl = missionVideoEntity.getSrtUrl();
//        String unitId = missionVideoEntity.getOrgCode();
//        JSONObject srtObject = null;
//        if (srtUrl != null) {
//            log.info("#MissionVideoPhotoServiceImpl.putPhotoAnsy# srtUrl={}", srtUrl);
//            String srtJson = this.getSrtJson(srtUrl);
//            if (srtJson != null) {
//                srtObject = JSONObject.parseObject(srtJson);
//            }
//        }
//
//        log.info("#MissionVideoPhotoServiceImpl.putPhotoAnsy# missionVideoId={}, videoUrl={}", id, videoUrl);
//
//        FFmpegFrameGrabber grabber = null;
//        try(InputStream inputSteam = fileManager.getInputSteam(videoUrl)){
//            if(Objects.isNull(inputSteam)){
//                log.warn("视频文件[{}]不存在，无法进行抽帧操作", videoUrl);
//                return;
//            }
////            grabber = FFmpegFrameGrabber.createDefault(videoUrl);
//            grabber = new FFmpegFrameGrabber(inputSteam, 0);
//            // 图片存储位置
//            String uuid = BizIdUtils.snowflakeIdStr();
//            String targetFilePath = String.format("%s%s/", UploadTypeEnum.MISSION_VIDEO_PHOTO_PATH.getPath(), uuid);
//            String targetFilePathThumbnail = String.format("%s%s/", UploadTypeEnum.MISSION_VIDEO_PHOTO_THUMBNAIL_PATH.getPath(), uuid);
//            log.info("#MissionVideoPhotoServiceImpl.putPhotoAnsy# targetFilePath={}, thumbnail={}", targetFilePath, targetFilePathThumbnail);
//            grabber.start();
//            // 视频总帧数
//            int videoLength = grabber.getLengthInFrames();
//            double frameRate = grabber.getFrameRate(); // 此视频帧率
//            long duration = grabber.getLengthInTime() / (1000 * 1000); // 此视频时长(s/秒)
//            long totalCount = duration / seconds; //照片总张数
//            Frame frame = null;
//
//            //完成状态
//            String videoTimeStr = id + "-" +extractTimeStr; //missionVideoPhotoMap中key值
//            // 图片的类型
//            String imageMat = "jpg";
//            Map<String, Object> map = new HashMap<>(2);
//            map.put("extractTime", extractTime);
//            log.info("开始抽帧");
//            // 第几张照片
//            int numPic = 1;
//            int k = (int) (numPic * seconds * frameRate);
//            StoreConfig store = geoaiUosProperties.getStore();
//            while (k < videoLength) {
//
//                Integer state = missionVideoPhotoMap.get(videoTimeStr);
//                if (state==null|| state == activiting) {
//                    grabber.setFrameNumber(k);
//                    // 取一帧
//                    frame = grabber.grabFrame();
//                    while (frame == null || frame.image == null) {
//                        frame = grabber.grabFrame();
//                    }
//                    // 视频旋转度
//                    String rotate = grabber.getVideoMetadata("rotate");
//                    Java2DFrameConverter converter = new Java2DFrameConverter();
//                    // 绘制图片
//                    BufferedImage bi = converter.getBufferedImage(frame);
//                    if (rotate != null) {
//                        // 旋转图片
//                        bi = ThumbnailUtils.rotate(bi, Integer.parseInt(rotate));
//                    }
//                    //构造实体
//                    MissionVideoPhotoEntity missionVideoPhotoEntity = new MissionVideoPhotoEntity();
//                    missionVideoPhotoEntity.setMissionId(missionId);
//                    missionVideoPhotoEntity.setMissionRecordsId(missionRecordsId);
//                    missionVideoPhotoEntity.setExtractTime(localDateTime);
//                    missionVideoPhotoEntity.setSpaceTime(seconds);
//                    missionVideoPhotoEntity.setOrgCode(unitId);
//                    //第多少秒
//                    int num = numPic * seconds;
//                    missionVideoPhotoEntity.setSeconds(num);
//                    //设置经纬度
//                    if (srtObject != null) {
//                        String srt = srtObject.getString(String.valueOf(num));
//                        if (srt != null) {
//                            String[] split = srt.split(",");
//                            double lng = Double.parseDouble(split[0]);
//                            double lat = Double.parseDouble(split[1]);
//                            missionVideoPhotoEntity.setLatitude(lat);
//                            missionVideoPhotoEntity.setLongitude(lng);
//                        }
//                    }
//                    //写入图片,设置图片的完整路径和图片名
//                    String photoName = intToDateString(num) + "." + imageMat;
//                    String imagePath = targetFilePath + photoName;
//                    String imagePathThumbnail = targetFilePathThumbnail + photoName;
//                    log.info("#MissionVideoPhotoServiceImpl.putPhotoAnsy# targerFilePath={}, imagePath={}, imagePathThumbnail={}", targetFilePath, imagePath, imagePathThumbnail);
//
//                    try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
//                        ImageIO.write(bi, imageMat, os);
//                        InputStream is = new ByteArrayInputStream(os.toByteArray());
////                        MinIoUnit.putObject(imagePath, is);
////                        //输出缩略图
////                        InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
////                        DrawImageUtils.thumbnailImage(inputStream, imagePathThumbnail, new BigDecimal("0.6"));
//                        // 上传抽帧图片
//                        CommonFileInDO commonFileInDO = new CommonFileInDO();
//                        commonFileInDO.setFileName(photoName);
//                        commonFileInDO.setInputStream(is);
//                        Optional<FileStorageOutDO> optional = uploadManager.uploadFile(commonFileInDO);
//                        if(optional.isPresent()){
//                            // 生成缩略图
//                            String imageUrl = optional.get().getStoragePath() + SymbolConstants.SLASH_LEFT + optional.get().getFilename();
//                            String thumbnailUrl = fileManager.generateThumbnail(imageUrl, 0.6D, false);
//                            missionVideoPhotoEntity.setPhotoUrl(imageUrl);
//                            missionVideoPhotoEntity.setThumbnailUrl(thumbnailUrl);
//                        }
//                    }
//                    missionVideoPhotoEntity.setName(photoName);
//                    this.save(missionVideoPhotoEntity);
//                    map.put("id",id);
//                    map.put("videoName",videoName);
//                    map.put("count", numPic);
//                    map.put("state", activiting);
//                    map.put("totalCount",totalCount);
//                    String message = WebSocketRes.ok().topic(WebSocketTopicEnum.VIDEO_PHOTO).data(map).toJSONString();
//                    ChannelService.sendMessageByType4Channel(videoTimeStr, message);
//                    numPic++;
//                    k = (int) (numPic * seconds * frameRate);
//                }else if(state == canceling){
//                    log.info("#MissionVideoPhotoServiceImpl.putPhotoAnsy# missionVideoId={}, 抽帧已取消", id);
//                    map.put("state", canceling);
//                    String message = WebSocketRes.ok().topic(WebSocketTopicEnum.VIDEO_PHOTO).data(map).toJSONString();
//                    ChannelService.sendMessageByType4Channel(id + "-" +extractTimeStr, message);
//                    QueryWrapper<MissionVideoPhotoEntity> missionVideoPhotoEntityQueryWrapper = new QueryWrapper<MissionVideoPhotoEntity>().eq("extract_time", extractTime);
//                    List<MissionVideoPhotoEntity> list = this.list(missionVideoPhotoEntityQueryWrapper);
//                    if(list.size()>0){
//                        List<String> collect = list.stream().map(MissionVideoPhotoEntity::getPhotoUrl).collect(Collectors.toList());
//                        fileManager.deleteFiles(collect);
//                    }
//                    this.remove(missionVideoPhotoEntityQueryWrapper);
//                    break;
//                }else{
//                    break;
//                }
//            }
//            Integer state = missionVideoPhotoMap.get(videoTimeStr);
//            if(state==null || !state.equals(canceling)){
//                log.info("#MissionVideoPhotoServiceImpl.putPhotoAnsy# missionVideoId={}, 抽帧已完成", id);
//                map.put("state", completing);
//                String message = WebSocketRes.ok().topic(WebSocketTopicEnum.VIDEO_PHOTO).data(map).toJSONString();
//                ChannelService.sendMessageByType4Channel(videoTimeStr, message);
//            }
//            missionVideoPhotoMap.remove(videoTimeStr);
//
//        } catch (IOException e) {
//            log.error("#MissionVideoPhotoServiceImpl.putPhotoAnsy# missionVideoId={}, 视频信息帧数处理发生异常", id, e);
//        } finally {
//            if (grabber != null) {
//                try {
//                    grabber.stop();
//                } catch (FrameGrabber.Exception e) {
//                    log.error("#MissionVideoPhotoServiceImpl.putPhotoAnsy# missionVideoId={}, 视频抽帧关闭异常", id, e);
//                }
//            }
//        }
//    }

    @Override
    public List<Map> getAllPhotos(Integer missionRecordsId) {
        List<MissionVideoPhotoEntity> missionVideoPhotoEntities = this.list(new QueryWrapper<MissionVideoPhotoEntity>().eq("mission_records_id", missionRecordsId).eq("deleted", 0).orderByDesc("extract_time").orderByAsc("create_time"));
        List<Map> list = new ArrayList<>();
        if (missionVideoPhotoEntities.size() > 0) {
            Map<LocalDateTime, List<MissionVideoPhotoEntity>> collect = missionVideoPhotoEntities.stream().collect(Collectors.groupingBy(MissionVideoPhotoEntity::getExtractTime));
            Iterator<Map.Entry<LocalDateTime, List<MissionVideoPhotoEntity>>> iterator = collect.entrySet().iterator();
            while (iterator.hasNext()) {
                Map map = new HashMap();
                Map.Entry<LocalDateTime, List<MissionVideoPhotoEntity>> next = iterator.next();
                LocalDateTime key = next.getKey();
                List<MissionVideoPhotoEntity> value = next.getValue();
                map.put("extractTime", key);
                map.put("list", value);
                map.put("spaceTime", value.get(0).getSpaceTime());
                list.add(map);
            }
        }
        return list;
    }

    @Override
    public void cancel(Integer id, String extractTime) {
//        //String key = getKey(id);
//        LocalDateTime localDateTime = LocalDateTime.parse(extractTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        String extractTimeStr = localDateTime.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
//        String videoTimeStr = id + "-" +extractTimeStr; //missionVideoPhotoMap中key值
//        missionVideoPhotoMap.put(videoTimeStr,canceling);
//        //redisService.hSet(key, extractTimeStr, canceling);
        LocalDateTime localDateTime = LocalDateTime.parse(extractTime, DateUtils.DATE_TIME_FORMATTER_OF_CN);
        String notifyChannelId = id + SymbolConstants.MINUS + localDateTime.format(DateUtils.DATE_TIME_FORMATTER);
        Optional<VideoFrameExtraction> optional = VIDEO_FRAME_EXTRACTION_MAP.values()
                .stream()
                .filter(r -> Objects.equals(r.getNotifyChannelId(), notifyChannelId))
                .findFirst();
        if (optional.isPresent()) {
            doCancelExtracting(optional.get());
            return;
        }
        // TODO 国际化
        throw new BizException("视频抽帧任务已完成或已取消");
    }

    @Resource
    private MissionPhotoManager missionPhotoManager;



    @Override
    public boolean del(List<Integer> ids) {
        List<MissionPhotoEntity> missionPhotos = missionPhotoService.list(new QueryWrapper<MissionPhotoEntity>().in("video_photo_id", ids));
        if (missionPhotos.size() > 0) {
            List<Long> photoIds = missionPhotos.stream().map(MissionPhotoEntity::getId).collect(Collectors.toList());

            missionPhotoManager.updateDelete(photoIds);
            dataProblemService.remove(new QueryWrapper<DataProblemEntity>().in("data_id", photoIds).ne("problem_source", 8).ne("problem_source", 17));
        }
        missionVideoPhotoManager.updateDelete(ids);
        return true;
    }

    @Override
    public void downlandPic(String ids, HttpServletResponse response) {
        if (ToolUtil.isEmpty(ids)) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DOWNLOAD_FAILED_PLEASE_SELECT_THE_CORRECT_PICTURE.getContent()));
        }
        // 选择部分图片下载
        List<String> photoIdStr = Arrays.asList(ids.split(","));
        List<Integer> idList = photoIdStr.stream().map(e -> Integer.parseInt(e)).collect(Collectors.toList());
        List<MissionVideoPhotoEntity> missionVideoPhotoEntities = this.list(new QueryWrapper<MissionVideoPhotoEntity>().in("id", idList));
        if (ToolUtil.isNotEmpty(missionVideoPhotoEntities)) {
            // 人工上传
            String fileName = "视屏抽帧照片压缩包_" + System.currentTimeMillis() + ".zip";
            DownLoadZipUtil.downloadUploadVideoPhoto(fileName, missionVideoPhotoEntities, response);
        } else {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DOWNLOAD_FAILED_THE_CORRESPONDING_PICTURE_IS_NOT_FOUND.getContent()));
        }

    }

    @Override
    public boolean extractSrt(Integer videoId) {
        MissionVideoEntity missionVideoEntity = missionVideoService.getById(videoId);
        //映射的地址
        String videoUrl = missionVideoEntity.getVideoUrl();
        String name = videoUrl.substring(0, videoUrl.lastIndexOf("."));
        String srtUrl = missionVideoEntity.getSrtUrl();
        //./ffmpeg -i ../南沙涌06.MOV -map 0:s:0 subs.srt
        String sshStr = geoaiUosProperties.getStore().getVideoFfmpegPath() + "ffmpeg -i " + videoUrl + " -map 0:s:0 " + srtUrl;
        log.info("ffmpeg命令:" + sshStr);
        sshHelper.exector(sshStr);
        if (fileManager.checkFileExists(srtUrl)) {
            String s = PartUploadFileAsync.convertSrtJson(srtUrl);
            missionVideoEntity.setSrtId(null);
            missionVideoEntity.setSrtFileUrl(srtUrl);
            missionVideoEntity.setSrtUrl(s);
            missionVideoService.updateById(missionVideoEntity);
            log.info("执行ffmpeg命令成功");
            return true;
        } else {
            log.info("执行ffmpeg命令失败");
            return false;
        }
    }


    /**
     * 转换秒数为00-00-00格式
     *
     * @param value
     * @return
     */
    private String intToDateString(int value) {
        int hour = value / 3600;
        int min = value / 60 % 60;
        int second = value % 60;
        String hourString;
        String minString;
        String secondString;
        if (hour < 10) {
            hourString = "0" + hour;
        } else {
            hourString = hour + "";
        }
        if (min < 10) {
            minString = "0" + min;
        } else {
            minString = min + "";
        }
        if (second < 10) {
            secondString = "0" + second;
        } else {
            secondString = second + "";
        }
        return hourString + "-" + minString + "-" + secondString;
    }

    /**
     * 获取srtJson
     *
     * @param jsonUrl
     * @return
     */
    public String getSrtJson(String jsonUrl) {
        try (InputStream inputSteam = fileManager.getInputSteam(jsonUrl)) {
            if (inputSteam != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputSteam));
                return bufferedReader.readLine();
            }
        } catch (IOException ioException) {
            log.error("读取srt json文件时发生错误", ioException);
            return null;
        }
        return null;
    }

    @Override
    public void deleteVideoPhoto(List<Integer> missionRecordIds) {
        baseMapper.deleteByRecordId(missionRecordIds);
    }

    @Override
    public int selectNum(String[] ids, String orgCode) {
        return missionVideoPhotoManager.selectNum(ids, orgCode);
    }

}
