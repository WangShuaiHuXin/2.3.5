package com.imapcloud.nest.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.constant.SymbolConstants;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.core.util.JsonUtils;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.imapcloud.nest.common.constant.DataConstant;
import com.imapcloud.nest.common.constant.MissionConstant;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.convert.MediaFieToPhotoEntityConvert;
import com.imapcloud.nest.convert.MediaFieToVideoEntityConvert;
import com.imapcloud.nest.enums.*;
import com.imapcloud.nest.mapper.MissionPhotoMapper;
import com.imapcloud.nest.model.*;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.pojo.dto.dataProblemDTO.PhotoTagSourceDTO;
import com.imapcloud.nest.pojo.dto.reqDto.DefectMonthListReqDto;
import com.imapcloud.nest.pojo.dto.reqDto.MissionPhotosReqDto;
import com.imapcloud.nest.pojo.dto.reqDto.RecordDto;
import com.imapcloud.nest.pojo.dto.unifyAirLineDto.*;
import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.sdk.listener.DataTranCompleteListenerFactory;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.service.event.UploadMediaEvent;
import com.imapcloud.nest.utils.*;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.FileIOReadException;
import com.imapcloud.nest.v2.common.exception.UosServiceErrorCode;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.common.properties.UploadConfig;
import com.imapcloud.nest.v2.common.utils.DownLoadUtils;
import com.imapcloud.nest.v2.manager.dataobj.FileUrlMappingDO;
import com.imapcloud.nest.v2.manager.dataobj.in.CommonFileInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestAccountOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileStorageOutDO;
import com.imapcloud.nest.v2.manager.nacos.NacosConfigurationService;
import com.imapcloud.nest.v2.manager.rest.FileManager;
import com.imapcloud.nest.v2.manager.rest.UploadManager;
import com.imapcloud.nest.v2.manager.sql.BaseNestAccountManager;
import com.imapcloud.nest.v2.service.*;
import com.imapcloud.nest.v2.service.converter.MissionPhotoPointConverter;
import com.imapcloud.nest.v2.service.dto.out.MissionPhotoPointOutDTO;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.base.BaseManager;
import com.imapcloud.sdk.manager.camera.enums.CameraLensVideoSourceEnum;
import com.imapcloud.sdk.manager.media.entity.CpsUploadFileEntity;
import com.imapcloud.sdk.pojo.BaseResult3;
import com.imapcloud.sdk.pojo.constant.*;
import com.imapcloud.sdk.pojo.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
@Service
@Slf4j
public class MissionPhotoServiceImpl extends ServiceImpl<MissionPhotoMapper, MissionPhotoEntity> implements MissionPhotoService {

    @Resource
    private MissionRecordsService missionRecordsService;

    @Resource
    private MissionPhotoService missionPhotoService;

    @Resource
    private MissionVideoService missionVideoService;

    @Resource
    private MissionService missionService;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private AirLineService airLineService;

    @Resource
    private DownLoadZipUtil downLoadZipUtil;

    @Resource
    private TaskService taskService;

    @Resource
    private StationCheckpointService checkpointService;

    @Autowired
    private SysTaskTagService sysTaskTagService;

    @Autowired
    private StationIdentifyRecordService stationIdentifyRecordService;

    @Autowired
    private StationInfraredRecordService stationInfraredRecordService;

    @Autowired
    private StationDeviceMatchService stationDeviceMatchService;

    @Autowired
    private DefectInfoService defectInfoService;

    @Autowired
    private SysTagService sysTagService;

    @Autowired
    private CommonNestStateService commonNestStateService;

    @Autowired
    private MissionPhotoTagRelService missionPhotoTagRelService;

    @Autowired
    private MissionPhotoTypeRelService missionPhotoTypeRelService;

    @Autowired
    private StationInfraredThresholdService stationInfraredThresholdService;

    @Autowired
    private StationInfraredRecordRectangleService stationInfraredRecordRectangleService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UploadService uploadService;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private ApplicationContext applicationContext;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private BaseNestAccountManager baseNestAccountManager;

    /**
     * 是否使用华为云（Huawei Cloud Machine）主机
     */
    private boolean isUsedHCM() {
        return geoaiUosProperties.getAi().isUseHuawei();
    }

    @Resource
    private DataScenePhotoService dataScenePhotoService;

    @Resource
    private BaseAppService baseAppService;

    @Resource
    private GridMissionService gridMissionService;

    @Resource
    private DataSynService dataSynService;

    @Resource
    private UploadManager uploadManager;

    @Resource
    private FileManager fileManager;
    @Resource
    private MissionMediaErrLogService missionMediaErrLogService;

    /**
     * 同步源数据请求
     */
    @Override
    public RestRes getPhoto(Integer recordId, String nestUuid, String env) {
        MissionRecordsEntity missionRecordsEntity = missionRecordsService.getById(recordId);
        if (missionRecordsEntity == null) {
            log.info("检测不到架次记录，recordId={}", recordId);
            return RestRes.err("检测不到架次记录");
        }
        missionMediaErrLogService.deleteErrLog(recordId);
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
        // 校验是否可以同步源数据
        String execId = missionRecordsEntity.getExecId();
        // 获取无人机标识
        Integer uavWhich = missionRecordsEntity.getUavWhich();

        //G503校验状态
        NestTypeEnum nestTypeEnum = this.baseNestService.getNestTypeByUuidCache(nestUuid);
        if (NestTypeEnum.G503.equals(nestTypeEnum)) {
            NestState nestState = this.commonNestStateService.getNestState(nestUuid, AirIndexEnum.getInstance(uavWhich));
            //G503没有换电池功能
            if (nestState != null && !nestState.getAircraftConnected() && !nestState.getUsbDeviceConnected()) {
                String msg = String.format("G503，%d号无人机或遥控器未连接，请先到控制页面装载电池，打开遥控器", uavWhich);
                log.info("【getPhoto】nestUUid -> {} , recordId -> {} ,{}", nestUuid, recordId, msg);
                RestRes.err(msg);
            }
        }

        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);

        // 查看机巢是否连接,未连接则连接机巢
        if (ToolUtil.isEmpty(cm)) {
            cm = ComponentManagerFactory.getInstance(nestUuid);
        }
        // 再次判断机巢是否连接
        if (ToolUtil.isNotEmpty(cm)) {
            // 查询数据库，获取图片List或视频List，为空则重新全部保存，不为空则比较缺失的图片或视频，补充拿缺失的图片或视频
            List<String> fileIdList = new ArrayList<>();
            // 获取图片
            List<String> photoFileIdList = baseMapper.getFileIdListByRecordId(recordId);
            if (photoFileIdList != null) {
                fileIdList.addAll(photoFileIdList);
            }

            // 获取视频
            List<String> videoFileIdList = missionVideoService.getFileIdListByRecordId(recordId);
            if (videoFileIdList != null) {
                fileIdList.addAll(videoFileIdList);
            }

            if (fileIdList.size() > 0) {
                // 获取cps的多媒体列表
                Map<String, Object> res = getCpsFileList(cm, execId, nestUuid, uavWhich);
                // 比较缺失的图片或视频,并获取图片或视频
                if ((boolean) res.get("success")) {
                    return UploadLostMedia((List<String>) res.get("cpsFileList"), fileIdList, recordId, missionId, execId, nestUuid, missionName, uavWhich, cm, env);
                } else {
                    return RestRes.err(missionName + MessageUtils.getMessageByLang(MessageEnum.GEOAI_UOS_FAILED_TO_GET_MULTIMEDIA_LIST.getContent(), env) + "," + res.get("msg"));
                }
            } else {
                // 重新全部保存
                uploadMediaToServer(execId, recordId, missionId, nestUuid, uavWhich, cm, env);
                return RestRes.ok(missionName + MessageUtils.getMessageByLang(MessageEnum.GEOAI_UOS_UPLOAD_COMMAND_SENT_SUCCESSFULLY_READY_TO_START_UPLOADING_ALL_DATA_TO_THE_SERVER.getContent(), env));
            }

        }
        return RestRes.err(missionName + MessageUtils.getMessageByLang(MessageEnum.GEOAI_UOS_SYNCHRONIZATION_FAILURE_THE_NEST_IS_NOT_CONNECTED.getContent(), env));
    }

    @Override
    public RestRes batchTranData(String nestId, List<Integer> recordIdList) {
        log.info("batchTranData参数，nestId:{},recordIdList:{}", nestId, recordIdList);
        if (Objects.isNull(nestId) || CollectionUtil.isEmpty(recordIdList)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }

        Integer uavWhich = 0;
        MissionRecordsEntity missionRecordsEntity = missionRecordsService.getById(recordIdList.get(0));
        if (Objects.nonNull(missionRecordsEntity)) {
            uavWhich = missionRecordsEntity.getUavWhich();
        }
//        String nestUuid = nestService.getUuidById(Integer.valueOf(nestId));
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        //检测基站是否在传输文件
        NestTypeEnum nestType = baseNestService.getNestTypeByNestIdCache(nestId);
        if (NestTypeEnum.G503.equals(nestType)) {
            MediaStateV2 mediaState1 = commonNestStateService.getMediaStateV2(nestUuid, AirIndexEnum.ONE);
            MediaStateV2 mediaState2 = commonNestStateService.getMediaStateV2(nestUuid, AirIndexEnum.TWO);
            MediaStateV2 mediaState3 = commonNestStateService.getMediaStateV2(nestUuid, AirIndexEnum.THREE);
            if (MediaStatusV2Enum.UPLOADING.getValue().equals(mediaState1.getCurrentState()) ||
                    MediaStatusV2Enum.DOWNLOADING.getValue().equals(mediaState1.getCurrentState()) ||
                    MediaStatusV2Enum.DOWNLOAD_UPLOAD_MEANWHILE.getValue().equals(mediaState1.getCurrentState())) {

                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_CURRENT_BASE_STATION_IS_SYNCHRONIZING_DATA_THE_STATUS_IS.getContent())
                        + "【" + mediaState1.getCurrentState() + "】");
            }
            if (MediaStatusV2Enum.UPLOADING.getValue().equals(mediaState2.getCurrentState()) ||
                    MediaStatusV2Enum.DOWNLOADING.getValue().equals(mediaState2.getCurrentState()) ||
                    MediaStatusV2Enum.DOWNLOAD_UPLOAD_MEANWHILE.getValue().equals(mediaState2.getCurrentState())) {

                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_CURRENT_BASE_STATION_IS_SYNCHRONIZING_DATA_THE_STATUS_IS.getContent())
                        + "【" + mediaState2.getCurrentState() + "】");
            }
            if (MediaStatusV2Enum.UPLOADING.getValue().equals(mediaState3.getCurrentState()) ||
                    MediaStatusV2Enum.DOWNLOADING.getValue().equals(mediaState3.getCurrentState()) ||
                    MediaStatusV2Enum.DOWNLOAD_UPLOAD_MEANWHILE.getValue().equals(mediaState3.getCurrentState())) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_CURRENT_BASE_STATION_IS_SYNCHRONIZING_DATA_THE_STATUS_IS.getContent())
                        + "【" + mediaState3.getCurrentState() + "】");
            }
        } else {
            MediaStateV2 mediaState = commonNestStateService.getMediaStateV2(nestUuid);
            if (MediaStatusV2Enum.UPLOADING.getValue().equals(mediaState.getCurrentState()) ||
                    MediaStatusV2Enum.DOWNLOADING.getValue().equals(mediaState.getCurrentState()) ||
                    MediaStatusV2Enum.DOWNLOAD_UPLOAD_MEANWHILE.getValue().equals(mediaState.getCurrentState())) {

                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_CURRENT_BASE_STATION_IS_SYNCHRONIZING_DATA_THE_STATUS_IS.getContent())
                        + "【" + mediaState.getCurrentState() + "】");
            }
        }

        if (NestTypeEnum.S110_MAVIC3.equals(nestType)) {
            NestState nestState = this.commonNestStateService.getNestState(nestUuid);
            if (!nestState.getRemoteControllerConnected()) {
                return RestRes.err("御3无人机同步数据请先打开遥控器");
            }
        }

        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BATCH_TRAN_DATA, nestUuid);
        redisService.del(redisKey);

        BatchTranDataDto batchTranDataDto = new BatchTranDataDto();
        batchTranDataDto.setLastIndex(-1);
        batchTranDataDto.setRecordIdList(recordIdList);
        boolean set = redisService.set(redisKey, batchTranDataDto);

        if (set) {
            // 开启另一个线程进行同步数据操作
            executorService.execute(() -> startTranDataRunnable(nestUuid));
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BEGIN_SYNCHRONIZATION.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_START_SYNCHRONIZING_SOURCE_DATA.getContent()));
    }

    @Override
    public RestRes cancelBatchTranData(String nestId, Integer recordId) {
//        String nestUuid = nestService.getUuidById(nestId);
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BATCH_TRAN_DATA, nestUuid);
        BatchTranDataDto batchTranDataDto = (BatchTranDataDto) redisService.get(redisKey);
        if (batchTranDataDto != null) {
            Integer currRecordId = batchTranDataDto.currRecordId();
            if (currRecordId != -1) {
                return stopGetMedia(nestUuid, currRecordId);
            }
        }
        if (recordId != null) {
            return stopGetMedia(nestUuid, recordId);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_CANCEL_BATCH_DATA_TRANSFER.getContent()));
    }

    @Override
    public RestRes uploadSrt(String execMissionID, MultipartFile fileData) {
        log.info("上传轨迹文件........execMissionID:" + execMissionID);
        MissionRecordsEntity missionRecordsEntity = missionRecordsService.getByExecId(execMissionID);
        List<MissionVideoEntity> list = missionVideoService.lambdaQuery()
                .eq(MissionVideoEntity::getMissionRecordsId, missionRecordsEntity.getId())
                .eq(MissionVideoEntity::getType, MissionConstant.MissionVideoType.DOWNLOAD_VIDEO)
                .list();
        if (list.size() > 0) {
            MissionVideoEntity missionVideoEntity = list.get(0);
            String gpsJson;
            try (InputStream inputStream = fileData.getInputStream()) {
                gpsJson = parseGpsJsonFromSrt(inputStream, missionVideoEntity.getMediaCreateTime());
            } catch (IOException e) {
                throw new FileIOReadException(UosServiceErrorCode.FILE_IO_READ_ERROR.getI18nMessage(fileData.getOriginalFilename()));
            }
            String gpsStorageUrl = uploadSrtGpsJsonFile(gpsJson, fileData.getOriginalFilename().replace(".srt", ".json"));

            /*String srtTxtName = missionVideoEntity.getId() + "-" + fileData.getOriginalFilename();
            String uploadPath = DataConstant.VIDEO + missionVideoEntity.getMissionId() + "/" + missionVideoEntity.getMissionRecordsId() + "/";
            String srtPath = MinIoUnit.upload(getMinioBucketName(), fileData, srtTxtName, uploadPath);
            log.info("上传轨迹文件地址:" + srtPath);
            JSONObject jsonObject = covertJsonByTxt(missionVideoEntity.getMediaCreateTime(), fileData);
            String videoUrl = missionVideoEntity.getVideoUrl();
            String srtJsonName = videoUrl.substring(0, videoUrl.lastIndexOf(".")) + "-" + missionVideoEntity.getId() + ".json";
            String srtJsonFile = PartUploadFileAsync.writeFileContext(jsonObject, srtJsonName);
            log.info("上传轨迹json地址:" + srtJsonFile);*/

            for (MissionVideoEntity videoEntity : list) {
                videoEntity.setSrtUrl(gpsStorageUrl);
                missionVideoService.updateById(videoEntity);
            }
        }
        log.info("===============写经纬度成功=====================");
        return RestRes.ok();
    }

    private String parseGpsJsonFromSrt(InputStream srtFileInputStream, LocalDateTime createTime) {
        Map<String, String> map = new LinkedHashMap<>();
        try (InputStream inputStream = srtFileInputStream;
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                int i = line.indexOf(",");
                String data = line.substring(i + 1);
                String createTimeStr = line.substring(0, i);
                LocalDateTime srtTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(createTimeStr)), ZoneId.systemDefault());
                if (srtTime.isAfter(createTime)) {
                    Duration duration = Duration.between(createTime, srtTime);
                    long seconds = duration.getSeconds();
                    map.put(String.valueOf(seconds), data);
                }
            }
        } catch (IOException e) {
            log.error("解析CPS飞行轨迹失败");
            throw new BizException("解析CPS飞行轨迹失败");
        }
        // 生成json文件
        return JsonUtils.writeJson(map);
    }

    private String uploadSrtGpsJsonFile(String gpsJson, String gpsJsonFilename) {
        try (InputStream inputStream = new ByteArrayInputStream(gpsJson.getBytes())) {
            CommonFileInDO fileInDO = new CommonFileInDO();
            fileInDO.setInputStream(inputStream);
            fileInDO.setFileMd5(BizIdUtils.simpleUuid());
            fileInDO.setFileName(gpsJsonFilename);
            Optional<FileStorageOutDO> outDO = uploadManager.uploadFile(fileInDO);
            if (outDO.isPresent()) {
                FileStorageOutDO storage = outDO.get();
                return storage.getStoragePath() + SymbolConstants.SLASH_LEFT + storage.getFilename();
            }
        } catch (IOException e) {
            log.error("上传飞行轨迹解析JSON文件");
            throw new BizException("上传飞行轨迹解析JSON文件错误");
        }
        return null;
    }

    private JSONObject covertJsonByTxt(LocalDateTime createTime, MultipartFile fileData) {
        JSONObject jsonObject = new JSONObject();
        try {
            String s = new String(fileData.getBytes());
            String[] split = s.split("\n");
            for (String one : split) {
                int i = one.indexOf(",");
                String data = one.substring(i + 1);
                String createTimestr = one.substring(0, i);
                LocalDateTime srtTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(createTimestr)), ZoneId.systemDefault());
                if (srtTime.isAfter(createTime)) {
                    Duration duration = Duration.between(createTime, srtTime);
                    long seconds = duration.getSeconds();
                    jsonObject.put(String.valueOf(seconds), data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public RestRes checkNestMediaStatus(String nestId) {
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        if (nestUuid == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_NEST_CANNOT_BE_QUERIED.getContent()));
        }
        Integer tran = 0;
        MediaStateV2 mediaState = commonNestStateService.getMediaStateV2(nestUuid);
        NestTypeEnum nestTypeEnum = commonNestStateService.getNestType(nestUuid);
        //G503校验
        if (NestTypeEnum.G503.equals(nestTypeEnum)) {
            MediaStateV2 mediaState1 = commonNestStateService.getMediaStateV2(nestUuid, AirIndexEnum.ONE);
            MediaStateV2 mediaState2 = commonNestStateService.getMediaStateV2(nestUuid, AirIndexEnum.TWO);
            MediaStateV2 mediaState3 = commonNestStateService.getMediaStateV2(nestUuid, AirIndexEnum.THREE);
            //是否需要添加判断条件是否还存在同步的任务队列
            tran = (MediaStatusV2Enum.IDLE.getValue().equals(mediaState1.getCurrentState()) &&
                    MediaStatusV2Enum.IDLE.getValue().equals(mediaState2.getCurrentState()) &&
                    MediaStatusV2Enum.IDLE.getValue().equals(mediaState3.getCurrentState())
            ) ? 0 : 1;
        } else {
            //其他基站
            log.info("mediaState.getCurrentState() ,{}",mediaState.getCurrentState());
            if (StringUtils.isEmpty(mediaState.getCurrentState()) || ObjectUtils.isEmpty(mediaState)) {
                tran = 0;
            } else {
                tran = MediaStatusV2Enum.IDLE.getValue().equals(mediaState.getCurrentState()) ? 0 : 1;
            }
        }
        return RestRes.ok("tran", tran);
    }

    @Override
    public RestRes tranDataCheck(String nestId, Integer uavWhich) {
//        String nestUuid = nestService.getUuidById(nestId);
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        if (nestUuid == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_NEST_CANNOT_BE_QUERIED.getContent()));
        }
        Map<String, Object> data = new HashMap<>(2);
        MissionState missionState = commonNestStateService.getMissionState(nestUuid, AirIndexEnum.getInstance(uavWhich));
        if (!MissionCommonStateEnum.IDLE.equals(missionState.getCurrentState())) {
            data.put("idle", 0);
            data.put("airConnected", -1);
            return RestRes.ok(data).msg(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_BASE_STATION_IS_NOT_IDLE_PLEASE_CHECK_THE_BASE_STATION_AND_THEN_SYNCHRONIZE_THE_DATA_OPERATION.getContent()));
        }
        NestState nestState = commonNestStateService.getNestState(nestUuid, AirIndexEnum.getInstance(uavWhich));
        if (!nestState.getRemoteControllerConnected() || !nestState.getAircraftConnected()) {
            data.put("idle", 1);
            data.put("airConnected", 0);
            String msg = MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DETECTED_THAT_THE_UAV_IS_NOT_CONNECTED.getContent());
            NestTypeEnum nestTypeEnum = baseNestService.getNestType(nestId);
            if (NestTypeEnum.G503.equals(nestTypeEnum)) {
                msg = "G503基站-检测到无人机未连接，若确认同步，需手工开启无人机以及打开遥控器开关";
            }
            return RestRes.ok(data).msg(msg);
        }
        data.put("idle", 1);
        data.put("airConnected", 1);
        return RestRes.ok(data).msg(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_BASE_STATION_HAS_THE_CONDITION_OF_SYNCHRONIZING_DATA_SOURCE_PLEASE_CONFIRM_THE_SYNCHRONIZATION.getContent()));
    }


    private String getMinioBucketName() {
        return geoaiUosProperties.getMinio().getBucketName();
    }


    /**
     * 获取缩略图分页列表
     *
     * @param params
     * @param missionRecordId
     * @return
     */
    @Override
    public RestRes getThumbnailPage(Map<String, Object> params, Integer missionRecordId, Integer airLineId) {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        IPage<MissionPhotoEntity> totalPage = baseMapper.getThumbnailPage(new Query<MissionPhotoEntity>().getPage(params), missionRecordId, orgCode);
        List<MissionPhotoEntity> missionPhotoEntityList = totalPage.getRecords().stream().map(row -> {
            MissionPhotoEntity resRow = new MissionPhotoEntity();
            BeanUtils.copyProperties(row, resRow);
            return resRow;
        }).collect(Collectors.toList());

        PageUtils page = new PageUtils(totalPage);
        Map<String, Object> map = new HashMap<>(2);

        if (missionPhotoEntityList.size() > 0) {
            // 对图片路径进行编码
            missionPhotoEntityList.forEach(e -> {
                try {
                    e.setPhotoUrl(URLEncoder.encode(e.getPhotoUrl(), "utf-8").replace("+", "%20"));
                    e.setThumbnailUrl(URLEncoder.encode(e.getThumbnailUrl(), "utf-8").replace("+", "%20"));
                } catch (UnsupportedEncodingException ex) {
                    ex.printStackTrace();
                }
            });
        }
        page.setList(missionPhotoEntityList);
        map.put("page", page);
        return RestRes.ok(map);
    }

