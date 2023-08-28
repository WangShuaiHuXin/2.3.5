package com.imapcloud.nest.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.netty.service.*;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.*;
import com.imapcloud.nest.mapper.MissionMapper;
import com.imapcloud.nest.model.*;
import com.imapcloud.nest.pojo.BuildKmzMissionRes;
import com.imapcloud.nest.pojo.BuildMissionRes;
import com.imapcloud.nest.pojo.CfRes;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.pojo.dto.app.EasyFlyPoint;
import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.AutoMissionQueueBody;
import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.G503AutoMissionQueueBody;
import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.MissionQueueAddDTO;
import com.imapcloud.nest.pojo.dto.autoMissionQueueDTO.MissionQueueTopDTO;
import com.imapcloud.nest.pojo.dto.reqDto.NestReqDto;
import com.imapcloud.nest.pojo.dto.unifyAirLineDto.AppMissionAircraftDTO;
import com.imapcloud.nest.pojo.dto.unifyAirLineDto.AroundFlyParam;
import com.imapcloud.nest.pojo.dto.unifyAirLineDto.LinearParam;
import com.imapcloud.nest.pojo.dto.unifyAirLineDto.UnifyAirLineFormatDto;
import com.imapcloud.nest.sdk.CommonNestState;
import com.imapcloud.nest.sdk.CommonNestStateFactory;
import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.sdk.listener.MissionQueueListenerFactory;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.service.event.MqttSwitchZoomCameraEvent;
import com.imapcloud.nest.service.event.MqttTakeOffRecordEvent;
import com.imapcloud.nest.service.listener.MissionQueueListenerService;
import com.imapcloud.nest.utils.*;
import com.imapcloud.nest.utils.airline.PointCloudWaypoint;
import com.imapcloud.nest.utils.airline.WaypointUtil;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisKeyEnum;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.properties.DomainConfig;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.common.properties.UploadConfig;
import com.imapcloud.nest.v2.manager.dataobj.out.AccountOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.FileAccessLinkOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.MissionPhotoOutDO;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import com.imapcloud.nest.v2.manager.feign.FileServiceClient;
import com.imapcloud.nest.v2.manager.sql.MissionPhotoManager;
import com.imapcloud.nest.v2.service.*;
import com.imapcloud.nest.v2.service.dto.GridMissionDTO;
import com.imapcloud.nest.v2.service.dto.in.DJITaskFileInDTO;
import com.imapcloud.nest.v2.service.dto.out.*;
import com.imapcloud.sdk.manager.*;
import com.imapcloud.sdk.manager.dji.DjiDockManagerCf;
import com.imapcloud.sdk.manager.mission.MissionManager;
import com.imapcloud.sdk.manager.mission.MissionManagerCf;
import com.imapcloud.sdk.manager.system.SystemManagerCf;
import com.imapcloud.sdk.pojo.BaseResult3;
import com.imapcloud.sdk.pojo.constant.*;
import com.imapcloud.sdk.pojo.constant.dji.DjiErrorCodeEnum;
import com.imapcloud.sdk.pojo.constant.dji.ResultEnum;
import com.imapcloud.sdk.pojo.djido.DjiCommonDataDO;
import com.imapcloud.sdk.pojo.djido.DjiDockPropertyOsdDO;
import com.imapcloud.sdk.pojo.djido.FlightTaskPrepareDO;
import com.imapcloud.sdk.pojo.djido.FlightTaskResourceGetReplyDO;
import com.imapcloud.sdk.pojo.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static com.imapcloud.sdk.pojo.constant.MissionRunStateEnum.*;