//    @Override
//    public void downloadDefect(String idList, HttpServletResponse response, HttpServletRequest request) {
//        List<MissionPhotoDefectDto> list = new ArrayList<>();
//        if (StringUtils.isNotBlank(idList)) {
//            List<String> ids = Arrays.asList(idList.split(","));
//            for (int j = 0; j < ids.size(); j++) {
//                MissionPhotoEntity byId = this.getById(ids.get(j));
//                //获取成果对应的未消缺的缺陷
//                List<DefectInfoEntity> defectInfoEntities = defectInfoService.lambdaQuery()
//                        .eq(DefectInfoEntity::getPhotoId, ids.get(j))
//                        .eq(DefectInfoEntity::getDeleted, 0).list();
//                if (byId != null && !CollectionUtils.isEmpty(defectInfoEntities)) {
//                    String name = ids.size() > 1 ? byId.getName().substring(0, byId.getName().lastIndexOf(".")) + "(" + (j + 1) + ")"
//                            : byId.getName().substring(0, byId.getName().lastIndexOf("."));
//                    List<String> collect = defectInfoEntities.stream().map(DefectInfoEntity::getType).collect(Collectors.toList());
//                    for (int i = 0; i < defectInfoEntities.size(); i++) {
//                        String name1 = defectInfoEntities.size() > 1 ? collect.get(i) + "（" + (i + 1) + ")" : collect.get(i);
//                        DefectInfoEntity defectInfoEntity = defectInfoEntities.get(i);
//                        List<StationIdentifyRecordEntity> stationIdentifyRecordEntities = stationIdentifyRecordService.lambdaQuery()
//                                .eq(StationIdentifyRecordEntity::getId, defectInfoEntities.get(i).getRecordId())
//                                .eq(StationIdentifyRecordEntity::getStatus, 2).eq(StationIdentifyRecordEntity::getDeleted, 0).list();
//                        if (!CollectionUtils.isEmpty(stationIdentifyRecordEntities)) {
//                            stationIdentifyRecordEntities.forEach(s -> {
//                                //获取每个缺陷以及对应的word
//                                getMissionPhotoDefectDtos(list, s, defectInfoEntity, byId, name, name1);
//                            });
//                        }
//                    }
//                }
//            }
//            ids.forEach(e -> {
//
//            });
//
//            //下载zip包
//            try {
//                ZipUtil.downloadZip(list, "缺陷文档.zip", response);
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                list.forEach(e -> {
//                    if (e.getFilePath().endsWith(".docx")) {
//                        File file = new File(e.getFilePath());
//                        file.delete();
//                    }
//                });
//            }
//        }
//
//    }
//
//    private void getMissionPhotoDefectDtos(List<MissionPhotoDefectDto> list, StationIdentifyRecordEntity stationIdentifyRecordEntity, DefectInfoEntity defectInfoEntity, MissionPhotoEntity missionPhotoEntity, String name, String name1) {
//        Map<String, Object> params = new HashMap<>();
//        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
//        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
//        AccountDetailOutDTO accountDetails = uosAccountService.getAccountDetails(stationIdentifyRecordEntity.getCreateUserId().toString());
//        //设置word模板内容
//        params.put("picName", missionPhotoEntity.getName().substring(0, missionPhotoEntity.getName().lastIndexOf(".")));
//        params.put("discoveryTime", stationIdentifyRecordEntity.getCreateTime().format(dtf));
//        params.put("fillTime", sdf.format(new Date()));
//        params.put("defectFeatures", missionPhotoEntity.getName().substring(0, missionPhotoEntity.getName().lastIndexOf("."))
//                + ("硅胶变色".equals(defectInfoEntity.getType()) ? defectInfoEntity.getType()
//                + "(硅胶潮解全部变色或硅胶自上而下变色)" : "(" + defectInfoEntity.getType() + ")"));
//        params.put("defectType", defectInfoEntity.getType());
//        params.put("discoveryUser", accountDetails != null ? (StringUtils.isNotBlank(accountDetails.getName()) ? accountDetails.getName() : "") : "");
//        params.put("fillUser", "");
//
//        MissionPhotoDefectDto missionPhotoDefectDto = new MissionPhotoDefectDto();
//
//        //拼接zip路径
//        StringBuilder fileZipPath = new StringBuilder();
//        fileZipPath.append("现存缺陷/");
//        if (defectInfoEntity.getTypeId() != null) {
//            DefectTypeTrafficEntity byId = defectTypeTrafficService.getById(defectInfoEntity.getTypeId());
//            if (byId != null) {
//                switch (byId.getDefectLevel()) {
//                    case 0:
//                        params.put("defectLevel", "一般");
//                        fileZipPath.append("一般/");
//                        break;
//                    case 1:
//                        params.put("defectLevel", "严重");
//                        fileZipPath.append("严重/");
//                        break;
//                    case 2:
//                        params.put("defectLevel", "紧急");
//                        fileZipPath.append("紧急/");
//                        break;
//                }
//            }
//        }
//        fileZipPath.append("220kV哈尔滨变电站HY");
//
//        //拼接缺陷时间
//        LocalDateTime createTime = missionPhotoEntity.getCreateTime();
//        if (createTime != null) {
//            DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyyMMdd");
//            fileZipPath.append(createTime.format(dtf1)).append("/");
//        }
//
//        //拼接文件名
//        if (StringUtil.isNotEmpty(missionPhotoEntity.getName())) {
//            fileZipPath.append(name).append("/");
//        }
//
//        fileZipPath.append(name1).append("/");
//        if (StringUtils.isNotBlank(stationIdentifyRecordEntity.getDefectPhotoPath())) {
//            MissionPhotoDefectDto missionPhotoDefectDto1 = new MissionPhotoDefectDto();
//            missionPhotoDefectDto1.setFileZipPath(fileZipPath.toString() + sdf1.format(new Date()) + defectInfoEntity.getType() + ".jpg");
//            missionPhotoDefectDto1.setFilePath(stationIdentifyRecordEntity.getDefectPhotoPath());
//            list.add(missionPhotoDefectDto1);
//        }
//        String s = System.getProperty("user.dir") + File.separator + UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8) + ".docx";
//        WordUtil.exportWord(s, "/word", "WordTemplate.xml", params);
//        missionPhotoDefectDto.setFileZipPath(fileZipPath.toString() + missionPhotoEntity.getName().substring(0, missionPhotoEntity.getName().lastIndexOf(".")) + stationIdentifyRecordEntity.getDefectContent() + ".docx");
//        missionPhotoDefectDto.setFilePath(s);
//        list.add(missionPhotoDefectDto);
//    }

    /**
     * 根据标签名字按月份查询
     *
     * @param defectMonthListReqDto
     * @return
     */
    @Override
    public RestRes getDefectMonthList(DefectMonthListReqDto defectMonthListReqDto) {
        //List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        //List<StationDefectPhotoDTO> list = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        //resultMap.put("list", list);

        Integer defectStatus = defectMonthListReqDto.getDefectStatus();
        String endTime = defectMonthListReqDto.getEndTime();
        List<Integer> missionRecordsIds = singletonList(defectMonthListReqDto.getMissionRecordsId());
        List<String> names = singletonList(defectMonthListReqDto.getName());
        String startTime = defectMonthListReqDto.getStartTime();
        List<Integer> tagIds = singletonList(defectMonthListReqDto.getTagId());
        Integer type = defectMonthListReqDto.getType();
        defectMonthListReqDto.getDefectStatus();
        List<StationDefectPhotoDTO> photoInfoList;

        /*if (type != null && CollectionUtils.isNotEmpty(tagIds) && CollectionUtils.isEmpty(names) && CollectionUtils.isNotEmpty(taskIds)) {

            //分页查询
            if (defectMonthListReqDto.getPage() != null && defectMonthListReqDto.getLimit() != null) {
                Map<String, Object> params = new HashMap<>();
                params.put("page", defectMonthListReqDto.getPage());
                params.put("limit", defectMonthListReqDto.getLimit());
                IPage<StationDefectPhotoDTO> totalPage = baseMapper.getDefectByTaskId(new Query<StationDefectPhotoDTO>().getPage(params), missionRecordsIds, tagIds, taskIds, type, defectStatus, startTime, endTime);
                photoInfoList = totalPage.getRecords();
                // 获取DefectInfoList和PhotoUrlVisible和ThumbnailUrlVisible，set进photoInfoList
                getDefectInfoListAndVisibleUrl(photoInfoList, type);
                Map<String, Object> result = new HashMap<>();
                result.put("list", totalPage);
                return RestRes.ok(result);
            } else {
                Map<String, Object> params = new HashMap<>();
                params.put("page", 1);
                params.put("limit", 500);
                IPage<StationDefectPhotoDTO> totalPage = baseMapper.getDefectByTaskId(new Query<StationDefectPhotoDTO>().getPage(params), missionRecordsIds, tagIds, taskIds, type, defectStatus, startTime, endTime);
                photoInfoList = totalPage.getRecords();
            }
        }
        else */
        if (type != null && !CollectionUtils.isEmpty(tagIds)) {
            Map<String, Object> params = new HashMap<>();
            if (defectMonthListReqDto.getPage() != null && defectMonthListReqDto.getLimit() != null) {
                params.put("page", defectMonthListReqDto.getPage());
                params.put("limit", defectMonthListReqDto.getLimit());
                IPage<StationDefectPhotoDTO> page = baseMapper.getDefectMonthList(new Query<StationDefectPhotoDTO>().getPage(params),
                        tagIds, names, missionRecordsIds, defectStatus, startTime, endTime, type);
                Map<String, Object> map = new HashMap<>(2);
                getDefectInfoListAndVisibleUrl(page.getRecords(), type);
                map.put("page", new PageUtils(page));
                return RestRes.ok(map);
            } else {
                params.put("page", 1);
                params.put("limit", Integer.MAX_VALUE);
                IPage<StationDefectPhotoDTO> page = baseMapper.getDefectMonthList(new Query<StationDefectPhotoDTO>().getPage(params),
                        tagIds, names, missionRecordsIds, defectStatus, startTime, endTime, type);
                getDefectInfoListAndVisibleUrl(page.getRecords(), type);
                resultMap.put("page", new PageUtils(page));
                return RestRes.ok(resultMap);
            }

        } else {
            return RestRes.ok(resultMap);
        }
        // 获取DefectInfoList和PhotoUrlVisible和ThumbnailUrlVisible，set进photoInfoList
        /*Map<String, List<StationDefectPhotoDTO>> map = photoInfoList.stream().collect(Collectors.groupingBy(StationDefectPhotoDTO::getPMonth));
        Iterator<Map.Entry<String, List<StationDefectPhotoDTO>>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<StationDefectPhotoDTO>> entry = iterator.next();
            //String pMonth = entry.getKey();
            List<StationDefectPhotoDTO> stationDefectPhotoDTOS = entry.getValue();
            //Map<String, Object> photoMap = new HashMap<>();
            //photoMap.put("month", pMonth);
            //photoMap.put("missionPhotoEntities", stationDefectPhotoDTOS);
            //list.add(photoMap);
            list.addAll(stationDefectPhotoDTOS);
        }*/
    }

    private <T> List<T> singletonList(T o) {
        return Collections.singletonList(o).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }


//    @Override
//    public void downloadDefectInspection(String idList, HttpServletResponse response, HttpServletRequest request) {
//        //获取导出文档数据
//        if (StringUtils.isNotBlank(idList)) {
//            List<String> ids = Arrays.asList(idList.split(","));
//            Map<String, Object> dataMap = new HashMap<String, Object>();
//            List<Map<String, Object>> listMap = new ArrayList<>();
//            List<Map<String, Object>> urlList = new ArrayList<>();
//            dataMap.put("defectInfoList", listMap);
//            dataMap.put("imageList", urlList);
//            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            for (int i = 0; i < ids.size(); i++) {
//                MissionPhotoEntity missionPhotoEntity = this.lambdaQuery().eq(MissionPhotoEntity::getId, ids.get(i)).eq(MissionPhotoEntity::getDeleted, false).list().get(0);
//                MissionRecordsEntity missionRecordsEntity = missionRecordsService.lambdaQuery().eq(MissionRecordsEntity::getId, missionPhotoEntity.getMissionRecordsId()).list().get(0);
//                StationIdentifyRecordEntity stationIdentifyRecordEntity = stationIdentifyRecordService.lambdaQuery().eq(StationIdentifyRecordEntity::getPhotoId, ids.get(i)).eq(StationIdentifyRecordEntity::getDeleted, false).list().get(0);
//                List<DefectInfoEntity> defectInfoEntities = defectInfoService.lambdaQuery().eq(DefectInfoEntity::getPhotoId, ids.get(i)).eq(DefectInfoEntity::getDeleted, false).list();
//                List<String> defectTypeNames = defectInfoEntities.stream().map(DefectInfoEntity::getType).distinct().collect(Collectors.toList());
//                Map<String, Object> map = new HashMap<String, Object>();
//                Map<String, Object> map1 = new HashMap<String, Object>();
//                map.put("num", i + 1);
//                map.put("photoName", StringUtils.isNotBlank(missionPhotoEntity.getName()) ? missionPhotoEntity.getName().substring(0, missionPhotoEntity.getName().lastIndexOf(".")) : "");
//                map.put("defectType", !CollectionUtils.isEmpty(defectTypeNames) ? StringUtils.join(defectTypeNames.toArray(), ",") : "");
//                map.put("taskTime", missionRecordsEntity.getEndTime() != null ? df.format(missionRecordsEntity.getEndTime()) : "");
////                map1.put("img", FileUtil.getImageBase64String(stationIdentifyRecordEntity.getDefectPhotoPath()));
////                map1.put("imgName", StringUtils.isNotBlank(missionPhotoEntity.getName()) ? missionPhotoEntity.getName().substring(0, missionPhotoEntity.getName().lastIndexOf(".")) : "");
//                urlList.add(map1);
//                listMap.add(map);
//            }
//            WordUtil.exportWord1("巡检报告.docx", "WordTemplate1.xml", dataMap, response);
//        }
//    }

    @Override
    public List<MissionRecordsEntity> getMissionRecords(String startTime, String endTime) {
        return baseMapper.getMissionRecords(startTime, endTime);
    }

    @Override
    public List<Map> getInspect(List<Integer> missionRecordsIds) {
        return baseMapper.getInspect(missionRecordsIds);
    }

    /**
     * 创建xml文件
     */
    private void BuildXML(String folderName, String fileName, String fileUrl, Integer width, Integer height,
                          List<DefectInfoEntity> defectInfoList, ZipOutputStream zos, String defectTypeTrafficName) {
        try {
            // 设置生成xml的格式
            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
            outputFormat.setEncoding("UTF-8");
            // 创建document对象
            Document document = DocumentHelper.createDocument();
            // 创建根节点rss
            Element annotation = document.addElement("annotation");
            // 生成子节点及子节点内容
            Element folder = annotation.addElement("folder");

            folder.setText(folderName.substring(folderName.lastIndexOf("/") + 1));
            Element filename = annotation.addElement("filename");
            filename.setText(fileName);
            Element path = annotation.addElement("path");
            path.setText(fileUrl);

            Element source = annotation.addElement("source");
            Element sourceChannel = source.addElement("database");
            sourceChannel.setText("Unknown");

            Element size = annotation.addElement("size");
            Element widthSize = size.addElement("width");
            widthSize.setText(width.toString());
            Element heightSize = size.addElement("height");
            heightSize.setText(height.toString());
            Element depth = size.addElement("depth");
            depth.setText("3");

            Element segmented = annotation.addElement("segmented");
            segmented.setText("0");

            if (ToolUtil.isNotEmpty(defectInfoList)) {
                defectInfoList.forEach(e -> {
                    // 获取框框的坐标宽高
                    String x = String.valueOf(Double.parseDouble(e.getX().substring(0, e.getX().length() - 1)) / 100 * width);
                    String y = String.valueOf(Double.parseDouble(e.getY().substring(0, e.getY().length() - 1)) / 100 * height);
                    String x1 = String.valueOf(Double.parseDouble(e.getX1().substring(0, e.getX1().length() - 1)) / 100 * width);
                    String y1 = String.valueOf(Double.parseDouble(e.getY1().substring(0, e.getY1().length() - 1)) / 100 * height);

                    Element object = annotation.addElement("object");
                    Element name = object.addElement("name");
                    name.setText(e.getCode());
                    Element pose = object.addElement("pose");
                    pose.setText("Unspecified");
                    Element truncated = object.addElement("truncated");
                    truncated.setText("0");
                    Element difficult = object.addElement("object");
                    difficult.setText("0");

                    Element bndbox = object.addElement("bndbox");
                    Element xmin = bndbox.addElement("xmin");
                    xmin.setText(x.substring(0, x.lastIndexOf(".")));
                    Element ymin = bndbox.addElement("ymin");
                    ymin.setText(y.substring(0, y.lastIndexOf(".")));
                    Element xmax = bndbox.addElement("xmax");
                    xmax.setText(x1.substring(0, x1.lastIndexOf(".")));
                    Element ymax = bndbox.addElement("ymax");
                    ymax.setText(y1.substring(0, y1.lastIndexOf(".")));
                });
            }

            // xml文件名称
            String xmlName = fileName.substring(0, fileName.lastIndexOf(".")) + ".xml";

            // 往zip包插入xml
            DataOutputStream os = new DataOutputStream(zos);
            zos.putNextEntry(new ZipEntry(defectTypeTrafficName + File.separator + defectTypeTrafficName + "-XML" + File.separator + xmlName));
            XMLWriter writer = new XMLWriter(os, outputFormat);
            writer.setEscapeText(false);
            writer.write(document);
        } catch (Exception e) {
            e.printStackTrace();
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILURE_TO_GENERATE_XML.getContent()));
        }
    }

    @Override
    public List<MissionPhotoEntity> getAllPhotoByMissionRecordsId(Integer missionRecordsId) {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        LambdaQueryWrapper<MissionPhotoEntity> condition = Wrappers.lambdaQuery(MissionPhotoEntity.class)
                .eq(MissionPhotoEntity::getMissionRecordsId, missionRecordsId)
                .eq(MissionPhotoEntity::getDeleted, false)
                .likeRight(MissionPhotoEntity::getOrgCode, orgCode);
        return this.list(condition);
    }


    @Override
    public List<StationDefectPhotoDTO> getAllStationDefectPhotoDTO1(Integer type, Long id, String startTime, String endTime, Integer defectStatus) {
        return baseMapper.getAllStationDefectPhotoDTO1(type, id, startTime, endTime, defectStatus);
    }


    /**
     * 下载图片(zip包)
     *
     * @param ids              要下载的图片idList
     * @param isAll            是否选中全部图片下载
     * @param missionRecordsId 架次执行id
     * @param response
     * @param request
     */
    @Override
    public void downlandPic(String ids, boolean isAll, Integer missionRecordsId, HttpServletResponse response) {
        if (ToolUtil.isEmpty(ids) && !isAll) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DOWNLOAD_FAILED_PLEASE_SELECT_THE_CORRECT_PICTURE.getContent()));
        }
        if (ToolUtil.isNotEmpty(ids) && isAll) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DOWNLOAD_FAILED_PLEASE_SELECT_THE_CORRECT_PICTURE.getContent()));

        }

        // 压缩包名称
        String missionName = missionRecordsService.getMissionName(missionRecordsId);
        Integer flyIndex = missionRecordsService.getFlyIndex(missionRecordsId);
        String fileName = missionName + "_第" + flyIndex + "次执行_成果图片压缩包_" + System.currentTimeMillis() + ".zip";

        List<MissionPhotoEntity> photoList;
        List<Long> photoIdList;
        // 下载全部巡检同步回来的图片
        if (isAll) {
            photoList = baseMapper.selectList(new QueryWrapper<MissionPhotoEntity>().eq("mission_records_id", missionRecordsId).eq("deleted", 0));
            if (ToolUtil.isNotEmpty(photoList)) {
                photoIdList = photoList.stream().map(MissionPhotoEntity::getId).collect(Collectors.toList());
            } else {
                throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DOWNLOAD_FAILED_THERE_IS_NO_PICTURE_IN_THE_EXECUTION_SORTIE.getContent()));
            }
        } else {
            // 选择部分图片下载
            List<String> photoIdStr = Arrays.asList(ids.split(","));
            photoIdList = photoIdStr.stream().map(e -> Long.parseLong(e)).collect(Collectors.toList());
            if (ToolUtil.isNotEmpty(photoIdList)) {
                photoList = baseMapper.selectList(new QueryWrapper<MissionPhotoEntity>().in("id", photoIdList));
            } else {
                throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DOWNLOAD_FAILED_THE_CORRESPONDING_PICTURE_IS_NOT_FOUND.getContent()));
            }
        }

        List<String> photoUrlList = photoList.stream().map(MissionPhotoEntity::getPhotoUrl).collect(Collectors.toList());
        Integer photoSource = baseMapper.getPhotoSource(photoIdList.get(0));
        if (2 == photoSource) {
            // 人工上传
            fileName = "人工上传照片压缩包_" + System.currentTimeMillis() + ".zip";
            List<FileUrlMappingDO> results = photoList.stream().map(r -> {
                FileUrlMappingDO fileUrlMappingDO = new FileUrlMappingDO();
                fileUrlMappingDO.setOriginName(r.getName());
                fileUrlMappingDO.setStorageUri(r.getPhotoUrl());
                return fileUrlMappingDO;
            }).collect(Collectors.toList());
            DownLoadZipUtil.downloadUploadPhotoOrVideo(fileName, results, response);
        } else {
            // 下载后重命名的照片名称
            List<FileUrlMappingDO> photoNameList = getPhotoNames(photoList);
            // 原图的路径
            Integer missionId = missionRecordsService.getMissionIdByRecordId(missionRecordsId);
            String photoPre = (!CollectionUtils.isEmpty(photoUrlList) && photoUrlList.get(0).contains("autoUpload")) ? "autoUpload/photo/" : DataConstant.PHOTO;
            String path = String.format("%s%s/%s/", photoPre, missionId, missionRecordsId);
            //适配大疆机场
            if (photoUrlList.stream().findFirst().map(x -> x.contains("dji/media")).orElseGet(() -> false)) {
                DownLoadUtils.downloadZipWithPip(photoUrlList, fileName, response);
                return;
            }
            if (photoUrlList.stream().findFirst().map(x -> x.contains(UploadTypeEnum.DJI_PILOT_AUTO_UPLOAD.getPath())).orElseGet(() -> false)) {
                DownLoadUtils.downloadZipWithPip(photoUrlList, fileName, response);
                return;
            }

            // 将图片从MinIO下载，并打包到压缩包里下载到浏览器
            DownLoadZipUtil.downloadUploadPhotoOrVideo(fileName, photoNameList, response);
        }
    }

    private List<FileUrlMappingDO> getPhotoNames(List<MissionPhotoEntity> photoList) {
        Map<String, Integer> map = new HashMap<>(photoList.size());
        return photoList.stream()
                .map(e -> {
                    String name = e.getName();
                    String photoName = name;
                    if (name.endsWith(".jpg") || name.endsWith(".JPG")) {
                        photoName = name.substring(0, name.lastIndexOf("."));
                    }
                    String timeStr = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
                    String changeName = photoName + "_" + timeStr;
                    Integer count = map.get(changeName);
                    if (count != null) {
                        count = count + 1;
                        map.put(changeName, count);
                        changeName = changeName + "_" + count + ".JPG";
                    } else {
                        count = 0;
                        map.put(changeName, count);
                        changeName = changeName + ".JPG";
                    }
                    FileUrlMappingDO fileUrlMappingDO = new FileUrlMappingDO();
                    fileUrlMappingDO.setStorageUri(e.getPhotoUrl());
                    fileUrlMappingDO.setOriginName(changeName);
                    return fileUrlMappingDO;
                }).collect(Collectors.toList());
    }

    @Override
    public void deletePhoto(List<Integer> recordIdList) {
        baseMapper.deleteByRecordId(recordIdList);
    }

    @Override
    public Integer getPhotoNum(Integer recordId) {
        return baseMapper.getPhotoNum(recordId);
    }

    @Override
    public Boolean checkGetMediaSuccess(Integer missionId, Integer recordId, Integer successType, Integer errorType) {
        // 判断是否完成同步
        // 获取该架次航线的拍照数,视频数（预计数量）
        Map<String, Integer> countMap = missionService.getMissionPhotoCountById(missionId);
        Integer photoCount = countMap.get("photoCount");
        Integer videoCount = countMap.get("videoCount");
        log.info("photoCount: " + photoCount + "; videoCount: " + videoCount);
        // 获取当前上传到服务器的图片、视频数量（实际获取到的数量）
        Integer photoNum = missionPhotoService.getPhotoNum(recordId);
        Integer videoNum = missionVideoService.getVideoNum(recordId);
        log.info("photoNum: " + photoNum + "; videoNum: " + videoNum);

        if (photoNum < photoCount || videoNum < videoCount) {
            // 修改图片数据传输状态为(3-同步到机巢异常 或 4-同步到平台异常)
            missionRecordsService.updateDataStatusById(errorType, recordId);
            return false;
        } else {
            // 修改图片数据传输状态为（1-同步到机巢 或 2-同步到平台）
            missionRecordsService.updateDataStatusById(successType, recordId);
            return true;
        }
    }

    @Override
    public List<Integer> getMediaRecordSuccess(Integer missionId, Integer recordId) {
        return baseMapper.getIdentifyRecord(missionId, recordId);
    }

    @Override
    public List<Map> getRecordMap(Integer missionId, Integer recordId) {
        return baseMapper.getRecordMap(missionId, recordId);
    }

    @Override
    public Integer getRecordType(Integer missionId) {
        return baseMapper.getRecordType(missionId);
    }


    @Override
    public RestRes stopGetMedia(String nestUuid, Integer recordId) {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        // 查看机巢是否连接,未连接则连接机巢
//        if (ToolUtil.isEmpty(cm)) {
//            Integer nestId = nestService.getIdByUuid(nestUuid);
//            nestService.initNest(nestId);
//            cm = ComponentManagerFactory.getInstance(nestUuid);
//        }
        // 再次判断机巢是否连接
        if (Objects.nonNull(cm)) {
            //返回两个值，无法判断是成功。
            MissionRecordsEntity missionRecordsEntity = missionRecordsService.getById(recordId);
            Integer which = 0;
            if (Objects.nonNull(missionRecordsEntity)) {
                which = missionRecordsEntity.getUavWhich();
            }
            MqttResult<BaseResult3> mqttResult = cm.getMediaManagerCf().resetMediaManager(AirIndexEnum.getInstance(which));

//            if (mqttResult.getSuccess()) {
//            String message = WebSocketRes.ok().topic(WebSocketTopicEnum.UPLOAD_MEDIA_TO_SERVER_TIP).msg("取消数据同步").toJSONString();
//            pushPhotoTransMsgByWs(nestUuid, message);
            // 修改图片数据传输状态为（5-停止同步）
            missionRecordsService.updateDataStatusById(MissionConstant.MissionExecDataStatus.STOP_GAIN, recordId);
//            }
            Map<String, Object> map = new HashMap<>(2);
            map.put("uavWhich", which);
            String message = WebSocketRes.err().topic(WebSocketTopicEnum.UPLOAD_MEDIA_TO_SERVER_TIP).msg("geoai_uos_cancel_data_synchronization").toJSONString();
            pushPhotoTransMsgByWs(nestUuid, message);
            Integer taskId = missionRecordsService.getTaskIdByRecordsId(recordId);
            TaskEntity taskEntity = taskService.selectById(taskId);
            photoAndGridRel(taskEntity, recordId);
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_STOP_SYNCHRONIZING_DATA_COMMAND_WAS_SENT_SUCCESSFULLY.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_NEST_IS_OFFLINE_STOP_SYNCHRONIZATION.getContent()));
    }

    @Override
    public PageUtils getAppMissionRecordPage(Integer page, Integer limit, String appId, String taskName) {
        IPage<MissionDto> totalPage = missionService.getAppMissionRecordPage(new Page<>(page, limit), appId, taskName);
        return new PageUtils(totalPage);
    }

    /**
     * 获取缺陷识别的数目统计
     *
     * @param
     * @param
     * @return
     */
    @Override
    public RestRes getDefectStatistics(String startTime, String endTime) {
        List<Integer> tagList = getTagListIds();
        Map<String, Object> resultMap = new HashMap<>();
        Map map = baseMapper.getDefectStatistics(startTime, endTime, tagList);
        resultMap.put("stationStatisticsNum", map);
        return RestRes.ok(resultMap);
    }

    public List<Integer> getTagListIds() {
        // 查询用户单位信息
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        return sysTagService.getTagIdsByOrgCode(orgCode);
    }


    /**
     * 删除成果图片
     *
     * @param reqDtoList
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestRes deleteMissionPhoto(List<MissionPhotosReqDto> reqDtoList) {
        if (!CollectionUtils.isEmpty(reqDtoList)) {
            for (MissionPhotosReqDto missionPhotosReqDto : reqDtoList) {
                Integer photoId = missionPhotosReqDto.getPhotoId();
                Integer type = missionPhotosReqDto.getType();
                Integer tagId = missionPhotosReqDto.getTagId();
                if (photoId != null && type != null && tagId != null) {
                    MissionPhotoEntity byId = missionPhotoService.getById(photoId);
                    if (byId != null) {
                        //删除表计或缺陷表
                        if (type == 0 || type == 1) {
                            stationIdentifyRecordService.lambdaUpdate().set(StationIdentifyRecordEntity::getDeleted, 1).eq(StationIdentifyRecordEntity::getPhotoId, photoId)
                                    .eq(StationIdentifyRecordEntity::getDeleted, 0).eq(StationIdentifyRecordEntity::getSource, type).update();
                            defectInfoService.lambdaUpdate().set(DefectInfoEntity::getDeleted, 1).eq(DefectInfoEntity::getPhotoId, photoId)
                                    .eq(DefectInfoEntity::getDeleted, 0).eq(DefectInfoEntity::getSource, type).update();
                        }
                        //删除红外表
                        if (type == 2) {
                            stationInfraredRecordService.lambdaUpdate().set(StationInfraredRecordEntity::getDeleted, 1).eq(StationInfraredRecordEntity::getPhotoId, photoId)
                                    .eq(StationInfraredRecordEntity::getDeleted, 0).update();
                            stationInfraredRecordRectangleService.lambdaUpdate().set(StationInfraredRecordRectangleEntity::getDeleted, 1).eq(StationInfraredRecordRectangleEntity::getPhotoId, photoId)
                                    .eq(StationInfraredRecordRectangleEntity::getDeleted, 0).update();
                        }
                        //删除照片对应的标签和类型的关系
                        List<MissionPhotoTagRelEntity> list = missionPhotoTagRelService.lambdaQuery().eq(MissionPhotoTagRelEntity::getDeleted, 0)
                                .eq(MissionPhotoTagRelEntity::getMissionPhotoId, photoId).eq(MissionPhotoTagRelEntity::getTagId, tagId).list();
                        if (list.size() > 0) {
                            missionPhotoTagRelService.updateById(list.get(0));
                        }
                        missionPhotoTypeRelService.lambdaUpdate().set(MissionPhotoTypeRelEntity::getDeleted, 1).eq(MissionPhotoTypeRelEntity::getDeleted, 0)
                                .eq(MissionPhotoTypeRelEntity::getMissionPhotoId, photoId).eq(MissionPhotoTypeRelEntity::getType, type).update();
                    }
                }
            }
        }
        return RestRes.ok();
    }

    /**
     * 删除成果图片
     *
     * @param photoIds
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestRes updatePhotoDefectStatus(List<Integer> photoIds, Integer defectStatus) {
        List<MissionPhotoEntity> missionPhotoEntities = missionPhotoService.list(new QueryWrapper<MissionPhotoEntity>().in("id", photoIds));
        if (CollectionUtils.isEmpty(missionPhotoEntities)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_RESULT_CANNOT_BE_QUERIED.getContent()));
        }

        List<StationIdentifyRecordEntity> stationIdentifyRecordEntityList = stationIdentifyRecordService.list(new QueryWrapper<StationIdentifyRecordEntity>().eq("deleted", false).in("photo_id", photoIds));
        if (Objects.isNull(stationIdentifyRecordEntityList)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_DEFECT_OF_THE_RESULT_CANNOT_BE_QUERIED.getContent()));
        }
        stationIdentifyRecordEntityList.forEach(e -> {
            //后面修改status,修改为消缺的状态
            e.setStatus(defectStatus);
            //e.setDefectPhotoPath("");
            //defectInfoService.remove(new QueryWrapper<DefectInfoEntity>().eq("record_id", e.getId()));
        });

        stationIdentifyRecordService.updateBatchById(stationIdentifyRecordEntityList);
        return RestRes.ok();
    }

    @Override
    public RestRes updatePhotoName(Long id, String photoName) {
        MissionPhotoEntity missionPhotoEntity = new MissionPhotoEntity();
        missionPhotoEntity.setName(photoName);
        missionPhotoService.update(missionPhotoEntity, new QueryWrapper<MissionPhotoEntity>().eq("id", id));
        return RestRes.ok();
    }

    @Override
    public RestRes getPhotoListPage(Integer tagId, String name, String missionRecordId, Integer defectStatus, Integer pageNum, Integer pageSize) {
        if (pageNum == null || pageSize == null) {
            pageNum = 1;
            pageSize = Integer.MAX_VALUE;
        }
        IPage<StationIdentifyPhotoDTO> totalPage = stationIdentifyRecordService.getPhotoListPage(new Page<>(pageNum, pageSize), tagId, name, missionRecordId, defectStatus);
        List<StationIdentifyPhotoDTO> photoInfoList = totalPage.getRecords().stream().map(row -> {
            StationIdentifyPhotoDTO resRow = new StationIdentifyPhotoDTO();
            BeanUtils.copyProperties(row, resRow);
            return resRow;
        }).collect(Collectors.toList());

        photoInfoList.stream().forEach(e -> {
            if (e.getSid() != null) {
                List<DefectInfoEntity> entities = defectInfoService.list(new QueryWrapper<DefectInfoEntity>().eq("record_id", e.getSid()).eq("deleted", false).eq("source", MissionConstant.PhotoType.METER));
                e.setDefectInfoEntityList(entities);
            }
        });

//        List<DefectInfoEntity> entities;
//        for (StationIdentifyPhotoDTO stationIdentifyPhotoDTO : photoInfoList) {
//            if (stationIdentifyPhotoDTO.getSid() != null) {
//                entities = defectInfoService.list(new QueryWrapper<DefectInfoEntity>().eq("record_id",stationIdentifyPhotoDTO.getSid()).eq("deleted", false).eq("source", MissionConstant.PhotoType.METER));
//                stationIdentifyPhotoDTO.setDefectInfoEntityList(entities);
//            }
//        }
        totalPage.setRecords(photoInfoList);
        Map<String, Object> map = new HashMap<>(2);
        map.put("page", new PageUtils(totalPage));
        return RestRes.ok(map);
    }

    @Override
    public RestRes getNumberList(MeterReadDTO meterReadDTO) {
        Integer tagId = meterReadDTO.getTagId();
        String photoName = meterReadDTO.getPhotoName();
        String startTime = meterReadDTO.getStartTime();
        String endTime = meterReadDTO.getEndTime();
        // 获取平台任务架次飞行完获取的图片、表计读数手动上传的图片
        List<StationDevicePhotoDTO> list = stationIdentifyRecordService.getAllPhotoMeterNum(tagId, photoName, startTime, endTime);

        Map<String, Object> map = new HashMap<>(2);
        map.put("list", list);
        return RestRes.ok(map);
    }

    //
    private List<StationDefectPhotoDTO> getStationDefectPhotoDTOList(MissionPhotoEntity missionPhotoEntity, Integer type, Integer flag, String typeName, List<StationIdentifyRecordEntity> recordEntities, List<StationInfraredRecordEntity> stationInfraredRecordEntities) {
        List<StationDefectPhotoDTO> collect = null;

        if (type != 2) {
            List<StationIdentifyRecordEntity> list = recordEntities.stream().filter(e -> missionPhotoEntity.getId().equals(e.getPhotoId())).collect(Collectors.toList());
            collect = list.stream().map(e -> {
                StationDefectPhotoDTO stationDefectPhotoDTO = new StationDefectPhotoDTO();
                BeanUtils.copyProperties(missionPhotoEntity, stationDefectPhotoDTO);
                stationDefectPhotoDTO.setMeterNum(e.getMeterNum());
                stationDefectPhotoDTO.setMeterStatus(e.getMeterStatus());
                stationDefectPhotoDTO.setMeterPhoto(e.getMeterPhoto());
                stationDefectPhotoDTO.setStatus(e.getStatus());
                stationDefectPhotoDTO.setDefectContent(e.getDefectContent());
                stationDefectPhotoDTO.setDefectPhotoPath(e.getDefectPhotoPath());
                stationDefectPhotoDTO.setSource(e.getSource());
                stationDefectPhotoDTO.setFlag(e.getFlag());
                stationDefectPhotoDTO.setTypeName(typeName);
                return stationDefectPhotoDTO;
            }).collect(Collectors.toList());
        } else {
            /*t3.max_temperature,t3.avg_temperature,t3.min_temperature,t3.sun_url,t3.pip_url,t3.record_url,gr.tag_id as tagId*/
            List<StationInfraredRecordEntity> list = stationInfraredRecordEntities.stream().filter(e -> missionPhotoEntity.getId().intValue() == e.getPhotoId()).collect(Collectors.toList());
            collect = list.stream().map(e -> {
                StationDefectPhotoDTO stationDefectPhotoDTO = new StationDefectPhotoDTO();
                BeanUtils.copyProperties(missionPhotoEntity, stationDefectPhotoDTO);
                stationDefectPhotoDTO.setMaxTemperature(e.getMaxTemperature());
                stationDefectPhotoDTO.setAvgTemperature(e.getAvgTemperature());
                stationDefectPhotoDTO.setMinTemperature(e.getMinTemperature());
                stationDefectPhotoDTO.setSunUrl(e.getSunUrl());
                stationDefectPhotoDTO.setPipUrl(e.getPipUrl());
                stationDefectPhotoDTO.setRecordUrl(e.getRecordUrl());
                stationDefectPhotoDTO.setFlag(e.getFlag());
                stationDefectPhotoDTO.setTypeName(typeName);
                return stationDefectPhotoDTO;
            }).collect(Collectors.toList());
        }
        if (ToolUtil.isEmpty(collect) && flag == null) {
            // 没入库到StationIdentifyRecordEntity或者StationInfraredRecordEntity的照片
            StationDefectPhotoDTO stationDefectPhotoDTO = new StationDefectPhotoDTO();
            BeanUtils.copyProperties(missionPhotoEntity, stationDefectPhotoDTO);
            stationDefectPhotoDTO.setTypeName(typeName);
            collect.add(stationDefectPhotoDTO);
        }
        return collect;
    }

    @Override
    public RestRes getDeviceList(Integer tagId, List<Integer> types, Integer defectStatus, String startTime, String endTime, Integer flag) {
        //flag为空时查询分析中台的数据，不为空查询异常数据
        if (flag == null) {
            return getAnalysisCenter(tagId, types, defectStatus, startTime, endTime);
        } else {
            return getExceptions(tagId, types, startTime, endTime);
        }
    }

    @Override
    public RestRes getMissionList(Integer tagId, Integer type, Integer defectStatus, String startTime, String endTime) {
        if (startTime != null && endTime != null) {
            startTime = LocalDate.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .atTime(LocalTime.MIN).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            endTime = LocalDate.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .atTime(LocalTime.MAX).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        List<StationDevicePhotoDTO> photoInfoLists = baseMapper.getPhotoInfoMissionRecordList(null, tagId, type, defectStatus, startTime, endTime);
        List<Map<String, Object>> lists = new ArrayList<>();
        Map<String, Map<String, Object>> maps = new LinkedHashMap<>();
        Map<Integer, StationDevicePhotoDTO> children = null;
        for (StationDevicePhotoDTO photoInfoList : photoInfoLists) {
            Map<String, Object> photoMap = maps.get(photoInfoList.getTaskName());
            if (photoMap == null) {
                photoMap = new HashMap<>();
                maps.put(photoInfoList.getTaskName(), photoMap);
                photoMap.put("id", photoInfoList.getTaskName());
                photoMap.put("photoInfo", photoInfoList);
                children = new HashMap<>();
                photoMap.put("children", children);
            }
            children = (Map<Integer, StationDevicePhotoDTO>) photoMap.get("children");
            children.put(photoInfoList.getMissionRecordsId(), photoInfoList);
        }
        List<Object> children0 = new ArrayList<>();
        for (String key : maps.keySet()) {
            Map<String, Object> photoMap = maps.get(key);
            children = (Map<Integer, StationDevicePhotoDTO>) photoMap.get("children");
            StationDevicePhotoDTO photoInfoList = (StationDevicePhotoDTO) photoMap.get("photoInfo");
            List<Map> photoMapList = new ArrayList<>();
            for (Integer key1 : children.keySet()) {
                Map<String, Object> tmpOfmap = new HashedMap<>();
                StationDevicePhotoDTO tmpOf = children.get(key1);
                tmpOfmap.put("photoId", tmpOf.getPhotoId());
                tmpOfmap.put("photoName", tmpOf.getPhotoName());
                tmpOfmap.put("defectStatus", tmpOf.getDefectStatus());
                tmpOfmap.put("createTime", tmpOf.getCreateTime());
                tmpOfmap.put("missionRecordsId", tmpOf.getMissionRecordsId());
                photoMapList.add(tmpOfmap);
            }
            photoInfoList.setPhotoMapList(photoMapList);
            photoMap.put("children", children0);
            lists.add(photoMap);
        }
        Map<String, Object> remap = new HashMap<>(2);
        remap.put("list", lists);
        return RestRes.ok(remap);
    }

    /*public RestRes getAnalysisCenterImpl(Integer tagId, List<Integer> types, Integer defectStatus, String startTime, String endTime) {
		Map<String, Object> photoMap = new HashMap<>();
		Map<String, Object> remap = new HashMap<>(2);
		// 根据tagId获取设备图片名称List
		Integer type = types.get(0);
		List<StationDevicePhotoDTO> photoInfoList = baseMapper.getPhotoInfoMissionRecordList(null, tagId, type, defectStatus, startTime, endTime);
		List<String> missionNameList = photoInfoList.stream().map(StationDevicePhotoDTO::getTaskName).collect(Collectors.toList());
		missionNameList.removeAll(Collections.singleton(null));
		missionNameList = missionNameList.stream().distinct().collect(Collectors.toList());
		List<Map<String,Object>> lists = new ArrayList<>();
		List<Object> children = new ArrayList<>(0);
		if (ToolUtil.isNotEmpty(missionNameList)) {
			for (String e : missionNameList) {
				List<StationDevicePhotoDTO> stationDevicePhotoDTOS = photoInfoList.stream().filter(info -> e.equals(info.getTaskName())).collect(Collectors.toList());
				// 通过设备uuid、tagId获取该设备的图片List
				if (stationDevicePhotoDTOS.size() > 0) {
					StationDevicePhotoDTO photoInfo = getPhotoInfo(type, stationDevicePhotoDTOS);
					photoMap.put("photoInfo", photoInfo);
					photoMap.put("id", e);
					photoMap.put("children", children);
					lists.add(photoMap);
				}
			}
		}
		remap.put("list", lists);
		return RestRes.ok(remap);
	}*/


    //获取分析数据
    public RestRes getAnalysisCenter(Integer tagId, List<Integer> types, Integer defectStatus, String startTime, String endTime) {
        Map<String, Object> photoMap = new HashMap<>();
        Map<String, Object> remap = new HashMap<>(2);
        // 根据tagId获取设备图片名称List
        Integer type = types.get(0);
        List<MissionPhotoEntity> missionPhotoEntities = getTaskPhoto(tagId, type, startTime, endTime);
        List<String> deviceNameList = missionPhotoEntities.stream().map(MissionPhotoEntity::getName).collect(Collectors.toList());
        deviceNameList.removeAll(Collections.singleton(null));
        deviceNameList = deviceNameList.stream().distinct().collect(Collectors.toList());
        System.out.println(deviceNameList.toString());
        if (startTime != null && endTime != null) {
            startTime = LocalDate.parse(startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .atTime(LocalTime.MIN).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            endTime = LocalDate.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    .atTime(LocalTime.MAX).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        List<StationDevicePhotoDTO> photoInfoList = baseMapper.getPhotoInfoList(null, tagId, type, defectStatus, startTime, endTime);
        List<String> nameList = new ArrayList<>();
        if (ToolUtil.isNotEmpty(deviceNameList)) {
            for (String e : deviceNameList) {
                List<StationDevicePhotoDTO> stationDevicePhotoDTOS = photoInfoList.stream().filter(info -> e.equals(info.getPhotoName())).collect(Collectors.toList());
                // 通过设备uuid、tagId获取该设备的图片List
                if (stationDevicePhotoDTOS.size() > 0) {
                    StationDevicePhotoDTO photoInfo = getPhotoInfo(type, stationDevicePhotoDTOS);
                    photoMap.put(e, photoInfo);
                    nameList.add(e);
                }
            }
        }
        //递归返回
        JSONArray jsonArray = getNameList1(nameList, photoMap);
        remap.put("list", jsonArray);
        return RestRes.ok(remap);
    }

    //获取异常数据
    private RestRes getExceptions(Integer tagId, List<Integer> types, String startTime, String endTime) {
        Map<String, Object> photoMap = new HashMap<>();
        Map<String, Object> remap = new HashMap<>(2);
        // 根据tagId获取设备图片名称List
        List<String> deviceNameList = new ArrayList<>();
        //异常list
        List<StationDefectPhotoDTO> infoList = new ArrayList<>();
        for (Integer type : types) {
            List<StationDefectPhotoDTO> infos = getAllStationDefectPhotoDTO(tagId, type, startTime, endTime, 1);
            if (type != 2) {
                infos = infos.stream().filter(e -> e.getSource().equals(type)).collect(Collectors.toList());
            }
            if (type == 1 || type == 2) {
                infos = infos.stream().filter(e -> e.getFlag() != null && e.getFlag() == 1).collect(Collectors.toList());
            } else {
                infos = infos.stream().filter(e -> e.getStatus() != null && (e.getStatus() == 2 || e.getStatus() == 3)).collect(Collectors.toList());
            }
            List<String> defectList = infos.stream().map(StationDefectPhotoDTO::getName).collect(Collectors.toList());
            deviceNameList.addAll(defectList);
            infoList.addAll(infos);
        }
        deviceNameList.removeAll(Collections.singleton(null));
        System.out.println(deviceNameList.toString());
        JSONArray jsonArray = null;
        if (ToolUtil.isNotEmpty(deviceNameList)) {
            for (String e : deviceNameList) {
                StationDevicePhotoDTO photoInfo = new StationDevicePhotoDTO();
                List<StationDefectPhotoDTO> allDefectPhotos = infoList.stream().filter(info -> e.equals(info.getName())).map(stationDefectPhotoDTO -> {
                    if (stationDefectPhotoDTO.getLatitude() == null) {
                        stationDefectPhotoDTO.setLatitude(0D);
                        stationDefectPhotoDTO.setLongitude(0D);
                    }
                    if (stationDefectPhotoDTO.getStatus() != null && (stationDefectPhotoDTO.getStatus() == 2 || stationDefectPhotoDTO.getStatus() == 3)) {
                        List<DefectInfoEntity> entities = defectInfoService.lambdaQuery().eq(DefectInfoEntity::getPhotoId, stationDefectPhotoDTO.getId()).eq(DefectInfoEntity::getDeleted, 0).list();
                        stationDefectPhotoDTO.setDefectInfoEntityList(entities);
                    }
                    return stationDefectPhotoDTO;
                }).collect(Collectors.toList());
                photoInfo.setDefectList(allDefectPhotos);
                photoInfo.setPhotoName(e);
                photoMap.put(e, photoInfo);
            }
        }
        //递归返回
        jsonArray = getNameList(deviceNameList, photoMap);
        remap.put("list", jsonArray);
        return RestRes.ok(remap);
    }

    @Resource
    private NacosConfigurationService nacosConfigurationService;

    @Override
    public List<StationDefectPhotoDTO> getAllStationDefectPhotoDTO(Integer tagId, Integer type, String startTime, String endTime, Integer flag) {
        List<MissionPhotoEntity> missionPhotoEntities = getTaskPhoto(tagId, type, startTime, endTime);
        List<StationDefectPhotoDTO> infos = new ArrayList<>();

        // 通过idenValue查询查询二级目录名称
        String typeName = nacosConfigurationService.getNameByIdenValue(type);
        List<StationIdentifyRecordEntity> recordEntities = stationIdentifyRecordService.list(new QueryWrapper<StationIdentifyRecordEntity>().eq("source", type).eq("deleted", 0));
        List<StationInfraredRecordEntity> stationInfraredRecordEntities = stationInfraredRecordService.list(new QueryWrapper<StationInfraredRecordEntity>().eq("deleted", 0));
        for (MissionPhotoEntity missionPhotoEntity : missionPhotoEntities) {
            List<StationDefectPhotoDTO> stationDefectPhotoDTOList = getStationDefectPhotoDTOList(missionPhotoEntity, type, flag, typeName, recordEntities, stationInfraredRecordEntities);
            infos.addAll(stationDefectPhotoDTOList);
        }
        return infos;
    }

    @Override
    public List<PhotoTagSourceDTO> getPhotoTagSourceList(Integer problemStatus, String startTime, String endTime, List<Integer> problemSourceList, Integer tagId) {
        return baseMapper.getPhotoTagSourceList(problemStatus, problemSourceList, startTime, endTime, tagId);
    }

    //获取图片
    public List<MissionPhotoEntity> getTaskPhoto(Integer tagId, Integer type, String startTime, String endTime) {
        List<MissionPhotoEntity> missionPhotoEntities = baseMapper.getMissionPhotoByIds(tagId, type, startTime, endTime);
        //missionPhotoEntities = missionPhotoEntities.stream().filter(distinctByKey(MissionPhotoEntity::getId)).collect(Collectors.toList());
        return missionPhotoEntities;
    }

    //获取photoInfo
    public StationDevicePhotoDTO getPhotoInfo(Integer type, List<StationDevicePhotoDTO> photoInfoList) {
        StationDevicePhotoDTO photoInfo = new StationDevicePhotoDTO();
        // 获取表计读数的状态值为null的数据，有则说明有新照片
        List<StationDevicePhotoDTO> stationIsNullList = null;
        List<StationDevicePhotoDTO> stationNotNullList = null;
        if (type == 1) {
            stationIsNullList = photoInfoList.stream().filter((StationDevicePhotoDTO stationDevicePhotoDTO) -> stationDevicePhotoDTO.getMeterStatus() == null).collect(Collectors.toList());
            stationNotNullList = photoInfoList.stream().filter((StationDevicePhotoDTO stationDevicePhotoDTO) -> stationDevicePhotoDTO.getMeterStatus() != null).collect(Collectors.toList());
        } else if (type == 2) {
            stationIsNullList = photoInfoList.stream().filter((StationDevicePhotoDTO stationDevicePhotoDTO) -> stationDevicePhotoDTO.getMaxTemperature() == null).collect(Collectors.toList());
            stationNotNullList = photoInfoList.stream().filter((StationDevicePhotoDTO stationDevicePhotoDTO) -> stationDevicePhotoDTO.getMaxTemperature() != null).collect(Collectors.toList());
        } else {
            stationIsNullList = photoInfoList.stream().filter((StationDevicePhotoDTO stationDevicePhotoDTO) -> stationDevicePhotoDTO.getDefectStatus() == null || stationDevicePhotoDTO.getDefectStatus() == 0).collect(Collectors.toList());
            stationNotNullList = photoInfoList.stream().filter((StationDevicePhotoDTO stationDevicePhotoDTO) -> stationDevicePhotoDTO.getDefectStatus() != null && stationDevicePhotoDTO.getDefectStatus() != 0).collect(Collectors.toList());
        }
        List<StationDevicePhotoDTO> defectList = photoInfoList.stream().filter((StationDevicePhotoDTO stationDevicePhotoDTO) -> stationDevicePhotoDTO.getDefectStatus() != null && stationDevicePhotoDTO.getDefectStatus() == 2).collect(Collectors.toList());
        if (stationIsNullList.size() > 0) {
            photoInfo = stationIsNullList.get(0);
        }
        if (stationNotNullList.size() > 0) {
            photoInfo = stationNotNullList.get(0);
        }
        if (stationIsNullList.size() > 0) {
            photoInfo.setHasNew(true);
        }
        //if (type != 1 && type != 2) {
        List<Map> photoMapList = photoInfoList.stream().map(ph -> {
            Map map = new HashMap();
            map.put("photoId", ph.getPhotoId());
            map.put("photoName", ph.getPhotoName());
            map.put("defectStatus", ph.getDefectStatus());
            map.put("createTime", ph.getCreateTime());
            map.put("missionRecordsId", ph.getMissionRecordsId());
            return map;
        }).collect(Collectors.toList());
        photoInfo.setPhotoMapList(photoMapList);
        //}
        if (defectList.size() > 0) {
            photoInfo.setDefectStatus(2);
            photoInfo.setIsDefect(true);
        }
        return photoInfo;
    }

    @Override
    public RestRes getTotalPhotoAndVideo(String startTime, String endTime) {

        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();

        String key = String.format(RedisKeyConstantList.TOTAL_PHOTO_AND_VIDEO, orgCode);
        Object o = redisService.get(key);

        Map<String, Object> resMap;
        if (o == null) {
            resMap = Maps.newHashMap();
            Map<String, Object> map = Maps.newHashMap();
            map.put("photoCount", 0);
            map.put("videoCount", 0);
            resMap.put("total", map);
            resMap.put("photoList", Collections.emptyList());
            resMap.put("videoList", Collections.emptyList());
        } else {
            BaseNestAccountOutDO outDO = baseNestAccountManager.selectByUserId(TrustedAccessTracerHolder.get().getAccountId());
            resMap = (Map<String, Object>) JSONUtil.toBean(JSONUtil.parseObj(o), Map.class);
            List<MissionPhotoVideoTotalDTO.PhotoTagBean> photoList = JSONUtil.toList(JSONUtil.toJsonStr(resMap.get("photoList")), MissionPhotoVideoTotalDTO.PhotoTagBean.class);
            List<MissionPhotoVideoTotalDTO.VideoTagBean> videoList = JSONUtil.toList(JSONUtil.toJsonStr(resMap.get("videoList")), MissionPhotoVideoTotalDTO.VideoTagBean.class);
            Map<String, Object> map = Maps.newHashMap();
            resMap.put("photoList", Collections.emptyList());
            resMap.put("videoList", Collections.emptyList());
            map.put("photoCount", 0);
            map.put("videoCount", 0);
            resMap.put("total", map);
            resMap.get("videoList");
            if (ObjectUtils.isEmpty(outDO)) {
                return RestRes.ok(resMap);
            }
            List<String> nestId = outDO.getBaseNestId();
            if (CollectionUtil.isEmpty(nestId)) {
                return RestRes.ok(resMap);
            }
            if (CollectionUtil.isNotEmpty(photoList) && CollectionUtil.isNotEmpty(nestId)) {
                photoList = photoList.stream().filter(e -> nestId.contains(e.getTagId())).collect(Collectors.toList());
            }
            if (CollectionUtil.isNotEmpty(videoList) && CollectionUtil.isNotEmpty(nestId)) {
                videoList = videoList.stream().filter(e -> nestId.contains(e.getTagId())).collect(Collectors.toList());
            }
            resMap.put("photoList", photoList);
            resMap.put("videoList", videoList);
            if (CollectionUtil.isEmpty(photoList)) {
                map.put("photoCount", 0);
            }
            {
                Integer count = photoList.stream().map(e -> e.getPhotoCount()).reduce(0, Integer::sum);
                map.put("photoCount", count);
            }
            if (CollectionUtil.isEmpty(videoList)) {
                map.put("videoCount", 0);
            }
            {
                Integer count = videoList.stream().map(e -> e.getVideoCount()).reduce(0, Integer::sum);
                map.put("videoCount", count);
            }
            resMap.put("total", map);
        }
        return RestRes.ok(resMap);
    }

    @Resource
    private UosAccountService uosAccountService;

    @Override
    public RestRes getTotalNestPhotoByTag(String startTime, String endTime, String nestId) {
        String visitorId = TrustedAccessTracerHolder.get().getAccountId();
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        Long userId = Long.valueOf(visitorId);
        // 单位管理员查单位下数据，否则查用户直接关联的数据
        if (!uosAccountService.checkAccountHasOrgDefaultRole(visitorId)) {
            userId = null;
        }
        // 获取全部照片数量List
        List<MissionRecordsTotalDTO> nestList = baseMapper.getTotalNestPhotoByTag(startTime, endTime, orgCode, userId, nestId);

        Map<String, Object> resMap = new HashMap<>(1);
        resMap.put("photoList", nestList);
        return RestRes.ok(resMap);
    }


    @Override
    public RestRes getTotalNestVideoByTag(String startTime, String endTime, String nestId) {
        String visitorId = TrustedAccessTracerHolder.get().getAccountId();
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        Long userId = Long.valueOf(visitorId);
        // 单位管理员查单位下数据，否则查用户直接关联的数据
        if (!uosAccountService.checkAccountHasOrgDefaultRole(visitorId)) {
            userId = null;
        }

        // 获取全部视频数量List
        List<MissionRecordsTotalDTO> nestList = baseMapper.getTotalNestVideoByTag(startTime, endTime, orgCode, userId, nestId);
        Map<String, Object> resMap = new HashMap<>(1);
        resMap.put("videoList", nestList);
        return RestRes.ok(resMap);
    }

    @Override
    public RestRes getPhotoByNestPageList(String startTime, String endTime, String nestId, String appId, Integer page, Integer limit) {
        IPage<MissionPhotoVideoTotalDTO.PhotoNestPageBean> totalPage = null;
        if (ToolUtil.isNotEmpty(nestId)) {
            totalPage = baseMapper.getPhotoByNestPageList(new Page<>(page, limit), startTime, endTime, nestId);
        } else if (ToolUtil.isNotEmpty(appId)) {
            totalPage = baseMapper.getPhotoByAppPageList(new Page<>(page, limit), startTime, endTime, appId);
        } else {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_REQUEST_PARAMETER_ERROR.getContent()));
        }
        Map<String, Object> map = new HashMap<>(2);
        map.put("page", new PageUtils(totalPage));
        return RestRes.ok(map);
    }

    @Override
    public RestRes getVideoByNestPageList(String startTime, String endTime, String nestId, String appId, Integer page, Integer limit) {
        IPage<MissionPhotoVideoTotalDTO.VideoNestPageBean> totalPage = null;
        if (ToolUtil.isNotEmpty(nestId)) {
            totalPage = baseMapper.getVideoByNestPageList(new Page<>(page, limit), startTime, endTime, nestId);
        } else if (ToolUtil.isNotEmpty(appId)) {
            totalPage = baseMapper.getVideoByAppPageList(new Page<>(page, limit), startTime, endTime, appId);
        } else {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_REQUEST_PARAMETER_ERROR.getContent()));
        }
        Map<String, Object> map = new HashMap<>(2);
        map.put("page", new PageUtils(totalPage));
        return RestRes.ok(map);
    }

    /**
     * 识别重置
     *
     * @param recordDto
     * @return
     */
    @Override
    public RestRes resetPhotoRecord(RecordDto recordDto) {
        if (TaskIdentificationTypeEnum.INFRARED_THERMOMETRY.getValue().equals(recordDto.getRecordType())) {
            // 删除红外数据以及关联数据
            stationInfraredRecordService.lambdaUpdate()
                    //.set(StationInfraredRecordEntity::getDeleted, true)
                    .set(StationInfraredRecordEntity::getMaxTemperature, null)
                    .set(StationInfraredRecordEntity::getAvgTemperature, null)
                    .set(StationInfraredRecordEntity::getMinTemperature, null)
                    .set(StationInfraredRecordEntity::getFlag, 0)
                    .in(StationInfraredRecordEntity::getPhotoId, recordDto.getPhotoIds())
                    .eq(StationInfraredRecordEntity::getDeleted, false)
                    .update();
            stationInfraredRecordRectangleService.lambdaUpdate()
                    .set(StationInfraredRecordRectangleEntity::getDeleted, true)
                    .in(StationInfraredRecordRectangleEntity::getPhotoId, recordDto.getPhotoIds())
                    .eq(StationInfraredRecordRectangleEntity::getDeleted, false)
                    .update();
        } else if (TaskIdentificationTypeEnum.DEFECT_IDENTIFICATION.getValue().equals(recordDto.getRecordType())) {
            // 缺陷
            stationIdentifyRecordService.lambdaUpdate()
                    .set(StationIdentifyRecordEntity::getDeleted, 1)
                    .set(StationIdentifyRecordEntity::getDefectPhotoPath, "")
                    .in(StationIdentifyRecordEntity::getPhotoId, recordDto.getPhotoIds())
                    .eq(StationIdentifyRecordEntity::getDeleted, 0)
                    .update();

        } else if (TaskIdentificationTypeEnum.METER_READING.getValue().equals(recordDto.getRecordType())) {
            // 表计
            stationIdentifyRecordService.lambdaUpdate()
                    .set(StationIdentifyRecordEntity::getDeleted, 1)
                    .set(StationIdentifyRecordEntity::getMeterPhoto, "")
                    .in(StationIdentifyRecordEntity::getPhotoId, recordDto.getPhotoIds())
                    .eq(StationIdentifyRecordEntity::getDeleted, 0)
                    .update();

        } else {
            // 其他类型
            stationIdentifyRecordService.lambdaUpdate()
                    .set(StationIdentifyRecordEntity::getDeleted, 1)
                    .set(StationIdentifyRecordEntity::getDefectPhotoPath, "")
                    .in(StationIdentifyRecordEntity::getPhotoId, recordDto.getPhotoIds())
                    .eq(StationIdentifyRecordEntity::getDeleted, 0)
                    .update();
        }

        defectInfoService.lambdaUpdate()
                .set(DefectInfoEntity::getDeleted, 1)
                .in(DefectInfoEntity::getPhotoId, recordDto.getPhotoIds())
                .eq(DefectInfoEntity::getSource, recordDto.getRecordType())
                .eq(DefectInfoEntity::getDeleted, 0)
                .update();
        return RestRes.ok();
    }

//    /**
//     * 下载缺陷识别图片
//     *
//     * @param recordType
//     * @param photoIds
//     * @param response
//     */
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public void downloadPhoto(Integer recordType, String photoIds, HttpServletResponse response) {
//        if (StringUtil.isEmpty(photoIds)) {
//            return;
//        }
//        List<String> photoIdstr = Arrays.asList(photoIds.split(","));
//        List<Long> photoList = photoIdstr.stream().map(e -> Long.parseLong(e)).collect(Collectors.toList());
//        System.out.println(photoIds);
//        try {
//            String missionName = URLEncoder.encode("分析应用-" + System.currentTimeMillis() + ".zip", "UTF-8");
//            response.setCharacterEncoding("utf-8");
//            response.setContentType("application/octet-stream");
//            response.setHeader("Content-Disposition", "attachment;filename=" + missionName);
//        /*String zipPath = "";
//        File file = null;
//        if(TaskIdentificationTypeEnum.INFRARED_THERMOMETRY.getValue().equals(recordType)){         //红外
//            zipPath = globalConfig.getDownloadPath() + "infrared/" + missionName;
//            //file = new File(zipPath);
//        }else if(TaskIdentificationTypeEnum.METER_READING.getValue().equals(recordType)){
//            zipPath = globalConfig.getDownloadPath() + "meter/" + missionName;
//            //file = new File(zipPath);
//        }else {
//            // 创建压缩包，放在图片文件夹下
//            zipPath = globalConfig.getDownloadPath() + "defect/" + missionName;
//            //file = new File(zipPath);
//        }*/
//            @Cleanup
//            ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
//
//            if (TaskIdentificationTypeEnum.DEFECT_IDENTIFICATION.getValue().equals(recordType)) {
//                // 缺陷-优先下载识别、标记后的照片，没有则下载原图（missionPhoto的photoUrl）
//                downloadDefectPhoto(photoList, zipOut);
//            } else if (TaskIdentificationTypeEnum.METER_READING.getValue().equals(recordType)) {
//                // 表记-优先下载标记后的照片， 没有则下载原图（missionPhoto的photoUrl）
//                downloadMeterPhoto(photoList, zipOut);
//            } else if (TaskIdentificationTypeEnum.INFRARED_THERMOMETRY.getValue().equals(recordType)) {
//                // 红外-优先下载识别、标记等候的照片,没有则下载缩略图（missionPhoto的thumbnailUrl）
//                downloadInfraredPhoto(photoList, zipOut);
//            } else {
//                // 其他-优先下载标记后的照片，没有则下载原图（missionPhoto的photoUrl）(和缺陷一样的方法)
//                downloadDefectPhoto(photoList, zipOut);
//            }
//            // 缺陷、表记、其他：没有则下载原图（missionPhoto的photoUrl）
//            if (photoList.size() > 0) {
//                List<MissionPhotoEntity> missionPhotoEntities = this.lambdaQuery()
//                        .in(MissionPhotoEntity::getId, photoList)
//                        .eq(MissionPhotoEntity::getDeleted, 0)
//                        .list();
//                for (MissionPhotoEntity missionPhotoEntity : missionPhotoEntities) {
//                    // 下载文件
//                    InputStream input = new FileInputStream(new File(missionPhotoEntity.getPhotoUrl()));
//                    // 压缩文件到压缩包里
//                    DownLoadZipUtil.zipOutInput(missionPhotoEntity.getId() + missionPhotoEntity.getName(), input, zipOut);
//                }
//            }
//        } catch (IOException e) {
//            log.info(e.getMessage());
//            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DOWNLOAD_FAILED_FILE_COMPRESSION_ERROR.getContent()));
//        }
//    }
//
//    /**
//     * 下载缺陷照片： 缺陷-优先下载识别、标记后的照片，没有则下载原图（missionPhoto的photoUrl）
//     *
//     * @param photoList
//     * @param zipOut
//     * @throws IOException
//     */
//    private void downloadDefectPhoto(List photoList, ZipOutputStream zipOut) throws IOException {
//        List<Long> recordIds = new ArrayList<>();
//        List<StationIdentifyRecordEntity> stationIdentifyRecordEntities
//                = stationIdentifyRecordService.lambdaQuery()
//                .in(StationIdentifyRecordEntity::getPhotoId, photoList)
//                .eq(StationIdentifyRecordEntity::getDeleted, 0)
//                .list();
//        if (stationIdentifyRecordEntities != null && stationIdentifyRecordEntities.size() > 0) {
//            for (StationIdentifyRecordEntity stationIdentifyRecordEntity : stationIdentifyRecordEntities) {
//                String defectPhotoPath = stationIdentifyRecordEntity.getDefectPhotoPath();
//                if (defectPhotoPath != null && defectPhotoPath.length() > 0) {
//                    InputStream input = new FileInputStream(new File(defectPhotoPath));
//                    // 压缩文件到压缩包里
//                    DownLoadZipUtil.zipOutInput(defectPhotoPath.substring(defectPhotoPath.lastIndexOf("/")), input, zipOut);
//                    recordIds.add(stationIdentifyRecordEntity.getPhotoId());
//                }
//            }
//        }
//        photoList.removeAll(recordIds);
//    }
//
//    /**
//     * 下载表记照片 ：表记-优先下载标记后的照片，没有则下载原图（missionPhoto的photoUrl）
//     *
//     * @param photoList
//     * @param zipOut
//     * @throws IOException
//     */
//    private void downloadMeterPhoto(List photoList, ZipOutputStream zipOut) throws IOException {
//        List<Long> recordIds = new ArrayList<>();
//        List<StationIdentifyRecordEntity> stationIdentifyRecordEntities
//                = stationIdentifyRecordService.lambdaQuery()
//                .in(StationIdentifyRecordEntity::getPhotoId, photoList)
//                .eq(StationIdentifyRecordEntity::getDeleted, 0)
//                .list();
//        if (stationIdentifyRecordEntities != null && stationIdentifyRecordEntities.size() > 0) {
//            for (StationIdentifyRecordEntity stationIdentifyRecordEntity : stationIdentifyRecordEntities) {
//                Integer recordId = stationIdentifyRecordEntity.getId();
//                Long photoId = stationIdentifyRecordEntity.getPhotoId();
//                List<DefectInfoEntity> defectInfoEntities = defectInfoService.lambdaQuery()
//                        .eq(DefectInfoEntity::getPhotoId, photoId)
//                        .eq(DefectInfoEntity::getRecordId, recordId)
//                        .eq(DefectInfoEntity::getSource, MissionConstant.PhotoType.METER)
//                        .eq(DefectInfoEntity::getDeleted, 0)
//                        .list();
//                // 判断是否有标记过，有则下载标记的照片
//                if (defectInfoEntities != null && defectInfoEntities.size() > 0) {
//                    //String meterPhoto = stationIdentifyRecordEntity.getMeterPhoto();
//                    String meterPhoto = stationIdentifyRecordEntity.getDefectPhotoPath();
//                    if (meterPhoto != null && meterPhoto.length() > 0) {
//                        // 下载文件
//                        InputStream input = new FileInputStream(new File(meterPhoto));
//                        // 压缩文件到压缩包里
//                        DownLoadZipUtil.zipOutInput(meterPhoto.substring(meterPhoto.lastIndexOf("/")), input, zipOut);
//                        recordIds.add(stationIdentifyRecordEntity.getPhotoId());
//                    }
//                }
//            }
//        }
//        photoList.removeAll(recordIds);
//    }
//
//    /**
//     * 下载红外照片：红外-优先下载识别、标记等候的照片, 没有则下载缩略图（missionPhoto的thumbnailUrl）
//     *
//     * @param photoList
//     * @param zipOut
//     * @throws IOException
//     */
//    private void downloadInfraredPhoto(List photoList, ZipOutputStream zipOut) throws IOException {
//        List<Long> recordIds = new ArrayList<>();
//        List<StationInfraredRecordEntity> stationInfraredRecordEntities = stationInfraredRecordService.lambdaQuery()
//                .in(StationInfraredRecordEntity::getPhotoId, photoList)
//                .eq(StationInfraredRecordEntity::getDeleted, 0)
//                .list();
//        if (stationInfraredRecordEntities != null && stationInfraredRecordEntities.size() > 0) {
//            for (StationInfraredRecordEntity stationInfraredRecordEntity : stationInfraredRecordEntities) {
//                Long photoId = Long.valueOf(stationInfraredRecordEntity.getPhotoId());
//                // 优先下载识别、标记等候的照片, 没有则下载缩略图
//                String recordUrl = stationInfraredRecordEntity.getRecordUrl();
//                if (recordUrl != null && recordUrl.length() > 0) {
//                    InputStream input = new FileInputStream(new File(recordUrl));
//                    // 压缩文件到压缩包里
//                    DownLoadZipUtil.zipOutInput(recordUrl.substring(recordUrl.lastIndexOf("/")), input, zipOut);
//                    recordIds.add(photoId);
//                } else {
//                    // 下载缩略图
//                    MissionPhotoEntity missionPhotoEntity = this.getOne(new QueryWrapper<MissionPhotoEntity>().eq("id", photoId).eq("deleted", false));
//                    String thumbnailUrl = missionPhotoEntity.getThumbnailUrl();
//                    InputStream input = new FileInputStream(new File(thumbnailUrl));
//                    // 压缩文件到压缩包里
//                    DownLoadZipUtil.zipOutInput(thumbnailUrl.substring(thumbnailUrl.lastIndexOf("/")), input, zipOut);
//                    recordIds.add(photoId);
//                }
//            }
//        }
//        photoList.removeAll(recordIds);
//    }

    @Override
    public Map getThreshold() {
        Map<String, List<StationInfraredThresholdEntity>> map = new HashMap<String, List<StationInfraredThresholdEntity>>();
        /// 暂时写死哈尔滨的机巢uuid
//        String nestUuid = "bf9a5fbc-385e-444e-8ff5-e5d6811d4f01";
        /*String nestUuid = "c4e157eb-d1e5-440c-894f-703cf74b1161";
        Integer nestId = nestService.getIdByUuid(nestUuid);

        //初始化机巢
        nestService.initNest(nestId);

        // 哈尔滨机巢的气象信息
        AerographyInfoDto aerographyInfoDto = commonNestStateService.getAerographyInfoDto(nestUuid);
        Double temperature = aerographyInfoDto.getTemperature();
        for (int i = 1; i < 10; i++) {
            if (temperature == 0.0) {
                log.info("重试次数： " + i);
                temperature = tryAgain(nestUuid);
            }
        }*/
        List<StationInfraredThresholdEntity> stationInfraredThresholdEntity = stationInfraredThresholdService.lambdaQuery().eq(StationInfraredThresholdEntity::getName, "表计读数").list();
        map.put("value", stationInfraredThresholdEntity);
        return map;
    }

    private Double tryAgain(String nestUuid) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return commonNestStateService.getAerographyInfoDto(nestUuid).getTemperature();
    }

    @Override
    public RestRes setThreshold(String value) {
        baseMapper.setThreshold(value);
        return RestRes.ok();
    }


    /**
     * 通过上传图片时的描述信息匹配台账
     *
     * @param airlineId
     * @param mediaFile
     * @return 返回新的文件名
     */
    private StationCheckpointEntity relateToDeviceByDescription(Integer airlineId, MediaFile mediaFile) {
        FlyPoint source = new FlyPoint();
        source.setAircraftLocationLongitude(mediaFile.getLongitude());
        source.setAircraftLocationLatitude(mediaFile.getLatitude());
        source.setAircraftLocationAltitude(mediaFile.getAltitude());
        //查找航线，并解析出拍照航点
        AirLineEntity airLineEntity = airLineService.getById(airlineId);
        String airLine = airLineEntity.getWaypoints();
        if (airLine == null || airLine.isEmpty()) {
            return null;
        }
        return getCheckpoint(airLine, source);
    }

    /**
     * 获取台账
     *
     * @param airLine
     * @param source
     * @return
     */
    private StationCheckpointEntity getCheckpoint(String airLine, FlyPoint source) {
        List<FlyPoint> flyPointList = JSONObject.parseArray(airLine, FlyPoint.class).stream().filter(flyPoint -> flyPoint.getWaypointType().equals(0)).collect(Collectors.toList());
        // 获取误差值
        StationDeviceMatchEntity stationDeviceMatchEntity = stationDeviceMatchService.getOne(new QueryWrapper<StationDeviceMatchEntity>().eq("id", 1));
        Double rang = stationDeviceMatchEntity.getErrorRange();
        //获取距离最近的点
        FlyPoint target = DistanceUtil.getClosestPoint(source, flyPointList, rang);
        if (target == null) {
            return null;
        }
        //获取台账
        StationCheckpointEntity checkpoint = checkpointService.getOne(new QueryWrapper<StationCheckpointEntity>().eq("uuid", target.getCheckpointuuid()));
        return checkpoint;
    }