/**
 * <p>
 * 架次表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
@Service
@Slf4j
public class MissionServiceImpl extends ServiceImpl<MissionMapper, MissionEntity> implements MissionService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private NestService nestService;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private MissionRecordsService missionRecordsService;

    @Autowired
    private MissionParamService missionParamService;

    @Autowired
    private AirLineService airLineService;

    @Autowired
    private WsTaskProgressService wsTaskProgressService;

    @Autowired
    private G503WsTaskProgressService g503WsTaskProgressService;

    @Autowired
    private WsDjiTaskProgressService wsDjiTaskProgressService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private AppService appService;

//    @Autowired
//    private AircraftService aircraftService;

    @Autowired
    private MissionVideoService missionVideoService;

    @Autowired
    private CommonNestStateService commonNestStateService;

    @Resource
    private UosAccountService uosAccountService;

    @Autowired
    @Lazy
    private MissionQueueListenerService missionQueueListenerService;

    @Autowired
    private InspectionPlanRecordService inspectionPlanRecordService;

    @Autowired
    private InspectionPlanRecordMissionService inspectionPlanRecordMissionService;

    @Autowired
    private ApplicationContext applicationContext;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Autowired
    private BaseNestService baseNestService;

    @Autowired
    private BaseUavService baseUavService;

    @Resource
    private SysTagService sysTagService;

    @Autowired
    private DJITaskFileService djiTaskFileService;

    @Resource
    private MissionPhotoManager missionPhotoManager;

    @Resource
    private GridMissionService gridMissionService;

    @Resource
    private ExecutorService executeDjiMissionExecutorService;

    @Resource
    private AccountServiceClient accountServiceClient;

    public static final String CPS_VERSION = "24000";

    @Resource
    private MissionMediaErrLogService missionMediaErrLogService;

    @Resource
    private FileServiceClient fileServiceClient;

    @Override
    public List<MissionEntity> listMissionByTaskId(int taskId) {
        return list(new QueryWrapper<MissionEntity>().eq("task_id", taskId).eq("deleted", false));
    }

    @Override
    public List<MissionEntity> listByTaskIds(List<Integer> taskIds) {
        if (CollectionUtils.isEmpty(taskIds)) {
            return Collections.emptyList();
        }
        return baseMapper.batchSelectByTaskIdList(taskIds);
    }

    @Override
    public RestRes startMission(Integer missionId, Integer gainDataMode, Integer gainVideo, Boolean multiTask, Integer flightStrategy) {
        return null;
    }

    @Override
    public RestRes startMission2(StartMissionParamDto paramDto) {
        //飞行策略，通过传输进来的飞行策略跟上一次的进行比较，如果跟上一次的不一样，就需要重新buildMission
        MissionEntity missionEntity = baseMapper.selectById(paramDto.getMissionId());
        if (missionEntity == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_CORRESPONDING_MISSION_CANNOT_BE_QUERIED.getContent()));
        }
        TaskEntity taskEntity = taskService.lambdaQuery()
                .eq(TaskEntity::getId, missionEntity.getTaskId())
                .select(TaskEntity::getType, TaskEntity::getMold)
                .one();
        Integer taskType = taskEntity.getType();
        Integer mold = taskEntity.getMold();
        StartMissionNestInfoOutDTO nestParam = baseNestService.getStartMissionParamByMissionId(paramDto.getMissionId());
        if (Objects.isNull(nestParam)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CORRESPONDING_NEST_CANNOT_BE_QUERIED.getContent()));
        }
        AirIndexEnum uavWhich = AirIndexEnum.getInstance(paramDto.getUavWhich());
        String nestUuid = nestParam.getNestUuid();

        NestStateEnum nestStateEnum = commonNestStateService.getNestStateEnum(nestUuid, uavWhich);
        /**
         * 云冠除外的判断基站状态是否允许发送任务
         */
        if (NestTypeEnum.I_CREST2.getValue() != nestParam.getNestType()) {
            if (NestTypeEnum.G503.getValue() == nestParam.getNestType()) {
                if (!g503CheckNestAllowSendTask(nestUuid, uavWhich)) {
                    return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CURRENT_STATUS_NEST_STATUE_NOT_ALLOWED_SENT.getContent())
                            .replace("STATUE", nestStateEnum.getChinese()));
                }
            } else {
                if (!checkNestAllowSendTask(nestStateEnum)) {
                    return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CURRENT_STATUS_NEST_STATUE_NOT_ALLOWED_SENT.getContent())
                            .replace("STATUE", nestStateEnum.getChinese()));
                }
            }
        }


        if (NestTypeEnum.I_CREST2.getValue() == nestParam.getNestType()) {
            return iCrestStartMission(paramDto.getFlyType(), missionEntity, nestParam, taskType, 0, mold);
        }

        ComponentManager cm = ComponentManagerFactory.getInstance(nestParam.getNestUuid());

        if (cm != null) {
            //从机巢获取该任务
            MissionManagerCf missionManagerCf = cm.getMissionManagerCf();
            MqttResult<Waypoint> waypointMqttResult = missionManagerCf.listWaypointByMissionId(missionEntity.getUuid(), uavWhich);
            boolean existState = waypointMqttResult.isSuccess() && !waypointMqttResult.getResList().isEmpty();
            MqttResult<String> uploadMissionRes;
            if (!existState) {
                //检查任务是否属于调用kmz航线执行
                BuildKmzMissionRes buildKmzMissionRes = isKmzMission(nestParam, taskType, missionEntity);
                log.info("检查任务是否属于调用kmz航线执行 isKmzMission ，{}", buildKmzMissionRes);
                if (buildKmzMissionRes.getFlag()) {
                    if (buildKmzMissionRes.getKzmMission() == null) {
                        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NEST_MISSION_CONSTRUCTION_FAILED.getContent()));
                    }
                    uploadMissionRes = cm.getMissionManagerCf().uploadKmzMission(buildKmzMissionRes.getKzmMission(), uavWhich);
                } else {
                    BuildMissionRes bmr = buildMission(missionEntity, nestParam, taskType, mold, uavWhich);
                    Mission mission = bmr.getMission();
                    if (mission == null) {
                        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NEST_MISSION_CONSTRUCTION_FAILED.getContent()) + bmr.getErrMsg());
                    }
                    uploadMissionRes = cm.getMissionManagerCf().uploadMission(mission, uavWhich);
                }
                boolean uploadState = uploadMissionRes.isSuccess() && uploadMissionRes.getRes() != null;
                String uploadMissionFailMsg = uploadMissionRes.getMsg();
                if (!uploadState) {
                    return RestRes.err(uploadMissionFailMsg);
                }
            }

            DomainConfig domain = geoaiUosProperties.getDomain();
            UploadConfig uploadConfig = geoaiUosProperties.getUpload();
            String chunkInitUrl = String.format("%s%s", domain.getNginx(), uploadConfig.getChunkInitPath()),
                    chunkCombineUrl = String.format("%s%s", domain.getNginx(), uploadConfig.getChunkCombinePath()),
                    chunkSyncUrl = String.format("%s%s", domain.getNginx(), uploadConfig.getChunkSyncPath());

            StartMissionParamEntity startMissionParamEntity = new StartMissionParamEntity();
            startMissionParamEntity.setMissionUuid(missionEntity.getUuid());
            startMissionParamEntity.setMode(paramDto.getGainDataMode());
            startMissionParamEntity.setUploadUrl(uploadConfig.getUrl());
            startMissionParamEntity.setChunkCombineUrl(chunkCombineUrl);
            startMissionParamEntity.setChunkInitUrl(chunkInitUrl);
            startMissionParamEntity.setChunkSyncUrl(chunkSyncUrl);
            startMissionParamEntity.setUploadByChunks(true);
            startMissionParamEntity.setDisableRtkInMission(PositionStrategyEnum.completeDisableRtkInMission(paramDto.getPositionStrategy()));
            startMissionParamEntity.setOssConfig(geoaiUosProperties.getOss());
            MqttResult<NullParam> startMissionRes = cm.getMissionManagerCf().startMission(paramDto.getFlyType(),startMissionParamEntity, uavWhich);
            if (startMissionRes.isSuccess()) {
                //检测是否是起飞录制
                boolean takeOffRecord = checkLinearTakeOffRecord(TaskModeEnum.getInstance(taskType), missionEntity.getAirLineId());
                boolean switchZoomCamera = (NestTypeEnum.G900.equals(NestTypeEnum.getInstance(nestParam.getNestType())) ||
                        NestTypeEnum.G900_CHARGE.equals(NestTypeEnum.getInstance(nestParam.getNestType())))
                        && checkFocalAirLine(missionEntity.getAirLineId());

                Integer flyTimes = missionRecordsService.countFlyByMissionId(paramDto.getMissionId());
                ListenMissionRunDTO listenMissionRunDTO = new ListenMissionRunDTO()
                        .setCm(cm)
                        .setMissionId(paramDto.getMissionId())
                        .setMissionUuid(missionEntity.getUuid())
                        .setGainDataMode(paramDto.getGainDataMode())
                        .setGainVideo(paramDto.getGainVideo())
                        .setNestUuid(nestUuid)
                        .setFlyTimes(flyTimes)
                        .setStartTime(LocalDateTime.now())
                        .setSwitchZoomCamera(switchZoomCamera)
                        .setTakeOffRecord(takeOffRecord)
                        .setTaskId(missionEntity.getTaskId())
                        .setTaskType(taskEntity.getType())
                        .setMissionName(missionEntity.getName())
                        .setTakeOffRecord(takeOffRecord)
                        .setUavWhich(uavWhich.getVal())
                        .setGridInspectId(paramDto.getGridInspectId())
                        .setAccountId(paramDto.getAccountId());

                //监听任务过程，记录道数据库
                listenMissionRunning(listenMissionRunDTO);

                //初始化任务进度并且传输
                InitTaskProgressDtoParam initTaskProgressDtoParam = new InitTaskProgressDtoParam()
                        .setFlyIndex(flyTimes + 1)
                        .setMissionId(paramDto.getMissionId())
                        .setMultiTask(paramDto.isMultiTask())
                        .setGainDataMode(paramDto.getGainDataMode())
                        .setGainVideo(paramDto.getGainVideo())
                        .setSwitchZoomCamera(switchZoomCamera)
                        .setUavWhich(uavWhich.getVal())
                        .setTakeOffRecord(takeOffRecord);
                if (NestTypeEnum.G503.getValue() == nestParam.getNestType()) {
                    g503WsTaskProgressService.initTaskProgressDto(initTaskProgressDtoParam);
                } else {
                    wsTaskProgressService.initTaskProgressDto(initTaskProgressDtoParam);
                }
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_MISSION_OPENING_COMMAND.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SEND_MISSION_START_COMMAND.getContent()) + startMissionRes.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SEND_TASK_START_COMMAND_THE_NEST_IS_NOT_CONNECTED.getContent()));
    }

    private BuildKmzMissionRes isKmzMission(StartMissionNestInfoOutDTO nestParam, Integer taskType, MissionEntity missionEntity) {
        BuildKmzMissionRes res = new BuildKmzMissionRes();
        //基站是否为S110-御三  任务类型是否为 大疆本地航线
        log.info("检查参数 nestParam：{} taskType：{}  missionEntity:{}", nestParam, taskType, missionEntity);
        if (NestTypeEnum.S110_MAVIC3.getValue() != nestParam.getNestType() || TaskModeEnum.DJI_KML.getValue() != taskType) {
            return res;
        }
        //记录文件是否为 .kmz结尾
        DJITaskFileInDTO.DJITaskFileQueryInDTO djiTaskFileQueryInDTO = new DJITaskFileInDTO.DJITaskFileQueryInDTO();
        djiTaskFileQueryInDTO.setTaskId(missionEntity.getTaskId().toString());
        djiTaskFileQueryInDTO.setMissionId(missionEntity.getId().toString());
        DJITaskOutDTO.DJITaskFileQueryOutDTO djiTaskFileQueryOutDTO = djiTaskFileService.queryOutDTO(djiTaskFileQueryInDTO);
        if (ObjectUtils.isEmpty(djiTaskFileQueryOutDTO)) {
            return res;
        }
        if (!djiTaskFileQueryOutDTO.getFileUrl().endsWith(".kmz")) {
            return res;
        }
        //CPS版本是否大于2.4.0
        SystemManagerCf systemManagerCf = ComponentManagerFactory.getInstance(nestParam.getNestUuid()).getSystemManagerCf();
        if (ObjectUtils.isEmpty(systemManagerCf)) {
            throw new BusinessException("基站离线");
        }
        MqttResult<CpsVersionCode> cpsVersionCode = systemManagerCf.getCpsVersionCode();
        if (!cpsVersionCode.isSuccess()) {
            return res;
        }
        log.info("Cps_Version {}", cpsVersionCode.getRes().getVersion_code());
        if (Integer.valueOf(cpsVersionCode.getRes().getVersion_code()) < Integer.valueOf(CPS_VERSION)) {
            return res;
        }
        res.setFlag(Boolean.TRUE);
        BuildKmzMissionRes.KzmMission kzmMission = new BuildKmzMissionRes.KzmMission();
        kzmMission.setKmzUrl(geoaiUosProperties.getDomain().getNginx().concat(djiTaskFileQueryOutDTO.getFileUrl()));
       /* Result<Boolean> booleanResult = fileServiceClient.checkFileExists(djiTaskFileQueryOutDTO.getFileUrl());
        if(booleanResult.getData()) {
            Result<FileAccessLinkOutDO> downloadLink = fileServiceClient.getDownloadLink(djiTaskFileQueryOutDTO.getFileUrl(),false);
            log.info("BuildKmzMissionRes ->getAccessUrl,{}",downloadLink.getData().getAccessUrl());
            String accessUrl = downloadLink.getData().getAccessUrl();
            try {
                String encode = URLDecoder.decode(accessUrl, "UTF-8");
                kzmMission.setKmzUrl(encode);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }*/
        kzmMission.setMissionID(missionEntity.getUuid());
        kzmMission.setName(missionEntity.getName());
        res.setKzmMission(kzmMission);
        return res;
    }

    @Override
    public RestRes startMissionDji(StartMissionParamDto paramDto) {
        Integer missionId = paramDto.getMissionId();
        DjiStartTaskParamDTO djiStartTaskParamDTO = taskService.getDjiStartTaskParamDTOByMissionId(missionId);
        if (Objects.isNull(djiStartTaskParamDTO)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_START_TASK_NO_TASK_WAS_QUERIED.getContent()));
        }
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(djiStartTaskParamDTO.getBaseNestId());
        if (StrUtil.isEmpty(nestUuid)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_START_TASK_CANT_QUERY_NESTUUID.getContent()));
        }
        DJITaskFileInDTO.DJITaskFileQueryInDTO dto = new DJITaskFileInDTO.DJITaskFileQueryInDTO();
        //调整为按missionId去查找
        dto.setMissionId(missionId.toString());
        DJITaskOutDTO.DJITaskFileQueryOutDTO djiTaskFileQueryOutDTO = djiTaskFileService.queryOutDTO(dto);
        if (Objects.isNull(djiTaskFileQueryOutDTO)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_START_MISSION_CANT_FIND_DJI_ROUTE.getContent()));
        }
        String fileUrl = geoaiUosProperties.getDomain().getNginx() + djiTaskFileQueryOutDTO.getFileUrl();
        String fileMd5 = djiTaskFileQueryOutDTO.getFileMd5();
        String flightId = UUID.fastUUID().toString();
        FlightTaskPrepareDO.File file = new FlightTaskPrepareDO.File();
        file.setUrl(fileUrl);
        file.setFingerprint(fileMd5);
        FlightTaskPrepareDO build = FlightTaskPrepareDO.builder()
                .flightId(flightId)
                .executeTime(System.currentTimeMillis())
                .taskType(FlightTaskPrepareDO.TaskTypeEnum.IMMEDIATE_TASK.getValue())
                .waylineType(FlightTaskPrepareDO.WaylineTypeEnum.COMMON_WAYPOINT.getValue())
                .rthAltitude(djiStartTaskParamDTO.getRthAltitude())
                .outOfControlAction(FlightTaskPrepareDO.OutOfControlActionEnum.GO_HOME.getValue())
                .file(file)
                .build();

        StartMissionNestInfoOutDTO nestParam = baseNestService.getStartMissionParamByMissionId(paramDto.getMissionId());
        if (Objects.isNull(nestParam)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_START_MISSION_FAILED_TO_GET_PARAMETERS_FOR_MISSION_START.getContent()));
        }
        //基站状态判断 && 无人机开机判断
        DjiDockPropertyOsdDO djiDockPropertyOsdDO = commonNestStateService.getDjiDockPropertyOsdDO(nestParam.getNestUuid());
        if (Objects.isNull(djiDockPropertyOsdDO)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_START_MISSIONCANT_GET_BASE_STATION_STATUS.getContent()));
        }
        if (DjiDockPropertyOsdDO.ModeCodeEnum.IDLE.getValue() != djiDockPropertyOsdDO.getModeCode()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_START_THE_TASK_THE_STATUS_OF_THE_BASE_STATION_IS_NOT_IDLE.getContent())
                    + "【" + DjiDockPropertyOsdDO.ModeCodeEnum.getInstance(djiDockPropertyOsdDO.getModeCode()).getExpress() + "】");
        }
        //异步提交大疆任务
        executeDjiMissionExecutorService.execute(() -> djiMissionRunnable(paramDto, nestParam, build, djiStartTaskParamDTO));
        return RestRes.ok("大疆机场启动任务");
    }

    /**
     * 记录大疆数据到missionRecords
     */
    public Integer recordsDji(ListenMissionRunDTO listenMissionRunDTO) {

        MissionRecordsEntity missionRecordsEntity = new MissionRecordsEntity();
        missionRecordsEntity.setCreateTime(LocalDateTime.now());
        missionRecordsEntity.setStartTime(LocalDateTime.now());
        missionRecordsEntity.setMissionId(listenMissionRunDTO.getMissionId());
        missionRecordsEntity.setGainDataMode(listenMissionRunDTO.getGainDataMode());
        missionRecordsEntity.setGainVideo(listenMissionRunDTO.getGainVideo());
        missionRecordsEntity.setStatus(MissionRecordsStatusEnum.EXECUTING.getValue());
        missionRecordsEntity.setExecId(listenMissionRunDTO.getExecId());
        missionRecordsEntity.setFlyIndex(listenMissionRunDTO.getFlyTimes() + 1);
        missionRecordsEntity.setCreateUserId(Long.parseLong(listenMissionRunDTO.getAccountId()));
        //TODO 后面需要设置为航线最大值
        missionRecordsEntity.setReachIndex(2);
        //记录数据
        boolean b = missionRecordsService.saveOrUpdate(missionRecordsEntity,
                new UpdateWrapper<MissionRecordsEntity>().lambda().eq(MissionRecordsEntity::getExecId, listenMissionRunDTO.getExecId()));
        //补充网格化数据
        if (TaskModeEnum.GRID.getValue().equals(listenMissionRunDTO.getTaskType())) {
            GridMissionDTO gridMissionDTO = new GridMissionDTO();
            gridMissionDTO.setMissionId(listenMissionRunDTO.getMissionId());
            gridMissionDTO.setMissionName(listenMissionRunDTO.getMissionName());
            gridMissionDTO.setNestUuid(listenMissionRunDTO.getNestUuid());
            gridMissionDTO.setTaskId(listenMissionRunDTO.getTaskId());
            gridMissionDTO.setMissionRecordsId(missionRecordsEntity.getId());
            log.info("记录网格数据 gridMissionDTO->{}", gridMissionDTO);
            boolean res = gridMissionService.saveMissionToGrid(gridMissionDTO);
            boolean resSaveGridInspect = gridMissionService.saveGridInspect(gridMissionDTO);
        }
        return missionRecordsEntity.getId();
    }


    @Override
    public RestRes uploadMission(Integer missionId) {
        MissionEntity me = baseMapper.selectById(missionId);
        MissionParamEntity mp = missionParamService.getById(me.getMissionParamId());
        TaskEntity task = taskService.getById(me.getTaskId());
        List<Waypoint> waypointList = airLineService.parseAirLine(missionId, task.getBaseNestId(), 0);

        Mission mission = new Mission();
        mission.setMissionID(me.getUuid());
        mission.setName(me.getName());
        mission.setMission(waypointList);
        mission.setHeadingMode(HeadingModeEnum.getInstance(mp.getHeadingMode()));

        String uuid = nestService.getUuidById(task.getNestId());

        return uploadMissionToNest(mission, uuid);
    }


    @Override
    public RestRes pauseMission(String nestId) {
//        NestEntity ne = nestService.getNestByIdIsCache(nestId);
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            MissionManagerCf missionManager = cm.getMissionManagerCf();
            MqttResult<NullParam> res = missionManager.pauseMission();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_SUSPEND_MISSION.getContent()));
            } else {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SUSPEND_TASK.getContent())
                        + "," + res.getMsg());
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SUSPEND_TASK.getContent()));
    }

    @Override
    public RestRes continueMission(Boolean breakPoint, String nestId) {
//        NestEntity ne = nestService.getNestByIdIsCache(nestId);
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            MissionManagerCf missionManager = cm.getMissionManagerCf();
            MqttResult<NullParam> res = missionManager.continueMission(breakPoint);
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_CONTINUE_MISSION.getContent()));
            } else {
                return RestRes.err(res.getMsg());
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CONTINUE_MISSION_FAILED.getContent()));
    }

    @Override
    public RestRes stopMission(String nestId) {
//        NestEntity ne = nestService.getNestByIdIsCache(nestId);
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getMissionManagerCf().stopMission();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_TERMINATE_MISSION.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TERMINATE_TASK_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public Integer add(MissionEntity missionEntity) {
        return baseMapper.insert(missionEntity);
    }

    @Override
    public PageUtils getAllMissionPage(Map<String, Object> params, String taskName) {

        return null;
    }

    @Override
    public IPage<MissionDto> getAppMissionRecordPage(IPage<MissionDto> page, String appId, String taskName) {
        return baseMapper.getAppMissionRecordPage(page, appId, taskName);
    }

    @Override
    public PageUtils getAllMissionPageByNest(NestReqDto nestReqDto) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("page", nestReqDto.getCurrentPageNo());
        params.put("limit", nestReqDto.getCurrentPageSize());

        String nestId = nestReqDto.getNestId();
        if (Objects.isNull(nestId)) {
            return PageUtils.emptyPageUtils();
        }
        String taskName = nestReqDto.getTaskName();

        // @DataTypeEnum
        Integer dataType = nestReqDto.getDataType();
        List<String> execIds = new ArrayList<>();
        if (dataType != null && dataType == 7) {
            execIds = baseMapper.getMissionAirExecIds();
        }
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        IPage<MissionDto> totalPage = baseMapper.getAllMissionPage(new Query<MissionDto>().getPage(params), taskName, nestId, execIds, orgCode);
        List<MissionDto> records = totalPage.getRecords();
        records.forEach(r -> r.setName(replaceMission(r.getName())));
        setMissionDtoList(records, dataType);
        return new PageUtils(totalPage);
    }

    private Map<String, AccountOutDO> getAccountMap(List<MissionDto> records) {
        // 批量查询账号信息
        if (!CollectionUtils.isEmpty(records)) {
            List<String> accountIds = records.stream().map(MissionDto::getCreateUserId).map(Object::toString).collect(Collectors.toList());
            Result<List<AccountOutDO>> result = accountServiceClient.listAccountInfos(accountIds);
            if (result.isOk()) {
                return result.getData()
                        .stream()
                        .collect(Collectors.toMap(AccountOutDO::getAccountId, r -> r));
            }
        }
        return Collections.emptyMap();
    }

    private void setMissionDtoList(List<MissionDto> missionDtoList, Integer dataType) {
        List<Integer> missionRecordIdList = missionDtoList.stream().map(MissionDto::getId).collect(Collectors.toList());
        if (dataType != null && dataType == DataTypeEnum.VIDEO.getValue()) {
            List<MissionVideoEntity> missionVideoEntities;

            if (CollUtil.isEmpty(missionRecordIdList)) {
                missionVideoEntities = Collections.emptyList();
            } else {
                missionVideoEntities = missionVideoService.lambdaQuery().in(MissionVideoEntity::getMissionRecordsId, missionRecordIdList).list();
            }
            for (MissionDto missionDto : missionDtoList) {
                List<MissionVideoEntity> collect = missionVideoEntities.stream().filter(e -> e.getMissionRecordsId() != null && e.getMissionRecordsId().equals(missionDto.getId())).collect(Collectors.toList());
                boolean highVideo = collect.size() > 0 && collect.stream().allMatch(e -> e.getType() != 1 && e.getPhysicalDeleted());
                if (highVideo) {
                    missionDto.setHighVideo(1);
                } else {
                    missionDto.setHighVideo(0);
                }
                boolean recordVideo = collect.size() > 0 && collect.stream().anyMatch(e -> e.getType() == 1 && !e.getPhysicalDeleted());
                if (recordVideo) {
                    missionDto.setRecordVideo(0);
                } else {
                    missionDto.setRecordVideo(1);
                }
                missionDto.setHasAutoData(collect.size() > 0);
            }
        } else if (dataType != null && dataType == DataTypeEnum.PHOTO.getValue() && CollUtil.isNotEmpty(missionRecordIdList)) {
            //查询所有异常记录信息
            List<Integer> missionIds = missionDtoList.stream().map(MissionDto::getMissionId).collect(Collectors.toList());
            // 查询图片
            List<MissionPhotoOutDO.PhotoNumOutDO> photoNumOutDOList = missionPhotoManager.queryPhotoNum(missionRecordIdList);
            Map<Integer, Long> integerLongMap = photoNumOutDOList.stream()
                    .collect(Collectors.toMap(MissionPhotoOutDO.PhotoNumOutDO::getMissionRecordId, MissionPhotoOutDO.PhotoNumOutDO::getNum, (key1, key2) -> key1));
            for (MissionDto missionDto : missionDtoList) {
                Long num = integerLongMap.get(missionDto.getId());
                missionDto.setHasAutoData(num != null && num > 0);
            }
            //设置异常信息
            long count = missionDtoList.stream().filter(t -> t.getDataStatus() == 4).count();
            if (count > 0) {
                setErrLog(missionDtoList, missionIds);
            }
        }
        // 设置账号名称
        Map<String, AccountOutDO> result = getAccountMap(missionDtoList);
        missionDtoList.forEach(r -> {
            String accountId = Objects.nonNull(r.getCreateUserId()) ? r.getCreateUserId().toString() : "";
            if (!CollectionUtils.isEmpty(result) && result.containsKey(accountId)) {
                r.setRealName(result.get(accountId).getName());
            }
        });

    }

    public void setErrLog(List<MissionDto> missionDtoList, List<Integer> missionIds) {
        List<MissionMediaErrLogOutDTO> outDTOS = missionMediaErrLogService.selectList(missionIds);
        Map<Integer, List<MissionMediaErrLogOutDTO>> collect = outDTOS.stream().collect(Collectors.groupingBy(MissionMediaErrLogOutDTO::getMissionRecordId));
        missionDtoList.forEach(r -> {
            Integer id = r.getId();
            if (CollectionUtil.isNotEmpty(collect) && CollectionUtil.isNotEmpty(collect.get(id)) /*&& r.getDataStatus() == 4*/) {
                List<MissionMediaErrLogOutDTO> errLogOutDTOS = collect.get(id);
                Set<MissionMediaErrLogOutDTO> logOutDTOSet = new TreeSet<>(Comparator.comparing(MissionMediaErrLogOutDTO::getErrorCode));
                logOutDTOSet.addAll(errLogOutDTOS);
                List<MissionDto.ErrInfo> errInfos = logOutDTOSet.stream().map(t -> {
                    MissionDto.ErrInfo errInfo = new MissionDto.ErrInfo();
                    errInfo.setCode(t.getErrorCode());
                    errInfo.setDesc(DataSynEnum.getValueByCode(t.getErrorCode()));
                    return errInfo;
                }).collect(Collectors.toList());
                r.setErrInfo(errInfos);
            }
        });
    }

    @Override
    public Integer getNestIdById(Integer id) {
        return baseMapper.getNestIdById(id);
    }

    @Override
    public String getNestUuidById(Integer id) {
        Integer nestId = baseMapper.getNestIdById(id);
        return nestService.getUuidById(nestId);
    }

    @Override
    public Integer getNestTypeById(Integer id) {
        return baseMapper.getNestTypeById(id);
    }

    @Override
    public RestRes listMissionDataTransMode(Integer taskId) {
        String redisKey = RedisKeyEnum.REDIS_KEY.className("MissionServiceImpl").methodName("startMission").identity("gainDataMode", taskId.toString()).get();
        Object map = redisService.get(redisKey);
        Map<String, Object> resMap = new HashMap<>(2);
        if (map == null) {
            List<MissionEntity> list = this.list(new QueryWrapper<MissionEntity>().lambda().eq(MissionEntity::getTaskId, taskId));
            if (CollectionUtil.isNotEmpty(list)) {
                List<Integer> meIdList = list.stream().map(MissionEntity::getId).collect(Collectors.toList());
                Map<Integer, Integer> missionGainDataModeMap = new HashMap<>(meIdList.size());
                for (int i = 0; i < meIdList.size(); i++) {
                    missionGainDataModeMap.put(meIdList.get(i), 2);
                }
                resMap.put("missionTransModeMap", missionGainDataModeMap);
                redisService.set(redisKey, missionGainDataModeMap);
            } else {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_QUERY_SORTIE_INFORMATION_NOT_AVAILABLE.getContent()));
            }
        } else {
            resMap.put("missionTransModeMap", map);
        }
        return RestRes.ok(resMap);
    }

    @Override
    public Map<String, Integer> getMissionPhotoCountById(Integer missionId) {
        return baseMapper.selectPhotoCountById(missionId);
    }

    @Override
    public RestRes appStartMission(Integer missionId, Integer gainDataMode, Integer gainVideo) {
        NestEntity nestEntity = nestService.getNestByMissionId(missionId);
        appService.startMission(nestEntity.getUuid(), missionId, gainDataMode, gainVideo);
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_MISSION_OPENED.getContent()));
    }

    /**
     * 航线来源问题
     *
     * @param missionId
     * @return
     */
    @Override
    public RestRes getAppMissionData(Integer missionId) {
        MissionEntity missionEntity = this.getById(missionId);
        // 获取航线json
        Map<String, Object> result = getAppAirLineRes(missionEntity);
        if (CollectionUtil.isEmpty(result)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROUTE_NOT_QUERIED.getContent()));
        }
        return RestRes.ok(result);
    }

    @Override
    public Map<String, Object> getAppAirLineRes(MissionEntity missionEntity) {
        Map<String, Object> result = new HashMap<>(2);
        AirLineEntity airLineEntity = airLineService.getById(missionEntity.getAirLineId());
        String mode = "";
        TaskEntity taskEntity = taskService.lambdaQuery()
                .eq(TaskEntity::getId, missionEntity.getTaskId())
                .eq(TaskEntity::getDeleted, false)
                .select(TaskEntity::getType)
                .one();

        Map<String, Object> airLineJsonMap = new HashMap<>(4);
        if (airLineEntity != null) {
            if (StrUtil.isEmpty(airLineEntity.getWaypoints())) {
                return Collections.emptyMap();
            }
            if (airLineEntity.getType() == 1) {
                Integer type = taskEntity.getType();
                if (TaskModeEnum.ORTHOPHOTO.getValue().equals(type) || TaskModeEnum.SLOPEPHOTO.getValue().equals(type)) {
                    //TODO 多架次处理
                    UnifyAirLineFormatDto unifyAirLineFormatDto = JacksonUtil.json2Object(airLineEntity.getWaypoints(), UnifyAirLineFormatDto.class);
                    List pointss = (List) unifyAirLineFormatDto.getMapConfigs().get("points");
                    Integer seqId = missionEntity.getSeqId();
                    List airLineJson = null;
                    if (seqId != null && seqId > 0) {
                        airLineJson = (List) pointss.get(seqId - 1);
                    } else {
                        airLineJson = (List) pointss.get(0);
                    }
                    List ll = new ArrayList(1);
                    ll.add(airLineJson);
                    unifyAirLineFormatDto.getMapConfigs().put("points", ll);
                    result.put("airLineJson", unifyAirLineFormatDto);
                } else {
                    result.put("airLineJson", JSONObject.parse(airLineEntity.getWaypoints()));
                }

            } else {
                MissionParamEntity mpe = missionParamService.lambdaQuery()
                        .eq(MissionParamEntity::getId, missionEntity.getMissionParamId())
                        .eq(MissionParamEntity::getDeleted, false)
                        .select(MissionParamEntity::getAutoFlightSpeed, MissionParamEntity::getSpeed, MissionParamEntity::getStartStopPointAltitude)
                        .one();
                Map<String, Object> lineConfigsMap = new HashMap<>(2);
                if (airLineEntity.getType() == 2) {
                    List<EasyFlyPoint> waypointList = JSONObject.parseArray(airLineEntity.getWaypoints(), EasyFlyPoint.class);
                    airLineJsonMap.put("missions", waypointList);
                    if (TaskModeEnum.DELICACY.getValue().equals(taskEntity.getType())) {
                        mode = TaskModeEnum.DELICACY.name();
                        Map<String, Object> delicacyMap = new HashMap<>(8);
                        delicacyMap.put("taskMode", "CAMERA_TIMER");
                        delicacyMap.put("flightStrategy", "STUDY");
                        delicacyMap.put("inspectionMode", "IMAGE");
                        delicacyMap.put("reverseAirLine", false);
                        delicacyMap.put("aroundTowerSpeed", mpe.getSpeed());
                        delicacyMap.put("betweenTowerSpeed", mpe.getAutoFlightSpeed());
                        lineConfigsMap.put("DELICACY", delicacyMap);
                    } else {
                        //本地上传、动态规划两个任务类型
                        mode = TaskModeEnum.POINTCLOUD.name();

                        Map<String, Object> pointCloudMap = new HashMap<>(4);
                        pointCloudMap.put("takeOffLandAlt", mpe.getStartStopPointAltitude());
                        pointCloudMap.put("speed", mpe.getSpeed());
                        pointCloudMap.put("autoFlightSpeed", mpe.getAutoFlightSpeed());
                        lineConfigsMap.put("POINTCLOUD", pointCloudMap);
                    }

                } else if (airLineEntity.getType() == 3) {
                    //航点飞行任务
                    mode = TaskModeEnum.NEST.name();
                    Map<String, Object> nestMap = new HashMap<>(4);
                    nestMap.put("takeOffLandAlt", mpe.getStartStopPointAltitude());
                    nestMap.put("speed", mpe.getSpeed());
                    nestMap.put("autoFlightSpeed", mpe.getAutoFlightSpeed());
                    lineConfigsMap.put("NEST", nestMap);
                    List<Waypoint> waypointList = JSONObject.parseArray(airLineEntity.getWaypoints(), Waypoint.class);
                    airLineJsonMap.put("missions", waypointList);
                }
                airLineJsonMap.put("mode", mode);
                airLineJsonMap.put("lineConfigs", lineConfigsMap);
                result.put("airLineJson", airLineJsonMap);
            }
        }
        return result;
    }

    @Override
    public List<AppMissionAircraftDTO> getMissionByNestUuid(Integer limit, String nestUuid, Long lastTime) {
        return baseMapper.getMissionByNestUuid(limit, nestUuid, lastTime);
    }

    @Deprecated
    @Override
    public RestRes startBatchTask(List<Integer> taskIdList, Integer gainDataMode, Integer gainVideo, Integer flightStrategy) {
        if (CollectionUtil.isNotEmpty(taskIdList) && gainDataMode != null && gainVideo != null) {
            List<TaskEntity> taskEntityList = taskService.list(new QueryWrapper<TaskEntity>().lambda().in(TaskEntity::getId, taskIdList).eq(TaskEntity::getDeleted, false));
            taskEntityList = taskEntityList.stream().sorted(Comparator.comparingInt(o -> taskIdList.indexOf(o.getId()))).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(taskEntityList)) {
                return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MISSION_NOT_QUERIED.getContent()));
            }
            List<MissionEntity> missionEntityList = this.list(new QueryWrapper<MissionEntity>().lambda().in(MissionEntity::getTaskId, taskIdList).eq(MissionEntity::getDeleted, false));
            if (CollectionUtil.isEmpty(missionEntityList)) {
                return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_SORTIE_QUERY.getContent()));
            }

            Map<Integer, List<MissionEntity>> taskMissionMap = missionEntityList.stream().collect(Collectors.groupingBy(MissionEntity::getTaskId));
            String nestUuid = nestService.getUuidById(taskEntityList.get(0).getNestId());

            String batchTaskRunningKey = RedisKeyConstantList.BATCH_TASK_RUNNINGKEY_KEY;
            Integer batchTaskRunState = (Integer) redisService.hGet(batchTaskRunningKey, nestUuid);
            if (batchTaskRunState != null && batchTaskRunState == 1) {
                return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATCH_MISSION_IS_ALREADY_RUNNING_PLEASE_DONT_CLICK_IT_AGAIN.getContent()));
            }

            BatchTaskBody batchTaskBody = new BatchTaskBody();
            batchTaskBody.setGainDataMode(gainDataMode);
            batchTaskBody.setGainVideo(gainVideo);
            batchTaskBody.setFlightStrategy(flightStrategy);
            batchTaskBody.setNestUuid(nestUuid);
            batchTaskBody.setStartOrStop(BatchTaskBody.RunStateEnum.START.getValue());
            batchTaskBody.setPauseOrContinue(BatchTaskBody.MiddleStateEnum.CONTINUE.getValue());
            batchTaskBody.setTaskCurrentIndex(-1);
            batchTaskBody.setFlyTotalTime(0L);
            batchTaskBody.setTotalTaskCount(taskEntityList.size());

            List<TaskBody> taskBodyList = new ArrayList<>(taskEntityList.size());
            for (int i = 0; i < taskEntityList.size(); i++) {
                TaskEntity taskEntity = taskEntityList.get(i);
                TaskBody taskBody = new TaskBody();
                taskBody.setTaskType(taskEntity.getType());
                taskBody.setTaskId(taskEntity.getId());
                taskBody.setTaskName(taskEntity.getName());
                taskBody.setTaskState(TaskBody.TaskStateEnum.EXECUTED_UN.getValue());
                taskBody.setMissionCurrentIndex(-1);
                taskBody.setFlyTime(0L);
                List<MissionEntity> missionEntities = taskMissionMap.get(taskEntity.getId());
                if (CollectionUtil.isEmpty(missionEntities)) {
                    continue;
                }
                List<MissionBody> missionBodyList = missionEntities.stream().map(m -> {
                    MissionBody missionBody = new MissionBody();
                    missionBody.setMissionId(m.getId());
                    missionBody.setMissionName(m.getName());
                    missionBody.setMissionState(MissionBody.MissionStateEnum.EXECUTING.getValue());
                    missionBody.setMissionPercentage(0.0);
                    return missionBody;
                }).collect(Collectors.toList());

                taskBody.setMissionBodyList(missionBodyList);
                taskBodyList.add(taskBody);
            }
            batchTaskBody.setTaskBodyList(taskBodyList);
            String batchTaskBodyKey = String.format(RedisKeyConstantList.BATCH_TASK_BODY_KEY, nestUuid);
            redisService.set(batchTaskBodyKey, batchTaskBody);
            String pauseStateRedisKey = String.format(RedisKeyConstantList.BATCH_TASK_PAUSE_STATE_KEY, nestUuid);
            redisService.set(pauseStateRedisKey, 0);
            String stopStateRedisKey = String.format(RedisKeyConstantList.BATCH_TASK_STOP_STATE_KEY, nestUuid);
            redisService.set(stopStateRedisKey, 0);

            executorService.execute(() -> batchTaskRunnable(nestUuid));

            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATCH_MISSION_STARTED_SUCCESSFULLY.getContent()));
        }

        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INCORRECT_PARAMETERS.getContent()));
    }

    @Override
    public RestRes pauseOrStopOrEndBatchTask(Integer nestId, Integer type) {
        String uuid = nestService.getUuidById(nestId);

        //批量任务设置暂停，可以继续执行下一个任务
        if (type == 1) {
            String pauseStateRedisKey = String.format(RedisKeyConstantList.BATCH_TASK_PAUSE_STATE_KEY, uuid);
            redisService.set(pauseStateRedisKey, 1);
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATCH_TASK_HAS_BEEN_SET_TO_PAUSE_THE_NEXT_TASK_WILL_NOT_BE_EXECUTED.getContent()));

            //批量任务设置停止，不可以执行下一个任务，但是可以重新执行片该任务
        } else if (type == 2) {
            String stopStateRedisKey = String.format(RedisKeyConstantList.BATCH_TASK_STOP_STATE_KEY, uuid);
            redisService.set(stopStateRedisKey, 1);
            redisService.hSet(RedisKeyConstantList.BATCH_TASK_RUNNINGKEY_KEY, uuid, 0);
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATCH_TASK_HAS_BEEN_SET_TO_STOP_THE_NEXT_TASK_WILL_NOT_BE_EXECUTED.getContent()));

            //结束批量任务，不会执行下一个任务，立即关闭批量任务推流消息
        } else if (type == 3) {

            String stopStateRedisKey = String.format(RedisKeyConstantList.BATCH_TASK_STOP_STATE_KEY, uuid);
            redisService.set(stopStateRedisKey, 1);
            redisService.hSet(RedisKeyConstantList.BATCH_TASK_RUNNINGKEY_KEY, uuid, 0);
            wsTaskProgressService.stopPushBatchTaskListMsg(uuid);
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_BATCH_TASK_HAS_BEEN_SET_TO_END.getContent()));
        }
        return RestRes.err();

    }

    @Override
    public RestRes cancelBatchTaskOfOne(Integer nestId, Integer taskId) {
        String nestUuid = nestService.getUuidById(nestId);
        if (nestUuid != null) {
            String batchTaskBodyKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BATCH_TASK_BODY_KEY, nestUuid);
            BatchTaskBody batchTaskBody = (BatchTaskBody) redisService.get(batchTaskBodyKey);
            List<TaskBody> taskBodyList = batchTaskBody.getTaskBodyList();
            boolean updated = false;
            for (TaskBody tb : taskBodyList) {
                if (taskId.equals(tb.getTaskId())) {
                    if (tb.getTaskState() == 0) {
                        updated = true;
                        tb.setTaskState(3);
                    }
                }
            }

            redisService.set(batchTaskBodyKey, batchTaskBody);
            String cancelBatchTaskOfOneKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.CANCEL_BATCH_TASK_OF_ONE, nestUuid);
            redisService.hSet(cancelBatchTaskOfOneKey, taskId.toString(), true);
            redisService.expire(cancelBatchTaskOfOneKey, 2, TimeUnit.HOURS);
            if (updated) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_TASK_HAS_BEEN_CANCELLED_IT_WILL_SKIP_TO_CONTINUE_THE_NEXT_TASK.getContent()));
            } else {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_TASK_CANNOT_BE_CANCELED_IN_THE_CHANGED_STATE.getContent()));
            }
        }
        return RestRes.err();
    }


    @Override
    public RestRes continueBatchTask(Integer nestId) {
        String uuid = nestService.getUuidById(nestId);
        String pauseStateRedisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BATCH_TASK_PAUSE_STATE_KEY, uuid);
        Integer pauseState = (Integer) redisService.get(pauseStateRedisKey);
        if (pauseState != null && pauseState == 1) {
            redisService.set(pauseStateRedisKey, 0);
            executorService.execute(() -> batchTaskRunnable(uuid));
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CONTINUE_TASK.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_TASK_IS_NOT_ON_PAUSE.getContent()));
    }

    @Override
    public RestRes reStartBatchTask(Integer nestId) {
        String nestUuid = nestService.getUuidById(nestId);
        String batchTaskBodyKey = String.format(RedisKeyConstantList.BATCH_TASK_BODY_KEY, nestUuid);
        BatchTaskBody batchTaskBody = (BatchTaskBody) redisService.get(batchTaskBodyKey);

        //把batchTaskBody初始化
        batchTaskBody.setStartOrStop(1);
        batchTaskBody.setPauseOrContinue(1);
        batchTaskBody.setTaskCurrentIndex(-1);
        batchTaskBody.setFlyTotalTime(0L);

        List<TaskBody> taskBodyList = batchTaskBody.getTaskBodyList();
        taskBodyList.forEach(tb -> {
            tb.setTaskState(TaskBody.TaskStateEnum.EXECUTED_UN.getValue());
            tb.setMissionCurrentIndex(-1);
            tb.setFlyTime(0L);
            List<MissionBody> missionBodyList = tb.getMissionBodyList();
            missionBodyList.forEach(mb -> mb.setMissionState(MissionBody.MissionStateEnum.EXECUTING.getValue()));
        });

        redisService.set(batchTaskBodyKey, batchTaskBody);
        executorService.execute(() -> batchTaskRunnable(nestUuid));
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATCH_MISSION_STARTED_SUCCESSFULLY.getContent()));
    }

    @Override
    public RestRes batchTaskBeforeCheckContinueExec(Integer nestId) {
        String uuid = nestService.getUuidById(nestId);
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BATCH_TASK_BEFORE_CHECK_CONTINUE_EXEC, uuid);
        redisService.set(redisKey, 1);
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_TASK_CONTINUES_TO_BE_EXECUTED.getContent()));
    }

    @Override
    public RestRes batchTaskBeforeCheckCancelExec(Integer nestId) {
        String uuid = nestService.getUuidById(nestId);
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BATCH_TASK_BEFORE_CHECK_CONTINUE_EXEC, uuid);
        redisService.set(redisKey, 0);
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TASK_CANCELLATION.getContent()));
    }

    @Override
    public Integer getAirLineIdByMissionId(Integer missionId) {
        return baseMapper.getAirLineIdByMissionId(missionId);
    }

    @Override
    public List<AirLineEntity> getAirLineByTaskId(Integer taskId) {
        return baseMapper.getAirLineByTaskId(taskId);
    }

    @Override
    public AirLineEntity getAirLineByMissionId(Integer missionId) {
        return baseMapper.getAirLineByMissionId(missionId);
    }

    @Override
    public Integer getTaskIdByMissionId(Integer missionId) {
        return baseMapper.getTaskIdByMissionId(missionId);
    }

    @Override
    public List<Map> getAllMissionsByNestTaskName(NestReqDto nestReqDto) {
        List<Map> mapList = new ArrayList<>();
        String nestId = nestReqDto.getNestId();
        String taskName = nestReqDto.getTaskName();
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        List<MissionDto> missionDtos = baseMapper.getAllMissionsByNestTaskName(taskName, nestId, orgCode);
        if (missionDtos.size() > 0) {
            setMissionDtoList(missionDtos, nestReqDto.getDataType());
            Map<String, List<MissionDto>> listMap = missionDtos.stream().collect(Collectors.groupingBy(MissionDto::getTaskName));
            Iterator<Map.Entry<String, List<MissionDto>>> iterator = listMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, List<MissionDto>> next = iterator.next();
                String key = next.getKey();
                List<MissionDto> value = next.getValue();
                Map map = new HashMap();
                map.put("taskName", key);
                map.put("missionDtos", value);
                mapList.add(map);
            }
        }
        return mapList;
    }

    @Override
    public Map<String, Object> getMissionDetails(Integer missionId) {
        if (missionId == null) {
            return null;
        }
        Map<String, Object> missionDetails = baseMapper.getMissionDetails(missionId);
        log.info("#MissionServiceImpl.getMissionDetails# missionId={}, missionDetails={}", missionId, missionDetails);
        Map<String, Object> taskTypeAndNestAlt = baseMapper.selectTaskTypeAndNestAlt(missionId);
        log.info("#MissionServiceImpl.getMissionDetails# missionId={}, taskTypeAndNestAlt={}", missionId, taskTypeAndNestAlt);
        if (taskTypeAndNestAlt != null && missionDetails != null) {
            Integer taskType = (Integer) taskTypeAndNestAlt.get("taskType");
            Double nestAlt = (Double) taskTypeAndNestAlt.get("nestAlt");
            if (taskType != null) {
                if (TaskModeEnum.LOCAL.getValue().equals(taskType)
                        || TaskModeEnum.DELICACY.getValue().equals(taskType)
                        || TaskModeEnum.DYNAMIC.getValue().equals(taskType)
                        || TaskModeEnum.SUBSSTATION_PLANING.getValue().equals(taskType)
                ) {
                    nestAlt = nestAlt == null ? 0 : nestAlt;
                    Boolean absolute = (Boolean) missionDetails.get("absolute");
                    if (absolute != null && absolute) {
                        missionDetails.put("startStopAlt", (Double) missionDetails.get("startStopAlt") - nestAlt);
                    } else {
                        missionDetails.put("startStopAlt", (Double) missionDetails.get("startStopAlt"));
                    }

                }
            }
        }
        return missionDetails;
    }

    @Override
    public List<Integer> listMissionIdByNestId(String nestId) {
        if (nestId == null) {
            return Collections.emptyList();
        }
        return baseMapper.selectMissionIdByNestId(nestId);
    }

    @Override
    public RestRes startAutoMissionQueue(AutoMissionQueueDTO param) {

        StartMissionQueueNestInfoOutDTO nestInfo = baseNestService.getStartMissionQueueNestInfoByNestId(param.getNestId());

        if (Objects.isNull(nestInfo)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BASE_STATION_NOT_QUERIED.getContent()));
        }

        // 大疆机场不检测
        if (NestTypeEnum.DJI_DOCK.getValue() != nestInfo.getNestType()) {
            RestRes restRes = checkNestEnableExecMission(nestInfo.getNestUuid(), false);
            if (!restRes.isOk()) {
                return restRes;
            }
        }

        //校验，G900、G600才能进行多任务
        if (!checkBatchTaskNestType(param.getMissionIdList(), nestInfo.getNestType())) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONLY_G900_AND_G600_CAN_PERFORM_MULTITASKING.getContent()));
        }

        //如果是单架次、只执行一次
        return startMultiMission(param, nestInfo);
    }

    @Override
    public RestRes startG503AutoMissionQueue(G503AutoMissionQueueDTO param) {

        //获取启动任务的基站信息
        StartMissionQueueNestInfoOutDTO nestInfo = baseNestService.getStartMissionQueueNestInfoByNestId(param.getNestId());
        if (Objects.isNull(nestInfo)) {
            return RestRes.err("查询不到基站");
        }
        //检测基站是否能够下发任务
//        RestRes restRes = checkG503NestEnableExecMission(nestInfo.getNestUuid(),  AirIndexEnum.DEFAULT);
//        if (!restRes.isOk()) {
//            return restRes;
//        }
        //过滤不能执行的任务
        List<G503AutoMissionQueueDTO.Mission> missionList = param.getMissionList();
        param.setMissionList(missionList.stream().filter(mission -> commonNestStateService.g503NestWhichSelectableTasks(nestInfo.getNestUuid(), AirIndexEnum.getInstance(mission.getUavWhich()))).collect(Collectors.toList()));
        if (CollectionUtil.isEmpty(param.getMissionList())) {
            return RestRes.err("任务队列初始化失败，没有可执行的架次");
        }
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.G503_AUTO_MISSION_QUEUE, nestInfo.getNestUuid());
        G503AutoMissionQueueBody g503AutoMissionQueueBody = (G503AutoMissionQueueBody) redisService.get(redisKey);
        if (Objects.isNull(g503AutoMissionQueueBody) || g503AutoMissionQueueBody.isFinish()) {
            //初始化任务队列
            g503AutoMissionQueueBody = initG503AutoMissionQueueDTO(param, nestInfo);
            if (Objects.isNull(g503AutoMissionQueueBody)) {
                return RestRes.err("任务队列初始化失败，查询不到架次");
            }
            //关闭推送进度线程
            g503WsTaskProgressService.stopPushTaskProgressMsg(nestInfo.getNestUuid());
        } else {
            List<G503AutoMissionQueueBody.Mission> missions = listG503Mission(param);
            g503AutoMissionQueueBody.addMissions(missions);
        }

        //清空之前数据
        redisService.del(redisKey);