//    /**
//     * 上传视频
//     *
//     * @param file
//     * @param missionId
//     * @param recordId
//     */
//    private void uploadVideo(MultipartFile file, Integer missionId, Integer recordId, MediaFile mediaFile) {
//
//        //得到 文件的原始名称
//        String originalFilename = file.getOriginalFilename();
//        String missionName = missionRecordsService.getMissionName(recordId);
//        Integer flyIndex = missionRecordsService.getFlyIndex(recordId);
//        MissionEntity missionEntity = missionService.getById(missionId);
//        String videoName = missionName + "_第" + flyIndex + "次执行-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + "-" + originalFilename;
//        try {
//            // minIO方案
//            String uploadPath = DataConstant.VIDEO + missionId + "/" + recordId + "/";
//            String videoPath = MinIoUnit.upload(getMinioBucketName(), file, videoName, uploadPath);
//
//            // 截取视频第5帧，作为视频封面
////            log.info("同步的视频上传完毕，即将截取封面，架次记录id为：" + recordId);
////            String videoThumbnail = ThumbnailUtils.getScreenshot(globalConfig.getOriginPath() + videoPath, null);
////            log.info("同步的视频截取封面结束，架次记录id为：" + recordId);
//
//            StoreConfig store = geoaiUosProperties.getStore();
//            MissionVideoEntity missionVideoEntity = new MissionVideoEntity();
//            missionVideoEntity.setName(videoName);
//            missionVideoEntity.setVideoUrl(store.getOriginPath() + videoPath);
//            missionVideoEntity.setVideoSize(file.getSize());
//            missionVideoEntity.setMissionId(missionId);
//            missionVideoEntity.setMissionRecordsId(recordId);
//            missionVideoEntity.setOrgCode(missionEntity.getOrgCode());
//            if (originalFilename.contains("_WIDE")) {
//                missionVideoEntity.setType(MissionConstant.MissionVideoType.WIDE_VIDEO);
//            } else if (originalFilename.contains("_ZOOM")) {
//                missionVideoEntity.setType(MissionConstant.MissionVideoType.ZOOM_VIDEO);
//            } else if (originalFilename.contains("_THRM")) {
//                missionVideoEntity.setType(MissionConstant.MissionVideoType.THRM_VIDEO);
//            } else if (originalFilename.contains("_SCRN")) {
//                missionVideoEntity.setType(MissionConstant.MissionVideoType.SCRN_VIDEO);
//            } else {
//                missionVideoEntity.setType(MissionConstant.MissionVideoType.DOWNLOAD_VIDEO);
//            }
////            missionVideoEntity.setVideoThumbnail(videoThumbnail);
//            missionVideoEntity.setExecId(mediaFile.getExecMissionID());
//            missionVideoEntity.setFileId(mediaFile.getFileId());
//            missionVideoEntity.setFileName(mediaFile.getFileName());
//            missionVideoEntity.setMediaType(mediaFile.getMediaType());
//            LocalDateTime timeCreated = LocalDateTime.ofInstant(Instant.ofEpochMilli(mediaFile.getTimeCreated()), ZoneId.systemDefault());
//            missionVideoEntity.setTimeCreated(timeCreated);
//            Integer downloaded = mediaFile.getFileDataDownloaded() ? 1 : 0;
//            missionVideoEntity.setDownloaded(downloaded);
//            log.info("即将保存视频数据，架次记录id为：" + recordId);
//            if (missionVideoEntity.getType().equals(MissionConstant.MissionVideoType.DOWNLOAD_VIDEO)) {
//                missionVideoEntity.setMediaCreateTime(getVideoCreateTime(store.getOriginPath() + videoPath));
//            }
//            missionVideoService.save(missionVideoEntity);
//            log.info("架次记录id为 " + recordId + " 的视频上传成功");
//            if (missionVideoEntity.getType().equals(MissionConstant.MissionVideoType.DOWNLOAD_VIDEO)) {
//                log.info("发送上传轨迹文件命令");
//                pushSrt(recordId);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.info(e.getMessage());
//            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SYNCHRONIZE_VIDEO_DATA.getContent()));
//        }
//
//    }

//    /**
//     * 获取video媒体创建时间
//     *
//     * @param filePath
//     * @return
//     */
//    public LocalDateTime getVideoCreateTime(String filePath) {
//        FFmpegFrameGrabber grabber;
//        try {
//            grabber = FFmpegFrameGrabber.createDefault(filePath);
//            grabber.start();
//            String creation_time = grabber.getMetadata("creation_time");
//            grabber.stop();
//            log.info("媒体创建时间" + creation_time);
//            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//            Date date = df.parse(StringUtil.isEmpty(creation_time) ? new Date().toString() : creation_time);
//            SimpleDateFormat df1 = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
//            Date date1 = df1.parse(date.toString());
//            DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String formatTime = df2.format(date1);
//            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            return LocalDateTime.parse(formatTime, dateTimeFormatter);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    /**
     * 上传轨迹命令
     *
     * @param recordId
     */
    public void pushSrt(Integer recordId) {
        String execIdById = missionRecordsService.getExecIdById(recordId);
//        NestEntity nestEntity = missionRecordsService.getNestUuidById(recordId);
//        String uuid = nestEntity.getUuid();
        String nestUuid = missionRecordsService.getNestUuidByRecordId(recordId);
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            String uploadUrl = geoaiUosProperties.getDomain().getNginx() + "/geoai-uos-service/nest-api/missionPhoto/result/uploadSrt";
            cm.getDataManagerCf().uploadFlightPath(execIdById, uploadUrl);
            log.info("发送上传轨迹文件命令成功");
        } else {
            log.info("基站连接不成功");
        }
    }


    /**
     * 上传缺失的图片
     *
     * @param cpsFileIdList 从cps获取的全部多媒体idList
     * @param fileIdList    数据库已有的多媒体数据idList
     * @param recordId
     * @param execId
     * @param nestUuid
     * @param cm
     */
    private RestRes UploadLostMedia(List<String> cpsFileIdList, List<String> fileIdList, Integer recordId, Integer missionId, String execId, String nestUuid
            , String missionName, Integer uavWhich, ComponentManager cm, String env) {
        log.info("---------开始缺失文件上传---------");

        // 比较2个list，获取的list和数据库查到的list，全部相同，则已经全部获取完毕，直接修改dataStatus为2已同步到平台
        if (fileIdList.containsAll(cpsFileIdList)) {
            // 修改图片数据传输状态为2：传输完成
            log.info("图片List比较完全相同，无需上传图片，即将直接修改数据库状态为2传输完成");
            missionRecordsService.updateDataStatusById(MissionConstant.MissionExecDataStatus.GAIN_TO_SERVER, recordId);
            startTranDataRunnable(nestUuid, env);
            return RestRes.ok(MessageUtils.getMessageByLang(MessageEnum.GEOAI_UOS_ALL_DATA_HAS_BEEN_SYNCHRONIZED_NO_NEED_TO_REPEAT_SYNCHRONIZATION.getContent(), env));
        } else {
            // for循环查出缺失的图片
            List<String> lostFileIdList = new ArrayList<>();
            for (String fileId : cpsFileIdList) {
                if (!fileIdList.contains(fileId)) {
                    lostFileIdList.add(fileId);
                }
            }

            // 获取上传进度推送
            listenMediaStatusV2(recordId, nestUuid, cm, uavWhich);
            NestTypeEnum nestType = cm.getNestType();
            CpsUploadFileEntity cpsUploadFileEntity = this.initCpsUploadFile("", lostFileIdList, uavWhich);
            if (NestTypeEnum.G503.equals(nestType)) {
                cpsUploadFileEntity.setAutoStartUp(false);
            }
            // 请求sdk接口下载缺失的图片或视频
            cm.getMediaManagerV2().downloadToServer(cpsUploadFileEntity, (baseResult3, isSuccess1, errMsg1) -> {
                // 回调处理
                this.applicationContext.publishEvent(new UploadMediaEvent(baseResult3, isSuccess1, recordId, missionId, nestUuid));
            });
            return RestRes.ok(missionName + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SEND_UPLOAD_COMMAND_READY_TO_START_UPLOADING_SOME_DATA_TO_THE_SERVER.getContent()));
        }
    }

    /**
     * 保存全部文件到服务器
     */
    private void uploadMediaToServer(String execId, Integer recordId, Integer missionId, String nestUuid, Integer uavWhich, ComponentManager cm, String env) {
        log.info("---------开始全部文件上传---------");
        // 获取上传进度推送
        listenMediaStatusV2(recordId, nestUuid, cm, uavWhich);
        NestTypeEnum nestType = cm.getNestType();
        CpsUploadFileEntity cpsUploadFileEntity = this.initCpsUploadFile(execId, null, uavWhich);
        if (NestTypeEnum.G503.equals(nestType)) {
            cpsUploadFileEntity.setAutoStartUp(false);
        }

        // 上传媒体文件到服务器
        cm.getMediaManagerV2().downloadToServer(cpsUploadFileEntity, (baseResult3, isSuccess, errMsg) -> {
            // 回调处理
//            dealUploadMediaCallback(isSuccess, baseResult3, recordId, missionId, nestUuid);
            this.applicationContext.publishEvent(new UploadMediaEvent(baseResult3, isSuccess, recordId, missionId, nestUuid, env));
        });

    }

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
                .ossConfig(geoaiUosProperties.getOss())
                .uavWhich(uavWhich)
                .build();

    }

    /**
     * 获取上传进度推送
     *
     * @param missionRecordId
     * @param nestUuid
     * @param cm
     */
    private void listenMediaStatusV2(Integer missionRecordId, String nestUuid, ComponentManager cm, Integer uavWhich) {
        // websocket推送主题对象
        BaseManager baseManager = cm.getBaseManager();
        baseManager.listenMediaStateV2((mediaState, isSuccess, errMsg) -> {
            if (isSuccess) {
                if (!MediaStatusV2Enum.IDLE.getValue().equals(mediaState.getCurrentState()) && !MediaStatusV2Enum.UNKNOWN.getValue().equals(mediaState.getCurrentState())) {
                    Map<String, Object> map = new HashMap<>(2);
                    map.put("missionRecordId", missionRecordId);
                    map.put("mediaState", mediaState);
                    map.put("uavWhich", uavWhich);
                    String message = WebSocketRes.ok().topic(WebSocketTopicEnum.UPLOAD_MEDIA_TO_SERVER).data(map).toJSONString();
                    // 推流（成果页面、中台页面进度条）
                    pushPhotoTransMsgByWs(nestUuid, message);
                }
            }
        }, AirIndexEnum.getInstance(uavWhich));
    }

    /**
     * 上传媒体文件到服务器回调的处理
     *
     * @param isSuccess
     * @param baseResult3
     * @param recordId
     * @param nestUuid
     */
    @Override
    public void dealUploadMediaCallback(Boolean isSuccess, BaseResult3 baseResult3, Integer recordId, Integer missionId, String nestUuid, String env) {
        MissionEntity me = missionService.lambdaQuery()
                .eq(MissionEntity::getId, missionId)
                .select(MissionEntity::getName)
                .one();
        MissionRecordsEntity mre = missionRecordsService.lambdaQuery()
                .eq(MissionRecordsEntity::getId, recordId)
                .select(MissionRecordsEntity::getFlyIndex, MissionRecordsEntity::getUavWhich)
                .one();
        String missionName = me.getName() + "#" + mre.getFlyIndex() + ",";
        Map<String, Object> wsData = new HashMap<>(2);
        wsData.put("uavWhich", mre.getUavWhich());
        if (isSuccess && ResultCodeEnum.REQUEST_SUCCESS.equals(baseResult3.getCode())) {
            log.info("---------" + missionName + "同步源数据到服务器请求发送成功---------");
            // 校验同步源数据是否全部成功，并对应修改dataStatus值为成功或保存到平台异常
            log.info("成果页面即将校验是否同步成功");
            missionRecordsService.updateDataStatusById(MissionConstant.MissionExecDataStatus.GAIN_TO_SERVER, recordId);
            String message = WebSocketRes.ok().data(wsData).msg("geoai_uos_success_data_synchronization").topic(WebSocketTopicEnum.UPLOAD_MEDIA_TO_SERVER_TIP).toJSONString();
            // 图片重命名
            log.info("准备进行图片重命名，架次id为：" + recordId);
            Integer airLineId = missionService.getAirLineIdByMissionId(missionId);
            updateAirlinePhotoName(airLineId, recordId);
            // 成果页面、中台页面进度条
            pushPhotoTransMsgByWs(nestUuid, message);
            log.info("---------数据同步结束：" + message + "---------");
        } else {
            // 上传失败
            // 成果页面、中台页面进度条
            String message = WebSocketRes.err().data(wsData).msg(missionName + baseResult3.getMsg()).topic(WebSocketTopicEnum.UPLOAD_MEDIA_TO_SERVER_TIP).toJSONString();
            pushPhotoTransMsgByWs(nestUuid, message);
            // 修改图片数据传输状态为4：同步到平台异常
            missionRecordsService.updateDataStatusById(MissionConstant.MissionExecDataStatus.SERVER_ERROR, recordId);
            log.info("---------" + missionName + "同步源数据失败---------" + baseResult3.toString());
            //保存架次记录异常信息
            saveMediaErrInfo(baseResult3.getParam(), recordId, missionId, nestUuid);
        }
        startTranDataRunnable(nestUuid);
        // 如果是航线是网格类型，则照片与网格关联
        TaskEntity taskEntity = taskService.getByMissionId(missionId);
        photoAndGridRel(taskEntity, recordId);
    }

    private void saveMediaErrInfo(String errParam, Integer recordId, Integer missionId, String nestUUid) {
        try {
            log.info("记录同步异常信息saveMediaErrInfo {},recordid {}",errParam,recordId);
            MissionMediaErrStatus missionMediaErrStatus = JSON.parseObject(errParam, MissionMediaErrStatus.class);
            if(ObjectUtils.isNotEmpty(missionMediaErrStatus)&&StringUtils.isNotEmpty(errParam)) {
                missionMediaErrLogService.saveErrLog(missionMediaErrStatus, recordId, missionId, nestUUid);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取cps的多媒体列表
     */
    private Map<String, Object> getCpsFileList(ComponentManager cm, String execId, String nestUuid, Integer uavWhich) {
        // 图片或视频列表
        log.info("execId: " + execId);
        MqttResult<MediaFile> mqttResult = cm.getMediaManagerCf().getMediaInfoList(execId, AirIndexEnum.getInstance(uavWhich));
        Map<String, Object> res = new HashMap<>(4);
        if (!mqttResult.isSuccess()) {
            log.info("同步源数据失败，获取多媒体列表请求发送失败");
            res.put("success", false);
            res.put("cpsFileList", null);
            res.put("msg", mqttResult.getMsg());
            return res;
        }
        res.put("success", true);
        res.put("msg", "success");
        List<MediaFile> resList = mqttResult.getResList();
        if (CollectionUtil.isNotEmpty(resList)) {
            res.put("cpsFileList", resList.stream().map(MediaFile::getFileId).collect(Collectors.toList()));
        } else {
            res.put("cpsFileList", Collections.emptyList());
        }
        return res;
    }

    /**
     * 推流（成果页面、中台页面进度条）
     *
     * @param uuid
     * @param message
     */
    @Override
    public void pushPhotoTransMsgByWs(String uuid, String message) {
        ChannelService.sendMessageByType3Channel(uuid, message);
        ChannelService.sendMessageByType4Channel(uuid, message);
    }

    /**
     * 判断是否以有架次：
     * 有-直接获取；
     * 无-判断是否有任务：
     * 有-获取任务，创建架次
     * 无-创建任务，创建架次
     */
    private Map<String, Object> createMissionRecord(String orgCode, String deviceId, Integer recordId, String fileOriginalFilename) {
        String photoName;
        String uploadPath = DataConstant.APP + File.separator;
        Integer missionId;
        if (recordId != null) {
            // recordId 不为null则为航线的该任务架次，直接数据库查找
            MissionRecordsEntity missionRecordsEntity = missionRecordsService.getById(recordId);
            missionId = missionRecordsEntity.getMissionId();
            String missionName = missionRecordsService.getMissionName(recordId);
            photoName = missionName + "_第" + missionRecordsEntity.getFlyIndex() + "次执行-" +
                    new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + "-" + fileOriginalFilename;
            uploadPath = uploadPath + missionId + "/" + recordId + "/";
        } else {
            // recordId 为null则为手动飞行的任务架次
            if (ToolUtil.isEmpty(orgCode) || ToolUtil.isEmpty(deviceId)) {
                throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_REQUEST_PARAMETER_ERROR.getContent()));
            }
            // 获取当前单位的手动飞行任务架次
            Integer taskId = taskService.getAPPManualFlyTaskId(orgCode);
            // 当前单位没有手动飞行任务，则生成一条手当前单位的手动飞行任务、任务架次
            if (taskId == null) {
                // 创建任务
                TaskEntity taskEntity = new TaskEntity();
                taskEntity.setName("app手动飞行任务");
                taskEntity.setMold(1);
                taskEntity.setType(TaskModeEnum.MANUAL_FLY.getValue());
                taskEntity.setOrgCode(orgCode);
                taskService.save(taskEntity);

                // 创建任务架次
                MissionEntity missionEntity = new MissionEntity();
                missionEntity.setName("app手动飞行任务-架次");
                missionEntity.setUuid(UUID.randomUUID().toString().replace("-", "").toLowerCase());
                missionEntity.setAirLineId(-1);
                missionEntity.setTaskId(taskEntity.getId());
                missionEntity.setMissionParamId(-1);
                missionService.save(missionEntity);
                missionId = missionEntity.getId();

            } else {
                // 已有任务、任务架次无需创建，直接获取任务架次的id
                // 根据taskId查询对应的missionId
                QueryWrapper<MissionEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("deleted", false).eq("task_id", taskId);
                MissionEntity missionEntity = missionService.getOne(queryWrapper);
                missionId = missionEntity.getId();
            }

            // 获取appId
//            SysAppEntity sysAppEntity = sysAppService.getOne(new QueryWrapper<SysAppEntity>().lambda()
//                    .eq(SysAppEntity::getDeviceId, deviceId).eq(SysAppEntity::getDeleted, false).select(SysAppEntity::getId));
//            if (ToolUtil.isEmpty(sysAppEntity)) {
//                return null;
//            }
//
//            Integer appId = ToolUtil.isNotEmpty(sysAppEntity) ? sysAppEntity.getId() : null;
            String appId = baseAppService.getAppIdByDeviceId(deviceId);

            // 保存一条手动飞行任务架次执行记录
            MissionRecordsEntity missionRecordsEntity = new MissionRecordsEntity();
            missionRecordsEntity.setMissionId(missionId);
            missionRecordsEntity.setExecId(UUID.randomUUID().toString().replace("-", "").toLowerCase());
            missionRecordsEntity.setStatus(2);
            missionRecordsEntity.setDataStatus(0);
            missionRecordsEntity.setFlyIndex(1);
            missionRecordsEntity.setGainDataMode(0);
            missionRecordsEntity.setGainVideo(0);
            missionRecordsEntity.setBaseAppId(appId);
            missionRecordsService.save(missionRecordsEntity);
            recordId = missionRecordsEntity.getId();

            photoName = "app手动飞行任务-架次_第" + missionRecordsEntity.getFlyIndex() + "次执行-" +
                    new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + "-" + fileOriginalFilename;
            uploadPath = uploadPath + missionId + "/" + recordId + "/";
        }

        Map<String, Object> map = new HashMap<>(3);
        map.put("photoName", photoName);
        map.put("uploadPath", uploadPath);
        map.put("missionId", missionId);
        return map;
    }

    /**
     * 对2个list进行合并、排序、截取前5条
     *
     * @param nestList
     * @param appList
     * @return
     */
    private List<MissionRecordsTotalDTO> sortAndSubList(List<MissionRecordsTotalDTO> nestList, List<MissionRecordsTotalDTO> appList, Integer type) {
        List<MissionRecordsTotalDTO> totalList = new ArrayList<>();
        totalList.addAll(appList);
        totalList.addAll(nestList);
        if (totalList.size() > 1) {
            // 排序
            if (type == 1) {
                Collections.sort(totalList, (MissionRecordsTotalDTO o1, MissionRecordsTotalDTO o2) -> {
                    if ((o1.getPhotoCount() > o2.getPhotoCount())) {
                        return -1;
                    }
                    if (o1.getPhotoCount().equals(o2.getPhotoCount())) {
                        return 0;
                    }
                    return 1;
                });
            } else if (type == 2) {
                Collections.sort(totalList, (MissionRecordsTotalDTO o1, MissionRecordsTotalDTO o2) -> {
                    if ((o1.getVideoCount() > o2.getVideoCount())) {
                        return -1;
                    }
                    if (o1.getVideoCount().equals(o2.getVideoCount())) {
                        return 0;
                    }
                    return 1;
                });
            }
        }
        // 筛选前5条
        if (totalList.size() > 5) {
            totalList = totalList.subList(0, 5);
        }
        return totalList;
    }

    /**
     * 航线照片重命名，并且绑定图片的识别类型以及标签
     */
    @Transactional
    @Override
    public RestRes updateAirlinePhotoName(Integer airLineId, Integer missionRecordId, boolean push) {
        return pushAnalysis2(airLineId, missionRecordId, push);
    }

    @Override
    public RestRes updateAirlinePhotoName(Integer airLineId, Integer missionRecordId) {
        return this.updateAirlinePhotoName(airLineId, missionRecordId, true);
    }

    @Override
    public RestRes pushAnalysis2(Integer airLineId, Integer missionRecordId, boolean push) {
        try {
            log.info("#MissionPhotoServiceImpl.pushAnalysis2# missionRecordId={}", missionRecordId);
            log.info("进入自动推送分析方法。。。");
            //获取航线数据
            AirLineEntity airLineEntity = airLineService.getOne(new QueryWrapper<AirLineEntity>().eq("id", airLineId).eq("deleted", false));
            log.info("#MissionPhotoServiceImpl.pushAnalysis2# missionRecordId={}, airLineEntity={}", missionRecordId, airLineEntity);
            if (ObjectUtil.isEmpty(airLineEntity)) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_ROUTE_CANNOT_BE_QUERIED_AND_THE_PUSH_ANALYSIS_FAILED.getContent()));
            }
            TaskEntity taskEntity = taskService.getNestIdByAirLineId(airLineId);
            Integer mold = taskEntity.getMold();
            Integer nestType = -1;
            if (mold == null) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_I_CANT_TELL_WHETHER_THE_ROUTE_IS_FROM_THE_NEST_OR_THE_MOBILE_TERMINAL.getContent()));
            } else if (mold == 0) {
//                Integer nestId = taskEntity.getNestId();
//                NestEntity nestEntity = nestService.getById(nestId);
//                if (ObjectUtil.isEmpty(nestEntity)) {
//                    return RestRes.err("查询不到航线对应的机巢，推送分析失败");
//                }
//                nestType = nestEntity.getType();
                String baseNestId = taskEntity.getBaseNestId();
                NestTypeEnum nestTypeEnum = baseNestService.getNestTypeByNestIdCache(baseNestId);
                if (Objects.isNull(nestTypeEnum)) {
                    return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANT_FIND_THE_CORRESPONDING_NEST_OF_THE_ROUTE_PUSH_ANALYSIS_FAILED.getContent()));
                }
                nestType = nestTypeEnum.getValue();
            }

            //航线的标签列表
            List<Integer> tagIdList = sysTaskTagService.selectTagIdByAirLineId(airLineEntity.getId());
            //把航点数据解析成每个航点
            Map<Integer, Map<String, Object>> propAndByNameMapGroup = analysisAirLineEntity(airLineEntity, taskEntity.getType());
            log.info("#MissionPhotoServiceImpl.pushAnalysis2# missionRecordId={}, propAndByNameMapGroup={}", missionRecordId, propAndByNameMapGroup);

            //获取照片列表
            List<MissionPhotoEntity> missionPhotoEntityList = this.list(new QueryWrapper<MissionPhotoEntity>().lambda()
                    .eq(MissionPhotoEntity::getMissionRecordsId, missionRecordId)
                    .eq(MissionPhotoEntity::getDeleted, false)
                    .orderByAsc(MissionPhotoEntity::getCreateTime));

            log.info("#MissionPhotoServiceImpl.pushAnalysis2# missionRecordId={}, missionPhotoEntityList={}", missionRecordId, missionPhotoEntityList);
            if (CollectionUtil.isEmpty(missionPhotoEntityList)) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILURE_TO_QUERY_THE_PHOTO_OF_THE_ROUTE_PUSH_ANALYSIS_FAILURE.getContent()));
            }

            //绑定照片的photoType