//        String redisKey1 = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.TASK_PROGRESS_DTO, nestInfo.getNestUuid());
//        redisService.del(redisKey1);

        //存入新的redis
        boolean set = redisService.set(redisKey, g503AutoMissionQueueBody, 30, TimeUnit.MINUTES);
        if (!set) {
            return RestRes.err("任务队列初始化失败，保存到redis失败");
        }
        List<AirIndexEnum> airIndexEnums = param.getMissionList().stream().map(m -> AirIndexEnum.getInstance(m.getUavWhich())).collect(Collectors.toList());
        //注册监听器
        boolean b = MissionQueueListenerFactory.g503Register(nestInfo.getNestUuid(), airIndexEnums);
        if (!b) {
            return RestRes.err("注册监听器失败");
        }
        return RestRes.ok("任务队列开始执行");
    }

    @Override
    public RestRes cancelG503AutoMissionQueue(String nestId) {
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        if (Objects.isNull(nestUuid)) {
            return RestRes.err("查询不到基站");
        }
//        //1、注销监听器
//        boolean destroy = MissionQueueListenerFactory.g503Destroy(nestUuid);
//        if (!destroy) {
//            return RestRes.err("注销监听器失败");
//        }
        //2、清空未执行的任务队列
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.G503_AUTO_MISSION_QUEUE, nestUuid);
        G503AutoMissionQueueBody g503AutoMissionQueueBody = (G503AutoMissionQueueBody) redisService.get(redisKey);
        if (Objects.nonNull(g503AutoMissionQueueBody)) {
            if (g503AutoMissionQueueBody.isFinish()) {
                //注销监听器
                boolean destroy = MissionQueueListenerFactory.g503Destroy(nestUuid);
                if (!destroy) {
                    return RestRes.err("注销监听器失败");
                }
            } else {
                //清空监听器任务队列
                boolean clear = MissionQueueListenerFactory.clearG503WhichQueue(nestUuid);
                if (!clear) {
                    return RestRes.err("清空监听器队列失败");
                }
            }
            g503AutoMissionQueueBody.clearTodoMissions();
            redisService.set(redisKey, g503AutoMissionQueueBody);
        } else {
            //如果从redis中找不到body,注销监听器
            boolean destroy = MissionQueueListenerFactory.g503Destroy(nestUuid);
            if (!destroy) {
                return RestRes.err("注销监听器失败");
            }
        }

        return RestRes.ok("取消任务队列成功");
    }

    @Override
    public RestRes rmFinishG503Mission(String nestId, Integer missionId) {
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        if (Objects.isNull(nestUuid)) {
            return RestRes.err("查询不到基站");
        }
        //2、清空未执行的任务队列
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.G503_AUTO_MISSION_QUEUE, nestUuid);
        G503AutoMissionQueueBody g503AutoMissionQueueBody = (G503AutoMissionQueueBody) redisService.get(redisKey);
        if (Objects.nonNull(g503AutoMissionQueueBody)) {
            boolean b = g503AutoMissionQueueBody.rmMission(missionId);
            if (b) {
                if (g503AutoMissionQueueBody.isQueueEmpty()) {
                    g503WsTaskProgressService.stopPushTaskProgressMsg(nestUuid);
                }
                redisService.set(redisKey, g503AutoMissionQueueBody);
                return RestRes.ok("移除成功");
            }
        }
        return RestRes.err("移除失败");
    }

    @Override
    public RestRes cancelAutoMissionQueue(String nestId) {
        //1、注销监听器
//        NestEntity nestEntity = nestService.lambdaQuery()
//                .eq(NestEntity::getId, nestId)
//                .select(NestEntity::getUuid)
//                .one();
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);

        if (Objects.isNull(nestUuid)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BASE_STATION_NOT_QUERIED.getContent()));
        }
        boolean destroy = MissionQueueListenerFactory.destroy(nestUuid);
        if (!destroy) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_LISTENER_CANCELLATION_FAILED.getContent()));
        }
        //2、取消弹窗、延迟线程
        missionQueueListenerService.destroyAutoTaskScheduledRunnable(nestUuid);

        //3、取消任务进度线程
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, nestUuid);
        AutoMissionQueueBody body = (AutoMissionQueueBody) redisService.get(redisKey);
        if (body != null) {
            wsTaskProgressService.stopPushTaskProgressMsg(body.getNestUuid());
            wsTaskProgressService.stopPushAutoTaskQueueMsg(body.getNestUuid(), true);
            //5、更新计划记录
            if (AutoMissionQueueBody.DriveTypeEnum.PLAN_TASK.getType() == body.getExtra().getDriveType()) {
                InspectionPlanRecordMissionEntity inspectionPlanRecordMissionEntity = new InspectionPlanRecordMissionEntity();
                inspectionPlanRecordMissionEntity.setId(body.getExtra().obtainPlanRecordMissionId(body.obtainReadyMissionId()));
                inspectionPlanRecordMissionEntity.setPlanRecordId(body.getExtra().getPlanRecordId());
                inspectionPlanRecordMissionEntity.setMissionId(body.obtainReadyMissionId());
                inspectionPlanRecordMissionEntity.setExecState(InspectionPlanMissionExecStateEnum.CANCELLED.getState());
                inspectionPlanRecordMissionEntity.setFlightDuration(0);
                inspectionPlanRecordMissionEntity.setFailureCause("手动取消任务");
                inspectionPlanRecordMissionEntity.setCreatorId(body.getExtra().getUserId());
                inspectionPlanRecordMissionService.updatePlanRecordMission(inspectionPlanRecordMissionEntity);
            }

            //4、移除任务队列
            redisService.del(redisKey);

            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EMPTYING_TASK_COLUMN_SUCCEEDED.getContent()));

        }

        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CLEAR_TASK_COLUMN_FAILED.getContent()));
    }

    @Override
    public RestRes pauseAutoMissionQueue(String nestId) {
//        NestEntity nestEntity = nestService.lambdaQuery()
//                .eq(NestEntity::getId, nestId)
//                .select(NestEntity::getUuid)
//                .one();

        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        if (Objects.isNull(nestUuid)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BASE_STATION_NOT_QUERIED.getContent()));
        }
        //1、注销监听器
        boolean destroy = MissionQueueListenerFactory.destroy(nestUuid);
        if (!destroy) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUSPEND_TASK_COLUMN_FAILED.getContent()));
        }
        //2、取消弹窗倒计时线程、取消延迟线程
        missionQueueListenerService.destroyAutoTaskScheduledRunnable(nestUuid);

        //3、修改任务任务队列状态
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, nestUuid);
        AutoMissionQueueBody body = (AutoMissionQueueBody) redisService.get(redisKey);
        if (body != null) {
            body.setQueueState(AutoMissionQueueBody.QueueState.PAUSE.getValue());
            redisService.set(redisKey, body, body.completeRedisExpire(), TimeUnit.MINUTES);
            //单个任务取消全部,将队列状态改成已取消
            if (!body.getExtra().getMultiTask() && AutoMissionQueueBody.DriveTypeEnum.PLAN_TASK.getType() == body.getExtra().getDriveType()) {
                InspectionPlanRecordMissionEntity inspectionPlanRecordMissionEntity = new InspectionPlanRecordMissionEntity();
                inspectionPlanRecordMissionEntity.setId(body.getExtra().obtainPlanRecordMissionId(body.obtainReadyMissionId()));
                inspectionPlanRecordMissionEntity.setPlanRecordId(body.getExtra().getPlanRecordId());
                inspectionPlanRecordMissionEntity.setMissionId(body.obtainReadyMissionId());
                inspectionPlanRecordMissionEntity.setExecState(InspectionPlanMissionExecStateEnum.CANCELLED.getState());
                inspectionPlanRecordMissionEntity.setFlightDuration(0);
                inspectionPlanRecordMissionEntity.setFailureCause("自动任务已取消");
                inspectionPlanRecordMissionEntity.setCreatorId(body.getExtra().getUserId());
                inspectionPlanRecordMissionService.updatePlanRecordMission(inspectionPlanRecordMissionEntity);
                //单个任务计划取消时清空redis
                redisService.del(redisKey);
            }
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUSPEND_TASK_QUEUE_SUCCESS.getContent()));
    }

    @Override
    public RestRes continueAutoMissionQueue(String nestId) {
//        NestEntity nestEntity = nestService.lambdaQuery()
//                .eq(NestEntity::getId, nestId)
//                .select(NestEntity::getUuid)
//                .one();

        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        if (Objects.isNull(nestUuid)) {
            return RestRes.err("MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BASE_STATION_NOT_QUERIED.getContent())");
        }
        RestRes restRes = checkNestEnableExecMission(nestUuid, true);
        if (!restRes.isOk()) {
            return restRes;
        }
        //3、修改任务任务队列状态
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, nestUuid);
        AutoMissionQueueBody body = (AutoMissionQueueBody) redisService.get(redisKey);
        if (body != null) {
            //检测任务是否执行完毕
            if (body.completeFinish()) {
                //保存任务结束状态
                redisService.set(redisKey, body, body.completeRedisExpire(), TimeUnit.MINUTES);
                //同步数据
                missionQueueListenerService.autoUploadData(body.getNestUuid());
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TASK_QUEUE_HAS_BEEN_EXECUTED.getContent()));
            }
            body.setQueueState(AutoMissionQueueBody.QueueState.RUNNING.getValue());
            redisService.set(redisKey, body, body.completeRedisExpire(), TimeUnit.MINUTES);
        }
        boolean register = MissionQueueListenerFactory.register(nestUuid);
        if (!register) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CONTINUE_TASK_QUEUE_FAILED.getContent()));
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CONTINUE_TASK_QUEUE_SUCCEEDED.getContent()));
    }

    @Override
    public RestRes stopAutoMissionQueue(String nestId) {
//        NestEntity nestEntity = nestService.lambdaQuery()
//                .eq(NestEntity::getId, nestId)
//                .select(NestEntity::getUuid)
//                .one();
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        if (Objects.isNull(nestUuid)) {
            return RestRes.err("MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BASE_STATION_NOT_QUERIED.getContent())");
        }
        //1、注销监听器
        boolean destroy = MissionQueueListenerFactory.destroy(nestUuid);
        if (!destroy) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_LISTENER_CANCELLATION_FAILED.getContent()));
        }
        //2、取消弹窗、延迟线程
        missionQueueListenerService.destroyAutoTaskScheduledRunnable(nestUuid);

        //3、重置任务列
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, nestUuid);
        AutoMissionQueueBody body = (AutoMissionQueueBody) redisService.get(redisKey);
        body.setQueueState(AutoMissionQueueBody.QueueState.STOP.getValue());
        redisService.set(redisKey, body, body.completeRedisExpire(), TimeUnit.MINUTES);

        //5、更新计划记录
        if (AutoMissionQueueBody.DriveTypeEnum.PLAN_TASK.getType() == body.getExtra().getDriveType()) {
            InspectionPlanRecordMissionEntity inspectionPlanRecordMissionEntity = new InspectionPlanRecordMissionEntity();
            inspectionPlanRecordMissionEntity.setId(body.getExtra().obtainPlanRecordMissionId(body.obtainReadyMissionId()));
            inspectionPlanRecordMissionEntity.setPlanRecordId(body.getExtra().getPlanRecordId());
            inspectionPlanRecordMissionEntity.setMissionId(body.obtainReadyMissionId());
            inspectionPlanRecordMissionEntity.setExecState(InspectionPlanMissionExecStateEnum.CANCELLED.getState());
            inspectionPlanRecordMissionEntity.setFlightDuration((int) body.obtainReadyMissionFlyTime());
            inspectionPlanRecordMissionEntity.setFailureCause("手动终止任务");
            inspectionPlanRecordMissionEntity.setCreatorId(body.getExtra().getUserId());
            inspectionPlanRecordMissionService.updatePlanRecordMission(inspectionPlanRecordMissionEntity);
        }

        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TERMINATE_TASK_QUEUE_SUCCESS.getContent()));
    }

    @Override
    public RestRes autoMissionQueueTop(MissionQueueTopDTO missionQueueTopDTO) {
//        NestEntity nestEntity = nestService
//                .lambdaQuery()
//                .eq(NestEntity::getId, missionQueueTopDTO.getNestId())
//                .eq(NestEntity::getDeleted, false)
//                .select(NestEntity::getUuid)
//                .one();

        String nestUuid = baseNestService.getNestUuidByNestIdInCache(missionQueueTopDTO.getNestId());
        if (Objects.isNull(nestUuid)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }

        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, nestUuid);
        AutoMissionQueueBody body = (AutoMissionQueueBody) redisService.get(redisKey);
        if (body == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_AUTOMATIC_TASK_QUEUE_WAS_NOT_QUERIED.getContent()));
        }
        boolean b = body.topMission(missionQueueTopDTO.getMissionId());
        if (!b) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TASK_TOPPING_FAILED.getContent()));
        }
        redisService.set(redisKey, body);
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TASK_TOPPING_SUCCESS.getContent()));
    }

    @Override
    public RestRes autoMissionQueueRemove(MissionQueueTopDTO missionQueueTopDTO) {
//        NestEntity nestEntity = nestService
//                .lambdaQuery()
//                .eq(NestEntity::getId, missionQueueTopDTO.getNestId())
//                .eq(NestEntity::getDeleted, false)
//                .select(NestEntity::getUuid)
//                .one();

        String nestUuid = baseNestService.getNestUuidByNestIdInCache(missionQueueTopDTO.getNestId());
        if (Objects.isNull(nestUuid)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }

        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, nestUuid);
        AutoMissionQueueBody body = (AutoMissionQueueBody) redisService.get(redisKey);
        if (body == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_AUTOMATIC_TASK_QUEUE_WAS_NOT_QUERIED.getContent()));
        }
        boolean b = body.removeMissionBody(missionQueueTopDTO.getMissionId());
        if (!b) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TASK_REMOVAL_FAILED.getContent()));
        }
        redisService.set(redisKey, body, body.completeRedisExpire(), TimeUnit.MINUTES);
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_REMOVE_TASK_SUCCESS.getContent()));
    }

    @Override
    public RestRes autoMissionQueueAdd(MissionQueueAddDTO missionQueueAddDTO) {
//        NestEntity nestEntity = nestService
//                .lambdaQuery()
//                .eq(NestEntity::getId, missionQueueAddDTO.getNestId())
//                .eq(NestEntity::getDeleted, false)
//                .select(NestEntity::getUuid)
//                .one();
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(missionQueueAddDTO.getNestId());
        if (Objects.isNull(nestUuid)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }

        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, nestUuid);
        AutoMissionQueueBody body = (AutoMissionQueueBody) redisService.get(redisKey);
        if (body == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_AUTOMATIC_TASK_QUEUE_WAS_NOT_QUERIED.getContent()));
        }

        if (missionQueueAddDTO.getMissionIdList().size() > body.obtainEnableMissionSize()) {
            int i = body.obtainEnableMissionSize();

            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_MAXIMUM_NUMBER_OF_AUTOMATIC_TASKS_IN_THE_QUEUE_IS_8_AND_THE_MAXIMUM_NUMBER_OF_TASKS_ALLOWED_TO_BE_ADDED.getContent())
                    .replace("SIZE", String.valueOf(body.obtainEnableMissionSize())));
        }

        List<MissionEntity> missionList = this.lambdaQuery()
                .in(MissionEntity::getId, missionQueueAddDTO.getMissionIdList())
                .eq(MissionEntity::getDeleted, false)
                .list();

        missionList = missionList.stream().sorted(Comparator.comparingInt(o -> missionQueueAddDTO.getMissionIdList().indexOf(o.getId()))).collect(Collectors.toList());

        if (CollectionUtil.isEmpty(missionList)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_SORTIE_LIST_CANNOT_BE_QUERIED.getContent()));
        }

        List<AutoMissionQueueBody.MissionBody> missionBodyList = missionList.stream().map(m ->
                new AutoMissionQueueBody
                        .MissionBody()
                        .setId(m.getId())
                        .setName(m.getName())
                        .setProgress(0.0)
                        .setState(AutoMissionQueueBody.MissionState.TODO.getValue())
                        .setAccountId(missionQueueAddDTO.getAccountId())
        ).collect(Collectors.toList());


        List<AutoMissionQueueBody.MissionBody> existMissionBodyList = body.addMissionBodyList(missionBodyList);
        redisService.set(redisKey, body, body.completeRedisExpire(), TimeUnit.MINUTES);
        if (CollectionUtil.isNotEmpty(existMissionBodyList)) {
            String existMissionName = existMissionBodyList.stream().map(AutoMissionQueueBody.MissionBody::getName).collect(Collectors.joining(","));
            if (existMissionBodyList.size() < missionBodyList.size()) {
                return RestRes.ok(existMissionName + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_SORTIES_ALREADY_EXIST_AND_SOME_OF_THEM_WERE_ADDED_SUCCESSFULLY.getContent()));
            }

            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_NEW_SIZE_SORTIES_ALREADY_EXIST_PLEASE_CHECK.getContent())
                    .replace("SIZE", String.valueOf(existMissionBodyList.size())));
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ALL_SORTIES_WERE_ADDED_SUCCESSFULLY.getContent()));
    }

    @Override
    public List<String> listUnitIdsByMissionId(Integer missionId) {
        if (missionId != null) {
            String s = baseMapper.selectUnitIdById(missionId);
            if (s != null) {
                String[] split = s.split(",");
                return Arrays.stream(split).filter(StringUtils::hasText).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public RestRes autoMissionQueueRcStart(String nestId) {
//        NestEntity nestEntity = nestService.lambdaQuery()
//                .eq(NestEntity::getId, nestId)
//                .select(NestEntity::getUuid, NestEntity::getType, NestEntity::getName)
//                .one();
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);

        if (Objects.isNull(nestUuid)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_QUERY_THE_BASE_STATION_TASK_RETRANSMISSION_FAILED.getContent()));
        }

        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, nestUuid);
        AutoMissionQueueBody body = (AutoMissionQueueBody) redisService.get(redisKey);
        if (body == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_TASK_RETRANSMISSION_FAILS_BECAUSE_THE_TASK_QUEUE_IS_NOT_QUERIED.getContent()));
        }

        Boolean multiTask = body.getExtra().getMultiTask();
        Boolean planTask = Objects.equals(AutoMissionQueueBody.DriveTypeEnum.PLAN_TASK.getType(), body.getExtra().getDriveType());
        if (multiTask || planTask) {
            //继续任务列
            if (AutoMissionQueueBody.QueueState.PAUSE.getValue() == body.getQueueState()) {
                redisService.set(redisKey, body, body.completeRedisExpire(), TimeUnit.MINUTES);
                return this.continueAutoMissionQueue(nestId);
            } else {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATCH_TASK_COLUMN_IS_NOT_SUSPENDED_STATE_CAN_NOT_CONTINUE_THE_TASK.getContent()));
            }
        } else {
            //单任务重新
            StartMissionParamDto startMissionParamDto = new StartMissionParamDto();
            startMissionParamDto.setMissionId(body.getMissionList().get(0).getId());
            startMissionParamDto.setMultiTask(false);
            startMissionParamDto.setGainVideo(body.getExtra().getGainVideo());
            startMissionParamDto.setGainDataMode(body.getExtra().getGainDataMode());
            startMissionParamDto.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
            return startMission2(startMissionParamDto);
        }
    }

    @Override
    public RestRes getMissionTypeDetails(Integer missionId) {
        Map<String, Object> map = baseMapper.selectMissionTypeDetails(missionId);
        return RestRes.ok(map);
    }

    @Override
    public RestRes countDownImmediatelyAutoUploadData(String nestId) {
//        String nestUuid = nestService.getUuidById(nestId);
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        if (Objects.isNull(nestUuid)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_BASE_STATION_INFORMATION_CANNOT_BE_QUERIED.getContent()));
        }
        boolean b = missionQueueListenerService.countDownImmediatelyAutoUploadData(nestUuid);
        if (!b) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXECUTE_BATCH_DATA_SYNCHRONIZATION_IMMEDIATELY_FAILS.getContent()));
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_EXECUTE_BATCH_DATA_SYNCHRONIZATION_SUCCESSFULLY.getContent()));
    }

    @Override
    public RestRes cancelAutoUploadData(String nestId) {
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        if (Objects.isNull(nestUuid)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_BASE_STATION_INFORMATION_CANNOT_BE_QUERIED.getContent()));
        }
        boolean b = missionQueueListenerService.cancelAutoUploadData(nestUuid);
        if (!b) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANCELLATION_OF_BATCH_DATA_SYNCHRONIZATION_FAILED.getContent()));
        }
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANCELLATION_OF_BATCH_DATA_SYNCHRONIZATION_SUCCEEDED.getContent()));
    }

    @Override
    public RestRes checkNestEnableStartMission(String nestId) {
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        if (Objects.isNull(nestUuid)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_BASE_STATION_INFORMATION_CANNOT_BE_QUERIED.getContent()));
        }
        StartMissionQueueNestInfoOutDTO nestInfo = baseNestService.getStartMissionQueueNestInfoByNestId(nestId);
        RestRes restRes;
        if (nestInfo != null && NestTypeEnum.DJI_DOCK.getValue() == nestInfo.getNestType()) {
            ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
            if (cm == null || !cm.getNestLinked()) {
                restRes = RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_STATUS_OF_THE_BASE_STATION_IS_OFFLINE_UNABLE_TO_SEND_TASKS.getContent()));
            } else {
                restRes = RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_BASE_STATION_IS_ALLOWED_TO_EXECUTE_TASKS.getContent()));
            }
        } else {
            restRes = checkNestEnableExecMission(nestUuid, false);
        }
        //如果检测到可以执行任务，清空redis，清空推送任务进度的经常
        if (restRes.isOk()) {
            //关闭推送进程
            wsTaskProgressService.stopPushAutoTaskQueueMsg(nestUuid, true);
            wsTaskProgressService.stopPushTaskProgressMsg(nestUuid);

            //清空redis
            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, nestUuid);
            redisService.del(redisKey);
            String redisKey1 = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.TASK_PROGRESS_DTO, nestUuid);
            redisService.del(redisKey1);
        }
        return restRes;
    }

    @Override
    public RestRes stopPushTaskProgressDto(Integer taskId) {
        TaskEntity taskEntity = taskService.lambdaQuery()
                .eq(TaskEntity::getId, taskId)
                .select(TaskEntity::getBaseNestId, TaskEntity::getType)
                .one();

        if (Objects.nonNull(taskEntity)) {
            NestTypeEnum nestTypeEnum = this.baseNestService.getNestTypeByNestIdCache(taskEntity.getBaseNestId());
            if (NestTypeEnum.DJI_DOCK.equals(nestTypeEnum)) {
                String baseNestId = taskEntity.getBaseNestId();
                String nestUuid = baseNestService.getNestUuidByNestIdInCache(baseNestId);
                wsDjiTaskProgressService.stopPushTaskProgressDto(nestUuid);
            } else {
                wsTaskProgressService.stopPushTaskProgressDto(taskId);
            }
            return RestRes.ok(new HashMap<>(2));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MISSION_NOT_QUERIED.getContent()));
    }

    @Override
    public MissionEntity queryMissionByMissionRecordsId(Integer missionRecordsId) {
        return baseMapper.queryMissionByMissionRecordsId(missionRecordsId);
    }


    @Override
    public List<MissionEntity> listAirLineIdAndTaskIdByTaskIdList(List<Integer> taskIdList) {
        if (!CollectionUtils.isEmpty(taskIdList)) {
            return baseMapper.batchSelectAirLineIdAndTaskIdByTaskIdList(taskIdList);
        }
        return Collections.emptyList();
    }

    @Override
    public int batchSoftDeleteByIds(List<Integer> idList) {
        if (!CollectionUtils.isEmpty(idList)) {
            return baseMapper.batchSoftDeleteByIds(idList);
        }
        return 0;
    }


    /**
     * 上传任务到机巢
     *
     * @param mission
     * @param uuid
     * @return
     */
    private RestRes uploadMissionToNest(Mission mission, String uuid) {

        ComponentManager cm = ComponentManagerFactory.getInstance(uuid);
        if (cm != null && cm.getNestLinked()) {
            MissionManager missionManager = cm.getMissionManager();
            if (missionManager != null) {
                CompletableFuture<String> future = new CompletableFuture<>();
                missionManager.uploadMission(mission, (result, isSuccess, errMsg) -> {
                    if (isSuccess) {
                        future.complete(result);
                    }
                });
                try {

                    String missionId;
                    if ((missionId = future.get(3, TimeUnit.SECONDS)) != null) {
                        Map<String, Object> map = new HashMap<>(2);
                        map.put("missionId", missionId);
                        return RestRes.ok(map);
                    }
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_UPLOAD_FAILED_MACHINE_NEST_IS_NOT_CONNECTED.getContent()));
    }

    private void sendMessageByWs(String uuid, String message) {
        ChannelService.sendMessageByType3Channel(uuid, message);
    }

    private void sendMessageBy10Ws(String uuid, String message) {
        ChannelService.sendMessageByType10Channel(uuid, message);
    }

    private List<Map<String, Object>> buildAirLineJson(String airLineStr, Integer type) {
        if (airLineStr != null) {
            JSONArray jsonArray = JSONArray.parseArray(airLineStr);
            List<Map<String, Object>> list = new ArrayList<>(jsonArray.size());
            if (type == 3) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Double wayPointLatitude = jsonObject.getDouble("wayPointLatitude");
                    Double wayPointLongitude = jsonObject.getDouble("wayPointLongitude");
                    Map<String, Object> airLineMap = new HashMap<>(2);
                    airLineMap.put("lat", wayPointLatitude);
                    airLineMap.put("lng", wayPointLongitude);
                    list.add(airLineMap);
                }
            } else if (type == 2) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Double wayPointLatitude = jsonObject.getDouble("aircraftLocationLatitude");
                    Double wayPointLongitude = jsonObject.getDouble("aircraftLocationLongitude");
                    Map<String, Object> airLineMap = new HashMap<>(2);
                    airLineMap.put("lat", wayPointLatitude);
                    airLineMap.put("lng", wayPointLongitude);
                    list.add(airLineMap);
                }
            }
            return list;
        }
        return Collections.emptyList();
    }

    private BuildMissionRes buildMission(MissionEntity missionEntity, StartMissionNestInfoOutDTO nestParam, Integer taskType, Integer mold, AirIndexEnum uavWhich) {
        BuildMissionRes buildMissionRes = new BuildMissionRes();
        Mission mission = new Mission();
        if (missionEntity == null || nestParam == null || taskType == null) {
            return buildMissionRes.setMission(null).setErrMsg(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TASK_ENTITY_BASE_STATION_ENTITY_AND_TASK_TYPE_CANNOT_BE_QUERIED.getContent()));
        }
        List<Waypoint> waypointList = null;
        AirLineEntity airLineEntity = airLineService.getById(missionEntity.getAirLineId());
        MissionParamEntity missionParamEntity = missionParamService.getById(missionEntity.getMissionParamId());