//            log.info("#MissionPhotoServiceImpl.pushAnalysis2# missionRecordId={}", missionRecordId);
//            bindPhotoMarkAndPhotoType(missionPhotoEntityList, nestType, mold);
            log.info("#MissionPhotoServiceImpl.pushAnalysis2# missionRecordId={}", missionRecordId);
            handleWaypointIndex(airLineEntity.getType(), taskEntity.getType(), mold, missionPhotoEntityList);
            log.info("#MissionPhotoServiceImpl.pushAnalysis2# missionRecordId={}", missionRecordId);
            missionPhotoListRename(missionPhotoEntityList, airLineEntity, taskEntity.getType());
            log.info("#MissionPhotoServiceImpl.pushAnalysis2# missionRecordId={}, propAndByNameMapGroup={}", missionRecordId, propAndByNameMapGroup);
            log.info("#MissionPhotoServiceImpl.pushAnalysis2# missionRecordId={}, missionPhotoEntityList={}", missionRecordId, missionPhotoEntityList);
            // 入库，修改照片名称name,修改photoMark、photoType
            if (CollectionUtil.isNotEmpty(missionPhotoEntityList)) {
                this.updateBatchById(missionPhotoEntityList);
            }

            List<Long> photoIdList = missionPhotoEntityList.stream().map(MissionPhotoEntity::getId).collect(Collectors.toList());
            //保存照片与Tag的关系
            List<MissionPhotoTagRelEntity> missionPhotoTagRelEntityList = bindMissionPhotoTagRelEntityList(tagIdList, missionPhotoEntityList);
            log.info("#MissionPhotoServiceImpl.pushAnalysis2# missionRecordId={}, missionPhotoTagRelEntityList={}", missionRecordId, missionPhotoTagRelEntityList);
            if (CollectionUtil.isNotEmpty(missionPhotoTagRelEntityList)) {
                missionPhotoTagRelService.remove(new QueryWrapper<MissionPhotoTagRelEntity>().in("mission_photo_id", photoIdList));
                missionPhotoTagRelService.saveBatch(missionPhotoTagRelEntityList);
            }

            if (!push) {
                return RestRes.ok();
            }

            //保存照片与type的关系
            List<MissionPhotoTypeRelEntity> missionPhotoTypeRelEntityList = bindMissionPhotoTypeRelEntityList(propAndByNameMapGroup, missionPhotoEntityList, taskEntity.getType());
            log.info("#MissionPhotoServiceImpl.pushAnalysis2# missionRecordId={}, missionPhotoTypeRelEntityList={}", missionRecordId, missionPhotoTypeRelEntityList);
            if (CollectionUtil.isNotEmpty(missionPhotoTypeRelEntityList)) {
                missionPhotoTypeRelService.remove(new QueryWrapper<MissionPhotoTypeRelEntity>().in("mission_photo_id", photoIdList));
                missionPhotoTypeRelService.saveBatch(missionPhotoTypeRelEntityList);
            }
            analysis(missionPhotoTypeRelEntityList, missionRecordId);
            // 自动推送 网格化与照片与网格做关联
            photoAndGridRel(taskEntity, missionRecordId);

            return RestRes.ok();
        } catch (Exception e) {
            log.error("#MissionPhotoServiceImpl.pushAnalysis2# missionRecordId={}", missionRecordId, e);
            return RestRes.err();
        }
    }

    /**
     * 手工与自动推送关联网格
     */
    @Override
    public void photoAndGridRel(TaskEntity taskEntity, Integer missionRecordId) {
        if (!ObjectUtil.isEmpty(taskEntity) && TaskModeEnum.GRID.getValue().equals(taskEntity.getType())) {
            List<Long> photos = baseMapper.getPhotoByRecordId(missionRecordId);
            Integer taskId = taskEntity.getId();
            String orgCode = taskEntity.getOrgCode();
            log.info("#MissionPhotoServiceImpl.pushAnalysis2.photoAndGridRel# photoIdListToGrid: {} ，missionRecordId: {} , taskId: {} , orgCode: {}", photos , missionRecordId , taskId, orgCode);
            Boolean gridRelRes = gridMissionService.relPhotoToGrid(photos, taskId, missionRecordId, orgCode);
            log.info("#MissionPhotoServiceImpl.pushAnalysis2.photoAndGridRel# gridRelRes={}", gridRelRes);
        }
    }

    @Resource
    private PowerMeterDataService powerMeterDataService;

    private void analysis(List<MissionPhotoTypeRelEntity> missionPhotoTypeRelEntityList, Integer missionRecordId) {
        // 分析统计
        List<MissionPhotoTypeRelEntity> relEntityList = missionPhotoTypeRelEntityList
                .stream().filter(bean -> bean.getType() == RoleIdenValueEnum.ANALYSIS_STATISTICS_ZH.getIdenValue())
                .collect(Collectors.toList());
        log.info("#MissionPhotoServiceImpl.pushAnalysis2.analysis# relEntityList={}", relEntityList);
        if (CollUtil.isNotEmpty(relEntityList)) {
            Set<Integer> set = relEntityList.stream().map(MissionPhotoTypeRelEntity::getMissionPhotoId).collect(Collectors.toSet());
            try {
                dataScenePhotoService.push(Lists.newArrayList(set));
            } catch (Exception e) {
                log.error("#MissionPhotoServiceImpl.pushAnalysis2.analysis# relEntityList={}", relEntityList, e);
            }
        }
        Map<Integer, List<MissionPhotoTypeRelEntity>> entityGroupMap = missionPhotoTypeRelEntityList.stream()
                .collect(Collectors.groupingBy(MissionPhotoTypeRelEntity::getType));

        // 电力统计-表记读数
        List<MissionPhotoTypeRelEntity> entityList = entityGroupMap.get(RoleIdenValueEnum.ABNORMAL_FIND_DL_BJDS_NEW.getIdenValue());
        log.info("#MissionPhotoServiceImpl.pushAnalysis2.analysis# bjds entityList={}", entityList);
        if (CollUtil.isNotEmpty(entityList)) {
            Set<Integer> set = entityList.stream().map(MissionPhotoTypeRelEntity::getMissionPhotoId).collect(Collectors.toSet());
            try {
                powerMeterDataService.push(missionRecordId, Lists.newArrayList(set));
            } catch (Exception e) {
                log.error("#MissionPhotoServiceImpl.pushAnalysis2.analysis# bjds entityList={}", entityList, e);
            }
        }
        // 电力统计-红外测温
        List<MissionPhotoTypeRelEntity> infraredEntityList = entityGroupMap.get(RoleIdenValueEnum.ABNORMAL_FIND_DL_HWCW_NEW.getIdenValue());
        if (CollUtil.isNotEmpty(infraredEntityList)) {
            log.info("#MissionPhotoServiceImpl.pushAnalysis2.analysis# hwcw infraredEntityList={}", infraredEntityList);
            Set<Integer> set = infraredEntityList.stream().map(MissionPhotoTypeRelEntity::getMissionPhotoId).collect(Collectors.toSet());
            try {
                powerMeterDataService.pushInfrared(missionRecordId, Lists.newArrayList(set));
            } catch (Exception e) {
                log.error("#MissionPhotoServiceImpl.pushAnalysis2.analysis# hwcw infraredEntityList={}", infraredEntityList, e);
            }
        }
        // 电力统计-缺陷识别
        List<MissionPhotoTypeRelEntity> defectEntityList = entityGroupMap.get(RoleIdenValueEnum.ABNORMAL_FIND_DL_QXSB_NEW.getIdenValue());
        if (CollUtil.isNotEmpty(defectEntityList)) {
            log.info("#MissionPhotoServiceImpl.pushAnalysis2.analysis# qxsb defectEntityList={}", defectEntityList);
            Set<Integer> set = defectEntityList.stream().map(MissionPhotoTypeRelEntity::getMissionPhotoId).collect(Collectors.toSet());
            try {
                powerMeterDataService.pushDefect(missionRecordId, Lists.newArrayList(set));
            } catch (Exception e) {
                log.error("#MissionPhotoServiceImpl.pushAnalysis2.analysis# qxsb defectEntityList={}", defectEntityList, e);
            }
        }
    }

    private String dealPhotoName(String photoName) {
        if (org.springframework.util.StringUtils.hasText(photoName)) {
            return photoName.replaceAll("#", "");
        }
        return photoName;
    }

    private String getDefaultName(String photoName, int photoType, int i) {
        String photoCode = PhotoTypeEnum.getCodeByValue(photoType);
        String code = String.format("%04d", i + 1);
        photoName = photoName.substring(0, photoName.lastIndexOf("."));
        if (photoName.length() > 6) {
            photoName = photoName.substring(0, 6);
        }
        String defaultName = photoName + "_" + photoCode + "_" + code;
        return defaultName;
    }


    /**
     * 根据航线的json，获取对应的设备台账List
     *
     * @param airLineEntity
     * @return
     */
    private List<Map<String, Object>> getCheckPointNameListByAirLineJson(AirLineEntity airLineEntity) {
        // 设备名称List
        List<Map<String, Object>> checkPointList = new ArrayList<>();
        JSONArray jsonArray = JSONArray.parseArray(airLineEntity.getWaypoints());
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            Integer waypointType = object.getInteger("waypointType");
            if (WaypointTypeEnum.PHOTO_POINT.getValue() == waypointType) {
                JSONArray photoPropListJsonArray = object.getJSONArray("photoPropList");
                if (ToolUtil.isNotEmpty(photoPropListJsonArray)) {
                    List<Integer> photoPropList = photoPropListJsonArray.toJavaList(Integer.class);
                    String byname = object.getString("byname");
                    Map<String, Object> map = new HashMap<>(2);
                    map.put("photoPropList", photoPropList);
                    map.put("byname", byname);
                    checkPointList.add(map);
                }
            }
        }
        return checkPointList;
    }

    /**
     * 获取树形名字
     *
     * @param a:不同名字的图片名称
     * @param photoMap：图片信息
     * @return
     */
    private JSONArray getNameList(List<String> a, Map<String, Object> photoMap) {
        Map<String, List<Map<String, String>>> trees = new HashMap<>();
        Map<String, String> item = new HashMap<String, String>();

        for (String str : a) {
            List<Map<String, String>> list = new ArrayList<>();
            String strArr[] = str.split("_");
            for (int i = 0; i < strArr.length; i++) {
                item = new HashMap<String, String>();
                if (i == 0) {
                    item.put("id", strArr[i]);
                    item.put("parentId", "");
                } else {
                    item.put("id", strArr[i]);
                    item.put("parentId", strArr[i - 1]);
                }
                if (i == strArr.length - 1) {
                    item.put("photoName", str);
                }
                list.add(item);
            }
            trees.put(str, list);
        }

        // 开始递归
        JSONArray jsonArray = new JSONArray();
        for (String key : trees.keySet()) {
            List<Map<String, String>> list = trees.get(key);
            for (Map<String, String> itemT : list) {
                if ("".equals(itemT.get("parentId"))) {
                    JSONObject jsonObject = new JSONObject();
                    if (!photoMap.isEmpty() && itemT.get("photoName") != null) {
                        String photoName = itemT.get("photoName");
                        jsonObject.put("photoInfo", photoMap.get(photoName));
                        jsonObject.put("id", itemT.get("id").substring(0, itemT.get("id").lastIndexOf(".")));
                        JSONArray jsonArray1 = new JSONArray();
                        jsonObject.put("children", jsonArray1);
                    } else {
                        jsonObject.put("id", itemT.get("id"));
                        seracherItem(jsonObject, list, photoMap);
                    }
                    jsonArray.add(jsonObject);
                }
            }
        }
        return jsonArray;
    }

    /**
     * 获取树形名字
     *
     * @param a
     * @return
     */
    private JSONArray getNameList1(List<String> a, Map<String, Object> photoMap) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> item = new HashMap<String, String>();

        for (String str : a) {
            String strArr[] = str.split("_");
            for (int i = 0; i < strArr.length; i++) {
                item = new HashMap<String, String>();
                if (i == 0) {
                    item.put("id", strArr[i]);
                    item.put("parentId", "");
                } else {
                    item.put("id", strArr[i]);
                    item.put("parentId", strArr[i - 1]);
                }
                if (i == strArr.length - 1) {
                    item.put("photoName", str);
                }
                // 判断是否已经存在值相同的
                boolean isAdd = true;
                for (Map<String, String> itemT : list) {
                    if (itemT.get("id").equals(item.get("id")) && itemT.get("parentId").equals(item.get("parentId"))) {
                        isAdd = false;
                    }
                }
                if (isAdd) {
                    list.add(item);
                }

            }
        }

        // 开始递归
        JSONArray jsonArray = new JSONArray();
        for (Map<String, String> itemT : list) {
            if ("".equals(itemT.get("parentId"))) {
                JSONObject jsonObject = new JSONObject();
                if (!photoMap.isEmpty() && itemT.get("photoName") != null) {
                    String photoName = itemT.get("photoName");
                    jsonObject.put("photoInfo", photoMap.get(photoName));
                    jsonObject.put("id", itemT.get("id").substring(0, itemT.get("id").lastIndexOf(".")));
                    JSONArray jsonArray1 = new JSONArray();
                    jsonObject.put("children", jsonArray1);
                } else {
                    jsonObject.put("id", itemT.get("id"));
                    seracherItem(jsonObject, list, photoMap);
                }
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    /**
     * 递归调用
     *
     * @param jsonObject
     * @param list
     */
    private void seracherItem(JSONObject jsonObject, List<Map<String, String>> list, Map<String, Object> photoMap) {
        JSONArray jsonArray = new JSONArray();
        jsonObject.put("children", jsonArray);
        for (Map<String, String> itemT : list) {
            if (jsonObject.get("id").equals(itemT.get("parentId"))) {
                JSONObject jsonObjectT = new JSONObject();
                if (!photoMap.isEmpty() && itemT.get("photoName") != null) {
                    String photoName = itemT.get("photoName");
                    jsonObjectT.put("photoInfo", photoMap.get(photoName));
                    jsonObjectT.put("id", itemT.get("id").substring(0, itemT.get("id").lastIndexOf(".")));
                } else {
                    jsonObjectT.put("id", itemT.get("id"));
                }
                jsonArray.add(jsonObjectT);
                seracherItem(jsonObjectT, list, photoMap);
            }
        }
    }

    private void buildMissionPhotoTagRelEntityList(Integer missionPhotoId, List<Integer> tagIdList, List<MissionPhotoTagRelEntity> missionPhotoTagRelEntityList) {
        if (CollectionUtil.isNotEmpty(tagIdList)) {
            for (Integer tagId : tagIdList) {
                MissionPhotoTagRelEntity missionPhotoTagRelEntity = new MissionPhotoTagRelEntity();
                missionPhotoTagRelEntity.setMissionPhotoId(missionPhotoId);
                missionPhotoTagRelEntity.setTagId(tagId);
                missionPhotoTagRelEntityList.add(missionPhotoTagRelEntity);
            }
        }
    }

    private void buildMissionPhotoIdenTypeRelEntityList(Integer missionPhotoId, List<Integer> photoPropList, List<MissionPhotoTypeRelEntity> missionPhotoTypeRelEntityList) {
        if (CollectionUtil.isNotEmpty(photoPropList)) {
            for (int j = 0; j < photoPropList.size(); j++) {
                MissionPhotoTypeRelEntity missionPhotoTypeRelEntity = new MissionPhotoTypeRelEntity();
                missionPhotoTypeRelEntity.setMissionPhotoId(missionPhotoId);
                missionPhotoTypeRelEntity.setType(photoPropList.get(j));
                missionPhotoTypeRelEntityList.add(missionPhotoTypeRelEntity);
            }
        }
    }

    private void buildMissionPhotoEntityList(String byname, Long photoId, String nestId, List<MissionPhotoEntity> missionPhotoEntityList) {
        MissionPhotoEntity missionPhotoEntity = new MissionPhotoEntity();
        missionPhotoEntity.setId(photoId);
        if (StrUtil.isNotEmpty(byname)) {
            missionPhotoEntity.setName(byname + ".JPG");
        }
        if (nestId != null) {
            missionPhotoEntity.setBaseNestId(nestId);
        }
        missionPhotoEntityList.add(missionPhotoEntity);
    }

    private void sortPhotoByDjiName(List<MissionPhotoEntity> missionPhotoEntities) {
        if (CollectionUtil.isNotEmpty(missionPhotoEntities)) {
            missionPhotoEntities.sort((o1, o2) -> {
                String fileName1 = o1.getFileName();
                String fileName2 = o2.getFileName();
                if (fileName1 != null) {
                    if (fileName1.contains("THRM") || fileName1.contains("WIDE") || fileName1.contains("ZOOM")) {
                        String[] fs1 = fileName1.split("_");
                        String[] fs2 = fileName2.split("_");
                        if (fs1.length == 4) {
                            String time1 = fs1[1];
                            String time2 = fs2[1];
                            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                            return LocalDateTime.parse(time1, df).compareTo(LocalDateTime.parse(time2, df));
                        }

                    } else {
                        String[] fs1 = fileName1.split("_");
                        String[] fs2 = fileName2.split("_");
                        if (fs1.length == 2) {
                            String index1 = fs1[1].split("\\.")[0];
                            String index2 = fs2[1].split("\\.")[0];
                            return Integer.parseInt(index1) - Integer.parseInt(index2);
                        }

                    }
                }
                return 0;
            });
        }
    }

    private Map<Integer, Map<String, Object>> analysisAirLineEntity(AirLineEntity airLineEntity, Integer taskType) {
        if (ObjectUtil.isNotEmpty(airLineEntity)) {
            if (airLineEntity.getType() == AirLineTypeEnum.POINT_CLOUD_AIR_LINE.getValue()) {
                return analysisPointCloudAirLine(airLineEntity);
            } else if (airLineEntity.getType() == AirLineTypeEnum.UNIFY_AIR_LINE.getValue()) {
                return analysisUnifyAirLine(airLineEntity, taskType);
            } else if (airLineEntity.getType() == AirLineTypeEnum.NEST_AIR_LINE.getValue()) {
                return analysisNestAirLine(airLineEntity);
            }
        }
        return Collections.emptyMap();
    }


    private List<String> listAirLineBynames(AirLineEntity airLineEntity, Integer taskType) {
        try {
            if (ObjectUtil.isNotEmpty(airLineEntity)) {
                if (airLineEntity.getType() == AirLineTypeEnum.POINT_CLOUD_AIR_LINE.getValue()) {
                    return listPointCloudAirLineBynames(airLineEntity);
                } else if (airLineEntity.getType() == AirLineTypeEnum.UNIFY_AIR_LINE.getValue()) {
                    return listUnifyAirLineBynames(airLineEntity, taskType);
                } else if (airLineEntity.getType() == AirLineTypeEnum.NEST_AIR_LINE.getValue()) {
                    return listNestAirLineBynames(airLineEntity);
                }
            }
        } catch (Exception e) {
            log.error("#MissionPhotoServiceImpl.listAirLineBynames# airLineEntity={}", airLineEntity, e);
        }
        return Collections.emptyList();
    }

    private Map<Integer, Map<String, Object>> analysisPointCloudAirLine(AirLineEntity airLineEntity) {
        JSONArray jsonArray = JSONArray.parseArray(airLineEntity.getOriginalWaypoints());
        Map<Integer, Map<String, Object>> resMap = new HashMap<>(jsonArray.size());
        int index = 1;
        //有拍照动作，证明是拍照点
        String photoAction = String.valueOf(ActionTypeEnum.START_TAKE_PHOTO.getValue());
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            String wayPointAction = object.getString("wayPointAction");
            JSONArray photoPropListJsonArray = object.getJSONArray("photoPropList");
            List<Integer> list;
            if (CollectionUtil.isNotEmpty(photoPropListJsonArray)) {
                list = photoPropListJsonArray.toJavaList(Integer.class);
            } else {
                list = Collections.emptyList();
            }
            if (wayPointAction.contains(photoAction)) {
                for (String s : wayPointAction.split(",")) {
                    if (photoAction.equals(s.trim())) {
                        Map<String, Object> map = new HashMap<>(2);
                        map.put("photoPropList", list);
                        map.put("byname", object.getString("byname"));
                        resMap.put(index++, map);
                    }
                }
            }
        }
        return resMap;
    }

    private List<String> listPointCloudAirLineBynames(AirLineEntity airLineEntity) {
        if (StringUtils.isBlank(airLineEntity.getOriginalWaypoints()) || !JSONUtil.isTypeJSONArray(airLineEntity.getOriginalWaypoints())) {
            return Collections.emptyList();
        }
        JSONArray jsonArray = JSONArray.parseArray(airLineEntity.getOriginalWaypoints());
        List<String> bynameList = new ArrayList<>(jsonArray.size());
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            String wayPointAction = object.getString("wayPointAction");
            // 1 拍照动作
            if (wayPointAction.contains("1")) {
                for (String s : wayPointAction.split(",")) {
                    if ("1".equals(s.trim())) {
                        bynameList.add(object.getString("byname"));
                    }
                }
            }
        }
        return bynameList;
    }

    private Map<Integer, Map<String, Object>> analysisUnifyAirLine(AirLineEntity airLineEntity, Integer taskType) {
        if (Objects.equals(TaskModeEnum.DJI_KML.getValue(), taskType)) {
            UnifyAirLineFormatDto djiKmlUnifyAirLineFormatDto = JSONObject.parseObject(airLineEntity.getDjiWaypoints(), UnifyAirLineFormatDto.class);
            JSONArray points = (JSONArray) djiKmlUnifyAirLineFormatDto.getMapConfigs().get("points");
            List<UnifyPoint> unifyPointList = JSONArray.parseArray(points.toJSONString(), UnifyPoint.class);
            return analysisDjiKmlUnifyAirLine(unifyPointList);
        } else {
            Map<Integer, Map<String, Object>> resMap = new HashMap<>();
            String waypoints = airLineEntity.getWaypoints();
            UnifyAirLineFormatDto unifyAirLineFormatDto = JSONObject.parseObject(waypoints, UnifyAirLineFormatDto.class);
            String byname = null;
            List<Integer> photoPropList = null;
            TaskModeEnum mode = unifyAirLineFormatDto.getMode();
            switch (mode) {
                case LINEAR:
                    JSONObject linearParamJson = (JSONObject) unifyAirLineFormatDto.getLineConfigs().get(TaskModeEnum.LINEAR.name());
                    LinearParam linearParam = JSONObject.parseObject(linearParamJson.toJSONString(), LinearParam.class);
                    byname = linearParam.getByname();
                    photoPropList = linearParam.getPhotoPropList();
                    break;
                case PANORAMA:
                    JSONObject panoramaParamJson = (JSONObject) unifyAirLineFormatDto.getLineConfigs().get(TaskModeEnum.PANORAMA.name());
                    PanoramaParam panoramaParam = JSONObject.parseObject(panoramaParamJson.toJSONString(), PanoramaParam.class);
                    byname = panoramaParam.getByname();
                    photoPropList = panoramaParam.getPhotoPropList();
                    break;
                case ORTHOPHOTO:
                    JSONObject orthophotoParamJson = (JSONObject) unifyAirLineFormatDto.getLineConfigs().get(TaskModeEnum.ORTHOPHOTO.name());
                    OrthophotoParam orthophotoParam = JSONObject.parseObject(orthophotoParamJson.toJSONString(), OrthophotoParam.class);
                    byname = orthophotoParam.getByname();
                    photoPropList = orthophotoParam.getPhotoPropList();
                    break;
                case SLOPEPHOTO:
                    JSONObject slopePhotoParamJson = (JSONObject) unifyAirLineFormatDto.getLineConfigs().get(TaskModeEnum.SLOPEPHOTO.name());
                    SlopePhotoParam slopePhotoParam = JSONObject.parseObject(slopePhotoParamJson.toJSONString(), SlopePhotoParam.class);
                    byname = slopePhotoParam.getByname();
                    photoPropList = slopePhotoParam.getPhotoPropList();
                    break;
                case GRID:
                    JSONObject gridPhotoParamJson = (JSONObject) unifyAirLineFormatDto.getLineConfigs().get(TaskModeEnum.GRID.name());
                    GridPhotoParam gridPhotoParam = JSONObject.parseObject(gridPhotoParamJson.toJSONString(), GridPhotoParam.class);
                    byname = gridPhotoParam.getByname();
                    photoPropList = gridPhotoParam.getPhotoPropList();
                    break;
                default:
                    break;
            }

            Map<String, Object> propAndBynameMap = new HashMap<>(2);
            propAndBynameMap.put("byname", byname);
            propAndBynameMap.put("photoPropList", photoPropList);
            resMap.put(1, propAndBynameMap);
            return resMap;
        }
    }

    private List<String> listUnifyAirLineBynames(AirLineEntity airLineEntity, Integer taskType) {
        if (Objects.equals(TaskModeEnum.DJI_KML.getValue(), taskType)) {
            UnifyAirLineFormatDto djiKmlUnifyAirLineFormatDto = JSONObject.parseObject(airLineEntity.getDjiWaypoints(), UnifyAirLineFormatDto.class);
            JSONArray points = (JSONArray) djiKmlUnifyAirLineFormatDto.getMapConfigs().get("points");
            List<UnifyPoint> unifyPointList = JSONArray.parseArray(points.toJSONString(), UnifyPoint.class);
            return analysisDjiKmlUnifyAirLineRename(unifyPointList);
        }
        {
            String waypoints = airLineEntity.getWaypoints();
            UnifyAirLineFormatDto unifyAirLineFormatDto = JSONObject.parseObject(waypoints, UnifyAirLineFormatDto.class);
            String byname = "";
            TaskModeEnum mode = unifyAirLineFormatDto.getMode();
            switch (mode) {
                case LINEAR:
                    JSONObject linearParamJson = (JSONObject) unifyAirLineFormatDto.getLineConfigs().get(TaskModeEnum.LINEAR.name());
                    LinearParam linearParam = JSONObject.parseObject(linearParamJson.toJSONString(), LinearParam.class);
                    byname = linearParam.getByname();
                    break;
                case PANORAMA:
                    JSONObject panoramaParamJson = (JSONObject) unifyAirLineFormatDto.getLineConfigs().get(TaskModeEnum.PANORAMA.name());
                    PanoramaParam panoramaParam = JSONObject.parseObject(panoramaParamJson.toJSONString(), PanoramaParam.class);
                    byname = panoramaParam.getByname();
                    break;
                case ORTHOPHOTO:
                    JSONObject orthophotoParamJson = (JSONObject) unifyAirLineFormatDto.getLineConfigs().get(TaskModeEnum.ORTHOPHOTO.name());
                    OrthophotoParam orthophotoParam = JSONObject.parseObject(orthophotoParamJson.toJSONString(), OrthophotoParam.class);
                    byname = orthophotoParam.getByname();
                    break;
                case SLOPEPHOTO:
                    JSONObject slopePhotoParamJson = (JSONObject) unifyAirLineFormatDto.getLineConfigs().get(TaskModeEnum.SLOPEPHOTO.name());
                    SlopePhotoParam slopePhotoParam = JSONObject.parseObject(slopePhotoParamJson.toJSONString(), SlopePhotoParam.class);
                    byname = slopePhotoParam.getByname();
                    break;
                default:
                    break;
            }
            return Collections.singletonList(byname);
        }

    }

    private Map<Integer, Map<String, Object>> analysisNestAirLine(AirLineEntity airLineEntity) {
        Map<Integer, Map<String, Object>> resMap = new HashMap<>();
        String waypoints = airLineEntity.getWaypoints();
        List<Waypoint> waypointList = JSONArray.parseArray(waypoints, Waypoint.class);
        int flag = 1;
        //有拍照动作，证明是拍照点
        String photoAction = String.valueOf(ActionTypeEnum.START_TAKE_PHOTO.getValue());
        for (Waypoint wp : waypointList) {
            String wayPointAction = wp.getWayPointAction();
            if (StrUtil.isNotEmpty(wayPointAction)) {

                if (wayPointAction.contains(photoAction)) {
                    List<Integer> photoPropList = wp.getPhotoPropList();
                    for (String s : wayPointAction.split(",")) {
                        if (photoAction.equals(s.trim())) {
                            Map<String, Object> propAndBynameMap = Maps.newHashMap();
                            propAndBynameMap.put("photoPropList", photoPropList);
                            resMap.put(flag++, propAndBynameMap);
                        }
                    }
                }
            }
        }
        // 航线识别类型
        String showInfo = airLineEntity.getShowInfo();
        if (StringUtils.isNotBlank(showInfo) && JSONUtil.isTypeJSONObject(showInfo)) {
            Map map = JSONUtil.toBean(showInfo, Map.class);
            Object photoPropList = map.get("photoPropList");
            if (photoPropList != null && JSONUtil.isTypeJSONArray(photoPropList.toString())) {
                List<Integer> integerList = JSONUtil.toList(photoPropList.toString(), Integer.class);
                Map<String, Object> propAndBynameMap = new HashMap<>(4);
                propAndBynameMap.put("photoPropList", integerList);
                resMap.put(MANUAL_WAYPOINT_INDEX, propAndBynameMap);
            }
        }

        return resMap;
    }

    private List<String> listNestAirLineBynames(AirLineEntity airLineEntity) {
        String waypoints = airLineEntity.getWaypoints();
        List<String> bynameList = new ArrayList<>();
        List<Waypoint> waypointList = JSONArray.parseArray(waypoints, Waypoint.class);
        for (int i = 0; i < waypointList.size(); i++) {
            Waypoint wp = waypointList.get(i);
            String wayPointAction = wp.getWayPointAction();
            if (StrUtil.isNotEmpty(wayPointAction)) {
                //有拍照动作，证明是拍照点
                int photoAction = ActionTypeEnum.START_TAKE_PHOTO.getValue();
                int index = wayPointAction.indexOf(String.valueOf(photoAction));
                if (index >= 0) {
                    String byname = wp.getByname();
                    List<String> bynames = wp.getBynameList();
                    if (CollectionUtil.isNotEmpty(bynames)) {
                        bynameList.addAll(bynames);
                    } else {
                        List<String> bynames2 = Arrays.stream(wayPointAction.split(",")).filter(e -> {
                            if (e != null && e.trim().length() > 0) {
                                return Integer.valueOf(e.trim()).equals(1);
                            }
                            return false;
                        }).map(e -> byname).collect(Collectors.toList());
                        bynameList.addAll(bynames2);
                    }
                }
            }
        }
        return bynameList;
    }

    private void bindPhotoMarkAndPhotoType(List<MissionPhotoEntity> missionPhotoEntityList, Integer nestType, Integer mold) {
        // 标明是不是同一个点的照片，同一个镜头生成的照片
        // p4r可见光镜头，只有一张照片，通过（经纬度+照片生成时间）.hashCode()
        // 双光红外的每个拍照动作会生成两张照片，通过（经纬度+照片生成时间）.hashCode()
        // M300的挂载两个镜头，会生成三张照片，广角和变焦镜头生成一张（经纬度+生成时间）一直，红外生成一张（经纬度+生成时间）与红外和广角的不一致

        //绑定photoMark，m300的绑定

        //绑定照片类型
        //P4R
        if (mold == 0) {
            if (NestTypeEnum.G600.getValue() == nestType) {
                missionPhotoEntityList.forEach(mp -> {
                    mp.setPhotoType(PhotoTypeEnum.VISIBLE.getValue());
                });
            }
            //Mavic2
            if (NestTypeEnum.S100_V1.getValue() == nestType ||
                    NestTypeEnum.S100_V2.getValue() == nestType ||
                    NestTypeEnum.S110_AUTEL.getValue() == nestType ||
                    NestTypeEnum.S110_MAVIC3.getValue() == nestType
            ) {
                Map<Integer, List<MissionPhotoEntity>> photoMarkGroup = missionPhotoEntityList.stream().collect(Collectors.groupingBy(MissionPhotoEntity::getWaypointIndex));
                missionPhotoEntityList.forEach(mp -> {
                    Integer waypointIndex = mp.getWaypointIndex();
                    List<MissionPhotoEntity> missionPhotoEntities = photoMarkGroup.get(waypointIndex);
                    MissionPhotoEntity mpe = missionPhotoEntities.stream().max(Comparator.comparing(MissionPhotoEntity::getPhotoSize)).get();
                    if (mpe.getId().equals(mp.getId())) {
                        mp.setPhotoType(PhotoTypeEnum.VISIBLE.getValue());
                    } else {
                        mp.setPhotoType(PhotoTypeEnum.INFRARED.getValue());
                    }
                });
            }

            //m300
            if (NestTypeEnum.G900.getValue() == nestType || NestTypeEnum.G900_CHARGE.getValue() == nestType) {
                missionPhotoEntityList.forEach(mp -> {
                    String fileName = mp.getFileName();
                    if (StrUtil.isNotEmpty(fileName)) {
                        int photoType = -1;
                        if (fileName.contains("THRM")) {
                            photoType = PhotoTypeEnum.THRM.getValue();
                        } else if (fileName.contains("WIDE")) {
                            photoType = PhotoTypeEnum.WIDE.getValue();
                        } else if (fileName.contains("ZOOM")) {
                            photoType = PhotoTypeEnum.ZOOM.getValue();
                        }
                        mp.setPhotoType(photoType);
                    }
                });
            }
        } else if (mold == 1) {
            missionPhotoEntityList.forEach(mp -> {
                mp.setPhotoType(PhotoTypeEnum.VISIBLE.getValue());
            });
        }
    }


    /**
     * 获取DefectInfoList和PhotoUrlVisible和ThumbnailUrlVisible，set进photoInfoList
     *
     * @param photoInfoList
     * @param type
     */
    private void getDefectInfoListAndVisibleUrl(List<StationDefectPhotoDTO> photoInfoList, Integer type) {
       /* photoInfoList.stream().forEach(e -> {
            if (type == 3) {
                if (e.getPhotoType() == PhotoTypeEnum.INFRARED.getValue()) {
                    MissionPhotoEntity missionPhotoEntity = missionPhotoService.lambdaQuery().eq(MissionPhotoEntity::getPhotoType, PhotoTypeEnum.VISIBLE.getValue()).eq(MissionPhotoEntity::getMissionRecordsId, e.getMissionRecordsId())
                            .eq(MissionPhotoEntity::getWaypointIndex, e.getWaypointIndex()).list().get(0);
                    e.setPhotoUrlVisible(missionPhotoEntity.getPhotoUrl());
                    e.setThumbnailUrlVisible(missionPhotoEntity.getThumbnailUrl());
                }
                if (e.getPhotoType() == PhotoTypeEnum.THRM.getValue()) {
                    MissionPhotoEntity missionPhotoEntity = missionPhotoService.lambdaQuery().eq(MissionPhotoEntity::getPhotoType, PhotoTypeEnum.WIDE.getValue()).eq(MissionPhotoEntity::getMissionRecordsId, e.getMissionRecordsId())
                            .eq(MissionPhotoEntity::getWaypointIndex, e.getWaypointIndex()).list().get(0);
                    e.setPhotoUrlVisible(missionPhotoEntity.getPhotoUrl());
                    e.setThumbnailUrlVisible(missionPhotoEntity.getThumbnailUrl());
                }
            }
            if (e.getSid() != null) {
                List<DefectInfoEntity> entities = defectInfoService.list(new QueryWrapper<DefectInfoEntity>().eq("record_id", e.getSid()).eq("deleted", false).eq("source", type));
                e.setDefectInfoEntityList(entities);
            }
        });*/
        for (StationDefectPhotoDTO stationDefectPhotoDTO : photoInfoList) {
            //List<DefectInfoEntity> entities = defectInfoService.list(new QueryWrapper<DefectInfoEntity>().eq("record_id", stationDefectPhotoDTO.getMissionRecordsId()).eq("photo_id",stationDefectPhotoDTO.getId()).eq("deleted", false).eq("source", type));
            List<DefectInfoEntity> entities = defectInfoService.list(new QueryWrapper<DefectInfoEntity>().eq("record_id", stationDefectPhotoDTO.getSid()).eq("photo_id", stationDefectPhotoDTO.getId()).eq("deleted", false).eq("source", type));
            stationDefectPhotoDTO.setDefectInfoEntityList(entities);
        }
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

//    /**
//     * 创建xml文件
//     */
//    private void BuildXML(String folderName, String fileName, String fileUrl, List<DefectInfoEntity> defectInfoList,
//                          ZipOutputStream zos, String defectTypeTrafficName) {
//        try {
//            // 设置生成xml的格式
//            OutputFormat outputFormat = OutputFormat.createPrettyPrint();
//            outputFormat.setEncoding("UTF-8");
//            // 创建document对象
//            Document document = DocumentHelper.createDocument();
//            // 创建根节点rss
//            Element annotation = document.addElement("annotation");
//            // 生成子节点及子节点内容
//            Element folder = annotation.addElement("folder");
//
//            folder.setText(folderName.substring(folderName.lastIndexOf("/") + 1));
//            Element filename = annotation.addElement("filename");
//            filename.setText(fileName);
//            Element path = annotation.addElement("path");
//            path.setText(fileUrl);
//
//            Element source = annotation.addElement("source");
//            Element sourceChannel = source.addElement("database");
//            sourceChannel.setText("Unknown");
//
//            // 图片长宽
//            File picture = new File(fileUrl);
//            BufferedImage sourceImg = ImageIO.read(new FileInputStream(picture));
//            Integer width = sourceImg.getWidth();
//            Integer height = sourceImg.getHeight();
//
//            Element size = annotation.addElement("size");
//            Element widthSize = size.addElement("width");
//            widthSize.setText(width.toString());
//            Element heightSize = size.addElement("height");
//            heightSize.setText(height.toString());
//            Element depth = size.addElement("depth");
//            depth.setText("3");
//
//            Element segmented = annotation.addElement("segmented");
//            segmented.setText("0");
//
//            if (ToolUtil.isNotEmpty(defectInfoList)) {
//                defectInfoList.forEach(e -> {
//                    // 获取框框的坐标宽高
//                    String x = String.valueOf(Double.parseDouble(e.getX().substring(0, e.getX().length() - 1)) / 100 * width);
//                    String y = String.valueOf(Double.parseDouble(e.getY().substring(0, e.getY().length() - 1)) / 100 * height);
//                    String x1 = String.valueOf(Double.parseDouble(e.getX1().substring(0, e.getX1().length() - 1)) / 100 * width);
//                    String y1 = String.valueOf(Double.parseDouble(e.getY1().substring(0, e.getY1().length() - 1)) / 100 * height);
//
//                    Element object = annotation.addElement("object");
//                    Element name = object.addElement("name");
//                    name.setText(e.getCode());
//                    Element pose = object.addElement("pose");
//                    pose.setText("Unspecified");
//                    Element truncated = object.addElement("truncated");
//                    truncated.setText("0");
//                    Element difficult = object.addElement("object");
//                    difficult.setText("0");
//
//                    Element bndbox = object.addElement("bndbox");
//                    Element xmin = bndbox.addElement("xmin");
//                    xmin.setText(x.substring(0, x.lastIndexOf(".")));
//                    Element ymin = bndbox.addElement("ymin");
//                    ymin.setText(y.substring(0, y.lastIndexOf(".")));
//                    Element xmax = bndbox.addElement("xmax");
//                    xmax.setText(x1.substring(0, x1.lastIndexOf(".")));
//                    Element ymax = bndbox.addElement("ymax");
//                    ymax.setText(y1.substring(0, y1.lastIndexOf(".")));
//                });
//            }
//
//            // xml文件名称
//            String xmlName = fileName.substring(0, fileName.lastIndexOf(".")) + ".xml";
//
//            // 往zip包插入xml
//            DataOutputStream os = new DataOutputStream(zos);
//            zos.putNextEntry(new ZipEntry(defectTypeTrafficName + File.separator + defectTypeTrafficName + "-XML" + File.separator + xmlName));
//            XMLWriter writer = new XMLWriter(os, outputFormat);
//            writer.setEscapeText(false);
//            writer.write(document);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILURE_TO_GENERATE_XML.getContent()));
//        }
//    }

    private void handleWaypointIndex(Integer airLineType, Integer taskType, Integer mold, List<MissionPhotoEntity> missionPhotoEntityList) {

//        if (mold == 1) {
//            missionPhotoEntityList.forEach(mpe -> {
//                mpe.setWaypointIndex(mpe.getWaypointIndex() + 1);
//            });
//        }

        //线状巡视是定时拍照的没有指定是某一个拍照点的照片，因此整条航线只有一个别名和一组识别类型
        //全景不处理
        if (airLineType == AirLineTypeEnum.UNIFY_AIR_LINE.getValue()
                && !TaskModeEnum.PANORAMA.getValue().equals(taskType)
                && !TaskModeEnum.GRID.getValue().equals(taskType)
                && !TaskModeEnum.DJI_KML.getValue().equals(taskType)) {
            missionPhotoEntityList.forEach(mpe -> {
                mpe.setWaypointIndex(1);
            });
        }
        log.info("#MissionPhotoServiceImpl.pushAnalysis2# handleWaypointIndex={}", taskType);
    }

    private void startTranDataRunnable(String nestUuid) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BATCH_TRAN_DATA, nestUuid);
        BatchTranDataDto batchTranDataDto = (BatchTranDataDto) redisService.get(redisKey);
        if (batchTranDataDto == null) {
            log.info("没有查询到任务队列");
            String message = WebSocketRes.err().msg("geoai_uos_no_task_queue_is_queried").topic(WebSocketTopicEnum.UPLOAD_MEDIA_TO_SERVER_TIP).toJSONString();
            ChannelService.sendMessageByType4Channel(nestUuid, message);
            return;
        }
        Integer readyRecordId = batchTranDataDto.readyRecordId();
        if (readyRecordId == -1) {
            log.info("readyRecordId:{}", readyRecordId);
            redisService.del(redisKey);
            log.info("执行了中文输出参数");
            closeNest(nestUuid);
            String message = WebSocketRes.ok().msg("geoai_uos_end_of_synchronization_of_source_data").topic(WebSocketTopicEnum.BATCH_TRAN_DATA_DTO).toJSONString();
            pushPhotoTransMsgByWs(nestUuid, message);
            return;
        }
        MissionRecordsEntity one = missionRecordsService.lambdaQuery()
                .eq(MissionRecordsEntity::getId, readyRecordId)
                .eq(MissionRecordsEntity::getDeleted, false)
                .select(MissionRecordsEntity::getUavWhich)
                .one();
        Integer uavWhich = Objects.nonNull(one) ? one.getUavWhich() : 0;
        redisService.set(redisKey, batchTranDataDto);
        RestRes restRes = getPhoto(readyRecordId, nestUuid, "");
        String message = "";
        Map<String, Object> wsData = new HashMap<>(2);
        wsData.put("uavWhich", uavWhich);
        if (restRes.getCode() == 20000) {
            message = WebSocketRes.ok().msg(restRes.getMsg()).data(wsData).topic(WebSocketTopicEnum.UPLOAD_MEDIA_TO_SERVER_TIP).toJSONString();
            ChannelService.sendMessageByType4Channel(nestUuid, message);
        } else {
            message = WebSocketRes.err().msg(restRes.getMsg()).data(wsData).topic(WebSocketTopicEnum.UPLOAD_MEDIA_TO_SERVER_TIP).toJSONString();
            ChannelService.sendMessageByType4Channel(nestUuid, message);

            // 本架次报错了，进行下一个架次的同步
            startTranDataRunnable(nestUuid);
        }

    }

    //国际化测试用
    private void startTranDataRunnable(String nestUuid, String env) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BATCH_TRAN_DATA, nestUuid);
        BatchTranDataDto batchTranDataDto = (BatchTranDataDto) redisService.get(redisKey);
        if (batchTranDataDto == null) {
            log.info("没有查询到任务队列");
            String message = WebSocketRes.err().msg("geoai_uos_no_task_queue_is_queried").topic(WebSocketTopicEnum.UPLOAD_MEDIA_TO_SERVER_TIP).toJSONString();
            ChannelService.sendMessageByType4Channel(nestUuid, message);
            return;
        }
        Integer readyRecordId = batchTranDataDto.readyRecordId();
        if (readyRecordId == -1) {
            log.info("readyRecordId:{}", readyRecordId);
            redisService.del(redisKey);
            closeNest(nestUuid);
            //String message = WebSocketRes.ok().msg(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_END_OF_SYNCHRONIZATION_OF_SOURCE_DATA.getContent())).topic(WebSocketTopicEnum.BATCH_TRAN_DATA_DTO).toJSONString();
            String msg = "geoai_uos_end_of_synchronization_of_source_data";
            log.info("当前语言环境的输出为" + msg);
            String message = WebSocketRes.ok().msg(msg).topic(WebSocketTopicEnum.BATCH_TRAN_DATA_DTO).toJSONString();
            log.info("测试message为:" + message);
            pushPhotoTransMsgByWs(nestUuid, message);
            return;
        }
        redisService.set(redisKey, batchTranDataDto);
        RestRes restRes = getPhoto(readyRecordId, nestUuid, env);
        String message = "";
        if (restRes.getCode() == 20000) {
            log.info("同步数据正常执行");
            message = WebSocketRes.ok().msg(restRes.getMsg()).topic(WebSocketTopicEnum.UPLOAD_MEDIA_TO_SERVER_TIP).toJSONString();
            ChannelService.sendMessageByType4Channel(nestUuid, message);
        } else {
            log.info("同步数据異常执行");
            message = WebSocketRes.err().msg(restRes.getMsg()).topic(WebSocketTopicEnum.UPLOAD_MEDIA_TO_SERVER_TIP).toJSONString();
            ChannelService.sendMessageByType4Channel(nestUuid, message);
            // 本架次报错了，进行下一个架次的同步
            startTranDataRunnable(nestUuid);
        }

    }

    @Override
    public void closeNest(String nestUuid) {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            NestTypeEnum nestType = cm.getNestType();
            if (NestTypeEnum.S100_V1.equals(nestType)) {
//                MqttResult<NullParam> result = cm.getRcManagerCf().controllerRcMachineOnOff();
//                if (result.isSuccess()) {
//                    log.info("nestUuid:{}遥控器关闭成功", nestUuid);
//                } else {
//                    log.info("nestUuid:{}遥控器关闭失败,{}", nestUuid, result.getMsg());
//                }
//                Timer timer = new Timer(true);
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
                MqttResult<NullParam> result = cm.getPowerManagerCf().onOffMiniV1Aircraft();
                if (result.isSuccess()) {
                    log.info("nestUuid:{}无人机关闭成功", nestUuid);
                }
//                    }
//                }, 5000);
            }

            //mini基站系列，需要关闭无人机再关闭遥控器
            if (NestTypeEnum.S100_V2.equals(nestType) ||
                    NestTypeEnum.S110_AUTEL.equals(nestType) ||
                    NestTypeEnum.S110_MAVIC3.equals(nestType) ||
                    NestTypeEnum.G900_CHARGE.equals(nestType)) {
                MqttResult<NullParam> result = cm.getPowerManagerCf().offMiniV2Aircraft();
                if (result.isSuccess()) {
                    log.info("nestUuid:{}无人机关闭成功", nestUuid);
                } else {
                    log.info("nestUuid:{}无人机关闭失败,{}", nestUuid, result.getMsg());
                }
//                Timer timer = new Timer(true);
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        MqttResult<NullParam> result = cm.getRcManagerCf().controllerRcMachineOnOff();
//                        if (result.isSuccess()) {
//                            log.info("nestUuid:{}遥控器关闭成功", nestUuid);
//                        } else {
//                            log.info("nestUuid:{}遥控器关闭失败,{}", nestUuid, result.getMsg());
//                        }
//                    }
//                }, 1000);
            }

            if (NestTypeEnum.G600.equals(nestType)) {
                MqttResult<NullParam> result = cm.getMotorManagerCf().unLoadBattery();
                if (result.isSuccess()) {
                    log.info("nestUuid:{}关闭成功", nestUuid);
                } else {
                    log.info("nestUuid:{}关闭失败,{}", nestUuid, result.getMsg());
                }
            }


            //G900电池不会自动关闭遥控器
            if (NestTypeEnum.G900.equals(nestType)) {
                boolean register = DataTranCompleteListenerFactory.register(nestUuid);
                log.info("G900同步数据完成，准备注册监听器，监听是否能够卸载电池，结果：{}", register);
            }

        }
    }

    private String renameObject(String fileName) {
        int indexOf = fileName.lastIndexOf("/");
        String pathPre = fileName.substring(0, indexOf + 1);
        String substring = fileName.substring(indexOf + 1);
        return String.format("%s%s_%s", pathPre, BizIdUtils.snowflakeIdStr(), substring);
    }

    /**
     * 缩略图功能-针对CPS
     *
     * @param missionId
     * @param recordId
     * @param mediaFile
     */
    private void uploadPhotoAndThumbnailCps(Integer missionId,
                                            Integer recordId,
                                            MediaFile mediaFile) {
        log.info("#MissionPhotoServiceImpl.uploadPhotoAndThumbnailCps# mediaFile={}", mediaFile);
        // 得到 文件的原始名称
        String originalFilename = mediaFile.getFileName();
        MissionEntity mission = missionService.getById(missionId);
        String missionName = missionRecordsService.getMissionName(recordId);
        TaskEntity taskEntity = taskService.getById(mission.getTaskId());
        String photoName = String.format("%s.JPG", taskEntity.getName());
        if (mediaFile.isManualRecord()) {
            photoName = mediaFile.getFileName();
        }

        try {
            String fileName = missionName.substring(0, missionName.lastIndexOf("-") + 1) + BizIdUtils.snowflakeIdStr() + "-" + originalFilename;
            String photoPath = mediaFile.getFilePath();
            if (StringUtils.isEmpty(photoPath)) {
                throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SYNCHRONIZE_PHOTO_DATA_NO_CORRESPONDING_FILE_PATH.getContent()));
            }
            photoPath = isOldVideoUpload(photoPath) ? photoPath : photoPath + SymbolConstants.SLASH_LEFT + originalFilename;
            // 生成缩略图
            String thumbnailPath = fileManager.generateThumbnail(photoPath, 0.6D, false);

            LocalDateTime timeCreated = LocalDateTime.ofInstant(Instant.ofEpochMilli(mediaFile.getTimeCreated()), ZoneId.systemDefault());
            Integer downloaded = mediaFile.getFileDataDownloaded() ? 1 : 0;
            // 双拍模式照片类型:-1:正常照片,0:可见光,1:红外,2:可见光红外合成
            int photoType = mediaFile.getLenType() == 3 ? 1 : 0;

            MissionPhotoEntity missionPhotoEntity = MissionPhotoEntity.builder()
                    .name(photoName)
                    // 兼容旧版CPS文件上传方式
                    .photoUrl(photoPath)
                    //文件大小 -MARK
                    .photoSize(Long.parseLong("1"))
                    .missionId(missionId)
                    .missionRecordsId(recordId)
                    .thumbnailName(fileName)
//                    .thumbnailUrl(geoaiUosProperties.getStore().getOriginPath() + thumbnailPath)
                    .thumbnailUrl(thumbnailPath)
                    .downloaded(downloaded)
                    .timeCreated(timeCreated)
                    .orgCode(mission.getOrgCode())
                    .waypointIndex(mediaFile.getWaypointIndex())
                    .photoType(photoType)
                    .lenType(mediaFile.getLenType())
                    .build();
            //将media值赋值给video
            MediaFieToPhotoEntityConvert.INSTANCES.updateMissionPhotoEntity(mediaFile, missionPhotoEntity);
            this.save(missionPhotoEntity);
            // 删除upload
            uploadService.deleteByFilePath(photoPath);
        } catch (Exception ex) {
            log.error("#MissionPhotoServiceImpl.uploadPhotoAndThumbnailCps# mediaFile={}", mediaFile, ex);
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SYNCHRONIZE_DATA_SOURCE_PLEASE_CONTACT_ADMINISTRATOR.getContent()));
        }

    }

    /**
     * 人工拍照 图片的WaypointIndex=-100
     */
    private static final int MANUAL_WAYPOINT_INDEX = -100;

    /**
     * 没有序号 默认的WaypointIndex=-90
     */
    private static final int DEFAULT_WAYPOINT_INDEX = -90;

    /**
     * 回写动作-CPS
     *
     * @param mediaFileString
     * @return
     */
    @Override
    public RestRes uploadPhotoCps(String mediaFileString) {
        log.info(".........进入上传图片视频的接口：mediaFileString：{} ", mediaFileString);
        log.info("#MissionPhotoServiceImpl.uploadPhotoCps# mediaFileString={}", mediaFileString);
        JSONObject json = JSONObject.parseObject(mediaFileString);
        MediaFile mediaFile = JSONObject.toJavaObject(json, MediaFile.class);
        String execId = mediaFile.getExecMissionID();
        Integer mediaType = mediaFile.getMediaType();
        mediaFile.setWaypointIndex(mediaFile.getShootIndex() == null ? DEFAULT_WAYPOINT_INDEX : mediaFile.getShootIndex());
        if (mediaFile.isManualRecord()) {
            mediaFile.setWaypointIndex(MANUAL_WAYPOINT_INDEX);
        }
        // 根据execId获取架次id和架次执行id
        MissionRecordsEntity missionRecordsEntity = missionRecordsService.getByExecId(execId);
        log.info("#MissionPhotoServiceImpl.uploadPhotoCps# mediaFileString={}, missionRecordsEntity={}", mediaFileString, missionRecordsEntity);
        if (missionRecordsEntity == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_SORTIE_QUERY.getContent()));
        }
        Integer missionId = missionRecordsEntity.getMissionId();
        Integer recordId = missionRecordsEntity.getId();
        // 如果是图片，则上传图片
        if (MissionConstant.MissionMedia.JPEG.equals(mediaType) ||
                MissionConstant.MissionMedia.RAW_DNG.equals(mediaType)) {
            // 上传图片到MinIO服务器
            uploadPhotoAndThumbnailCps(missionId, recordId, mediaFile);
        }
        // 如果是视频，则上传视频
        if (MissionConstant.MissionMedia.MOV.equals(mediaType) || MissionConstant.MissionMedia.MP4.equals(mediaType)) {
            uploadVideoCps(missionId, recordId, mediaFile);
        }
        log.info(String.format("数据回写成功 , recordId -> {}", recordId));
        return RestRes.ok();
    }

    private boolean isOldVideoUpload(String filepath) {
        if (!filepath.startsWith(SymbolConstants.SLASH_LEFT)) {
            filepath = SymbolConstants.SLASH_LEFT + filepath;
        }
        String originPath = geoaiUosProperties.getStore().getOriginPath();
        return filepath.startsWith(originPath);
    }

    /**
     * 上传视频-Cps
     *
     * @param missionId
     * @param recordId
     */
    private void uploadVideoCps(Integer missionId, Integer recordId, MediaFile mediaFile) {
        String originalFilename = mediaFile.getFileName();
        String missionName = missionRecordsService.getMissionName(recordId);
        Integer flyIndex = missionRecordsService.getFlyIndex(recordId);
        MissionEntity missionEntity = missionService.getById(missionId);
        String videoName = missionName + "_第" + flyIndex + "次执行-" + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()) + "-" + originalFilename;
        try {
            // minIO方案
            // 获取文件路径 上线要去掉 -MARK
            String videoPath = mediaFile.getFilePath();
            if (StringUtils.isEmpty(videoPath)) {
                throw new NestException("同步视频数据失败,没有对应的文件路径");
            }
            log.info("打印:{}", videoPath);

            LocalDateTime timeCreated = LocalDateTime.ofInstant(Instant.ofEpochMilli(null == mediaFile.getTimeCreated() ? System.currentTimeMillis() : mediaFile.getTimeCreated()), ZoneId.systemDefault());
            Integer downloaded = mediaFile.getFileDataDownloaded() ? 1 : 0;
            MissionVideoEntity missionVideoEntity = MissionVideoEntity.builder()
                    .name(videoName)
//                    .videoUrl(videoPath)
                    // 兼容旧版
                    .videoUrl(isOldVideoUpload(videoPath) ? videoPath : videoPath + SymbolConstants.SLASH_LEFT + originalFilename)
                    //文件大小 -MARK
                    .videoSize(Long.parseLong("1"))
                    .missionId(missionId)
                    .missionRecordsId(recordId)
                    .orgCode(missionEntity.getOrgCode())
                    .timeCreated(timeCreated)
                    .downloaded(downloaded)
                    .build();
            //将media值赋值给video
            MediaFieToVideoEntityConvert.INSTANCES.updateMissionVideoEntity(mediaFile, missionVideoEntity);
            if (originalFilename.contains("_WIDE")) {
                missionVideoEntity.setType(MissionConstant.MissionVideoType.WIDE_VIDEO);
            } else if (originalFilename.contains("_ZOOM")) {
                missionVideoEntity.setType(MissionConstant.MissionVideoType.ZOOM_VIDEO);
            } else if (originalFilename.contains("_THRM")) {
                missionVideoEntity.setType(MissionConstant.MissionVideoType.THRM_VIDEO);
            } else if (originalFilename.contains("_SCRN")) {
                missionVideoEntity.setType(MissionConstant.MissionVideoType.SCRN_VIDEO);
            } else {
                missionVideoEntity.setType(MissionConstant.MissionVideoType.DOWNLOAD_VIDEO);
            }
            log.info("即将保存视频数据");
            if (missionVideoEntity.getType().equals(MissionConstant.MissionVideoType.DOWNLOAD_VIDEO)) {
                missionVideoEntity.setMediaCreateTime(timeCreated);
            }
            missionVideoService.save(missionVideoEntity);
            log.info("发送上传轨迹文件命令");
            if (missionVideoEntity.getType().equals(MissionConstant.MissionVideoType.DOWNLOAD_VIDEO)) {
                pushSrt(recordId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
            throw new NestException(String.format("同步视频数据失败:%s", e.getMessage()));
        }

    }

    /**
     * 根据执行id获取文件路径
     *
     * @param execMissionId
     * @return
     */
    public String getURL(String execMissionId) {
        String filePath = "";
        List<UploadEntity> uploadEntityList = this.uploadService.lambdaQuery()
                .eq(UploadEntity::getExecMissionID, execMissionId)
                .list();
        if (!CollectionUtils.isEmpty(uploadEntityList)) {
            filePath = uploadEntityList.get(0).getFilePath();
        }
        return filePath;
    }

    private List<MissionPhotoTagRelEntity> bindMissionPhotoTagRelEntityList(List<Integer> tagIdList, List<MissionPhotoEntity> missionPhotoEntityList) {
        if (CollectionUtil.isNotEmpty(tagIdList) && CollectionUtil.isNotEmpty(missionPhotoEntityList)) {
            List<MissionPhotoTagRelEntity> missionPhotoTagRelEntityList = new ArrayList<>();
            for (MissionPhotoEntity mpe : missionPhotoEntityList) {
                for (Integer tagId : tagIdList) {
                    MissionPhotoTagRelEntity missionPhotoTagRelEntity = new MissionPhotoTagRelEntity();
                    missionPhotoTagRelEntity.setMissionPhotoId(mpe.getId().intValue());
                    missionPhotoTagRelEntity.setTagId(tagId);
                    missionPhotoTagRelEntityList.add(missionPhotoTagRelEntity);
                }
            }
            return missionPhotoTagRelEntityList;
        }
        return Collections.emptyList();
    }

    private List<MissionPhotoTypeRelEntity> bindMissionPhotoTypeRelEntityList(Map<Integer, Map<String, Object>> propAndByNameMapGroup
            , List<MissionPhotoEntity> missionPhotoEntityList, Integer taskType) {
        if (CollectionUtil.isNotEmpty(propAndByNameMapGroup) && CollectionUtil.isNotEmpty(missionPhotoEntityList)) {
            // 按拍照点分组，用于电力识别类型，表计读数先取变焦，再广角，普通可见光兜底
            Map<Integer, List<MissionPhotoEntity>> waypointIndexMap = missionPhotoEntityList.stream().collect(Collectors.groupingBy(MissionPhotoEntity::getWaypointIndex));
            Set<Long> photoIdSet = Sets.newHashSet();
            for (List<MissionPhotoEntity> value : waypointIndexMap.values()) {

                Map<Integer, MissionPhotoEntity> photoEntityMap = value.stream().collect(Collectors.toMap(MissionPhotoEntity::getLenType, bean -> bean, (key1, key2) -> key1));
                MissionPhotoEntity missionPhotoEntity;
                if ((missionPhotoEntity = photoEntityMap.get(CameraLensVideoSourceEnum.ZOOM.getValue())) != null) {
                    photoIdSet.add(missionPhotoEntity.getId());
                    continue;
                }
                if ((missionPhotoEntity = photoEntityMap.get(CameraLensVideoSourceEnum.WIDE.getValue())) != null) {
                    photoIdSet.add(missionPhotoEntity.getId());
                    continue;
                }

                if ((missionPhotoEntity = photoEntityMap.get(CameraLensVideoSourceEnum.UNKNOWN.getValue())) != null) {
                    photoIdSet.add(missionPhotoEntity.getId());
                }
            }
            List<MissionPhotoTypeRelEntity> missionPhotoTypeRelEntityList = new ArrayList<>();
            for (MissionPhotoEntity mpe : missionPhotoEntityList) {
                Integer waypointIndex = mpe.getWaypointIndex();
                // 网格化属于线状类型，识别类型只有一个
                if (TaskModeEnum.GRID.getValue().equals(taskType)) {
                    waypointIndex = 1;
                }
                Map<String, Object> propAndByNameMap = propAndByNameMapGroup.get(waypointIndex);
                if (propAndByNameMap == null) {
                    continue;
                }
                List<Integer> photoPropList = (List<Integer>) propAndByNameMap.get("photoPropList");
                if (CollectionUtil.isNotEmpty(photoPropList)) {
                    for (Integer integer : photoPropList) {
                        // 过滤不推送的图片
                        if (RoleIdenValueEnum.ABNORMAL_FIND_DL_BJDS_NEW.getIdenValue() == integer) {
                            if (!photoIdSet.contains(mpe.getId())) {
                                continue;
                            }
                        } else if (RoleIdenValueEnum.ABNORMAL_FIND_DL_QXSB_NEW.getIdenValue() == integer) {
                            if (!photoIdSet.contains(mpe.getId())) {
                                continue;
                            }
                        } else if (RoleIdenValueEnum.ABNORMAL_FIND_DL_HWCW_NEW.getIdenValue() == integer) {
                            if (mpe.getLenType() == null || CameraLensVideoSourceEnum.INFRARED.getValue() != mpe.getLenType()) {
                                continue;
                            }
                        }
                        MissionPhotoTypeRelEntity missionPhotoTypeRelEntity = new MissionPhotoTypeRelEntity();
                        missionPhotoTypeRelEntity.setMissionPhotoId(mpe.getId().intValue());
                        missionPhotoTypeRelEntity.setType(integer);
                        missionPhotoTypeRelEntityList.add(missionPhotoTypeRelEntity);
                    }
                }
            }
            return missionPhotoTypeRelEntityList;
        }
        return Collections.emptyList();
    }

    private void missionPhotoListRename(List<MissionPhotoEntity> missionPhotoEntityList, AirLineEntity airLineEntity, Integer taskType) {
        List<String> bynameList = listAirLineBynames(airLineEntity, taskType);
        log.info("#MissionPhotoServiceImpl.missionPhotoListRename# bynameList={}", bynameList);

        int flag = 0;
        for (int i = 0; i < missionPhotoEntityList.size(); i++) {
            MissionPhotoEntity mpe = missionPhotoEntityList.get(i);
            if (mpe.getWaypointIndex() <= 0) {
                continue;
            }
            int index = mpe.getWaypointIndex() - 1;
            String rename = null;
            if (airLineEntity.getType() == AirLineTypeEnum.UNIFY_AIR_LINE.getValue()) {
                if (Objects.equals(TaskModeEnum.DJI_KML.getValue(), taskType)) {
                    if (bynameList.size() > index) {
                        rename = bynameList.get(index);
                    }
                } else {
                    rename = bynameList.get(0);
                }
            } else {
                if (bynameList.size() > index) {
                    rename = bynameList.get(index);
                }
            }
            if (StrUtil.isEmpty(rename)) {
                //      String photoName = dealPhotoName(mpe.getName());
                rename = getDefaultName(mpe.getName(), mpe.getPhotoType(), flag);
                flag++;
            }
            mpe.setName(rename + ".JPG");
        }

    }


    /**
     * 根据照片Index范围找到对应的照片数据
     *
     * @param indexFrom
     * @param indexTo
     * @param missionRecordsId
     * @return
     */
    @Override
    public List<MissionPhotoPointOutDTO> getPhotoByPoint(Integer indexFrom, Integer indexTo, String missionRecordsId) {
        List<MissionPhotoPointOutDTO> outDTOList = new ArrayList<>();
        LambdaQueryWrapper<MissionPhotoEntity> condition = Wrappers.lambdaQuery(MissionPhotoEntity.class)
                .ge(MissionPhotoEntity::getWaypointIndex, indexFrom)
                .le(MissionPhotoEntity::getWaypointIndex, indexTo)
                .eq(MissionPhotoEntity::getMissionRecordsId, missionRecordsId)
                .eq(MissionPhotoEntity::getDeleted, false)
                .select(MissionPhotoEntity::getPhotoUrl
                        , MissionPhotoEntity::getMissionRecordsId
                        , MissionPhotoEntity::getThumbnailUrl
                        , MissionPhotoEntity::getWaypointIndex
                        , MissionPhotoEntity::getId
                        , MissionPhotoEntity::getLenType
                        , MissionPhotoEntity::getName
                        , MissionPhotoEntity::getMissionId
                );
        List<MissionPhotoEntity> missionPhotoEntityList = this.list(condition);
        if (CollectionUtil.isNotEmpty(missionPhotoEntityList)) {
            outDTOList = missionPhotoEntityList.stream().map(MissionPhotoPointConverter.INSTANCES::convert).collect(Collectors.toList());
        }
        return outDTOList;
    }

    @Override
    public List<MissionPhotoEntity> selectBatchPhoto(List<Long> photoIds) {
        List<MissionPhotoEntity> res = null;
        if (!CollectionUtils.isEmpty(photoIds)) {
            LambdaQueryWrapper<MissionPhotoEntity> condition = Wrappers.lambdaQuery(MissionPhotoEntity.class)
                    .in(MissionPhotoEntity::getId, photoIds);
            return this.list(condition);
        }
        return res;
    }


    private Map<Integer, Map<String, Object>> analysisDjiKmlUnifyAirLine(List<UnifyPoint> unifyPointList) {
        log.info("进入大疆重命名");
        if (CollectionUtils.isEmpty(unifyPointList)) {
            return Collections.emptyMap();
        }
        Map<Integer, Map<String, Object>> resMap = new HashMap<>(unifyPointList.size());
        int index = 1;
        for (UnifyPoint up : unifyPointList) {
            List<UnifyAction> customActions = up.getCustomActions();
            for (UnifyAction ua : customActions) {
                if (ActionTypeEnum.START_TAKE_PHOTO.equals(ua.getActionType())) {
                    Map<String, Object> map = new HashMap<>(2);
                    map.put("byname", ua.getByname());
                    map.put("photoPropList", ua.getIdentifyType());
                    resMap.put(index++, map);
                }
            }
        }
        return resMap;
    }

    private List<String> analysisDjiKmlUnifyAirLineRename(List<UnifyPoint> unifyPointList) {
        log.info("进入大疆重命名");
        if (CollectionUtils.isEmpty(unifyPointList)) {
            return Collections.emptyList();
        }
        return unifyPointList.stream()
                .map(UnifyPoint::getCustomActions)
                .flatMap(Collection::stream)
                .filter(ua -> ActionTypeEnum.START_TAKE_PHOTO.equals(ua.getActionType()))
                .map(UnifyAction::getByname)
                .collect(Collectors.toList());
    }

    /**
     * 批量数据同步
     *
     * @param nestId
     * @param recordIdList
     * @return
     */
    @Override
    public RestRes synDataList(String nestId, List<Integer> recordIdList) {
        new Thread(() -> {
            this.dataSynService.synDataList(nestId, recordIdList);
        }, String.format("Data-Syn-%s", nestId)).start();
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BEGIN_SYNCHRONIZATION.getContent()));
    }

    /**
     * @param recordId
     * @return
     */
    @Override
    public List<Long> getPhotoByRecordId(Integer recordId) {
        return this.baseMapper.getPhotoByRecordId(recordId);
    }

}