//        AircraftEntity aircraftEntity = aircraftService.getById(nestEntity.getAircraftId());
        String airType = baseUavService.getUavTypeByNestId(nestParam.getNestId(), uavWhich);
        if (Objects.isNull(airType)) {
            return buildMissionRes.setMission(null).setErrMsg(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_UAV_TYPE_CANNOT_BE_DETECTED_PLEASE_CHECK_WHETHER_THE_BASE_STATION_SETTINGS_ARE_FILLED_IN_COMPLETELY.getContent()));
        }
        //使用新的FinishAction模式，绕过大疆的自动返航,目前在测试阶段
        if (nestParam.getNestType() == NestTypeEnum.I_CREST2.getValue()) {
            mission.setGotoFirstWaypointMode(GoFirstWaypointModeEnum.SAFELY);
            mission.setFlightPathMode(FlightPathModeEnum.NORMAL);
            mission.setFinishAction(FinishActionEnum.GO_HOME);
            mission.setAutoFlightSpeed(9);
            mission.setUseHomeSeaLevelInRtkUnable(false);
        }

        if (nestParam.getNestType() == NestTypeEnum.G900.getValue() || nestParam.getNestType() == NestTypeEnum.T50.getValue()) {
            mission.setIntelligentMission(true);
        }

        if (missionParamEntity != null) {
            Integer headingMode = missionParamEntity.getHeadingMode();
            mission.setHeadingMode(HeadingModeEnum.getInstance(headingMode));
            mission.setAutoFlightSpeed(missionParamEntity.getAutoFlightSpeed());
            Integer flightPathMode = missionParamEntity.getFlightPathMode();
            mission.setFlightPathMode(FlightPathModeEnum.getInstance(flightPathMode));
        }


        if (airLineEntity != null) {
            if (airLineEntity.getType() == AirLineTypeEnum.UNIFY_AIR_LINE.getValue()) {
                waypointList = JSONArray.parseArray(airLineEntity.getOriginalWaypoints(), Waypoint.class);
            } else {
                waypointList = airLineService.parseAirLine(missionEntity.getId(), nestParam.getNestId(), mold);
                waypointList = WaypointUtil.transWaypointAction(waypointList);
            }

            if (TaskModeEnum.AROUND_FLY.getValue().equals(taskType)) {
                String waypoints = airLineEntity.getWaypoints();
                UnifyAirLineFormatDto unifyAirLineFormatDto = JSONObject.parseObject(waypoints, UnifyAirLineFormatDto.class);
                TaskModeEnum mode = unifyAirLineFormatDto.getMode();
                JSONObject aroundFlyParamJo = (JSONObject) unifyAirLineFormatDto.getLineConfigs().get(mode.name());
                AroundFlyParam aroundFlyParam = JSONObject.parseObject(aroundFlyParamJo.toJSONString(), AroundFlyParam.class);
                Map<String, Double> centerPoint = aroundFlyParam.getCenterPoint();
                mission.setPoiLongitude(centerPoint.get("lng"));
                mission.setPoiLatitude(centerPoint.get("lat"));
                mission.setFlightPathMode(FlightPathModeEnum.CURVED);
                mission.setIntelligentMission(false);
            }
        }
        mission.setMissionID(missionEntity.getUuid());
        mission.setName(missionEntity.getName());
        if (!checkWaypointList(waypointList)) {
            return buildMissionRes.setMission(null).setErrMsg(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ROUTE_DETECTION_DOES_NOT_PASS_THE_DISTANCE_BETWEEN_TAKEOFF_AND_LANDING_ALTITUDE_AND_THE_FIRST_POINT_MUST_BE_GREATER_THAN_05_METERS_PLEASE_CHECK_THE_ROUTE_IN_THE_ROUTE_PLANNING.getContent()));
        }
        mission.setMission(waypointList);

        if (nestParam.getNestType() == null || isRtkNest(nestParam.getNestType(), airType) == null) {
            return buildMissionRes.setMission(null).setErrMsg(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_BASE_STATION_TYPE_AND_UAV_TYPE_CANNOT_BE_DETECTED_PLEASE_CHECK_WHETHER_THE_BASE_STATION_SETTINGS_ARE_FILLED_IN_COMPLETELY.getContent()));
        }
        if (isRtkNest(nestParam.getNestType(), airType)) {
            //上传的航线飞绝对高度(以海平面0)，web页面上规划的飞相对高度（以飞机起飞点为0）
            if (isRelativeTask(taskType, airLineEntity.getAbsolute()) == null) {
                return buildMissionRes.setMission(null).setErrMsg(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CAN_NOT_DETERMINE_WHETHER_THE_ROUTE_IS_A_RELATIVE_ROUTE_OR_AN_ABSOLUTE_ROUTE.getContent()));
            }
            if (isRelativeTask(taskType, airLineEntity.getAbsolute())) {
                mission.setRelativeAltitude(true);
            } else {
                mission.setRelativeAltitude(false);
            }
        } else {
            /**
             *   非RTK的飞机，飞绝对海拔高度的时候，使用useHomeSeaLevelInRtkUnable这个字段配合基站海拔高度，用绝对的海拔高度减去
             *   绝对基站高度的高度，就可以得到相对行对基站的海拔高度航线
             */
            if (isRelativeTask(taskType, airLineEntity.getAbsolute()) == null) {
                return buildMissionRes.setMission(null).setErrMsg(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_UNABLE_TO_DETECT_WHETHER_THE_ROUTE_IS_AN_ABSOLUTE_ROUTE.getContent()));
            }
            if (isRelativeTask(taskType, airLineEntity.getAbsolute())) {
                mission.setRelativeAltitude(true);
                mission.setUseHomeSeaLevelInRtkUnable(false);
            } else {
                //绝对任务需要通过设置这个值来
                mission.setUseHomeSeaLevelInRtkUnable(true);
            }
        }


        //保存上传的的航线到air_line表
        boolean update = airLineService.lambdaUpdate()
                .set(AirLineEntity::getOriginalWaypoints, JacksonUtil.object2Json(mission.getMission()))
                .eq(AirLineEntity::getId, airLineEntity.getId()).update();

        log.info("准备上传的航线数据：{}", JacksonUtil.object2Json(mission));
        return buildMissionRes.setMission(mission).setErrMsg(null);
    }

    private Integer listenMissionRunning(ListenMissionRunDTO dto) {
        MissionManager missionManager = dto.getCm().getMissionManager();
        //创建任务记录
        missionManager.listenMissionRunning((payload) -> {
            BaseResult3 br3 = JSONObject.parseObject(payload, BaseResult3.class);
            MissionRunStateEnum state = MissionRunStateEnum.getInstance(br3.getpCode());
            boolean success = ResultCodeEnum.REQUEST_SUCCESS.equals(br3.getCode());
            String execId = success ? br3.getMsg() : "";
            String errMsg = !success ? br3.getMsg() : "success";
            log.info("missionId:{},state:{},success:{},execId:{}", dto.getMissionId(), state, success, execId);
            String param = br3.getParam();
            if (param != null) {
                JSONObject jsonObject = JSONObject.parseObject(param);
                String missionID = jsonObject.getString("missionID");
                String execMissionID = jsonObject.getString("execMissionID");
                if (!dto.getMissionUuid().equals(missionID)) {
                    log.info("监听到的任务UUID：{}，监听的任务UUID:{}", missionID, dto.getMissionUuid());
                    return;
                }
            }

            if (MISSION_START.equals(state)) {
                String msg;
                if (success) {
                    orderExecTaskOffRecordSwitchZoomCamera(dto);

                    //把根据execId记录保存到数据库
                    MissionRecordsEntity missionRecordsEntity = new MissionRecordsEntity();
                    missionRecordsEntity.setCreateTime(LocalDateTime.now());
                    missionRecordsEntity.setStartTime(LocalDateTime.now());
                    missionRecordsEntity.setMissionId(dto.getMissionId());
                    missionRecordsEntity.setGainDataMode(dto.getGainDataMode());
                    missionRecordsEntity.setGainVideo(dto.getGainVideo());
                    missionRecordsEntity.setStatus(MissionRecordsStatusEnum.EXECUTING.getValue());
                    missionRecordsEntity.setExecId(execId);
                    missionRecordsEntity.setFlyIndex(dto.getFlyTimes() + 1);
                    missionRecordsEntity.setUavWhich(dto.getUavWhich());
                    missionRecordsEntity.setCreateUserId(Long.parseLong(dto.getAccountId()));

                    boolean b = missionRecordsService.saveOrUpdate(missionRecordsEntity,
                            new UpdateWrapper<MissionRecordsEntity>().lambda().eq(MissionRecordsEntity::getExecId, execId));
                    String sysLogSaveKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.SYS_LOG_SAVE_KEY, dto.getNestUuid(), dto.getUavWhich());
                    redisService.set(sysLogSaveKey, missionRecordsEntity.getId());
                    String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, dto.getNestUuid());
                    AutoMissionQueueBody body = (AutoMissionQueueBody) redisService.get(redisKey);

                    // 网格化记录
                    if (TaskModeEnum.GRID.getValue().equals(dto.getTaskType())) {
                        log.info("#MissionServiceImpl.startMission2# missionId={}", dto.getMissionId());
                        log.info("#MissionServiceImpl.startMission2# missionName={}", dto.getMissionName());
                        log.info("#MissionServiceImpl.startMission2# nestUuid={}", dto.getNestUuid());
                        log.info("#MissionServiceImpl.startMission2# taskId={}", dto.getTaskId());
                        log.info("#MissionServiceImpl.startMission2# gridInspectId={}", dto.getGridInspectId());
                        log.info("#MissionServiceImpl.startMission2# missionRecordsId={}", missionRecordsEntity.getId());
                        GridMissionDTO gridMissionDTO = new GridMissionDTO();
                        gridMissionDTO.setMissionId(dto.getMissionId());
                        gridMissionDTO.setMissionName(dto.getMissionName());
                        gridMissionDTO.setNestUuid(dto.getNestUuid());
                        gridMissionDTO.setTaskId(dto.getTaskId());
                        gridMissionDTO.setMissionRecordsId(missionRecordsEntity.getId());
                        gridMissionDTO.setGridInspectId(dto.getGridInspectId());
                        boolean resSaveMissionToGrid = gridMissionService.saveMissionToGrid(gridMissionDTO);
                        boolean resSaveGridInspect = gridMissionService.saveGridInspect(gridMissionDTO);
                        log.info("#MissionServiceImpl.startMission2# saveMissionToGridRes={}", resSaveMissionToGrid);
                        log.info("#MissionServiceImpl.startMission2# resSaveGridInspect={}", resSaveGridInspect);
                    }

                    if (body != null) {
                        log.info("listenMissionRunningInsertMissionRecordIdMap{}:{}", dto.getMissionId(), missionRecordsEntity.getId());
                        body.getExtra().putMissionRecordId(dto.getMissionId(), missionRecordsEntity.getId());
                        if (AutoMissionQueueBody.DriveTypeEnum.PLAN_TASK.getType() == body.getExtra().getDriveType()) {
                            InspectionPlanRecordMissionEntity inspectionPlanRecordMissionEntity = new InspectionPlanRecordMissionEntity();
                            inspectionPlanRecordMissionEntity.setId(body.getExtra().obtainPlanRecordMissionId(body.obtainReadyMissionId()));
                            inspectionPlanRecordMissionEntity.setPlanRecordId(body.getExtra().getPlanRecordId());
                            inspectionPlanRecordMissionEntity.setMissionId(body.obtainReadyMissionId());
                            inspectionPlanRecordMissionEntity.setMissionRecordId(missionRecordsEntity.getId());
                            inspectionPlanRecordMissionEntity.setActualExecTime(LocalDateTime.now());
                            inspectionPlanRecordMissionEntity.setExecState(InspectionPlanMissionExecStateEnum.IN_EXECUTION.getState());
                            inspectionPlanRecordMissionEntity.setFlightDuration((int) body.obtainReadyMissionFlyTime());
                            inspectionPlanRecordMissionEntity.setCreatorId(body.getExtra().getUserId());
                            inspectionPlanRecordMissionService.updatePlanRecordMission(inspectionPlanRecordMissionEntity);
                        }
                        redisService.set(redisKey, body, body.completeRedisExpire(), TimeUnit.MINUTES);
                    }
                    msg = WebSocketRes.ok().msg("geoai_uos_sendMessageByTypeChannel_3").topic(WebSocketTopicEnum.TASK_START_PROGRESS_RESULT).uuid(dto.getNestUuid()).toJSONString();
                    redisService.set(RedisKeyConstantList.MISSION_START_RESULT, true);
                    // 设置本次飞行的架次id
                } else {
                    msg = WebSocketRes.err().msg(errMsg).topic(WebSocketTopicEnum.TASK_START_PROGRESS_RESULT).uuid(dto.getNestUuid()).toJSONString();
                    String msg2 = WebSocketRes.err().msg(errMsg).topic(WebSocketTopicEnum.BATCH_TASK_ERROR).uuid(dto.getNestUuid()).toJSONString();
                    sendMessageByWs(dto.getNestUuid(), msg2);
                    redisService.set(RedisKeyConstantList.MISSION_START_RESULT, false);
                }
                sendMessageByWs(dto.getNestUuid(), msg);
            }
            if (MISSION_UPLOAD.equals(state)) {
                String msg;
                if (success) {
                    msg = WebSocketRes.ok().msg(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_TASK_UPLOAD.getContent())).topic(WebSocketTopicEnum.TASK_START_PROGRESS_RESULT).uuid(dto.getNestUuid()).toJSONString();
                    redisService.set(RedisKeyConstantList.MISSION_START_RESULT, true);
                } else {
                    msg = WebSocketRes.err().msg(errMsg).topic(WebSocketTopicEnum.TASK_START_PROGRESS_RESULT).uuid(dto.getNestUuid()).toJSONString();
                    String msg2 = WebSocketRes.err().msg(errMsg).topic(WebSocketTopicEnum.BATCH_TASK_ERROR).uuid(dto.getNestUuid()).toJSONString();
                    sendMessageByWs(dto.getNestUuid(), msg2);
                    redisService.set(RedisKeyConstantList.MISSION_START_RESULT, false);
                }
                sendMessageByWs(dto.getNestUuid(), msg);
            }
            if (MISSION_FINISH.equals(state)) {
                missionManager.removeListenMissionRunning(AirIndexEnum.getInstance(dto.getUavWhich()));
                String redisKey1 = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.NEST_TAKE_OFF_RECORDS, dto.getNestUuid(), dto.getMissionId());
                String redisKey2 = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.SWITCH_ZOOM_CAMERA, dto.getNestUuid());
                redisService.del(redisKey1);
                redisService.del(redisKey2);
            }
        }, AirIndexEnum.getInstance(dto.getUavWhich()));
        return -1;
    }


    /**
     * 运行批量任务
     *
     * @param nestUuid
     */
    @Deprecated
    private void batchTaskRunnable(String nestUuid) {

        //首个架次不用推送飞行前检查
        boolean batchTaskOfFirst = true;

        String batchTaskRunningKey = RedisKeyConstantList.BATCH_TASK_RUNNINGKEY_KEY;
        String batchTaskBodyKey = String.format(RedisKeyConstantList.BATCH_TASK_BODY_KEY, nestUuid);
        String pauseStateRedisKey = String.format(RedisKeyConstantList.BATCH_TASK_PAUSE_STATE_KEY, nestUuid);
        String stopStateRedisKey = String.format(RedisKeyConstantList.BATCH_TASK_STOP_STATE_KEY, nestUuid);
        String cancelBatchTaskOfOneKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.CANCEL_BATCH_TASK_OF_ONE, nestUuid);
        //开始的时候删除上一次改机巢的单个任务暂停缓存
        redisService.del(cancelBatchTaskOfOneKey);
        //通过websocket开始推送批量任务列表消息
        wsTaskProgressService.startPushBatchTaskListMsg(nestUuid);

        String batchTaskBeforeCheckContinueExecKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BATCH_TASK_BEFORE_CHECK_CONTINUE_EXEC, nestUuid);
        redisService.set(batchTaskBeforeCheckContinueExecKey, -1);

        BatchTaskBody batchTaskBody = (BatchTaskBody) redisService.get(batchTaskBodyKey);
        if (batchTaskBody == null) {
            String msg = WebSocketRes.err()
                    .topic(WebSocketTopicEnum.BATCH_TASK_ERROR)
                    .uuid(nestUuid)
                    .msg(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATCH_TASK_DATA_EXCEPTION.getContent()))
                    .toJSONString();
            sendMessageByWs(nestUuid, msg);
            return;
        }

        Integer gainDataMode = batchTaskBody.getGainDataMode();
        Integer gainVideo = batchTaskBody.getGainVideo();
        Integer flightStrategy = batchTaskBody.getFlightStrategy();
        Integer taskCurrentIndex = batchTaskBody.getTaskCurrentIndex();
        List<TaskBody> taskBodyList = batchTaskBody.getTaskBodyList();

        boolean stopBatchTask = false;
        taskLoop:
        for (int i = (taskCurrentIndex + 1); i < taskBodyList.size(); i++) {
            TaskBody taskBody = taskBodyList.get(i);
            Integer pauseState = (Integer) redisService.get(pauseStateRedisKey);
            Integer stopState = (Integer) redisService.get(stopStateRedisKey);
            Boolean cancelBatchTaskOfOne = (Boolean) redisService.hGet(cancelBatchTaskOfOneKey, taskBody.getTaskId().toString());
            //如果该任务暂停了，就执行下个任务
            if (cancelBatchTaskOfOne != null && cancelBatchTaskOfOne) {
                taskBody.setTaskState(TaskBody.TaskStateEnum.EXECUTION_CANCEL.getValue());
                continue;
            }

            //如果批量任务暂停了，就停止任务循环
            if (pauseState == 1) {
                batchTaskBody.setPauseOrContinue(BatchTaskBody.MiddleStateEnum.PAUSE.getValue());
                break;
            } else {
                batchTaskBody.setPauseOrContinue(BatchTaskBody.MiddleStateEnum.CONTINUE.getValue());
            }

            //如果批量任务终止了，就停止任务循环
            if (stopState == 1) {
                batchTaskBody.setStartOrStop(BatchTaskBody.RunStateEnum.STOP.getValue());
                redisService.hSet(batchTaskRunningKey, nestUuid, 0);
                stopBatchTask = true;
                break;
            } else {
                batchTaskBody.setStartOrStop(BatchTaskBody.RunStateEnum.START.getValue());
            }

            taskBody.setTaskState(TaskBody.TaskStateEnum.EXECUTING.getValue());
            batchTaskBody.setTaskCurrentIndex(i);
            redisService.set(batchTaskBodyKey, batchTaskBody);

            List<MissionBody> missionBodyList = taskBody.getMissionBodyList();
            Integer missionCurrentIndex = taskBody.getMissionCurrentIndex();
            for (int j = (missionCurrentIndex + 1); j < missionBodyList.size(); j++) {

                MissionBody missionBody = missionBodyList.get(j);
                Integer missionId = missionBody.getMissionId();

                //飞行检查
                if (!batchTaskOfFirst) {
                    Boolean beforeCheckRes = false;
                    if (!beforeCheckRes) {
                        batchTaskBody.setStartOrStop(BatchTaskBody.RunStateEnum.STOP.getValue());
                        break taskLoop;
                    }
                }
                batchTaskOfFirst = false;
                StartMissionParamDto startMissionParamDto = new StartMissionParamDto();
                startMissionParamDto.setMissionId(missionId);
                startMissionParamDto.setGainDataMode(gainDataMode);
                startMissionParamDto.setGainVideo(gainVideo);
                startMissionParamDto.setMultiTask(true);
                //开始任务
                startMission2(startMissionParamDto);
                redisService.hSet(batchTaskRunningKey, nestUuid, 1);

                taskBody.setMissionCurrentIndex(j);
                missionBody.setMissionState(MissionBody.MissionStateEnum.EXECUTION_COMPLETE.getValue());
                redisService.set(batchTaskBodyKey, batchTaskBody);

                //监听任务结束
                Boolean taskFinishState = listenTaskFinish(missionId, nestUuid, redisService);
                if (taskFinishState) {
                    Long taskFlyTime = (Long) redisService.hGet(RedisKeyConstantList.TASK_FLY_TIME_KEY, taskBody.getTaskId().toString());
                    if (taskFlyTime != null && taskFlyTime != 0L) {
                        taskBody.setFlyTime(taskFlyTime);
                    }
                    missionBody.setMissionState(MissionBody.MissionStateEnum.EXECUTION_ERROR.getValue());
                } else {
                    missionBody.setMissionState(MissionBody.MissionStateEnum.PAUSE.getValue());
                    //通知前端异常暂停了批量任务
                    String msg = WebSocketRes.err().topic(WebSocketTopicEnum.BATCH_TASK_END).uuid(nestUuid).msg(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATCH_MISSION_EXCEPTION_END.getContent())).toJSONString();
                    sendMessageByWs(nestUuid, msg);
                    break taskLoop;
                }
                redisService.set(batchTaskBodyKey, batchTaskBody);
            }

            //一个任务结束，记录任务的状态
            taskBody.setTaskState(TaskBody.TaskStateEnum.EXECUTION_COMPLETE.getValue());
            redisService.set(batchTaskBodyKey, batchTaskBody);
            //关闭结束任务推送信息的线程
            wsTaskProgressService.stopPushTaskProgressDto(taskBody.getTaskId());
        }


        redisService.set(stopStateRedisKey, 0);
        if ((batchTaskBody.getTaskCurrentIndex() == batchTaskBody.getTotalTaskCount() - 1) || stopBatchTask) {
            redisService.hSet(batchTaskRunningKey, nestUuid, 0);
            batchTaskBody.setStartOrStop(BatchTaskBody.RunStateEnum.COMPLETE.getValue());
        }
        redisService.set(batchTaskBodyKey, batchTaskBody);
    }

    /**
     * 批量任务飞行前检查
     *
     * @param nestUuid
     * @return
     */
    private Boolean batchTaskBeforeCheck(String nestUuid) {
        Integer nestType = -1;
        BeforeStartCallable<Object> beforeStartCallable = new BeforeStartCallable<>(nestUuid, nestType, redisService);
        Future<Boolean> beforeStartCallableFuture = executorService.submit(beforeStartCallable);
        try {
            Boolean aBoolean = beforeStartCallableFuture.get(1, TimeUnit.MINUTES);
            if (aBoolean) {
                //检测通过倒计时10s通知（websocket）
                Map<String, Object> data = new HashMap<>(2);
                data.put("countDownTimeInSecond", 10);
                String msg = WebSocketRes.ok()
                        .topic(WebSocketTopicEnum.BATCH_TASK_COUNT_DOWN)
                        .uuid(nestUuid)
                        .data(data)
                        .msg(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FLIGHT_DETECTION_PASSED.getContent()))
                        .toJSONString();
                sendMessageByWs(nestUuid, msg);

                //暂停10秒钟，循环15次，每次1秒,前后端差距10秒
                for (int x = 0; x < 20; x++) {
                    Thread.sleep(1000);
                    //从redis中获取一个值，如果为是继续执行为true的话,就终止这个小循环
                    String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BATCH_TASK_BEFORE_CHECK_CONTINUE_EXEC, nestUuid);
                    Integer taskContinueExec = (Integer) redisService.get(redisKey);
                    if (taskContinueExec != null && taskContinueExec == 1) {
                        break;
                    } else if (taskContinueExec != null && taskContinueExec == 0) {
                        return false;
                    }
                }

            } else {
                //检测有警告倒计时20s通知（websocket）
                Map<String, Object> data = new HashMap<>(2);
                data.put("countDownTimeInSecond", 20);
                String msg = WebSocketRes.ok()
                        .topic(WebSocketTopicEnum.BATCH_TASK_COUNT_DOWN)
                        .uuid(nestUuid)
                        .data(data)
                        .msg(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FLIGHT_DETECTION_WARNING.getContent()))
                        .toJSONString();
                sendMessageByWs(nestUuid, msg);

                //暂停20秒，循环30次，每次1秒,前后端差距10秒
                for (int x = 0; x < 30; x++) {
                    Thread.sleep(1000);
                    //从redis中获取一个值，如果为是继续执行为true的话,就终止这个小循环
                    String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BATCH_TASK_BEFORE_CHECK_CONTINUE_EXEC, nestUuid);
                    Integer taskContinueExec = (Integer) redisService.get(redisKey);
                    if (taskContinueExec != null && taskContinueExec == 1) {
                        break;
                    } else if (taskContinueExec != null && taskContinueExec == 0) {
                        return false;
                    }
                }

            }
            String batchTaskBeforeCheckContinueExecKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.BATCH_TASK_BEFORE_CHECK_CONTINUE_EXEC, nestUuid);
            redisService.set(batchTaskBeforeCheckContinueExecKey, -1);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            String msg = WebSocketRes.err()
                    .topic(WebSocketTopicEnum.BATCH_TASK_ERROR)
                    .uuid(nestUuid)
                    .msg(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FLIGHT_DETECTION_TIMEOUT.getContent()))
                    .toJSONString();

            sendMessageByWs(nestUuid, msg);
            //检测异常通知前端(websocket)
            return false;
        }
        return true;
    }


    private Boolean listenTaskFinish(Integer missionId, String nestUuid, RedisService redisService) {
        MissionCallable<Integer> missionCallable = new MissionCallable<>(missionId, nestUuid, redisService);
        Future<Integer> missionCallablefuture = executorService.submit(missionCallable);

        try {
            //res:1->正常结束，0->执行异常,最长阻塞30分钟
            Integer res = missionCallablefuture.get(30, TimeUnit.MINUTES);
            if (res == 0) {
                return false;
            } else if (res == 1) {
                //获取任务的飞行时间
                return true;
            }
            return false;

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            //超时没有返回监听结果，停止任务循环和架次循环,记录当前的运行的值

            return false;
        }
    }

    private Boolean isRelativeTask(int taskType, Boolean absolute) {
        if (TaskModeEnum.LOCAL.getValue() == taskType || TaskModeEnum.DJI_KML.getValue() == taskType) {
            if (absolute == null || absolute) {
                return false;
            }
            return true;
        }


        if (TaskModeEnum.DYNAMIC.getValue() == taskType ||
                TaskModeEnum.SUBSSTATION_PLANING.getValue() == taskType ||
                TaskModeEnum.DELICACY.getValue() == taskType
        ) {
            return false;
        }

        if (TaskModeEnum.PANORAMA.getValue() == taskType ||
                TaskModeEnum.LINEAR.getValue() == taskType ||
                TaskModeEnum.CUSTOM.getValue() == taskType ||
                TaskModeEnum.ORTHOPHOTO.getValue() == taskType ||
                TaskModeEnum.GRID.getValue() == taskType ||
                TaskModeEnum.SLOPEPHOTO.getValue() == taskType ||
                TaskModeEnum.AROUND_FLY.getValue() == taskType
        ) {
            return true;
        }
        return null;
    }

    private Boolean isRtkNest(int nestType, String airType) {
        if (NestTypeEnum.G600.getValue() == nestType ||
                NestTypeEnum.T50.getValue() == nestType ||
                NestTypeEnum.G900.getValue() == nestType ||
                NestTypeEnum.G900_CHARGE.getValue() == nestType ||
                NestTypeEnum.I_CREST2.getValue() == nestType) {
            return true;
        }
        if (NestTypeEnum.S100_V1.getValue() == nestType) {
            return false;
        }
        if (NestTypeEnum.S100_V2.getValue() == nestType || NestTypeEnum.G503.getValue() == nestType) {
            if (Objects.equals(String.valueOf(AircraftCodeEnum.MAVIC2_PRO.getValue()), airType)) {
                return true;
            }
            return false;
        }
        if (NestTypeEnum.S110_AUTEL.getValue() == nestType) {
            return true;
        }

        if (NestTypeEnum.S110_MAVIC3.getValue() == nestType) {
            return true;
        }

        return null;
    }

    private boolean checkWaypointList(List<Waypoint> waypointList) {
        log.info("全景 waypointList {}", waypointList);
        if (CollectionUtil.isNotEmpty(waypointList)) {
            double distance = waypointList.get(0).getWayPointAltitude() - waypointList.get(1).getWayPointAltitude();
            return Math.abs(distance) > 0.5;
        }
        return false;
    }

    /**
     * 开启云冠任务，由于云冠目前很多指令都不完善，
     * 所以只能执行上传任务指令和开启任务指令
     *
     * @param missionEntity
     * @param nestPram
     * @param taskType
     * @param flightStrategy
     * @return
     */
    private RestRes iCrestStartMission(Integer flyType, MissionEntity missionEntity, StartMissionNestInfoOutDTO nestPram, Integer taskType, Integer flightStrategy, Integer mold) {
        BuildMissionRes bmr = buildMission(missionEntity, nestPram, taskType, mold, AirIndexEnum.DEFAULT);
        Mission mission = bmr.getMission();
        if (mission == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NEST_MISSION_CONSTRUCTION_FAILED.getContent()) + bmr.getErrMsg());
        }
        ComponentManager cm = ComponentManagerFactory.getInstance(nestPram.getNestUuid());
        if (cm != null) {
            MissionManager missionManager = cm.getMissionManager();

            CompletableFuture<CfRes> uploadFuture = new CompletableFuture<>();
            missionManager.uploadMission(mission, (resMissionId, isSuccess, errMsg) -> {
                CfRes cfRes = new CfRes();
                if (isSuccess) {
                    if (resMissionId != null) {
                        cfRes.setSuccess(true);
                    } else {
                        cfRes.setSuccess(false);
                    }
                } else {
                    cfRes.setSuccess(false);
                }
                cfRes.setMsg(errMsg);
                uploadFuture.complete(cfRes);
            });

            try {
                CfRes cfRes = uploadFuture.get(10, TimeUnit.SECONDS);
                if (cfRes.isSuccess()) {
                    CompletableFuture<CfRes> startFuture = new CompletableFuture<>();
                    missionManager.startMission(flyType, missionEntity.getUuid(), 0, geoaiUosProperties.getUpload().getUrl(), (result, isSuccess, errMsg) -> {
                        CfRes cfRes1 = new CfRes();
                        if (isSuccess) {
                            cfRes1.setSuccess(result);
                        } else {
                            cfRes1.setSuccess(false);
                        }
                        cfRes1.setMsg(errMsg);
                        startFuture.complete(cfRes1);
                    });

                    CfRes cfRes1 = startFuture.get(10, TimeUnit.SECONDS);
                    if (cfRes1.isSuccess()) {
                        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_MISSION_OPENED.getContent()));
                    }
                    return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MISSION_OPENING_FAILED.getContent()));
                }
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MISSION_UPLOAD_FAILED.getContent()));
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MISSION_START_TIMEOUT.getContent()));
            }

        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    private MissionRecordsEntity missionRecordsGetRedisByMissionRecordsId(Integer missionRecordsId) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.MISSION_RECORDS_TEMP_UPDATE, missionRecordsId);
        MissionRecordsEntity mre = (MissionRecordsEntity) redisService.get(redisKey);
        return mre;
    }

    private boolean saveMissionRecords(MissionRecordsEntity missionRecordsEntity) {
        return missionRecordsService.saveOrUpdate(missionRecordsEntity);
    }

    private boolean checkNestAllowSendTask(NestStateEnum nestStateEnum) {
        if (NestStateEnum.STANDBY.equals(nestStateEnum)
                || NestStateEnum.BATTERY_LOADED.equals(nestStateEnum)
                || NestStateEnum.READY_TO_GO.equals(nestStateEnum)
                || NestStateEnum.DRONE_STARTED.equals(nestStateEnum)) {
            return true;
        }
        return false;
    }

    private boolean g503CheckNestAllowSendTask(String nestUuid, AirIndexEnum airIndex) {
        NestState nestState = commonNestStateService.getNestState(nestUuid, airIndex);
        if (Objects.nonNull(nestState)) {
            Integer mpsDispatchState = nestState.getMpsDispatchState();
            NestStateEnum nestStateEnum = nestState.getNestStateConstant();
            if ((NestStateEnum.STANDBY.equals(nestStateEnum)
                    || NestStateEnum.BATTERY_LOADED.equals(nestStateEnum)
                    || NestStateEnum.READY_TO_GO.equals(nestStateEnum))
                    && NestState.MpsDispatchStateEnum.IDLE.getValue() == mpsDispatchState) {
                return true;
            }
        }
        return false;
    }

    /**
     * 如果是多任务，只能是G900、G600才能复合
     *
     * @param missionIdList
     * @param nestType
     * @return
     */
    private boolean checkBatchTaskNestType(List<Integer> missionIdList, Integer nestType) {
        if (missionIdList.size() > 1) {
            return NestTypeEnum.G900.getValue() != nestType || NestTypeEnum.G600.getValue() != nestType;
        }
        return true;
    }

    private RestRes startSingleMission(AutoMissionQueueDTO param) {
        //如果是计划任务需要进行飞行前检测
        StartMissionParamDto startMissionParamDto = new StartMissionParamDto();
        startMissionParamDto.setMissionId(param.getMissionIdList().get(0));
        startMissionParamDto.setMultiTask(false);
        startMissionParamDto.setGainVideo(param.getGainVideo());
        startMissionParamDto.setGainDataMode(param.getGainDataMode());
        startMissionParamDto.setPositionStrategy(param.getPositionStrategy());
        startMissionParamDto.setGridInspectId(param.getGridInspectId());
        startMissionParamDto.setAccountId(param.getUserId().toString());
        startMissionParamDto.setFlyType(param.getFlyType());
        return startMission2(startMissionParamDto);
    }

    private RestRes startMultiMission(AutoMissionQueueDTO param, StartMissionQueueNestInfoOutDTO nestInfo) {

        AutoMissionQueueBody.DriveTypeEnum driveType = (param.getPlanRecordId() != null) ? AutoMissionQueueBody.DriveTypeEnum.PLAN_TASK : AutoMissionQueueBody.DriveTypeEnum.BATCH_TASK;
        setParamOfAccountAndUserId(driveType, param);

        //1、初始化任务队列
        AutoMissionQueueBody autoMissionQueueBody = buildAutoMissionQueueBody(param, nestInfo, driveType);
        log.info("#MissionServiceImpl.startMultiMission# autoMissionQueueBody={}", JSONUtil.toJsonStr(autoMissionQueueBody));
        if (autoMissionQueueBody == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_SORTIE_QUERY.getContent()));
        }

        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, nestInfo.getNestUuid());
        //清空以前的redis
        redisService.del(redisKey);
        String redisKey1 = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.TASK_PROGRESS_DTO, nestInfo.getNestUuid());
        redisService.del(redisKey1);

        if (autoMissionQueueBody.getExtra().getMultiTask()) {
            // 开启一个线程推送批量进度状态、再第一个任务执行成功之后，关闭掉该线程，使用单任务的进程推送
            wsTaskProgressService.stopPushTaskProgressMsg(autoMissionQueueBody.getNestUuid());
            wsTaskProgressService.startPushAutoTaskQueueMsg(autoMissionQueueBody.getNestUuid());
        }

        //存入新的redis
        boolean set = redisService.set(redisKey, autoMissionQueueBody, autoMissionQueueBody.completeRedisExpire(), TimeUnit.MINUTES);
        if (!set) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_INITIALIZE_TASK_QUEUE.getContent()));
        }

        //2、注册监听器
        if (autoMissionQueueBody.getExtra().getMultiTask() || AutoMissionQueueBody.DriveTypeEnum.PLAN_TASK.getType() == autoMissionQueueBody.getExtra().getDriveType()) {
            boolean register = MissionQueueListenerFactory.register(nestInfo.getNestUuid());
            if (!register) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_REGISTER_A_LISTENER.getContent()));
            }
        }

        //单个任务，并且不是计划任务
        if (!autoMissionQueueBody.getExtra().getMultiTask() && AutoMissionQueueBody.DriveTypeEnum.BATCH_TASK.getType() == autoMissionQueueBody.getExtra().getDriveType()) {
            return startSingleMission(param);
        }


        if (AutoMissionQueueBody.DriveTypeEnum.PLAN_TASK.getType() == autoMissionQueueBody.getExtra().getDriveType()) {
            Map<Integer, Integer> idMap = updateInspectionPlanRecord(param, null, InspectionPlanMissionExecStateEnum.TO_BE_EXECUTED);
            AutoMissionQueueBody body = (AutoMissionQueueBody) redisService.get(redisKey);
            body.getExtra().putPlanRecordMissionId(idMap);
            redisService.set(redisKey, body);
        }

        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AUTOMATIC_TASK_QUEUE_STARTED_EXECUTION.getContent()));
    }

    private RestRes checkNestEnableExecMission(String nestUuid, boolean continueTask) {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm == null || !cm.getNestLinked()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_STATUS_OF_THE_BASE_STATION_IS_OFFLINE_UNABLE_TO_SEND_TASKS.getContent()));
        }

        MediaStateV2 mediaStateV2 = commonNestStateService.getMediaStateV2(nestUuid);
        String currentState = mediaStateV2.getCurrentState();
        if (!MediaStatusV2Enum.IDLE.getValue().equals(currentState)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_BASE_STATION_DATA_MANAGER_STATUS.getContent())
                    + "【" + currentState + "】," + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_ALLOWED_TO_SEND_TASKS.getContent()));
        }

        //1、检测基站状态是否是可执行任务的
        NestStateEnum nestStateEnum = commonNestStateService.getNestStateEnum(nestUuid);
        if (!checkNestAllowSendTask(nestStateEnum)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_CURRENT_BASE_STATION_STATUS_IS.getContent())
                    + "：【" + nestStateEnum.getChinese() + "】," + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SENDING_TASKS_ARE_NOT_ALLOWED.getContent()));
        }
        MissionState missionState = commonNestStateService.getMissionState(nestUuid);
        if (!MissionCommonStateEnum.IDLE.equals(missionState.getCurrentState()) && !missionState.getAborted() && !missionState.getErrorEncountered()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_BASE_STATION_IS_CURRENTLY_EXECUTING_TASKS_AND_IS_NOT_ALLOWED_TO_SEND_TASKS.getContent()));
        }

        //2、检测是否有批量自动任务正在执行
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_MISSION_QUEUE, nestUuid);
        AutoMissionQueueBody body = (AutoMissionQueueBody) redisService.get(redisKey);
        if (body != null) {
            if (AutoMissionQueueBody.QueueState.RUNNING.getValue() == body.getQueueState()) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_BASE_STATION_HAS_A_BATCH_OF_AUTOMATIC_TASKS_RUNNING.getContent()));
            }
            if (AutoMissionQueueBody.QueueState.PAUSE.getValue() == body.getQueueState() && !continueTask) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_BASE_STATION_HAS_A_BATCH_OF_AUTOMATIC_TASKS_ON_HOLD.getContent()));
            }
        }

        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_BASE_STATION_IS_ALLOWED_TO_EXECUTE_TASKS.getContent()));
    }


    private RestRes checkG503NestEnableExecMission(String nestUuid, AirIndexEnum airIndex) {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (Objects.isNull(cm) || !cm.getNestLinked()) {
            return RestRes.err("基站状态离线,无法下发任务");
        }
        CommonNestState cns = CommonNestStateFactory.getInstance(nestUuid);
        if (Objects.isNull(cns)) {
            return RestRes.err("获取不到基站状态");
        }
        //1、检测基站状态
        NestState nestState = cns.getNestState();
        if (!g503CheckNestAllowSendTask(nestUuid, airIndex)) {
            return RestRes.err("基站状态:【" + nestState.getNestStateConstant().getChinese() + "】，" +
                    "MPS调度状态:【" + NestState.MpsDispatchStateEnum.getExpressByValue(nestState.getMpsDispatchState()) + "】，不允许发送任务");
        }
        //2、检测基站总体任务状态
        MissionState missionState = commonNestStateService.getMissionState(nestUuid, airIndex);
        if (!MissionCommonStateEnum.IDLE.equals(missionState.getCurrentState()) && !missionState.getAborted() && !missionState.getErrorEncountered()) {
            return RestRes.err("当前基站正在执行任务，不允许发送任务");
        }
        //3、检测当前基站的机位是否还有未执行的任务


        //TODO g503 检测逻辑之后再写
        return RestRes.ok("基站允许执行任务");
    }

    private Map<Integer, Integer> updateInspectionPlanRecord(AutoMissionQueueDTO param, String errMsg, InspectionPlanMissionExecStateEnum execState) {
        List<Integer> missionIdList = param.getMissionIdList();
        InspectionPlanRecordEntity inspectionPlanRecordEntity = inspectionPlanRecordService.lambdaQuery()
                .eq(InspectionPlanRecordEntity::getId, param.getPlanRecordId())
                .select(InspectionPlanRecordEntity::getScheduleExecTime)
                .one();

        //如果是失败的，将首个架次改成【执行失败】，将其余架次改成【已取消】
        List<InspectionPlanRecordMissionEntity> planRecordMissionList = new ArrayList<>(missionIdList.size());
        for (Integer missionId : missionIdList) {
            InspectionPlanRecordMissionEntity inspectionPlanRecordMissionEntity = new InspectionPlanRecordMissionEntity();
            inspectionPlanRecordMissionEntity.setPlanRecordId(param.getPlanRecordId());
            inspectionPlanRecordMissionEntity.setPlanId(param.getPlanId());
            inspectionPlanRecordMissionEntity.setMissionId(missionId);
            inspectionPlanRecordMissionEntity.setMissionRecordId(null);
            inspectionPlanRecordMissionEntity.setScheduleExecTime(inspectionPlanRecordEntity.getScheduleExecTime());
            if (InspectionPlanMissionExecStateEnum.EXECUTION_FAILED.equals(execState)) {
                inspectionPlanRecordMissionEntity.setExecState(InspectionPlanMissionExecStateEnum.CANCELLED.getState());
            } else {
                inspectionPlanRecordMissionEntity.setExecState(execState.getState());
            }
            inspectionPlanRecordMissionEntity.setFlightDuration(0);
            if (errMsg != null) {
                inspectionPlanRecordMissionEntity.setFailureCause(errMsg);
            }
            inspectionPlanRecordMissionEntity.setCreatorId(param.getUserId());
            planRecordMissionList.add(inspectionPlanRecordMissionEntity);
        }
        if (InspectionPlanMissionExecStateEnum.EXECUTION_FAILED.equals(execState)) {
            planRecordMissionList.get(0).setExecState(InspectionPlanMissionExecStateEnum.EXECUTION_FAILED.getState());
        }

        InspectionPlanExecStateEnum planExecState = InspectionPlanMissionExecStateEnum.EXECUTION_FAILED.equals(execState) ?
                InspectionPlanExecStateEnum.EXECUTION_FAILED : InspectionPlanExecStateEnum.IN_EXECUTION;

        inspectionPlanRecordMissionService.saveBatch(planRecordMissionList);

        boolean update = inspectionPlanRecordService.lambdaUpdate()
                .set(InspectionPlanRecordEntity::getActualExecTime, LocalDateTime.now())
                .set(InspectionPlanRecordEntity::getExecState, planExecState.getState())
                .eq(InspectionPlanRecordEntity::getId, param.getPlanRecordId())
                .update();

        return planRecordMissionList.stream().collect(Collectors.toMap(InspectionPlanRecordMissionEntity::getMissionId, InspectionPlanRecordMissionEntity::getId));
    }

    private void setParamOfAccountAndUserId(AutoMissionQueueBody.DriveTypeEnum driveType, AutoMissionQueueDTO param) {
        if (AutoMissionQueueBody.DriveTypeEnum.PLAN_TASK.equals(driveType)) {
            // 查询用户信息
            AccountInfoOutDTO accountInfoOutDTO = uosAccountService.accountInfo(String.valueOf(param.getUserId()));
            param.setAccount(accountInfoOutDTO.getAccount());
        } else {

            param.setAccount(TrustedAccessTracerHolder.get().getUsername());
            param.setUserId(Long.parseLong(TrustedAccessTracerHolder.get().getAccountId()));
        }
    }

    private AutoMissionQueueBody buildAutoMissionQueueBody(AutoMissionQueueDTO param, StartMissionQueueNestInfoOutDTO nestInfo, AutoMissionQueueBody.DriveTypeEnum driveType) {
        List<MissionEntity> missionList = this.lambdaQuery().eq(MissionEntity::getDeleted, false)
                .in(MissionEntity::getId, param.getMissionIdList())
                .eq(MissionEntity::getDeleted, false)
                .list();

        missionList = missionList.stream().sorted(Comparator.comparingInt(o -> param.getMissionIdList().indexOf(o.getId()))).collect(Collectors.toList());

        if (CollectionUtil.isEmpty(missionList)) {
            return null;
        }
        AutoMissionQueueBody autoMissionQueueBody = new AutoMissionQueueBody()
                .setTotalMissions(missionList.size())
                .setCompleteMissions(0)
                .setTotalTime(0L)
                .setQueueState(AutoMissionQueueBody.QueueState.TODO.getValue())
                .setNestId(param.getNestId())
                .setNestUuid(nestInfo.getNestUuid())
                .setLastMissionIndex(-1)
                .setExtra(new AutoMissionQueueBody
                        .Extra()
                        //批量任务，只能结束了才能批量同步数据
                        .setGainDataMode(param.getGainDataMode())
                        .setGainVideo(param.getGainVideo())
                        .setAccount(param.getAccount())
                        .setDriveType(driveType.getType())
                        .setNestName(nestInfo.getNestName())
                        .setMultiTask(param.getMissionIdList().size() > 1)
                        .setPlanName(param.getPlanName())
                        .setUserId(param.getUserId())
                        .setPlanId(param.getPlanId())
                        .setPlanAuto(param.getPlanAuto() == null || param.getPlanAuto() == 1)
                        .setPlanRecordId(param.getPlanRecordId())
                        .setPositionStrategy(param.getPositionStrategy())

                ).setMissionList(missionList.stream().map(m ->
                                new AutoMissionQueueBody
                                        .MissionBody()
                                        .setId(m.getId())
                                        .setName(m.getName())
                                        .setProgress(0.0)
                                        .setState(AutoMissionQueueBody.MissionState.TODO.getValue())
                                        .setAccountId(param.getUserId().toString())
                        ).collect(Collectors.toList())
                );

        return autoMissionQueueBody;

    }

    private G503AutoMissionQueueBody initG503AutoMissionQueueDTO(G503AutoMissionQueueDTO param, StartMissionQueueNestInfoOutDTO nestInfo) {
        return G503AutoMissionQueueBody.builder()
                .nestId(nestInfo.getNestId())
                .nestUuid(nestInfo.getNestUuid())
                .lastMissionIndex(-1)
                .missionList(listG503Mission(param))
                .build();
    }

    private List<G503AutoMissionQueueBody.Mission> listG503Mission(G503AutoMissionQueueDTO param) {
        List<G503AutoMissionQueueDTO.Mission> missionList = param.getMissionList();
        if (!CollectionUtils.isEmpty(missionList)) {
            Map<Integer, Integer> missionIdWhichMap = missionList.stream().collect(Collectors.toMap(G503AutoMissionQueueDTO.Mission::getMissionId, G503AutoMissionQueueDTO.Mission::getUavWhich));
            List<Integer> missionIdList = missionList.stream().map(G503AutoMissionQueueDTO.Mission::getMissionId).collect(Collectors.toList());
            List<MissionEntity> missionEntList = this.lambdaQuery().eq(MissionEntity::getDeleted, false)
                    .in(MissionEntity::getId, missionIdList)
                    .eq(MissionEntity::getDeleted, false)
                    .list();

            if (CollectionUtils.isEmpty(missionIdList)) {
                return Collections.emptyList();
            }
            List<Integer> taskIdList = missionEntList.stream().map(MissionEntity::getTaskId).collect(Collectors.toList());
            List<TaskEntity> taskEntList = taskService.lambdaQuery().in(TaskEntity::getId, taskIdList).select(TaskEntity::getId, TaskEntity::getType, TaskEntity::getName).list();
            List<TaskTagDTO> sysTagList = sysTagService.listTagNameByTaskIds(taskIdList);
            Map<Integer, String> taskIdTagNameMap = sysTagList.stream().collect(Collectors.toMap(TaskTagDTO::getTaskId, TaskTagDTO::getTagName));
            Map<Integer, Integer> taskTypeMap = taskEntList.stream().collect(Collectors.toMap(TaskEntity::getId, TaskEntity::getType));
            Map<Integer, String> taskMap = taskEntList.stream().collect(Collectors.toMap(TaskEntity::getId, TaskEntity::getName));
            return missionEntList.stream().map(m -> {
                Integer uavWhich = missionIdWhichMap.get(m.getId());
                Integer taskType = taskTypeMap.get(m.getTaskId());
                String tagName = taskIdTagNameMap.get(m.getTaskId());
                String taskName = taskMap.get(m.getTaskId());
                return G503AutoMissionQueueBody.Mission.builder()
                        .gainDataMode(param.getGainDataMode())
                        .gainVideo(param.getGainVideo())
                        .taskId(m.getTaskId())
                        .tagName(tagName)
                        .taskType(taskType)
                        .taskName(taskName)
                        .id(m.getId())
                        .name(m.getName())
                        .progress(0.0)
                        .flyTime(0L)
                        .state(G503AutoMissionQueueBody.MissionStateEnum.TODO.getValue())
                        .uavWhich(uavWhich)
                        .accountId(param.getAccountId())
                        .build();
            }).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void setCameraLensVideoSource(String nestUuid) {
        if (nestUuid != null) {
            MqttSwitchZoomCameraEvent mqttSwitchZoomCameraEvent = new MqttSwitchZoomCameraEvent(nestUuid);
            applicationContext.publishEvent(mqttSwitchZoomCameraEvent);
        }
    }

    private boolean checkLinearTakeOffRecord(TaskModeEnum taskModeEnum, Integer airLineId) {
        if (TaskModeEnum.LINEAR.equals(taskModeEnum) && airLineId != null) {
            AirLineEntity airLineEntity = airLineService.lambdaQuery().eq(AirLineEntity::getId, airLineId).select(AirLineEntity::getWaypoints).one();
            if (airLineEntity != null) {
                try {
                    String waypoints = airLineEntity.getWaypoints();
                    UnifyAirLineFormatDto unifyAirLineFormatDto = JSONObject.parseObject(waypoints, UnifyAirLineFormatDto.class);
                    JSONObject linearParamJson = (JSONObject) unifyAirLineFormatDto.getLineConfigs().get(unifyAirLineFormatDto.getMode().name());
                    LinearParam linearParam = JSONObject.parseObject(linearParamJson.toJSONString(), LinearParam.class);
                    return LinearParam.TaskMode.VIDEO_CAPTURE.equals(linearParam.getTaskMode()) && linearParam.getTakeOffRecord();
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return false;
    }

    private boolean checkFocalAirLine(Integer airLineId) {
        AirLineEntity airLineEntity = airLineService.lambdaQuery()
                .eq(AirLineEntity::getId, airLineId)
                .select(AirLineEntity::getType, AirLineEntity::getWaypoints, AirLineEntity::getOriginalWaypoints)
                .one();
        if (airLineEntity != null) {
            try {
                if (AirLineTypeEnum.UNIFY_AIR_LINE.equals(AirLineTypeEnum.getInstance(airLineEntity.getType()))) {
                    String waypoints = airLineEntity.getWaypoints();
                    UnifyAirLineFormatDto unifyAirLineFormatDto = JSONObject.parseObject(waypoints, UnifyAirLineFormatDto.class);
                    TaskModeEnum mode = unifyAirLineFormatDto.getMode();
                    if (TaskModeEnum.LINEAR.equals(mode)) {
                        long count = getNestAirLineZoomCount(airLineEntity.getOriginalWaypoints());
                        return count > 0;
                    }
                    return false;
                }

                if (AirLineTypeEnum.POINT_CLOUD_AIR_LINE.equals(AirLineTypeEnum.getInstance(airLineEntity.getType()))) {
                    List<PointCloudWaypoint> flyPoints = JSONArray.parseArray(airLineEntity.getWaypoints(), PointCloudWaypoint.class);
                    long count = flyPoints.stream().filter(fp -> fp.getFocalLength() != null && fp.getFocalLength() > 0).count();
                    return count > 0;
                }

                if (AirLineTypeEnum.NEST_AIR_LINE.equals(AirLineTypeEnum.getInstance(airLineEntity.getType()))) {
                    long count = getNestAirLineZoomCount(airLineEntity.getWaypoints());
                    return count > 0;
                }
            } catch (Exception e) {
                //格式化错误
                return false;
            }
        }
        return false;
    }

    private long getNestAirLineZoomCount(String waypointStr) {
        List<Waypoint> waypointList = JSONArray.parseArray(waypointStr, Waypoint.class);
        long count = waypointList.stream().filter(wp -> {
            String wayPointAction = wp.getWayPointAction();
            if (wayPointAction != null) {
                return wayPointAction.contains(String.valueOf(ActionTypeEnum.CAMERA_ZOOM.getValue()));
            }
            return false;
        }).count();
        return count;
    }

    private void focalAirLineTakeOffRecord(String nestUuid, Integer missionId) {
        if (nestUuid != null && missionId != null) {
            MqttTakeOffRecordEventDTO mqttTakeOffRecordEventDTO = new MqttTakeOffRecordEventDTO();
            mqttTakeOffRecordEventDTO.setMissionId(missionId);
            mqttTakeOffRecordEventDTO.setNestUuid(nestUuid);
            MqttTakeOffRecordEvent mqttTakeOffRecordEvent = new MqttTakeOffRecordEvent(mqttTakeOffRecordEventDTO);
            applicationContext.publishEvent(mqttTakeOffRecordEvent);
        }
    }

    private void orderExecTaskOffRecordSwitchZoomCamera(ListenMissionRunDTO dto) {
        if (dto.getTakeOffRecord() && dto.getSwitchZoomCamera()) {
            focalAirLineTakeOffRecord(dto.getNestUuid(), dto.getMissionId());
            Timer timer = new Timer(true);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    setCameraLensVideoSource(dto.getNestUuid());
                }
            }, 5000);
            return;
        }

        if (dto.getTakeOffRecord()) {
            focalAirLineTakeOffRecord(dto.getNestUuid(), dto.getMissionId());
            return;
        }

        if (dto.getSwitchZoomCamera()) {
            setCameraLensVideoSource(dto.getNestUuid());
        }

    }


    /**
     * 国际化架次字符替换
     */
    private String replaceMission(String origin) {
        if (StringUtils.hasText(origin)) {
            return origin.replace("架次", MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MISSIONSERVICEIMPL_MISSION.getContent()));
        }
        return "";
    }

    private void djiMissionRunnable(StartMissionParamDto paramDto, StartMissionNestInfoOutDTO nestParam, FlightTaskPrepareDO build, DjiStartTaskParamDTO djiStartTaskParamDTO) {
        String bid = UUID.fastUUID().toString();
        String flightId = build.getFlightId();
        String nestUuid = nestParam.getNestUuid();
        String finalMsg = WebSocketRes.ok().topic(WebSocketTopicEnum.DJI_MISSION_STATE).uuid(nestUuid).data("dto", "").toJSONString();
        if (NestTypeEnum.DJI_DOCK.getValue() == nestParam.getNestType()) {
            ComponentManager cm = ComponentManagerFactory.getInstance(nestParam.getNestUuid());
            if (cm != null) {
                DjiDockManagerCf djiDockManagerCf = cm.getDjiDockManagerCf();

                //给机场下发任务
                String message1 = WebSocketRes.ok().topic(WebSocketTopicEnum.DJI_MISSION_STATE).uuid(nestUuid).data("dto", "下发任务中").toJSONString();
                sendMessageBy10Ws(nestUuid, message1);
                DjiMqttResult<DjiCommonDataDO> djiMqttResult = djiDockManagerCf.flightTaskPrepare(bid, build);
                String err = "";
                if (djiMqttResult.isSuccess()) {
                    if (ResultEnum.SUCCESS.getValue() == djiMqttResult.getCommonDO().getData().getResult()) {
                        //监听机场获取任务资源
                        FlightTaskResourceGetReplyDO flightTaskResourceGetReplyDO = new FlightTaskResourceGetReplyDO();
                        flightTaskResourceGetReplyDO.setResult(0);
                        FlightTaskResourceGetReplyDO.File file1 = new FlightTaskResourceGetReplyDO.File();
                        file1.setUrl(build.getFile().getUrl());
                        file1.setFingerprint(build.getFile().getFingerprint());
                        FlightTaskResourceGetReplyDO.Output output = new FlightTaskResourceGetReplyDO.Output();
                        output.setFile(file1);
                        flightTaskResourceGetReplyDO.setOutput(output);

                        String message2 = WebSocketRes.ok().topic(WebSocketTopicEnum.DJI_MISSION_STATE).uuid(nestUuid).data("dto", "执行任务中").toJSONString();
                        sendMessageBy10Ws(nestUuid, message2);
                        DjiMqttResult<DjiCommonDataDO> djiMqttResult1 = djiDockManagerCf.flightTaskExecute(bid, flightId);
                        if (djiMqttResult1.isSuccess() && ResultEnum.SUCCESS.getValue() == djiMqttResult1.getCommonDO().getData().getResult()) {
                            String message3 = WebSocketRes.ok().topic(WebSocketTopicEnum.DJI_MISSION_STATE).uuid(nestUuid).data("dto", "任务获取资源中").toJSONString();
                            sendMessageBy10Ws(nestUuid, message3);
                            boolean b = djiDockManagerCf.listenFlightTaskResourceGetOfRequests(bid, flightId, flightTaskResourceGetReplyDO);
                            if (!b) {
                                sendMessageBy10Ws(nestUuid, finalMsg);
                                String msg = WebSocketRes.err().msg(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_START_MISSION_NO_REQUEST_FOR_MISSION_RESOURCES_FROM_BASE_STATION.getContent())).topic(WebSocketTopicEnum.TASK_START_PROGRESS_RESULT).uuid(nestUuid).toJSONString();
                                sendMessageByWs(nestUuid, msg);
                            }
                            //开启监控模式
                            DjiInitTaskProgressDtoParam progressDtoParam = DjiInitTaskProgressDtoParam
                                    .builder()
                                    .taskName(djiStartTaskParamDTO.getTaskName())
                                    .missionName(djiStartTaskParamDTO.getMissionName())
                                    .missionId(paramDto.getMissionId())
                                    .taskId(djiStartTaskParamDTO.getTaskId())
                                    .nestUuid(nestParam.getNestUuid())
                                    .taskType(djiStartTaskParamDTO.getTaskType())
                                    .execMissionId(flightId)
                                    .build();
                            //记录数据
                            Integer flyTimes = missionRecordsService.countFlyByMissionId(paramDto.getMissionId());
                            String missionUuid = baseMapper.getUuidById(paramDto.getMissionId());
                            ListenMissionRunDTO listenMissionRunDTO = new ListenMissionRunDTO()
                                    .setCm(cm)
                                    .setMissionId(paramDto.getMissionId())
                                    .setMissionUuid(missionUuid)
                                    .setGainDataMode(paramDto.getGainDataMode())
                                    //录屏
                                    .setGainVideo(1)
                                    .setNestUuid(nestUuid)
                                    .setFlyTimes(flyTimes)
                                    .setStartTime(LocalDateTime.now())
                                    .setExecId(flightId)
                                    .setSwitchZoomCamera(false)
                                    .setTaskId(djiStartTaskParamDTO.getTaskId())
                                    .setTaskType(djiStartTaskParamDTO.getTaskType())
                                    .setMissionName(djiStartTaskParamDTO.getMissionName())
                                    .setTakeOffRecord(false)
                                    .setAccountId(paramDto.getAccountId());

                            //记录数据到missionRecords
                            Integer missionRecordsId = recordsDji(listenMissionRunDTO);
                            progressDtoParam.setMissionRecordsId(missionRecordsId);
                            wsDjiTaskProgressService.initTaskProgressDto(progressDtoParam);

                            sendMessageBy10Ws(nestUuid, finalMsg);
                            return;
                            //返回成功
//                            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DJI_AIRPORT_START_MISSION_SUCCESSFULLY.getContent()));
                        } else {
                            if (Objects.nonNull(djiMqttResult1.getCommonDO())) {
                                Integer result = djiMqttResult1.getCommonDO().getData().getResult();
                                err = DjiErrorCodeEnum.getMsg(result);
                            } else {
                                err = djiMqttResult1.getErrMsg();
                            }
                        }
                    } else {
                        if (Objects.nonNull(djiMqttResult.getCommonDO())) {
                            Integer result = djiMqttResult.getCommonDO().getData().getResult();
                            err = DjiErrorCodeEnum.getMsg(result);
                        } else {
                            err = djiMqttResult.getErrMsg();
                        }
                    }
                }
                sendMessageBy10Ws(nestUuid, finalMsg);
                String msg = WebSocketRes.err().msg("大疆机场启动任务失败," + err).topic(WebSocketTopicEnum.TASK_START_PROGRESS_RESULT).uuid(nestUuid).toJSONString();
                sendMessageByWs(nestUuid, msg);
            }
        }
    }

}
