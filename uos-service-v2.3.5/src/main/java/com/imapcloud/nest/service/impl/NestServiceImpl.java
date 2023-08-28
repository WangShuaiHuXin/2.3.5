package com.imapcloud.nest.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.enums.AircraftCodeEnum;
import com.imapcloud.nest.enums.InfraredColorEnum;
import com.imapcloud.nest.enums.NestFlowTypeEnum;
import com.imapcloud.nest.enums.NestShowStatusEnum;
import com.imapcloud.nest.mapper.NestMapper;
import com.imapcloud.nest.model.AircraftEntity;
import com.imapcloud.nest.model.NestEntity;
import com.imapcloud.nest.model.NestParamEntity;
import com.imapcloud.nest.model.NestSensorRelEntity;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.pojo.dto.unifyAirLineDto.AppMissionAircraftDTO;
import com.imapcloud.nest.sdk.CommonNestStateFactory;
import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.TxStreamUtil;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisKeyEnum;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestOutDO;
import com.imapcloud.nest.v2.manager.event.NestUnitChangedEvent;
import com.imapcloud.nest.v2.manager.sql.BaseNestManager;
import com.imapcloud.nest.v2.service.*;
import com.imapcloud.nest.v2.service.dto.UnitNodeDTO;
import com.imapcloud.nest.v2.service.dto.in.NestFlowUrlInDTO;
import com.imapcloud.nest.v2.service.dto.out.*;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.manager.camera.CameraManagerCf;
import com.imapcloud.sdk.manager.general.GeneralManager;
import com.imapcloud.sdk.manager.general.GeneralManagerCf;
import com.imapcloud.sdk.manager.general.entity.CameraInfo;
import com.imapcloud.sdk.manager.general.entity.NestNetworkStateEntity;
import com.imapcloud.sdk.manager.general.entity.PushStreamInfo;
import com.imapcloud.sdk.manager.general.enums.PushStreamMode;
import com.imapcloud.sdk.manager.media.MediaManager;
import com.imapcloud.sdk.manager.media.MediaManagerCf;
import com.imapcloud.sdk.manager.media.entity.AvailableCaptureCountEntity;
import com.imapcloud.sdk.manager.media.entity.AvailableRecordTimeEntity;
import com.imapcloud.sdk.manager.media.entity.SdCardRemainSpaceEntity;
import com.imapcloud.sdk.manager.media.entity.SdCardTotalSpaceEntity;
import com.imapcloud.sdk.manager.mission.MissionManager;
import com.imapcloud.sdk.manager.motor.G600MotorManagerCf;
import com.imapcloud.sdk.manager.motor.MotorManagerCf;
import com.imapcloud.sdk.manager.power.PowerManagerCf;
import com.imapcloud.sdk.manager.rc.RcManagerCf;
import com.imapcloud.sdk.manager.rc.entity.ElseWhereParam;
import com.imapcloud.sdk.manager.system.SystemManagerCf;
import com.imapcloud.sdk.mqttclient.MqttOptions;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.AircraftComponentNameEnum;
import com.imapcloud.sdk.pojo.constant.CameraFpvModeEnum;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import com.imapcloud.sdk.pojo.entity.AircraftComponent;
import com.imapcloud.sdk.pojo.entity.AircraftState;
import com.imapcloud.sdk.pojo.entity.Coordinate;
import com.imapcloud.sdk.pojo.entity.Waypoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 机巢信息表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
@Service
@Slf4j
public class NestServiceImpl extends ServiceImpl<NestMapper, NestEntity> implements NestService {

    @Resource
    private NestMapper nestMapper;

//    @Autowired
//    private RegionService regionService;

    @Autowired
    private MissionService missionService;

    @Autowired
    private AircraftService aircraftService;

    @Autowired
    private NestParamService nestParamService;

    @Autowired
    private NestSensorRelService nestSensorRelService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private CommonNestStateService commonNestStateService;

//    @Autowired
//    private BackLandFunService backLandFunService;

    @Autowired
    private ExecutorService executorService;

    @Autowired
    private SysWhiteListService sysWhiteListService;

    @Autowired
    private SysUnitService unitService;

//    @Autowired
//    private NestRtkService nestRtkService;

    @Resource
    private UosAccountService uosAccountService;

    @Resource
    private NestAccountService nestAccountService;

//    @Resource
//    private OrgAccountService orgAccountService;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private BaseNestService baseNestService;

//    @Resource
//    private UosOrgManager uosOrgManager;

    @Resource
    private NestOrgRefService nestOrgRefService;

    @Resource
    private BaseUavService baseUavService;


    @Override
    public List<NestEntity> listIdAndName() {
        return nestMapper.selectAllIdAndName();
    }

    @Deprecated
    @Override
    public void initNest(String nestId) {
        this.asynchronousBatchInitNest(Collections.singletonList(nestId));
    }

    @Deprecated
    @Override
    public void batchInitNest(List<Integer> nestIdList) {
//        if (CollectionUtil.isNotEmpty(nestIdList)) {
//            //FIXME: 修改查询mqtt信息，和基站信息
//            List<NestEntity> nestEntities = nestMapper.selectList(new QueryWrapper<NestEntity>().in("id", nestIdList).eq("deleted", false));
//            List<String> serverUrlList = geoaiUosProperties.getMqtt().getServerUrls();
//            if (!CollectionUtils.isEmpty(nestEntities)) {
//                for (int i = 0; i < nestEntities.size(); i++) {
//                    NestEntity ne = nestEntities.get(i);
//                    if (ne != null && serverUrlList.contains(ne.getServerUrl())) {
//                        MqttOptions options = MqttOptions.instance()
//                                .serverUri(ne.getServerUrl())
//                                .username(ne.getUsername())
//                                .password(ne.getPassword())
//                                .clientId(ne.getUuid() + System.currentTimeMillis())
//                                .nestType(ne.getType());
//                        ComponentManagerFactory.getInstance(options, ne.getUuid());
//                        CommonNestStateFactory.initCommonNestState(ne.getUuid(),ne.getType());
//                    } else {
//                        log.info("MQTT服务地址{},不在白名单", ne.getServerUrl());
//                    }
//                }
//            }
//        }
    }

    @Override
    public void asynchronousBatchInitNest(List<String> nestIdList) {
        executorService.execute(() -> {
            if (CollectionUtil.isNotEmpty(nestIdList)) {
//                List<NestEntity> nestEntities = nestMapper.selectList(new QueryWrapper<NestEntity>().in("id", nestIdList).eq("deleted", false));
                List<MqttInitParamOutDTO> mqttInitParamOutDTOList = baseNestService.listMqttInitParams(nestIdList);
                // 从数据库获取白名单
                List<String> serverUrlList = sysWhiteListService.listMqttBrokerWhiteListsCache();
                if (CollectionUtil.isNotEmpty(mqttInitParamOutDTOList)) {
                    for (int i = 0; i < mqttInitParamOutDTOList.size(); i++) {
                        MqttInitParamOutDTO mipo = mqttInitParamOutDTOList.get(i);
                        if (serverUrlList.contains(mipo.getMqttBrokerUrl())) {
                            //TODO 设置只有我本地能访问大疆基站，确保调试，后续要去掉
                            MqttOptions options = MqttOptions.instance()
                                    .serverUri(mipo.getMqttBrokerUrl())
                                    .username(mipo.getUsername())
                                    .password(mipo.getPassword())
                                    .clientId(mipo.getNestUuid() + System.currentTimeMillis())
                                    .nestType(mipo.getNestType());
//                            if("4TADK8B0000020".equals(mipo.getNestUuid())){
//                                options.serverUri("tcp://124.71.10.164:1883");
//                            }
                            ComponentManagerFactory.initInstance(options,mipo.getNestUuid(),mipo.getDjiTslSnParam());
//                            ComponentManagerFactory.getInstance(options, mipo.getNestUuid());
                            CommonNestStateFactory.initCommonNestState(mipo.getNestUuid(),mipo.getNestType());
                        } else {
                            log.info("MQTT服务地址{},不在白名单", mipo.getMqttBrokerUrl());
                        }
                    }
                }
            }
        });

    }

    @Override
    public boolean isNestLinked(String nestUuid) {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            return cm.getNestLinked();
        }
        return false;
    }

    @Override
    public String getUuidById(int id) {
        return nestMapper.selectUuidById(id);
    }

    @Override
    public List<NestEntity> listNestByIds(Collection<Integer> nestIds) {
        if (!CollectionUtils.isEmpty(nestIds)) {
            return this.lambdaQuery()
                    .eq(NestEntity::getDeleted, Boolean.FALSE)
                    .in(NestEntity::getId, nestIds)
                    .list();
        }
        return Collections.emptyList();
    }

    @Override
    public String getUuidByIdCache(Integer id) {
        if (id == null) {
            return null;
        }
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.UUID_BY_ID_CACHE, id);
        String nestUuid = (String) redisService.get(redisKey);
        if (StrUtil.isEmpty(nestUuid)) {
            nestUuid = nestMapper.selectUuidById(id);
            redisService.set(redisKey, nestUuid);
        }
        return nestUuid;
    }

    @Override
    public NestEntity getNestByIdIsCache(Integer id) {
        return this.getById(id);
    }

    @Override
    public List<BaseNestListOutDTO> listNestAndRegion() {
        /**
         * 获取当前登录用户信息,查询出与当前用户有关的机巢信息
         */
//        TrustedAccessTracerHolder.get();
//        String accountRegionListKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_REGION_LIST_KEY, visitorDetails.getVisitorCode());
//        String accountNestListDtoKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_NEST_LIST_DTO_KEY, visitorDetails.getVisitorCode());
//        List<NestEntity> nestList = listNestEntityInCache();
//        List<String> iCrest2UuidList = nestList.stream().filter(n -> n.getType() == NestTypeEnum.I_CREST2.getValue()).map(NestEntity::getUuid).collect(Collectors.toList());

//        List<Integer> nestIdList = nestList.stream().map(NestEntity::getId).collect(Collectors.toList());
//        List<String> nestIdList = nestList.stream().map(NestEntity::getId).map(Objects::toString).collect(Collectors.toList());
        //批量初始化机巢
//        asynchronousBatchInitNest(nestIdList);

//        List<Integer> regionIdList = nestList.stream().map(NestEntity::getRegionId).collect(Collectors.toList());
//        if (CollectionUtil.isEmpty(regionIdList)) {
//            return Collections.emptyList();
//        }
//
//        List<RegionEntity> regionList = (List<RegionEntity>) redisService.get(accountRegionListKey);
//        if (CollectionUtil.isEmpty(regionList)) {
//            regionList = regionService.listIdAndNameByIdList(regionIdList);
//            redisService.set(accountRegionListKey, regionList);
//            redisService.del(accountNestListDtoKey);
//        }
//
//        Map<Integer, String> regionIdAndNameMap = regionList.stream().collect(Collectors.toMap(RegionEntity::getId, RegionEntity::getName));
//        List<NestUnitEntity> nestUnitList = nestUnitService.lambdaQuery()
//                .in(NestUnitEntity::getNestId, nestIdList)
//                .list();
//
//        Map<Integer, List<Integer>> nestUnitIdListMap = nestUnitList.stream().collect(Collectors.groupingBy(NestUnitEntity::getNestId, Collectors.mapping(NestUnitEntity::getUnitId, Collectors.toList())));

//        List<NestListDto> sortList = (List<NestListDto>) redisService.get(accountNestListDtoKey);
//        if (CollectionUtil.isEmpty(sortList)) {
//            List<NestListDto> nestListDtoList = new ArrayList<>(nestList.size());
//            for (int i = 0; i < nestList.size(); i++) {
//                NestEntity ne = nestList.get(i);
//                NestListDto nestListDto = new NestListDto();
//                int nestState = commonNestStateService.getNestCurrentState(ne.getUuid());
//                nestListDto.setNestId(ne.getId());
//                List<Integer> list = nestUnitIdListMap.get(ne.getId());
//                nestListDto.setUnitIds(list);
//                nestListDto.setNestNumber(ne.getNumber());
//                nestListDto.setNestName(ne.getName());
//                nestListDto.setUuid(ne.getUuid());
//                nestListDto.setLat(ne.getLatitude());
//                nestListDto.setLon(ne.getLongitude());
//                nestListDto.setNestAddress(ne.getAddress());
//                nestListDto.setRegionId(ne.getRegionId());
//                nestListDto.setNestStatus(nestState);
//                nestListDto.setPicTranUrl(ne.getRtmpUrl());
//                nestListDto.setOuterVideoUrl(ne.getOutVideoUrl());
//                nestListDto.setInnerVideoUrl(ne.getInnerVideoUrl());
//                nestListDto.setNestType(ne.getType());
//                nestListDto.setShowStatus(ne.getShowStatus());
//                nestListDto.setAlt(ne.getAltitude());
//                nestListDto.setRegionName(regionIdAndNameMap.get(ne.getRegionId()) == null ? "其他" : regionIdAndNameMap.get(ne.getRegionId()));
//                nestListDtoList.add(nestListDto);
//            }
//            //自定义排序 1- 0- 2- -1
//            sortList = nestListDtoList.stream().sorted((o1, o2) -> {
//                Integer sort1 = getSortValue(o1.getNestStatus());
//                Integer sort2 = getSortValue(o2.getNestStatus());
//                return sort1 - sort2;
//            }).collect(Collectors.toList());

//            redisService.set(accountNestListDtoKey, sortList);
//        }
        String accountNestListDtoKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_NEST_LIST_DTO_KEY, TrustedAccessTracerHolder.get().getUsername());
        List<BaseNestListOutDTO> sortList = (List<BaseNestListOutDTO>) redisService.get(accountNestListDtoKey);
        if (CollectionUtil.isEmpty(sortList)) {
            sortList = baseNestService.listNestInfos();
            if (CollectionUtil.isNotEmpty(sortList)) {
                redisService.set(accountNestListDtoKey, sortList);
            }
        }
        //异步初始化
        List<String> nestIdList = sortList.stream().map(BaseNestListOutDTO::getNestId).collect(Collectors.toList());
        asynchronousBatchInitNest(nestIdList);
        return sortList;
    }

    private Map<String, List<String>> getNestOrgCodes(List<String> nestIdList) {
        List<NestOrgRefOutDTO> noRefs = nestOrgRefService.listNestOrgRefs(nestIdList, false);
        if (!CollectionUtils.isEmpty(noRefs)) {
            return noRefs.stream()
                    .collect(Collectors.groupingBy(NestOrgRefOutDTO::getNestId, Collectors.mapping(NestOrgRefOutDTO::getOrgCode, Collectors.toList())));
        }
        return Collections.emptyMap();
    }

    @Override
    public BaseNestListOutDTO getNestAndRegion(String nestUuid) {
        return baseNestService.getNestAndRegion(nestUuid);
    }

    private List<String> findNestOrgCodes(String nestId) {
        List<NestOrgRefOutDTO> noRefs = nestOrgRefService.listNestOrgRefs(Collections.singletonList(nestId), false);
        if (!CollectionUtils.isEmpty(noRefs)) {
            return noRefs.stream()
                    .map(NestOrgRefOutDTO::getOrgCode)
                    .distinct()
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 自定义排序   1- 0- 2- -1 对应机巢状态值排列顺序
     *
     * @param nestStatus
     * @return
     */
    private Integer getSortValue(Integer nestStatus) {
        switch (nestStatus) {
            case 1:
                return 1;
            case 0:
                return 2;
            case 2:
                return 3;
            default:
                return 4;
        }
    }

    @Override
    public NestEntity getNestByMissionId(Integer missionId) {
        Integer nestId = missionService.getNestIdById(missionId);
        return nestMapper.selectById(nestId);
    }

    @Override
    public String getNestUuidByMissionId(Integer missionId) {
        Integer nestId = missionService.getNestIdById(missionId);
        NestEntity nestEntity = this.lambdaQuery().eq(NestEntity::getId, nestId).eq(NestEntity::getDeleted, false).select(NestEntity::getUuid).one();
        return nestEntity == null ? null : nestEntity.getUuid();
    }

    @Deprecated
    @Override
    public RestRes getNestDetailsById(String nestId, String nestUuid) {
        if (nestId != null || nestUuid != null) {
//            NestDetailsInfoDto nestDetailsInfoDto = null;
//            if (nestId != null) {
//                nestDetailsInfoDto = nestMapper.getByNestId(nestId);
//            } else if (nestUuid != null) {
//                nestDetailsInfoDto = nestMapper.getByNestUuId(nestUuid);
//                nestId = nestDetailsInfoDto.getNestId();
//            }
//            if (nestDetailsInfoDto == null) {
//                return RestRes.err("查询不到机巢");
//            }
//            nestDetailsInfoDto.setAircraftCode(AircraftCodeEnum.getInstance(nestDetailsInfoDto.getAircraftTypeValue()).getCode());
//            // 查询基站所属单位
//            List<NestOrgRefOutDTO> noRefs = nestOrgRefService.listNestOrgRefs(Collections.singletonList(nestDetailsInfoDto.getNestId()), true);
//            if (!CollectionUtils.isEmpty(noRefs)) {
//                Map<String, String> orgMap = noRefs.stream().collect(Collectors.toMap(NestOrgRefOutDTO::getOrgCode, NestOrgRefOutDTO::getOrgName));
//                nestDetailsInfoDto.setUnitIds(new ArrayList<>(orgMap.keySet()));
//                String unitName = String.join(",", orgMap.values());
//                nestDetailsInfoDto.setUnitName(unitName);
//            }
//            List<BackLandFunEntity> backLandFunEntities = backLandFunService.lambdaQuery().eq(BackLandFunEntity::getBaseNestId, nestId).list();
//            nestDetailsInfoDto.setBackLandFunEntities(backLandFunEntities);
//            nestDetailsInfoDto.setRtkExpireTime(nestRtkService.getNestExpireRtk(nestId).getParam().get("expireTime"));
            Map<String, Object> map = new HashMap<>(2);
            if (Objects.nonNull(nestId)) {
                BaseNestInfoOutDTO baseNestInfo = baseNestService.getBaseNestInfo(nestId);
                String uavType = baseUavService.getUavTypeByNestId(nestId);
                if (Objects.nonNull(baseNestInfo) && Objects.nonNull(uavType)) {
                    NestUuidTypeUavCodeDTO dto = NestUuidTypeUavCodeDTO.builder()
                            .uuid(baseNestInfo.getUuid())
                            .type(baseNestInfo.getType())
                            .aircraftCode(AircraftCodeEnum.getInstance(uavType).getCode())
                            .build();
                    map.put("nestDetailsDto", dto);
                    return RestRes.ok(map);
                }
            }

            if (Objects.nonNull(nestUuid)) {
                BaseNestInfoOutDTO baseNestInfo = baseNestService.getBaseNestInfoByUuid(nestId);
                String uavType = baseUavService.getUavTypeByNestId(nestId);
                if (Objects.nonNull(baseNestInfo) && Objects.nonNull(uavType)) {
                    NestUuidTypeUavCodeDTO dto = NestUuidTypeUavCodeDTO.builder()
                            .uuid(baseNestInfo.getUuid())
                            .type(baseNestInfo.getType())
                            .aircraftCode(AircraftCodeEnum.getInstance(uavType).getCode())
                            .build();
                    map.put("nestDetailsDto", dto);
                    return RestRes.ok(map);
                }
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_CORRESPONDING_MACHINE_NEST.getContent()));
    }

    @Deprecated
    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public RestRes saveOrUpdateNestDetailsDto2(NestDetailsDto nestDetailsDto) throws RuntimeException {
        Long userId = Long.valueOf(TrustedAccessTracerHolder.get().getAccountId());
        nestDetailsDto.setCreateUserId(userId);
        boolean isAdd = nestDetailsDto.getNestId() == null;
        /**
         * 保存机巢信息
         */
        RestRes restRes1 = saveOrUpdateNestDetails(nestDetailsDto);
        if (!restRes1.isOk()) {
            throw new NestException(restRes1.getMsg());
        }
        Integer nestId = (Integer) restRes1.getParam().get("nestId");
        nestDetailsDto.setNestId(nestId);
        // 增加对nms转发的操作，校验rtmpUrl为腾讯云地址时，主动添加relay任务到nms
//        nodeMediaUtil.createOneRelayStream(nestDetailsDto.getRtmpUrl(), nestDetailsDto.getName());
        // 保存机巢参数表
        if (isAdd) {
            NestParamEntity nestParamEntity = new NestParamEntity();
            nestParamEntity.setNestId(nestId);
            nestParamEntity.setCreatorId(userId);
            nestParamService.save(nestParamEntity);
        }

        /*
         * 处理基站与单位关系
         */
        // 获取数据库基站关联的单位ID列表
        List<String> dbUnitIds = findNestOrgCodes(nestId.toString());
        List<UnitNodeDTO> oldUnitNodeInfos = unitService.getUnitNodeInfos(dbUnitIds);
        List<UnitNodeDTO> newUnitNodeInfos = unitService.getUnitNodeInfos(nestDetailsDto.getUnitIds());
        NestUnitChangedEvent.NestChangedInfo eventSource = getNestChangedInfo(nestId.toString(), oldUnitNodeInfos, newUnitNodeInfos);
        applicationContext.publishEvent(new NestUnitChangedEvent(eventSource));
        // 更新基站关联单位关系
        nestOrgRefService.updateNestOrgRefs(nestId.toString(), nestDetailsDto.getUnitIds());
        /*
         * 保存无人机信息
         */
        RestRes restRes3 = saveOrUpdateAirDetails(nestDetailsDto);
        if (!restRes3.isOk()) {
            throw new NestException(restRes3.getMsg());
        }
        RestRes restRes4 = saveOrUpdateNestSensorRel(isAdd, nestDetailsDto.getSensorIdList(), nestId, userId);
        if (!restRes4.isOk()) {
            throw new NestException(restRes4.getMsg());
        }

        // 保存rtk过期时间
//        RestRes restRes5 = nestRtkService.setExpireTime(nestId, nestDetailsDto.getRtkExpireTime());
//        if (!restRes5.isOk()) {
//            throw new NestException(restRes4.getMsg());
//        }

        /**
         * 清理redis缓存
         */
        clearRedisCache(nestId, TrustedAccessTracerHolder.get().getUsername(), isAdd);
        Map<String, Object> map = new HashMap<>(2);
        map.put("nestId", nestId);
        map.put("aircraftId", restRes3.getParam().get("airId"));

//        // 发布事件
//        NestUnitOutDO nestUnitOutDO = new NestUnitOutDO();
//        nestUnitOutDO.setNestId(nestId);
//        nestUnitOutDO.setUnitIds(nestDetailsDto.getUnitIds());
//        applicationContext.publishEvent(new NestCreatedEvent(nestUnitOutDO));
        return RestRes.ok(map);

    }

    @Override
    public void synNestOrg(String nestId, List<String> orgCodeList) {
        // 获取数据库基站关联的单位ID列表
        List<String> dbUnitIds = findNestOrgCodes(nestId);
        List<UnitNodeDTO> oldUnitNodeInfos = unitService.getUnitNodeInfos(dbUnitIds);
        List<UnitNodeDTO> newUnitNodeInfos = unitService.getUnitNodeInfos(orgCodeList);
        NestUnitChangedEvent.NestChangedInfo eventSource = getNestChangedInfo(nestId, oldUnitNodeInfos, newUnitNodeInfos);
        applicationContext.publishEvent(new NestUnitChangedEvent(eventSource));
    }

    private List<String> getNestOrgCodes(String nestId) {
        List<NestOrgRefOutDTO> noRefs = nestOrgRefService.listNestOrgRefs(Collections.singletonList(nestId), false);
        if (!CollectionUtils.isEmpty(noRefs)) {
            return noRefs.stream().map(NestOrgRefOutDTO::getOrgCode).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private NestUnitChangedEvent.NestChangedInfo getNestChangedInfo(String nestId, List<UnitNodeDTO> oldUnitNodes, List<UnitNodeDTO> newUUnitNodes) {
        Set<UnitNodeDTO> incrUnitNodes = new HashSet<>();
        Set<UnitNodeDTO> decrUnitNodes = new HashSet<>();
        // 新增和删除
        if (!CollectionUtils.isEmpty(oldUnitNodes) && !CollectionUtils.isEmpty(newUUnitNodes)) {
            for (UnitNodeDTO oldUnitNode : oldUnitNodes) {
                if (!newUUnitNodes.contains(oldUnitNode)) {
                    decrUnitNodes.add(oldUnitNode);
                }
            }
            for (UnitNodeDTO newUUnitNode : newUUnitNodes) {
                if (!oldUnitNodes.contains(newUUnitNode)) {
                    incrUnitNodes.add(newUUnitNode);
                }
            }
        }
        // 全部删除
        else if (!CollectionUtils.isEmpty(oldUnitNodes) && CollectionUtils.isEmpty(newUUnitNodes)) {
            decrUnitNodes.addAll(oldUnitNodes);
        }
        // 全部新增
        else if (CollectionUtils.isEmpty(oldUnitNodes) && !CollectionUtils.isEmpty(newUUnitNodes)) {
            incrUnitNodes.addAll(newUUnitNodes);
        }
//        List<OrgSimpleOutDO> allUnits = uosOrgManager.listAllOrgInfos();
        boolean debugEnabled = log.isDebugEnabled();
        if (debugEnabled) {
            log.debug("-------->开始获取获取待新增单位链<--------");
        }
        List<LinkedList<String>> incrUnitIdChains = getUnitIdChains(incrUnitNodes);
        if (debugEnabled) {
            log.debug("-------->开始获取获取待移除单位链<--------");
        }
        List<LinkedList<String>> decrUnitIdChains = getUnitIdChains(decrUnitNodes);
        if (debugEnabled) {
            log.debug("-------->开始获取获取期望最新单位链<--------");
        }
        List<LinkedList<String>> expectUnitIdChains = getUnitIdChains(new HashSet<>(newUUnitNodes));
        NestUnitChangedEvent.NestChangedInfo nestChangedInfo = new NestUnitChangedEvent.NestChangedInfo();
        nestChangedInfo.setNestId(nestId);
        nestChangedInfo.setExpectUnitChains(expectUnitIdChains);
        nestChangedInfo.setIncreasedUnitChains(incrUnitIdChains);
        nestChangedInfo.setDecreasedUnitChains(decrUnitIdChains);
        return nestChangedInfo;
    }

    @Resource
    private SysUnitService sysUnitService;

    private List<LinkedList<String>> getUnitIdChains(Set<UnitNodeDTO> unitNodes) {
        if (!CollectionUtils.isEmpty(unitNodes)) {
            List<LinkedList<String>> unitIdChains = new ArrayList<>(unitNodes.size());
            boolean debugEnabled = log.isDebugEnabled();

            for (UnitNodeDTO incrUnitNode : unitNodes) {
                LinkedList<String> unitIdChain = Lists.newLinkedList();
                // 不能忽略本身单位
                unitIdChain.add(incrUnitNode.getUnitId());
                List<String> stringList = sysUnitService.getSuperiorOrgCodes(incrUnitNode.getUnitId());
                if (CollUtil.isEmpty(stringList)) {
                    continue;
                }
                unitIdChain.addAll(stringList);
                Collections.reverse(unitIdChain);
                if (debugEnabled) {
                    StringBuilder chainPrint = new StringBuilder();
                    for (String unitId : unitIdChain) {
                        chainPrint.append(unitId).append("-->");
                    }
                    chainPrint.append("END");
                    log.debug("单位链: {}", chainPrint);
                }
                unitIdChains.add(unitIdChain);
            }
            return unitIdChains;
        }
        return Collections.emptyList();
    }

    @Deprecated
    @Override
    @Transactional(rollbackFor = {NestException.class})
    public RestRes softDeleteById(Integer id) {
        if (id != null) {
            // 删除机巢信息
            int res = nestMapper.softDelete(id);
            // 删除机巢习惯信息
            nestParamService.deleteByNestId(id);
            // 删除无人机的信息
            aircraftService.softDelete(id);
            // 删除传感器
            nestSensorRelService.deleteByNestId(String.valueOf(id));
            // 删除用户关联表
            nestAccountService.deleteByNestId(id);
            // 删除基站与单位关联表
            // TODO 王敏：需要正确传基站ID
            nestOrgRefService.deleteNestOrgRefs(id.toString());
            if (res > 0) {
                //在查询机巢列表的时候将列表缓存了，因此在这里要清空redis缓存
                String visitorCode = TrustedAccessTracerHolder.get().getUsername();
                clearNestListRedisCache();
                redisService.del(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_AIRCRAFT_LIST_KEY, visitorCode));
                redisService.del(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_REGION_LIST_KEY, visitorCode));
                String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_MAP_NEST_ID_LIST_KEY, visitorCode);
                redisService.del(redisKey);
                redisService.del(RedisKeyConstantList.ALL_NEST_UUID);
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_DELETED.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DELETE_FAILED.getContent()));
    }

    @Override
    public List<NestEntity> listNestByPages() {
        try {
            //IPage<NestEntity> page = new Page<>(pageNo, pageSize);
            //todo 展示的机巢，要根据用户角色和单位来分

            QueryWrapper<NestEntity> nestEntityQueryWrapper = new QueryWrapper<>();
            NestAccountInfoOutDTO nestAccountInfoOutDTO = nestAccountService.nestAccountInfo(TrustedAccessTracerHolder.get().getAccountId());
            if (Objects.nonNull(nestAccountInfoOutDTO) && !CollectionUtils.isEmpty(nestAccountInfoOutDTO.getInfoList())) {
                Set<String> nestIds = nestAccountInfoOutDTO.getInfoList().stream().map(NestAccountInfoOutDTO.Info::getNestId).collect(Collectors.toSet());
                nestEntityQueryWrapper.in("id", nestIds);
            }
            nestEntityQueryWrapper.eq("deleted", false);
            nestEntityQueryWrapper.orderByAsc("name");
            return nestMapper.selectList(nestEntityQueryWrapper);
        } catch (Exception e) {
            log.error("分页出现问题", e);
            return null;
        }
    }

    @Override
    public String getRtmpUrlByMissionId(Integer missionId) {
        return nestMapper.getRtmpUrlByMissionId(missionId);
    }

    @Override
    public Map<String, Object> getAppNestData(Integer limit, String nestUuid, Long lastTime) {
        Map<String, Object> map = new HashMap<>(2);
        List<AppMissionAircraftDTO> missionAircraftDTOList = missionService.getMissionByNestUuid(limit, nestUuid, lastTime);
        if (missionAircraftDTOList.size() > 0) {
            lastTime = missionAircraftDTOList.get(missionAircraftDTOList.size() - 1).getModifyTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            map.put("lastTime", lastTime);
        } else {
            map.put("lastTime", lastTime);
        }
        map.put("list", missionAircraftDTOList);
        return map;
    }

    @Override
    public Integer getIdByUuid(String uuid) {
        return baseMapper.selectIdByUuid(uuid);
    }

    @Override
    public Integer getNestTypeByUuidInCache(String uuid) {
        if (uuid != null) {
            Integer type = (Integer) redisService.hGet(RedisKeyConstantList.NEST_UUID_TYPE_MAP_KEY, uuid);
            if (type == null) {
                type = baseMapper.selectTypeByUuid(uuid);
                if (type != null) {
                    redisService.hSet(RedisKeyConstantList.NEST_UUID_TYPE_MAP_KEY, uuid, type);
                }
            }
            return type;
        }
        return null;
    }

    @Override
    public String getAircraftStateByUuidInCache(String uuid) {
        if (uuid != null) {
            String state = (String) redisService.hGet(RedisKeyConstantList.NEST_UUID_AIRCRAFT_STATE_MAP_KEY, uuid);
            if (state == null) {
                state = baseMapper.selectAircraftStateByUuid(uuid);
                if (state != null) {
                    redisService.hSet(RedisKeyConstantList.NEST_UUID_AIRCRAFT_STATE_MAP_KEY, uuid, state);
                }
            }
            return state;
        }
        return null;
    }


    @Override
    public RestRes updateNestShowStatus(String nestId, String regionId, Integer showStatus) {

        String visitorCode = TrustedAccessTracerHolder.get().getUsername();
        if (nestId != null) {
//            NestEntity nest = this.getById(nestId);
//            if (nest == null) {
//                return RestRes.err("当前机巢不存在！");
//            }
//            nest.setShowStatus(showStatus);

//            redisService.del(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_NEST_LIST_DTO_KEY, visitorCode));
//            clearNestListRedisCache();
//            return this.updateById(nest) ? RestRes.ok("当前机巢监控查看状态更改成功!") : RestRes.err();

            redisService.del(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_NEST_LIST_DTO_KEY, visitorCode));
            clearNestListRedisCache();
            boolean b = baseNestService.setShowStatusByNestId(nestId, showStatus);
            return b ? RestRes.ok(MessageEnum.GEOAI_UOS_SUCCESS_VIEW_NEST.getContent()) : RestRes.err();
        }
        if (regionId != null) {
//            RegionEntity region = regionService.getById(regionId);
//            if (region == null) {
//                return RestRes.err("当前机巢不存在！");
//            }
//            List<RegionEntity> regionEntities = regionService.list(new QueryWrapper<RegionEntity>().eq("id", regionId).eq("deleted", false));
//            if (CollectionUtils.isEmpty(regionEntities)) {
//                return RestRes.err("当前区域不存在机巢！");
//            }
//            redisService.del(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_NEST_LIST_DTO_KEY, visitorCode));
//            clearNestListRedisCache();
//            return this.lambdaUpdate().set(NestEntity::getShowStatus, showStatus).eq(NestEntity::getRegionId, regionId).update() ? RestRes.ok("当前机巢监控查看状态更改成功") : RestRes.err();
            redisService.del(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_NEST_LIST_DTO_KEY, visitorCode));
            clearNestListRedisCache();
            boolean b = baseNestService.setShowStatusByRegionId(regionId, showStatus);
            return b ? RestRes.ok(MessageEnum.GEOAI_UOS_SUCCESS_VIEW_NEST.getContent()) : RestRes.err();
        }
        return RestRes.err();
    }

    @Override
    public IPage<NestEntity> listFlowUrlBy(Integer pageNo, Integer pageSize, Integer flowType) {
        if (pageNo != null && pageSize != null) {
            try {
                IPage<NestEntity> page = new Page<>(pageNo, pageSize);
                //todo 展示的机巢，要根据用户角色和单位来分

                QueryWrapper<NestEntity> nestEntityQueryWrapper = new QueryWrapper<>();

                NestAccountInfoOutDTO nestAccountInfoOutDTO = nestAccountService.nestAccountInfo(TrustedAccessTracerHolder.get().getAccountId());
                if (Objects.nonNull(nestAccountInfoOutDTO) && !CollectionUtils.isEmpty(nestAccountInfoOutDTO.getInfoList())) {
                    Set<String> nestIds = nestAccountInfoOutDTO.getInfoList().stream().map(NestAccountInfoOutDTO.Info::getNestId).collect(Collectors.toSet());
                    nestEntityQueryWrapper.in("id", nestIds);
                }

                nestEntityQueryWrapper.eq("show_status", NestShowStatusEnum.COULD_BE_SEEN.getCode());
                nestEntityQueryWrapper.eq("deleted", false);
                IPage<NestEntity> nestEntityIPage = nestMapper.selectPage(page, nestEntityQueryWrapper);
                List<NestEntity> records = nestEntityIPage.getRecords();
                if (!CollectionUtils.isEmpty(records)) {
                    records.forEach(it -> {
                        int nestState = commonNestStateService.getNestCurrentState(it.getUuid());
                        it.setNestStatus(nestState);
                        if (NestFlowTypeEnum.OUTSIDE_URL.getCode().equals(flowType)) {
                            it.setFlowUrl(it.getOutVideoUrl());
                        } else if (NestFlowTypeEnum.INSIDE_URL.getCode().equals(flowType)) {
                            it.setFlowUrl(it.getInnerVideoUrl());
                        } else if (NestFlowTypeEnum.RTMP_URL.getCode().equals(flowType)) {
                            it.setFlowUrl(it.getRtmpUrl());
                        }
                    });
                }

                return nestEntityIPage;
            } catch (Exception e) {
                e.printStackTrace();
                log.info("分页出现问题： {}", e);
                return null;
            }
        }

        return null;
    }

    @Override
    public RestRes listFlowUrlBy2(Integer pageNo, Integer pageSize, Integer flowType) {
        if (pageNo != null && pageSize != null) {
            try {
                NestAccountInfoOutDTO nestAccountInfoOutDTO = nestAccountService.nestAccountInfo(TrustedAccessTracerHolder.get().getAccountId());
                if (Objects.isNull(nestAccountInfoOutDTO)) {
                    return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CANT_QUERY_USER_BASE_STATION_INFORMATION.getContent()));
                }
                List<String> nestIds = nestAccountInfoOutDTO.getInfoList().stream().map(dto -> String.valueOf(dto.getNestId())).collect(Collectors.toList());

                NestFlowUrlInDTO nestFlowUrlInDTO = NestFlowUrlInDTO.builder()
                        .nestIdList(nestIds)
                        .showStatus(NestShowStatusEnum.COULD_BE_SEEN.getCode())
                        .nestFlowTypeEnum(NestFlowTypeEnum.getInstance(flowType))
                        .build();

                List<NestFlowUrlOutDTO> nestFlowUrlOutDTOList = baseNestService.listNestFlowUrls(nestFlowUrlInDTO);
                log.info("#NestServiceImpl.listFlowUrlBy2#, nestFlowUrlOutDTOList={}", nestFlowUrlOutDTOList);
                Map<String, List<Map<String, Object>>> urlAndNestMap = new HashMap<>();
                for (NestFlowUrlOutDTO ne : nestFlowUrlOutDTOList) {
                    if (NestFlowTypeEnum.OUTSIDE_URL.getCode().equals(flowType) && isStandardFlowUrl(ne.getOuterStreamUrl())) {
                        List<Map<String, Object>> maps = urlAndNestMap.get(ne.getOuterStreamUrl());
                        if (CollectionUtil.isEmpty(maps)) {
                            maps = new ArrayList<>();
                        }
                        Map<String, Object> nestMap = new HashMap<>(8);
                        nestMap.put("nestName", ne.getNestName());
                        nestMap.put("nestId", ne.getNestId());
                        nestMap.put("nestUuid", ne.getNestUuid());
                        nestMap.put("type", ne.getType());
                        nestMap.put("nestStatus", commonNestStateService.getNestCurrentState(ne.getNestUuid()));
                        maps.add(nestMap);
                        urlAndNestMap.put(ne.getOuterStreamUrl(), maps);
                    } else if (NestFlowTypeEnum.INSIDE_URL.getCode().equals(flowType) && isStandardFlowUrl(ne.getInnerStreamUrl())) {
                        List<Map<String, Object>> maps = urlAndNestMap.get(ne.getInnerStreamUrl());
                        if (CollectionUtil.isEmpty(maps)) {
                            maps = new ArrayList<>();
                        }
                        Map<String, Object> nestMap = new HashMap<>(8);
                        nestMap.put("nestName", ne.getNestName());
                        nestMap.put("nestId", ne.getNestId());
                        nestMap.put("nestUuid", ne.getNestUuid());
                        nestMap.put("type", ne.getType());
                        nestMap.put("nestStatus", commonNestStateService.getNestCurrentState(ne.getNestUuid()));
                        maps.add(nestMap);
                        urlAndNestMap.put(ne.getInnerStreamUrl(), maps);
                    } else if (NestFlowTypeEnum.RTMP_URL.getCode().equals(flowType) && isStandardFlowUrl(ne.getUavStreamUrl())) {
                        List<Map<String, Object>> maps = urlAndNestMap.get(ne.getUavStreamUrl());
                        if (CollectionUtil.isEmpty(maps)) {
                            maps = new ArrayList<>();
                        }
                        Map<String, Object> nestMap = new HashMap<>(8);
                        nestMap.put("nestName", ne.getNestName());
                        nestMap.put("nestId", ne.getNestId());
                        nestMap.put("nestUuid", ne.getNestUuid());
                        nestMap.put("type", ne.getType());
                        nestMap.put("nestStatus", commonNestStateService.getNestCurrentState(ne.getNestUuid()));
                        maps.add(nestMap);
                        urlAndNestMap.put(ne.getUavStreamUrl(), maps);
                    }

                }
                log.info("#NestServiceImpl.listFlowUrlBy2#, urlAndNestMap={}", urlAndNestMap);
                List<MultiScreenDto> multiScreenDtoList = new ArrayList<>();
                Set<Map.Entry<String, List<Map<String, Object>>>> entries = urlAndNestMap.entrySet();
                for (Map.Entry<String, List<Map<String, Object>>> entry : entries) {
                    MultiScreenDto multiScreenDto = new MultiScreenDto();
                    multiScreenDto.setFlowUrl(entry.getKey());
                    multiScreenDto.setNestName(entry.getValue().get(0).get("nestName").toString());
                    multiScreenDto.setNestUuid(entry.getValue().get(0).get("nestUuid").toString());
                    multiScreenDto.setNestNameAndStatusList(entry.getValue());
                    multiScreenDtoList.add(multiScreenDto);
                }

                multiScreenDtoList.sort(Comparator.comparing(MultiScreenDto::getNestName));
                Map<String, Object> result = new HashMap<>(8);
                result.put("current", pageNo);
                int total = multiScreenDtoList.size();
                int pages = (total % pageSize) > 0 ? (total / pageSize) + 1 : total / pageSize;
                result.put("pages", pages);
                result.put("size", pageSize);
                result.put("total", total);
                int fromIndex = (pageNo - 1) * pageSize;
                int toIndex = pageNo * pageSize - 1;
                toIndex = Math.min(toIndex, total - 1);
                if (fromIndex >= total) {
                    result.put("records", Collections.emptyList());
                } else {
                    result.put("records", multiScreenDtoList.subList(fromIndex, toIndex + 1));
                }
                log.info("#NestServiceImpl.listFlowUrlBy2#, result={}", result);
                return RestRes.ok(result);
            } catch (Exception e) {
                log.error("#NestServiceImpl.listFlowUrlBy2#, error:", e);
                return null;
            }
        }
        log.info("#NestServiceImpl.listFlowUrlBy2#, result null");
        return null;
    }

    @Override
    public String getNestNameByUuidInCache(String uuid) {
        if (uuid != null) {
            String nestName = (String) redisService.hGet(RedisKeyConstantList.NEST_UUID_NAME_MAP_KEY, uuid);
            if (nestName == null) {
//                nestName = baseMapper.selectNameByUuid(uuid);
                nestName = baseNestService.getNestNameByUuid(uuid);
                if (nestName != null) {
                    redisService.hSet(RedisKeyConstantList.NEST_UUID_NAME_MAP_KEY, uuid, nestName);
                }
            }
            return nestName;
        }
        return null;
    }

    @Override
    public List<String> listNestUuidByAccountInCache(String account) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_MAP_NEST_ID_LIST_KEY, account);
        List<String> list = (List<String>) redisService.get(redisKey);
        if (CollectionUtil.isEmpty(list)) {
//            AccountDetailOutDTO accountDetailOutDTO = uosAccountService.getAccountDetailsByUsername(account);
            AccountDetailOutDTO accountDetailOutDTO = uosAccountService.getAccountDetailsByUsernameInGuavaCache(account);
            if (accountDetailOutDTO != null) {
                String accountId = accountDetailOutDTO.getAccountId();
                list = nestAccountService.listNestUuidsByAccountId(Long.valueOf(accountId));
                if (CollectionUtil.isNotEmpty(list)) {
                    redisService.set(redisKey, list);
                }
            }
        }
        return list;
    }

    @Override
    public List<String> listNestUuidInCache() {
//        String redisKey = RedisKeyConstantList.ALL_NEST_UUID;
//        List<String> list = (List<String>) redisService.get(redisKey);
//        if (CollectionUtil.isEmpty(list)) {
//            List<NestEntity> nestList = this.lambdaQuery()
//                    .eq(NestEntity::getDeleted, false)
//                    .select(NestEntity::getUuid)
//                    .list();
//
//            if (CollectionUtil.isNotEmpty(nestList)) {
//                list = nestList.stream().map(NestEntity::getUuid).collect(Collectors.toList());
//                redisService.set(redisKey, list);
//            }
//        }
        return Collections.emptyList();
    }

    @Override
    public List<String> listNestEntityInCache() {
        String username = TrustedAccessTracerHolder.get().getUsername();
        String accountNestListKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_NEST_LIST_KEY, username);
        List<String> nestIdList = (List<String>) redisService.get(accountNestListKey);
        if (CollectionUtil.isEmpty(nestIdList)) {
            List<BaseNestOutDO.BaseNestEntityOutDO> list = getNestEntitiesFromDb();

            if (CollectionUtils.isEmpty(list)) {
                return Collections.emptyList();
            }
            nestIdList = list.stream().map(BaseNestOutDO.BaseNestEntityOutDO::getNestId).collect(Collectors.toList());

            List<String> nestUuidList = list.stream().map(BaseNestOutDO.BaseNestEntityOutDO::getUuid).collect(Collectors.toList());
            redisService.set(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_MAP_NEST_ID_LIST_KEY, username), nestUuidList);

            redisService.set(accountNestListKey, nestIdList);
            redisService.expire(accountNestListKey, 1, TimeUnit.HOURS);

            redisService.del(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_REGION_LIST_KEY, username));
            redisService.del(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_NEST_LIST_DTO_KEY, username));

        }
        return nestIdList;
    }

    @Resource
    private BaseNestManager baseNestManager;

    private List<BaseNestOutDO.BaseNestEntityOutDO> getNestEntitiesFromDb() {
        String visitorId = TrustedAccessTracerHolder.get().getAccountId();
        Set<String> nestIds = null;
        NestAccountInfoOutDTO res1 = nestAccountService.nestAccountInfo(visitorId);
        if (Objects.nonNull(res1) && !CollectionUtils.isEmpty(res1.getInfoList())) {
            nestIds = res1.getInfoList().stream()
                    .map(NestAccountInfoOutDTO.Info::getNestId)
                    .collect(Collectors.toSet());
        }
        if (!CollectionUtils.isEmpty(nestIds)) {

            return baseNestManager.selectListByNestIdList(Lists.newArrayList(nestIds));
        }
        return Collections.emptyList();
    }

    @Override
    public RestRes setZoomRatio(String nestUuid, Float ratio) {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            CameraManagerCf cameraManagerCf = cm.getCameraManagerCf();
            MqttResult<NullParam> res = cameraManagerCf.setPhotoZoomRatio(ratio);
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_DEVICE_ZOOMS.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DEVICE_ZOOM_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DEVICE_ZOOM_REQUEST_FAILED.getContent()));
    }

    @Override
    public RestRes getPhotoZoomRatio(String nestUuid) {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            CameraManagerCf cameraManagerCf = cm.getCameraManagerCf();
            MqttResult<Double> res = cameraManagerCf.getPhotoZoomRatio();
            if (res.isSuccess()) {
                return RestRes.ok("ratio", res.getRes());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_GET_CAMERA_ZOOM.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes resetPhotoZoomRatio(String nestUuid) {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm == null) {
            cm = ComponentManagerFactory.getInstance(nestUuid);
        }
        if (cm != null) {
            MqttResult<NullParam> res = cm.getCameraManagerCf().resetPhotoZoomRatio();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_RESETTING_THE_ZOOM.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RESET_ZOOM_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes setCameraAngle(String nestUuid, Float pitchAngle, Float yawAngle) {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getCameraManagerCf().setCameraAngle(pitchAngle, yawAngle);
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MODIFY_HEAD_ANGLE_SUCCESS.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MODIFY_GIMBAL_ANGLE_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes resetCameraAngle(String nestUuid) {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getCameraManagerCf().resetCameraAngle();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CLOUD_STATION_BACK_TO_THE_SUCCESS.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILURE_TO_RETURN_TO_THE_CLOUD_STATION.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public Long getCpsMemoryRemainSpaceCache(GeneralManager generalManager, String nestId) {
        String redisKey = RedisKeyEnum.REDIS_KEY.className("NestServiceImpl").methodName("getCpsMemoryRemainSpaceCache").identity("nestId", nestId).type("long").get();
        Object remainSpace = redisService.get(redisKey);
        Long remainSpaceLong = null;
        if (remainSpace == null) {
            CompletableFuture<Long> future = new CompletableFuture<>();
            generalManager.getCpsMemoryRemainSpace((result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    Long res = result == null ? 0L : result;
                    future.complete(res);
                } else {
                    future.complete(0L);
                }
            });
            try {
                remainSpaceLong = future.get(5, TimeUnit.SECONDS);
                redisService.set(redisKey, remainSpaceLong, 600);
                return remainSpaceLong;
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                redisService.set(redisKey, -1L, 600);
                return -1L;
            }
        } else {
            if (remainSpace instanceof Long) {
                remainSpaceLong = (Long) remainSpace;
            } else if (remainSpace instanceof Long) {
                remainSpaceLong = ((Long) remainSpace).longValue();
            }
            return remainSpaceLong;
        }
    }

    @Override
    public Long getSdCardRemainSpaceCache(MediaManager mediaManager, String nestId) {
        String redisKey = RedisKeyEnum.REDIS_KEY.className("NestController").methodName("getSdCardRemainSpaceCache").identity("nestId", nestId).type("long").get();
        Long remainSpaceLong;
        Object remainSpace = redisService.get(redisKey);
        if (remainSpace == null) {
            CompletableFuture<Long> remainFuture = new CompletableFuture<>();
            mediaManager.getSdCardRemainSpace((result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    Long res = result == null ? 0 : Long.parseLong(result);
                    remainFuture.complete(res);
                } else {
                    remainFuture.complete(0L);
                }
            });
            try {
                remainSpaceLong = remainFuture.get(5, TimeUnit.SECONDS);
                if (remainSpace != null) {
                    redisService.set(redisKey, remainSpace, 600);
                }
                return remainSpaceLong;
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                redisService.set(redisKey, -1, 600);
                return -1L;
            }
        } else {
            remainSpaceLong = Long.valueOf(String.valueOf(redisService.get(redisKey))).longValue();
            return remainSpaceLong;
        }
    }

    @Override
    public Long getSdCardTotalSpaceCache(MediaManager mediaManager, String nestId) {
        return -1L;
//        String redisKey = RedisKeyEnum.REDIS_KEY.className("NestController").methodName("getSdCardTotalSpaceCache").identity("nestId", nestId).type("long").get();
//        Object totalSpace = redisService.get(redisKey);
//        if (totalSpace == null) {
//            CompletableFuture<Long> totalFuture = new CompletableFuture<>();
//            mediaManager.getSdCardTotalSpace((result, isSuccess, errMsg) -> {
//                if (isSuccess) {
//                    Long res = result == null ? 0 : Long.parseLong(result);
//                    totalFuture.complete(res);
//                } else {
//                    totalFuture.complete(0L);
//                }
//            });
//
//            try {
//                Long totalSpaceInMb = totalFuture.get(5, TimeUnit.SECONDS);
//                redisService.set(redisKey, totalSpaceInMb, 600);
//                return totalSpaceInMb;
//            } catch (InterruptedException | ExecutionException | TimeoutException e) {
//                redisService.set(redisKey, -1, 600);
//                return -1L;
//            }
//
//        } else {
//            return Long.valueOf(String.valueOf(totalSpace)).longValue();
//        }
    }

    /**
     * 相机版本
     * 飞控版本
     * 遥控版本
     * 电池版本
     * CPS版本
     * MPS版本
     * 软件版本
     *
     * @param nestId
     * @return
     */
    @Override
    public RestRes getVersionInfo(String nestId, Boolean clearCache) {
        Map<String, Object> resData = new HashMap<>(8);
        resData.put("sysVersion", geoaiUosProperties.getVersion());
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.VERSION_INFO_KEY, nestId);
        if (clearCache) {
            redisService.del(redisKey);
        }
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        resData.put("cpsVersion", getCpsVersionCache(redisKey, cm));
        resData.put("mpsVersion", getMpsVersionCache(redisKey, cm));
        List<AircraftComponent> aircraftComponents = listComponentSerialNumberCahce(redisKey, cm);

        for (AircraftComponent ac : aircraftComponents) {
            if (AircraftComponentNameEnum.CAMERA.equals(ac.getAircraftComponentName())) {
                resData.put("cameraVersion", ac.getAircraftFirmware());
            } else if (AircraftComponentNameEnum.FLIGHT_CONTROLLER.equals(ac.getAircraftComponentName())) {
                resData.put("fcVersion", ac.getAircraftFirmware());
            } else if (AircraftComponentNameEnum.REMOTE_CONTROLLER.equals(ac.getAircraftComponentName())) {
                resData.put("rcVersion", ac.getAircraftFirmware());
            } else if (AircraftComponentNameEnum.BATTERY.equals(ac.getAircraftComponentName())) {
                resData.put("batteryVersion", ac.getAircraftFirmware());
            }
        }

        return RestRes.ok(resData);
    }

    @Override
    public List<NestEntity> getAllNests() {
        return nestMapper.selectAllNests();
    }

    @Override
    public List<NestAppNameHttpUrlDTO> getNestAppNameHttpUrlList() {
        return baseMapper.getNestAppNameHttpUrlList();
    }

    @Override
    public RestRes getTxDefaultLive(Integer nestId) {
        NestEntity nest = this.getById(nestId);
        String uuid = nest.getUuid();
        String streamName = "";
        if (uuid.contains("-")) {
            streamName = uuid.substring(0, uuid.indexOf("-"));
        } else {
            streamName = uuid;
        }

        Map result = TxStreamUtil.getDefault(streamName);
        return RestRes.ok(result);
    }

    @Override
    public Page<NestEntity> selectPage(Page<NestEntity> page, QueryWrapper<NestEntity> queryWrapper) {
        if (page != null && queryWrapper != null) {
            return baseMapper.selectPage(page, queryWrapper);
        }
        return null;
    }


    @Override
    public RestRes clearCpsUpdateState(Integer nestId) {
        NestEntity one = this.lambdaQuery()
                .eq(NestEntity::getId, nestId)
                .select(NestEntity::getUuid)
                .one();
        if (one != null) {
//            String nestUuid = one.getUuid();
//            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.NEST_MAINTENANCE_STATE, nestUuid);
//            redisService.del(redisKey);
//            boolean update = this.lambdaUpdate()
//                    .set(NestEntity::getCpsUpdateState, 0)
//                    .eq(NestEntity::getId, nestId).update();
//            if (update) {
//                return RestRes.ok("重置状态成功");
//            }

        }

        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_DOES_NOT_CORRESPOND_TO_THE_NEST.getContent()));
    }


    @Override
    public double getNestAltCache(String nestUuid) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.NEST_ALT_CACHE, nestUuid);
        Double nestAlt = (Double) redisService.get(redisKey);
        if (nestAlt == null) {
            NestEntity nestEntity = this.lambdaQuery()
                    .eq(NestEntity::getUuid, nestUuid)
                    .eq(NestEntity::getDeleted, 0)
                    .select(NestEntity::getAltitude)
                    .one();

            if (nestEntity != null) {
                nestAlt = nestEntity.getAltitude();
                if (nestAlt != null) {
                    redisService.set(redisKey, nestAlt);
                    return nestAlt;
                }
            }
            redisService.set(redisKey, 0.0D, 10);
        }
        return nestAlt == null ? 0 : nestAlt;
    }

    @Override
    public RestRes setAutoGoToDefaultBackLandPointFun(String nestId, Boolean enable) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getMissionManagerCf().setAutoGoToDefaultBackLandPointFun(enable);
            if (res.isSuccess()) {
                return RestRes.ok(enable ? MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TURN_ON_THE_AUTO_STANDBY_FUNCTION_SUCCESSFULLY.getContent()) : MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TURN_OFF_THE_AUTO_STANDBY_FUNCTION_SUCCESSFULLY.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SET_AUTO_STANDBY_FUNCTION_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SET_AUTO_STANDBY_FUNCTION_FAILED_THE_NEST_IS_OFFLINE.getContent()));
    }

    @Override
    public RestRes getAutoGoToDefaultBackLandPointFun(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<Boolean> res = cm.getMissionManagerCf().getAutoGoToDefaultBackLandPointFun();
            if (res.isSuccess()) {
                return RestRes.ok("enable", res.getRes());
            }
            return RestRes.err("查询错误," + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIRCRAFT_NEST_IS_OFFLINE_THE_QUERY_CANNOT_BE_FOUND.getContent()));
    }

    @Override
    public RestRes immediatelyGoToDefaultBackLandPoint(String nestId, Double alt) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getMissionManagerCf().immediatelyGoToDefaultBackLandPoint(alt);
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_IMMEDIATELY_RESERVE_POINT_AND_EXECUTE_SUCCESSFULLY.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_GO_TO_THE_LANDING_SITE_IMMEDIATELY.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes immediatelyGoToGoToDesignBackLandPoint(BackLandFunDto backLandFunDto) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(backLandFunDto.getNestId());
        if (cm != null) {
            Coordinate coordinate = new Coordinate();
            coordinate.setAltitude(backLandFunDto.getGotoBackLandPointAlt());
            coordinate.setLatitude(backLandFunDto.getBackLandPointLat());
            coordinate.setLongitude(backLandFunDto.getBackLandPointLng());
            MqttResult<NullParam> res = cm.getMissionManagerCf().immediatelyGoToGoToDesignBackLandPoint(coordinate);
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_IMMEDIATELY_DESIGNATED_LANDING_POINT_AND_EXECUTE_SUCCESSFULLY.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_EXECUTE_TO_THE_DESIGNATED_LANDING_POINT_IMMEDIATELY.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public Integer getMaintenanceStateCache(String nestUuid) {
        //如果cps状态是不是最终状态，就代表其在更新
        Integer maintenanceState = 0;
        Boolean installing = commonNestStateService.isCpsInstalling(nestUuid);
        if (installing) {
            maintenanceState = 2;
        } else {
            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.NEST_MAINTENANCE_STATE, nestUuid);
            maintenanceState = (Integer) redisService.get(redisKey);
            if (maintenanceState == null) {
                if (nestUuid != null) {
                    NestEntity nestEntity = this.lambdaQuery().eq(NestEntity::getUuid, nestUuid).eq(NestEntity::getDeleted, 0).select(NestEntity::getMaintenanceState).one();

                    if (nestEntity != null) {
                        maintenanceState = nestEntity.getMaintenanceState();
                    } else {
                        maintenanceState = 0;
                    }

                    redisService.set(redisKey, maintenanceState);
                }
            }
        }
        return maintenanceState;
    }


    @Override
    public RestRes getBackLandPointInfo(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            Map<String, Object> res = new HashMap<>(4);
            MqttResult<Coordinate> res1 = cm.getMissionManagerCf().getDefaultBackLandPoint();
            if (res1.isSuccess()) {
                Coordinate coordinate = res1.getRes();
                res.put("backLandPointLng", coordinate.getLongitude());
                res.put("backLandPointLat", coordinate.getLatitude());
            }
            MqttResult<Double> res2 = cm.getMissionManagerCf().getAutoGoToDefaultBackLandPointAlt();
            if (res2.isSuccess()) {
                res.put("gotoBackLandPointAlt", res2.getRes());
            }
            if (CollectionUtil.isNotEmpty(res)) {
                return RestRes.ok(res);
            } else {
                return RestRes.noDataWarn();
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public List<NestDto> listNestByOrgCode(String orgCode) {
        return getBaseMapper().listNestByOrgCode(orgCode);
    }

    @Override
    public RestRes controlGimbal(ControlGimbalDto controlGimbalDto) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(controlGimbalDto.getNestId());
        if (cm != null) {
            Boolean pitch = controlGimbalDto.getPitch();
            Boolean yam = controlGimbalDto.getYam();
            MqttResult<NullParam> res = cm.getCameraManagerCf().setCameraAngle(pitch ? controlGimbalDto.getPitchAngle() : null, yam ? controlGimbalDto.getYamAngle() : null);
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MODIFY_HEAD_ANGLE_SUCCESS.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MODIFY_GIMBAL_ANGLE_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes getGbFlv(Integer nestId) {
        NestEntity nestEntity = this.lambdaQuery().eq(NestEntity::getId, nestId)
                .eq(NestEntity::getDeleted, false)
                .select(NestEntity::getInnerVideoId,
                        NestEntity::getOutVideoId,
                        NestEntity::getUseGb,
                        NestEntity::getOutVideoUrl,
                        NestEntity::getInnerVideoUrl
                )
                .one();
        if (nestEntity == null) {
            return RestRes.warn(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BASE_STATION_NOT_QUERIED.getContent()));
        }
        Map<String, Object> res = new HashMap<>(2);
        if (nestEntity.getUseGb()) {
            res.put("useGb", true);
            String innerVideoId = nestEntity.getInnerVideoId();
            String outVideoId = nestEntity.getOutVideoId();
            if (StrUtil.isNotEmpty(innerVideoId)) {
                String innerGbFlv = TxStreamUtil.getGbFlv(innerVideoId);
                res.put("innerGbFlv", innerGbFlv);
            } else {
                res.put("innerGbFlv", nestEntity.getInnerVideoUrl());
            }

            if (StrUtil.isNotEmpty(outVideoId)) {
                String outerGbFlv = TxStreamUtil.getGbFlv(outVideoId);
                res.put("outerGbFlv", outerGbFlv);
            } else {
                res.put("outerGbFlv", nestEntity.getOutVideoUrl());
            }

            return RestRes.ok(res);
        } else {
            res.put("useGb", false);
            return RestRes.ok(res);
        }

    }

    @Override
    public RestRes getSdCardAvailableCaptureCount(String nestId,Integer which) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MediaManagerCf mediaManagerCf = cm.getMediaManagerCf();
            MqttResult<AvailableCaptureCountEntity> res = mediaManagerCf.getSdCardAvailableCaptureCount(AirIndexEnum.getInstance(which));
            if (res.isSuccess()) {
                return RestRes.ok("count", res.getRes().getAvailableCaptureCount());
            }
            return RestRes.err(res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes getSdCardAvailableRecordTimes(String nestId,Integer which) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MediaManagerCf mediaManagerCf = cm.getMediaManagerCf();
            MqttResult<AvailableRecordTimeEntity> res = mediaManagerCf.getSdCardAvailableRecordTimes(AirIndexEnum.getInstance(which));
            if (res.isSuccess()) {
                AvailableRecordTimeEntity res1 = res.getRes();
                return RestRes.ok("times", res1.getAvailableRecordingTimeInSeconds());
            }
            return RestRes.err(res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes resetMqttClient(Integer nestId) {
//        NestEntity nest = this.lambdaQuery().eq(NestEntity::getId, nestId)
//                .eq(NestEntity::getDeleted, false)
//                .one();
//        if (nest != null) {
//            List<String> mqttServerUrlList = sysWhiteListService.listMqttBrokerWhiteListsCache();
//            if (CollectionUtil.isNotEmpty(mqttServerUrlList) && mqttServerUrlList.contains(nest.getServerUrl())) {
//                ComponentManagerFactory.destroy(nest.getUuid());
//                MqttOptions options = MqttOptions.instance()
//                        .serverUri(nest.getServerUrl())
//                        .username(nest.getUsername())
//                        .password(nest.getPassword())
//                        .clientId(String.valueOf(System.currentTimeMillis()))
//                        .nestType(nest.getType());
//                ComponentManagerFactory.getInstance(options, nest.getUuid());
//                CommonNestStateFactory.initCommonNestState(nest.getUuid(),nest.getType());
//                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_RESET.getContent()));
//            }
//            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MQTT_SERVICE_ADDRESS_IS_INCORRECT_PLEASE_CHECK_THE_MQTT_SERVICE_ADDRESS.getContent()));
//        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CORRESPONDING_MACHINE_NEST_IS_NOT_AVAILABLE.getContent()));
    }

    private RestRes getResult(CompletableFuture<Boolean> future, CompletableFuture<String> msg, String result) {
        try {
            if (future.get(5, TimeUnit.SECONDS)) {
                return RestRes.ok(result + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS.getContent()));
            } else {
                return RestRes.err(msg.get(5, TimeUnit.SECONDS));
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return RestRes.err(result + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILURE.getContent()));
        }
    }

    private void clearNestListRedisCache() {
        //由于超级管理员可以修改所有的基站信息，因此只能在推出的时候去掉缓存
        String pattern = "NESTSERVICEIMPL:LISTNESTANDREGION:NESTLIST:*";
        Set<String> keys = redisService.keys(pattern);
        redisService.del(keys);
    }

    private void clearRedisCache(Integer nestId, String account, Boolean isAdd) {
        //在查询机巢列表的时候将列表缓存了，因此在这里要清空redis缓存
        clearNestListRedisCache();
        String nestUuid = this.getUuidById(nestId);
        redisService.hSet(RedisKeyConstantList.NEST_UUID_TYPE_MAP_KEY, nestUuid, null);
        redisService.hSet(RedisKeyConstantList.NEST_UUID_NAME_MAP_KEY, nestUuid, null);
        redisService.del(RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_AIRCRAFT_LIST_KEY, account));
        redisService.hSet(RedisKeyConstantList.NEST_UUID_AIR_CODE_MAP_KEY, nestUuid, null);
        redisService.del(RedisKeyConstantList.NEST_ALT_CACHE);
        if (isAdd) {
            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.ACCOUNT_MAP_NEST_ID_LIST_KEY, account);
            redisService.del(redisKey);
            String allNestUuidRedisKey = RedisKeyConstantList.ALL_NEST_UUID;
            redisService.del(allNestUuidRedisKey);
        }
    }

    private boolean isStandardFlowUrl(String flowUrl) {
        return StrUtil.isNotEmpty(flowUrl) && (flowUrl.startsWith("http") || flowUrl.startsWith("rtmp"));
    }


//    private ComponentManager getComponentManager(int nestId) {
//        //FIXME：需要修改查询基站UUID
//        NestEntity nest = this.getNestByIdIsCache(nestId);
//        if (nest != null) {
//            return ComponentManagerFactory.getInstance(nest.getUuid());
//        }
//        return null;
//    }

    private String getCpsVersionCache(String redisKey, ComponentManager cm) {
        String cpsVersion = (String) redisService.hGet(redisKey, "cpsVersion");
        if (cpsVersion == null) {
            if (cm != null) {
                CompletableFuture<String> cf = new CompletableFuture<>();
                cm.getSystemManager().getCpsVersion((result, isSuccess, errMsg) -> {
                    if (isSuccess) {
                        cf.complete(result);
                    }
                });
                try {
                    cpsVersion = cf.get(10, TimeUnit.SECONDS);
                    if (cpsVersion != null) {
                        redisService.hSet(redisKey, "cpsVersion", cpsVersion, 3600);
                        return cpsVersion;
                    }
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    return "";
                }
            }
            return "";
        }
        return cpsVersion;
    }

    private String getMpsVersionCache(String redisKey, ComponentManager cm) {
        String mpsVersion = (String) redisService.hGet(redisKey, "mpsVersion");
        if (mpsVersion == null) {
            if (cm != null) {
                CompletableFuture<String> cf = new CompletableFuture<>();
                cm.getSystemManager().getMpsVersion((result, isSuccess, errMsg) -> {
                    if (isSuccess) {
                        cf.complete(result);
                    }
                });
                try {
                    mpsVersion = cf.get(10, TimeUnit.SECONDS);
                    if (mpsVersion != null) {
                        redisService.hSet(redisKey, "mpsVersion", mpsVersion, 3600);
                        return mpsVersion;
                    }
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    return "";
                }
            }
            return "";
        }

        return mpsVersion;
    }

    private List<AircraftComponent> listComponentSerialNumberCahce(String redisKey, ComponentManager cm) {
        List<AircraftComponent> aircraftComponents = (List<AircraftComponent>) redisService.hGet(redisKey, "aircraftComponents");
        if (CollectionUtil.isEmpty(aircraftComponents)) {
            if (cm != null) {
                CompletableFuture<List<AircraftComponent>> cf = new CompletableFuture<>();
                MqttResult<AircraftComponent> res = cm.getGeneralManagerCf().listComponentSerialNumber();
                if (res.isSuccess()) {
                    aircraftComponents = res.getResList();
                    if (CollectionUtil.isNotEmpty(aircraftComponents)) {
                        redisService.hSet(redisKey, "aircraftComponents", aircraftComponents, 3600);
                        return aircraftComponents;
                    }
                }
            }
            return Collections.emptyList();
        }
        return aircraftComponents;
    }


    @Override
    public RestRes setBackLandPointInfo(BackLandFunDto backLandFunDto) {
        String nestId = backLandFunDto.getNestId();
        Double backLandPointLat = backLandFunDto.getBackLandPointLat();
        Double backLandPointLng = backLandFunDto.getBackLandPointLng();
        Double goToBackLandPointAlt = backLandFunDto.getGotoBackLandPointAlt();
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MissionManager missionManager = cm.getMissionManager();
            CompletableFuture<Boolean> cf1 = new CompletableFuture<>();
            missionManager.setDefaultBackLandPoint(backLandPointLat, backLandPointLng, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    cf1.complete(result);
                } else {
                    cf1.complete(false);
                }
            });

            CompletableFuture<Boolean> cf2 = new CompletableFuture<>();
            missionManager.setAutoGoToDefaultBackLandPointAlt(goToBackLandPointAlt, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    cf2.complete(result);
                } else {
                    cf2.complete(false);
                }
            });

            try {
                if (cf1.get(10, TimeUnit.SECONDS) && cf2.get(10, TimeUnit.SECONDS)) {
                    log.info("默认备降点坐标、前往备降点高度设置成功");
                    //return RestRes.ok("默认备降点坐标、前往备降点高度设置失败");
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DEFAULT_LANDING_POINT_COORDINATES_AND_ALTITUDE_TO_LANDING_POINT_FAILED_TO_BE_SET.getContent()));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error("默认备降点坐标、前往备降点高度设置失败");
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DEFAULT_LANDING_POINT_COORDINATES_AND_ALTITUDE_TO_LANDING_POINT_FAILED_TO_BE_SET.getContent()));
    }

    @Override
    public Double getCpsMemoryUseRate(String nestUuid, AirIndexEnum airIndexEnum) {

        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
            MqttResult<Long> res = generalManagerCf.getCpsMemoryRemainSpace();
            if (res.isSuccess()) {
                /**
                 * 单位字节
                 */
                Long storageRemainSpace = res.getRes();
                Long storageTotalSpace = 16 * 1024 * 1024 * 1024L;
                double useRate = (double) (storageTotalSpace - storageRemainSpace) / storageTotalSpace;
                BigDecimal bd = new BigDecimal(useRate);
                double percentage = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                return percentage;
            }
        }
        return -1.0;
    }

    @Override
    public Double getSdMemoryUseRate(String nestUuid, AirIndexEnum airIndexEnum) {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            MediaManagerCf mediaManagerCf = cm.getMediaManagerCf();
            MqttResult<SdCardTotalSpaceEntity> totalRes = mediaManagerCf.getSdCardTotalSpace();
            MqttResult<SdCardRemainSpaceEntity> remainRes = mediaManagerCf.getSdCardRemainSpace();
            if (totalRes.isSuccess() && remainRes.isSuccess()) {
                SdCardTotalSpaceEntity res1 = totalRes.getRes();
                SdCardRemainSpaceEntity res2 = remainRes.getRes();
                Long totalSpaceInMB = res1.getTotalSpaceInMB();
                Long remainingSpaceInMB = res2.getRemainingSpaceInMB();
                if (totalSpaceInMB == 0) {
                    return -1.0;
                }
                double useRate = (double) (totalSpaceInMB - remainingSpaceInMB) / totalSpaceInMB;
                BigDecimal bd = new BigDecimal(useRate);
                double percentage = bd.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                return percentage;
            }
        }
        return -1.0;
    }

    @Override
    public RestRes oneKeyGoHomeCheck(String nestId) {
//        NestEntity nestEntity = this.lambdaQuery()
//                .eq(NestEntity::getId, nestId)
//                .eq(NestEntity::getDeleted, false)
//                .select(NestEntity::getType, NestEntity::getUuid)
//                .one();
//        if (nestEntity == null) {
//            return RestRes.err("机巢不存在");
//        }
//        ComponentManager cm = ComponentManagerFactory.getInstance(nestEntity.getUuid());
        BaseNestInfoOutDTO baseNestInfo = baseNestService.getBaseNestInfo(nestId);
        if (Objects.isNull(baseNestInfo)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_NEST_DOES_NOT_EXIST.getContent()));
        }
        ComponentManager cm = ComponentManagerFactory.getInstance(baseNestInfo.getUuid());
        if (cm == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }

        NestBaseStateDto nestBaseStateDto = commonNestStateService.getNestBaseStateDto(baseNestInfo.getUuid(), baseNestInfo.getType());
        Map<String, Object> data = new HashMap<>(4);
        data.put("nestType", baseNestInfo.getType());
        data.put("cabin", nestBaseStateDto.getCabin());
        data.put("lift", nestBaseStateDto.getLift());
        data.put("square", nestBaseStateDto.getSquare());
        return RestRes.ok(data).msg(nestBaseStateDto.getTips());
    }

    @Override
    public RestRes oneKeyGoHome(String nestId) {
//        String uuid = this.getUuidById(nestId);
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        AircraftState aircraftState = commonNestStateService.getAircraftState(nestUuid);
        if (aircraftState.getAreMotorsOn()) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIRCRAFT_IS_STILL_ROTATING_THE_PROPELLER_CANT_USE_ONE_KEY_RETURN_FUNCTION.getContent()));
        }
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        RcManagerCf rcManagerCf = cm.getRcManagerCf();
        MqttResult<ElseWhereParam> res = rcManagerCf.elseWhereGoHome(null);
        if (res.isSuccess()) {
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_KEY_TO_RETURN_TO_THE_NEST_IS_STARTED_SUCCESSFULLY_THE_DRONE_WILL_FLY_TO.getContent()) + "{'homeLng':" + res.getRes().getHomeLng() + ",'homeLat':" + res.getRes().getHomeLat() + "}点");
        }
        return RestRes.err(res.getMsg());
    }

    @Override
    public RestRes oneKeyOpen(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getMotorManagerCf().oneKeyOpen();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_CLICK_TO_OPEN_SUCCESSFULLY.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_KEY_START_FAILED.getContent()) + "," + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_KEY_OPEN_FAILED_NEST_IS_OFFLINE.getContent()));
    }

    @Override
    public RestRes oneKeyRecycle(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getMotorManagerCf().oneKeyRecovery();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_CLICK_RECYCLING_SUCCESS.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_CLICK_RECYCLING_FAILURE.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_CLICK_RECYCLING_FAILURE.getContent()));
    }

    @Override
    public RestRes oneKeyReset(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getMotorManagerCf().oneKeyReset();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_CLICK_RESET_SUCCESS.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_CLICK_RESET_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONE_KEY_RESET_FAILED_NEST_IS_OFFLINE.getContent()));
    }

    @Override
    public RestRes batteryLoad(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getMotorManagerCf().loadBattery(null);
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATTERY_LOADING_SUCCESS.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATTERY_LOADING_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATTERY_LOADING_FAILURE.getContent()));
    }

    @Override
    public RestRes batteryUnload(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getMotorManagerCf().unLoadBattery();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATTERY_UNLOADING_SUCCESS.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATTERY_UNLOADING_FAILURE.getContent())
                    + "," + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATTERY_UNLOAD_FAILED_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes openCabin(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getMotorManagerCf().openCabin();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESSFUL_HATCH_OPENING.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_HATCH_OPENING_FAILURE.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_HATCH_OPEN_FAILED_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes closeCabin(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getMotorManagerCf().closeCabin();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_HATCH_CLOSING_SUCCESS.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_HATCH_CLOSING_FAILED.getContent()) + "," + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_HATCH_CLOSING_FAILED_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes riseLift(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getMotorManagerCf().riseLift();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RAISE_PLATFORM_SUCCESS.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RAISING_PLATFORM_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes downLift(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getMotorManagerCf().downLift();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_LANDING_PLATFORM_SUCCESS.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_LANDING_PLATFORM_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes tightSquare(MotorManagerCf.SquareEnum squareEnum, String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getMotorManagerCf().tightSquare(squareEnum);
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CLAMPING_CENTERING_SUCCESS.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CLAMPING_CENTERING_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes looseSquare(MotorManagerCf.SquareEnum squareEnum, String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getMotorManagerCf().looseSquare(squareEnum);
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_LOOSE_CENTERING_SUCCESS.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_UNCLAMPING_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes rtkReconnect(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getRtkManagerCf().restartRtk();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RTK_RECONNECT_SUCCESSFUL.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RTK_RECONNECT_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RTK_RECONNECTION_FAILED_THE_NEST_IS_OFFLINE.getContent()));
    }

    @Override
    public RestRes rcSwitch(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getRcManagerCf().controllerSwitchMode();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_REMOTE_CONTROL_CUT_BLOCK_SUCCESSFULLY.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_REMOTE_CONTROL_CUT_BLOCK_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_REMOTE_CONTROL_CUT_BLOCK_FAILED.getContent()));
    }

    @Override
    public RestRes rcOnOff(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getRcManagerCf().controllerRcMachineOnOff();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SWITCHING_REMOTE_CONTROL_SUCCEEDED.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SWITCHING_REMOTE_CONTROL_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SWITCHING_REMOTE_CONTROL_FAILED_THE_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes restartPower(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getPowerManagerCf().restartNestPower();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TOTAL_POWER_REBOOT_SUCCESS.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TOTAL_POWER_RESTART_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TOTAL_POWER_RESTART_FAILEDNEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes startReturnToHome(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getRcManagerCf().startRth();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_START_RETURN_SUCCESSFUL.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_RETURN_TO_FLIGHT.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_START_RETURN_FAILED_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes aircraftRePush(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getGeneralManagerCf().reRtmpPush();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RE_STREAMING_SUCCESS.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RE_PUSH_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RE_PUSH_FAILEDNEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes getAircraftPushUrl(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<String> res = cm.getGeneralManagerCf().getRtmpUrl();
            if (res.isSuccess()) {
                return RestRes.ok("pushUrl", res.getRes());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_GET_THE_PUSH_ADDRESS.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NEST_CONNECTION_ABNORMAL.getContent()));
    }

    @Override
    public RestRes setGimbalAngle(SetGimbalAngleDTO dto) {
        Integer angle = dto.getAngle();
        String nestId = dto.getNestId();
        if (angle != null && angle > -90 && angle < 30) {
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                MqttResult<NullParam> res = cm.getCameraManagerCf().setGimbalYawAngle(angle);
                if (res.isSuccess()) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SET_GIMBAL_ANGLE_SUCCESS.getContent()));
                }
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SET_GIMBAL_ANGLE.getContent()) + "," + res.getMsg());
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SET_GIMBAL_ANGLE.getContent()));
    }

    @Override
    public RestRes s100AircraftChargeOn(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getPowerManagerCf().aircraftChargeOn();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIRCRAFT_CHARGING_SUCCESS.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIRCRAFT_CHARGING_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_AIRCRAFT_CHARGING_FAILED_THE_NEST_IS_OFFLINE.getContent()));
    }

    @Override
    public RestRes s100AircraftChargeOff(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getPowerManagerCf().aircraftChargeOff();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIRCRAFT_POWER_OFF_SUCCESS.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIRCRAFT_POWER_OFF_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIRCRAFT_POWER_FAILURENEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes miniCamerasThermalMode(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getCameraManagerCf().setCameraFpvMode(CameraFpvModeEnum.THERMAL_ONLY);
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SWITCHING_TO_INFRARED_LIGHT_MODE_SUCCEEDED.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SWITCH_TO_IR_MODE.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SWITCHING_INFRARED_LIGHT_MODE_FAILED_THE_NEST_IS_OFFLINE.getContent()));
    }

    @Override
    public RestRes miniCamerasVisibleMode(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getCameraManagerCf().setCameraFpvMode(CameraFpvModeEnum.VISUAL_ONLY);
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SWITCHING_VISIBLE_LIGHT_MODE_SUCCESSFULLY.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SWITCH_TO_VISIBLE_MODE.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SWITCHING_VISIBLE_MODE_FAILEDNEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes miniCameraMsxMode(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getCameraManagerCf().setCameraFpvMode(CameraFpvModeEnum.MSX);
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SWITCHING_DUAL_LIGHT_MODE_SUCCESSFULLY.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SWITCH_TO_DUAL_LIGHT_MODE.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SWITCH_TO_DUAL_LIGHT_MODE_THE_NEST_IS_OFFLINE.getContent()));
    }

    @Override
    public RestRes onAirConditioned(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getAcManagerCf().openAirCondition();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TURN_ON_AIR_CONDITIONER_SUCCESSFULLY.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIR_CONDITIONER_FAILED_TO_TURN_ON.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_AIR_CONDITIONER_FAILED_TO_TURN_ONTHE_NEST_IS_OFFLINE.getContent()));
    }

    @Override
    public RestRes offAirConditioned(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getAcManagerCf().closeAirCondition();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIR_CONDITIONING_OFF_SUCCESSFULLY.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIR_CONDITIONER_SHUTDOWN_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AIR_CONDITIONER_SHUTDOWN_FAILEDNEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes closeSteerPower(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getPowerManagerCf().closeSteerPower();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWER_OFF_THE_DRIVE_SUCCESSFULLY.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWER_OFF_THE_DRIVE_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes openSteerPower(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getPowerManagerCf().openSteerPower();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TURN_ON_DRIVE_POWER_SUCCESSFULLY.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWER_ON_DRIVE_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes mpsReset(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            PowerManagerCf powerManagerCf = cm.getPowerManagerCf();
            powerManagerCf.mpsSoftRestart();
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MPS_RESET_COMMAND_ISSUED_SUCCESSFULLY.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes cpsReset(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            PowerManagerCf powerManagerCf = cm.getPowerManagerCf();
            MqttResult<NullParam> res = powerManagerCf.cpsSoftRestart();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CPS_RESET_SUCCESSFUL.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CPS_RESET_FAILED.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes androidBoardsRestart(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            PowerManagerCf powerManagerCf = cm.getPowerManagerCf();
            MqttResult<NullParam> res = powerManagerCf.androidBoardsRestart();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CPS_ZK_RESET_SUCCESSFUL.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CPS_REBOOT_FAILED.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes formatCpsMemoryCard(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
//            GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
//            MqttResult<NullParam> res = generalManagerCf.formatCpsMemory();
            MediaManagerCf mediaManagerCf = cm.getMediaManagerCf();
            mediaManagerCf.deleteNestAllMediaFile();
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FORMATTING_CPS_COMMANDS_TO_ISSUE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes formatAirSdCard(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MediaManagerCf mediaManagerCf = cm.getMediaManagerCf();
            mediaManagerCf.formatAirStore(false);
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FORMAT_UAV_SD_CARD_COMMAND_ISSUED.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes stopStartUpProcess(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getMissionManagerCf().endStartUpProcess();
            //取消任务过程的监听
            cm.getMissionManager().removeListenMissionRunning(AirIndexEnum.DEFAULT);
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TERMINATE_TASK_START_PROCESS_SUCCESS.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TERMINATE_MISSION_START_PROCESS_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes stopFinishProcess(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getMissionManagerCf().endFinishProcess();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TERMINATE_MISSION_END_PROCESS_SUCCESSFULLY.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TERMINATE_MISSION_END_PROCESS_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes stopAllProcess(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getMissionManagerCf().endAllProcess();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TERMINATE_TASK_ALL_PROCESSES_SUCCESSFULLY.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TERMINATE_ALL_PROCESSES_OF_THE_TASK_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

//    @Override
//    public RestRes shutBoot(Integer nestId) {
//        ComponentManager cm = getComponentManager(nestId);
//        if (cm != null) {
//            MqttResult<NullParam> res = cm.getG600MotorManagerCf().controlBoot(G600MotorManagerCf.BootActionEnum.CLOSE);
//            if (res.isSuccess()) {
//                return RestRes.ok("关闭引导成功");
//            }
//            return RestRes.err("关闭引导失败," + res.getMsg());
//        }
//        return RestRes.err("关闭引导失败");
//    }

    @Override
    public RestRes onAircraftS100V2(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getPowerManagerCf().onMiniV2Aircraft();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TURN_ON_DRONE_SUCCESSFULLY.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_OPEN_THE_DRONE.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes offAircraftS100V2(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getPowerManagerCf().offMiniV2Aircraft();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SHUTTING_DOWN_THE_DRONE_SUCCEEDED.getContent()));
            }
            // return RestRes.err("关闭人机失败," + res.getMsg());
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SHUTTING_DOWN_THE_DRONE_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes m300ExchangeBattery(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getG900MotorManagerCf().exchangeBattery(null);
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CHANGING_THE_BATTERY_SUCCEEDED.getContent()));
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BATTERY_REPLACEMENT_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes resetLift(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getG900MotorManagerCf().resetLift();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RESET_PLATFORM_SUCCESS.getContent()));
            }
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RESET_PLATFORM_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes m300SwitchCamera(String nestId, Integer model) {
        if (nestId != null && model != null) {
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                CameraFpvModeEnum instance = CameraFpvModeEnum.getInstance(model);
                if (instance != null) {
                    MqttResult<NullParam> res = cm.getCameraManagerCf().setCameraFpvMode(instance);
                    if (res.isSuccess()) {
                        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SWITCHING_LENSES_SUCCESSFUL.getContent()));
                    }
                    return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SWITCHING_LENS_FAILED.getContent()) + res.getMsg());
                }
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes startPhotograph(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getCameraManagerCf().startPhotograph();
            if (res.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TAKE_A_PICTURE_SUCCESSFULLY.getContent())
                );
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_TAKE_A_PICTURE.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes getCpsVersion(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<String> res = cm.getGeneralManagerCf().getCpsVersion();
            if (res.isSuccess()) {
                return RestRes.ok("cpsVersion", res.getRes());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CPS_VERSION_QUERY_FAILED.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes startCompassSet(String nestId, Boolean active) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<NullParam> res = cm.getGeneralManagerCf().setCompass(active);
            if (res.isSuccess()) {
                return RestRes.ok(active ? MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TURN_ON_SUCCESS.getContent()) : MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TURN_OFF_SUCCESS.getContent()));
            }
            return RestRes.err(active ? MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_OPEN.getContent()) : MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_TURN_OFF.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes getNestErrMsg(String nestId,Integer which) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<String> res = cm.getSystemManagerCf().getMpsError(AirIndexEnum.getInstance(which));
            if (res.isSuccess()) {
                return RestRes.ok("errorInfo", res.getRes());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_GET.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes rcPair(String nestId) {
        if (nestId != null) {
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                MqttResult<NullParam> res = cm.getRcManagerCf().rcPair();
                if (res.isSuccess()) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_REMOTE_CONTROL_FREQUENCY_PAIRING_SUCCESS.getContent()));
                }
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_REMOTE_CONTROL_FREQUENCY_PAIRING_FAILED.getContent()) + res.getMsg());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @Override
    public RestRes calibrationCompass(Boolean enable, String nestId) {
        if (nestId != null && enable != null) {
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                MqttResult<NullParam> res = cm.getGeneralManagerCf().setCompass(enable);
                if (res.isSuccess()) {
                    return RestRes.ok(enable ? MessageUtils.getMessage(MessageEnum.GEOAI_UOS_START.getContent())
                            : MessageUtils.getMessage(MessageEnum.GEOAI_UOS_STOP.getContent()) + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_START_MAGNETIC_COMPASS_CALIBRATION_SUCCESSFUL.getContent())
                    );
                }
                return RestRes.err(res.getMsg());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @Override
    public RestRes loginDjiAccount(Map<String, Object> param) {
        if (param != null) {
            String username = (String) param.get("username");
            String password = (String) param.get("password");
            String nestId = (String) param.get("nestId");
            if (username != null && password != null && nestId != null) {
                ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
                if (cm != null) {
                    MqttResult<NullParam> res = cm.getGeneralManagerCf().loginDjiAccount(username, password);
                    if (res.isSuccess()) {
                        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DJI_ACCOUNT_LOGIN_SUCCESS.getContent()));
                    }
                    return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DJI_ACCOUNT_LOGIN_FAILED.getContent()) + res.getMsg());
                }
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @Override
    public RestRes getDjiLoginStatus(String nestId) {
        if (nestId != null) {
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                MqttResult<Boolean> res = cm.getGeneralManagerCf().getDjiLoginStatus();
                if (res.isSuccess()) {
                    return RestRes.ok("loginStatus", res.getRes());
                }
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_GET.getContent()) + res.getMsg());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @Override
    public RestRes detectionNetworkState(DetectionNetworkDTO detectionNetworkDto) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(detectionNetworkDto.getNestId());
        if (cm != null) {
            MqttResult<NestNetworkStateEntity> res = cm.getGeneralManagerCf().detectionNetworkState(detectionNetworkDto.getPingCount(),
                    detectionNetworkDto.getPingSize(),
                    detectionNetworkDto.getSpeed());
            if (res.isSuccess()) {
                return RestRes.ok("netState", res.getRes());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_GET_THE_NETWORK_STATUS_OF_THE_NEST.getContent()) + res.getMsg());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
    }

    @Override
    public RestRes clearDjiCacheFile(String nestId) {
        if (nestId != null) {
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                SystemManagerCf systemManagerCf = cm.getSystemManagerCf();
                MqttResult<NullParam> res = systemManagerCf.clearDjiCacheFile();
                if (res.isSuccess()) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CLEANUP_SUCCESSFUL.getContent()));
                }
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CLEANUP_FAILED.getContent()) + res.getMsg());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @Override
    public RestRes getWaypointList(String nestId, String missionId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm != null) {
            MqttResult<Waypoint> res = cm.getMissionManagerCf().listWaypointByMissionId(missionId);
            return RestRes.ok("res", res);
        }
        return RestRes.ok();
    }

    @Override
    public RestRes mpsSystemSelfCheck(String nestId) {
        if (nestId != null) {
            ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
            if (cm != null) {
                G600MotorManagerCf g600MotorManagerCf = cm.getG600MotorManagerCf();
                MqttResult<NullParam> result = g600MotorManagerCf.systemSelfCheck();
                if (result.isSuccess()) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SYSTEM_SELF_TEST_SUCCESS.getContent()));
                }
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SYSTEM_SELF_TEST_FAILED.getContent()) + result.getMsg());
            }
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
    }

    @Override
    public RestRes getCpsAndSdMemoryUseRate(String nestId,Integer which) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        if (nestUuid == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CORRESPONDING_MACHINE_NEST_IS_NOT_AVAILABLE.getContent()));
        }
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        Map<String, Double> resMap = commonNestStateService.getCpsAndSdCardSpaceUseRate(nestUuid,AirIndexEnum.getInstance(which));

        Map<String, Object> res = new HashMap<>(2);
        res.put("cpsMemoryUseRate", resMap.get("systemSpaceUseRate"));
        res.put("sdMemoryUseRate", resMap.get("sdSpaceUseRate"));
        return RestRes.ok(res);
    }

    @Override
    public RestRes originalRoadGoHome(String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        RcManagerCf rcManagerCf = cm.getRcManagerCf();
        MqttResult<NullParam> res = rcManagerCf.originalRoadGoHome();
        if (res.isSuccess()) {
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ORIGINAL_RETURN_TRIGGER_SUCCESSFUL.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILURE_OF_RETURN_TRIGGER.getContent()) + res.getMsg());
    }

    @Override
    public RestRes forceLand(String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        RcManagerCf rcManagerCf = cm.getRcManagerCf();
        MqttResult<NullParam> res = rcManagerCf.forceLand();
        if (res.isSuccess()) {
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FORCED_LANDING_TRIGGER_SUCCESSFUL.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FORCED_LANDING_TRIGGER_FAILED.getContent()) + res.getMsg());
    }

    /**
     * 自动降落
     *
     * @author sjx
     * @Date: 2022/3/22-15:42
     **/
    @Override
    public RestRes autoLand(String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        RcManagerCf rcManagerCf = cm.getRcManagerCf();
        MqttResult<NullParam> res = rcManagerCf.autoLand();
        if (res.isSuccess()) {
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AUTOMATIC_LANDING_TRIGGER_SUCCESSFUL.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_AUTO_LANDING_TRIGGER_FAILED.getContent()) + res.getMsg());
    }

    /**
     * 设置相机热红外模式的颜色
     *
     * @author sjx
     * @Date: 2022/3/22-16:42
     **/
    @Override
    public RestRes setCameraInfraredColor(String nestId, InfraredColorEnum infraredColor) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        MqttResult<NullParam> res = cm.getCameraManagerCf().setCameraInfraredColor(infraredColor);
        if (res.isSuccess()) {
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THERMAL_INFRARED_MODE_COLOR_SETTING_SUCCESSFUL.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THERMAL_INFRARED_MODE_COLOR_SETTING_ERROR.getContent()) + res.getMsg());
    }

    @Override
    public RestRes againLand(String nestId) {
        if (nestId == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        RcManagerCf rcManagerCf = cm.getRcManagerCf();
        MqttResult<NullParam> result = rcManagerCf.againLand(null);
        if (result.isSuccess()) {
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RE_LANDING_TRIGGER_SUCCESS.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RE_LANDING_TRIGGER_FAILED.getContent())
                + "，" + result.getMsg());
    }

    @Override
    public RestRes updatePushStreamMode(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        NestTypeEnum nestTypeEnum = baseNestService.getNestTypeByNestIdCache(nestId);
        if(NestTypeEnum.G503.equals(nestTypeEnum)){
            GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
            MqttResult<PushStreamMode> pushStreamMode1 = generalManagerCf.getPushStreamMode(AirIndexEnum.ONE);
            if (pushStreamMode1.isSuccess()) {
                commonNestStateService.setPushStreamMode(cm.getNestUuid(), pushStreamMode1.getRes(),AirIndexEnum.ONE);
            }
            MqttResult<PushStreamMode> pushStreamMode2 = generalManagerCf.getPushStreamMode(AirIndexEnum.TWO);
            if (pushStreamMode2.isSuccess()) {
                commonNestStateService.setPushStreamMode(cm.getNestUuid(), pushStreamMode2.getRes(),AirIndexEnum.TWO);
            }
            MqttResult<PushStreamMode> pushStreamMode3 = generalManagerCf.getPushStreamMode(AirIndexEnum.THREE);
            if (pushStreamMode3.isSuccess()) {
                commonNestStateService.setPushStreamMode(cm.getNestUuid(), pushStreamMode3.getRes(),AirIndexEnum.THREE);
            }
            return RestRes.ok("更新成功");
        }
        GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
        MqttResult<PushStreamMode> pushStreamMode = generalManagerCf.getPushStreamMode();
        if (pushStreamMode.isSuccess()) {
            boolean updateRes = commonNestStateService.setPushStreamMode(cm.getNestUuid(), pushStreamMode.getRes());
            if (updateRes) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_UPDATE_SUCCESSFUL.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_UPDATE_FAILED.getContent()));
    }

    @Override
    public RestRes resetPushStream(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
        MqttResult<PushStreamMode> res = generalManagerCf.getPushStreamMode();
        if (res.isSuccess()) {
            PushStreamMode mode = res.getRes();
            if (PushStreamMode.SOFT_PUSH.equals(mode) || PushStreamMode.SELF_DEVELOPED_PUSH.equals(mode)) {
                MqttResult<NullParam> res1 = generalManagerCf.resetSoftPushStream();
                if (res1.isSuccess()) {
                    boolean updateRes = commonNestStateService.setPushStreamMode(cm.getNestUuid(), mode);
                    log.info("更新软件推流状态:{}", updateRes);
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RESET_SOFTWARE_PUSH_SUCCESSFUL.getContent()));
                }
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RESET_SOFTWARE_PUSH_FAILED.getContent()) + res1.getMsg());
            }

            if (PushStreamMode.HDMI_PUSH1.equals(mode) || PushStreamMode.HDMI_PUSH2.equals(mode) || PushStreamMode.HDMI_PUSH3.equals(mode)) {
                MqttResult<NullParam> res2 = generalManagerCf.reStartHardPushStream();
                if (res2.isSuccess()) {
                    boolean updateRes = commonNestStateService.setPushStreamMode(cm.getNestUuid(), mode);
                    log.info("更新硬件推流状态:{}", updateRes);
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RESTART_HARDWARE_PUSH_SUCCESS.getContent()));
                }
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RESTART_HARDWARE_PUSH_FAILED.getContent()) + res2.getMsg());
            }

            if (PushStreamMode.ICREST_PUSH.equals(mode)) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CLOUDCREST_DOES_NOT_SUPPORT_RESETTING_PUSH_STREAMS_AT_THIS_TIME.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_GET_THE_PUSH_METHOD.getContent()));
    }

    @Override
    public RestRes reconnectUsb(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        MqttResult<NullParam> mqttRes = cm.getSystemManagerCf().reconnectUsb();
        if (mqttRes.isSuccess()) {
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_USB_RECONNECT_SUCCESS.getContent()));
        }
        return RestRes.err(mqttRes.isTimeout() ? MessageUtils.getMessage(MessageEnum.GEOAI_UOS_USB_RECONNECT_TIMEOUT.getContent()) : MessageUtils.getMessage(MessageEnum.GEOAI_UOS_USB_RECONNECT_FAILED.getContent()) + "," + mqttRes.getMsg());
    }

    @Override
    public RestRes listCameraInfosFromCps(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        MqttResult<CameraInfo> mqttResult = cm.getGeneralManagerCf().listNestCameraInfos();
        if (mqttResult.isSuccess()) {
            return RestRes.ok("cameraInfos", mqttResult.getResList());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_GET.getContent()) + mqttResult.getMsg());
    }

    @Override
    public RestRes setCameraInfosToCps(SetCameraInfosDTO setCameraInfosDTO) {
        String nestId = setCameraInfosDTO.getNestId();
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (cm == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        PushStreamInfo pushStreamInfo = new PushStreamInfo();
        BeanUtils.copyProperties(setCameraInfosDTO, pushStreamInfo);
        pushStreamInfo.setUserName(setCameraInfosDTO.getUsername());
        MqttResult<NullParam> mqttRes = cm.getGeneralManagerCf().setPushStreamInfo(pushStreamInfo);
        if (mqttRes.isSuccess()) {
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PUSH_STREAM_INFORMATION_SETTING_SUCCESS.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PUSH_STREAM_INFORMATION_SETTING_FAILED.getContent()) + mqttRes.getMsg());
    }

    @Override
    public RestRes setAirPushStreamUrl(AirPushStreamDTO airPushStreamDTO) {
//        NestEntity nestEntity = this.lambdaQuery()
//                .eq(NestEntity::getId, airPushStreamDTO.getNestId())
//                .eq(NestEntity::getDeleted, false)
//                .select(NestEntity::getUuid, NestEntity::getName)
//                .one();
//        if (nestEntity == null) {
//            return RestRes.err("查询不到机巢信息");
//        }
//        ComponentManager cm = ComponentManagerFactory.getInstance(nestEntity.getUuid());
//        if (cm == null) {
//            return RestRes.err("机巢离线");
//        }
        ComponentManager cm = baseNestService.getComponentManagerByNestId(airPushStreamDTO.getNestId());
        if (cm == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MACHINE_NEST_OFFLINE.getContent()));
        }
        GeneralManagerCf generalManagerCf = cm.getGeneralManagerCf();
        MqttResult<NullParam> res = generalManagerCf.setRtmpUrl(airPushStreamDTO.getPushUrl());
        if (res.isSuccess()) {
            // 增加对nms转发的操作，校验rtmpUrl为腾讯云地址时，主动添加relay任务到nms
//            nodeMediaUtil.createOneRelayStream(airPushStreamDTO.getPushUrl(), nestEntity.getName());
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SET_PUSH_ADDRESS_SUCCESS.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SET_PUSH_ADDRESS.getContent()) + res.getMsg());
    }

    @Override
    public RestRes chargeDeviceTight(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (Objects.nonNull(cm)) {
            MqttResult<NullParam> mqttRes = cm.getMotorManagerCf().chargeDeviceTight();
            if(mqttRes.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CHARGE_DEVICE_TIGHT_SUCCESS.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CHARGE_DEVICE_TIGHT_FAILURE.getContent()));
    }

    @Override
    public RestRes chargeDeviceLoose(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (Objects.nonNull(cm)) {
            MqttResult<NullParam> mqttRes = cm.getMotorManagerCf().chargeDeviceTight();
            if(mqttRes.isSuccess()) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CHARGE_DEVICE_LOOSE_SUCCESS.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CHARGE_DEVICE_LOOSE_FAILURE.getContent()));
    }

    @Override
    public List<String> listNestUuidsCache() {
        return null;
    }

    @Override
    public NestTypeEnum getNestTypeCache(String nestUuid) {
//        String redisKey = RedisKeyConstantList.NEST_TYPE;
//        Integer nestType = (Integer) redisService.hGet(redisKey, nestUuid);
//        if (nestType == null) {
//            NestEntity nestEntity = this.lambdaQuery().eq(NestEntity::getUuid, nestUuid).eq(NestEntity::getDeleted, false).select(NestEntity::getType).one();
//
//            if (nestEntity != null) {
//                nestType = nestEntity.getType();
//            } else {
//                nestType = -1;
//            }
//            redisService.hSet(redisKey, nestUuid, nestType);
//        }

        return NestTypeEnum.UNKNOWN;
    }

    private RestRes saveOrUpdateNestDetails(NestDetailsDto nestDetailsDto) {
        if (nestDetailsDto == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ERROR_PARAMETER.getContent()));
        }
        // 是新增还是修改
        nestDetailsDto.setUuid(nestDetailsDto.getUuid().trim());
        NestEntity nestEntity = new NestEntity();
        nestEntity.setId(nestDetailsDto.getNestId());
        nestEntity.setCreatorId(nestDetailsDto.getCreateUserId());
        BeanUtils.copyProperties(nestDetailsDto, nestEntity);
        nestEntity.setModifyTime(LocalDateTime.now());

        NestEntity one = this.lambdaQuery()
                .eq(NestEntity::getUuid, nestDetailsDto.getUuid())
                .eq(NestEntity::getDeleted, false)
                .one();
        if ((one != null && !one.getId().equals(nestDetailsDto.getNestId()))) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_BASE_STATION_UUID.getContent())
                    + one.getUuid() + MessageUtils.getMessage(MessageEnum.GEOAI_UOS__ALREADY_EXISTS_IN_THE_SYSTEM_THE_NAME_IS.getContent())
                    + one.getName() + MessageUtils.getMessage(MessageEnum.GEOAI_UOS__CAN_NOT_BE_REPEATEDLY_ADDED.getContent())
            );
        }
        //添加机巢编号，不能重复
        NestEntity nest = this.lambdaQuery().eq(NestEntity::getNumber, nestDetailsDto.getNestNumber()).eq(NestEntity::getDeleted, false).one();

        if (nest != null && !nest.getId().equals(nestDetailsDto.getNestId())) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO.getContent())
                    + nestDetailsDto.getNestNumber() + MessageUtils.getMessage(MessageEnum.GEOAI_UOS__ALREADY_EXISTS_IN_THE_SYSTEM_THE_NAME_IS.getContent())
                    + nest.getName() + MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CAN_NOT_TAKE_DUPLICATE_NUMBER.getContent())
            );
        }
        nestEntity.setNumber(nestDetailsDto.getNestNumber());

        // 保存机巢信息
        saveOrUpdate(nestEntity);
        return RestRes.ok("nestId", nestEntity.getId());
    }

    private RestRes saveOrUpdateAirDetails(NestDetailsDto nestDetailsDto) {
        AircraftEntity aircraftEntity = new AircraftEntity();
        Integer aircraftId = nestDetailsDto.getAircraftId() == null ? aircraftEntity.getId() : nestDetailsDto.getAircraftId();
        aircraftEntity.setId(aircraftId);
        aircraftEntity.setNestId(nestDetailsDto.getNestId());
        AircraftCodeEnum instance = AircraftCodeEnum.getInstance(String.valueOf(nestDetailsDto.getAircraftTypeValue()));
        aircraftEntity.setCode(instance.getCode());
        aircraftEntity.setTypeValue(nestDetailsDto.getAircraftTypeValue());
        aircraftEntity.setAircraftNumber(nestDetailsDto.getAircraftNumber());
        aircraftEntity.setControllerNumber(nestDetailsDto.getControllerNumber());
        aircraftEntity.setCameraName(nestDetailsDto.getCameraName());
        aircraftEntity.setCreatorId(nestDetailsDto.getCreateUserId());
        aircraftEntity.setModifyTime(LocalDateTime.now());
        boolean saveOrUpdate = aircraftService.saveOrUpdate(aircraftEntity);
        if (saveOrUpdate) {
            return RestRes.ok("airId", aircraftEntity.getId());
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DRONE_INFORMATION_SAVING_FAILURE.getContent()));
    }

    private RestRes saveOrUpdateNestSensorRel(boolean isAdd, List<Integer> sensorIdList, Integer nestId, Long userId) {
        // 保存机巢绑定的传感器
        if (CollectionUtil.isNotEmpty(sensorIdList)) {
            // 为编辑时，先删除旧的传感器关联信息再添加信息，新增则直接入库
            if (!isAdd) {
                // 修改该机巢绑定的传感器的delete为1
                //FIXME: wmin修改nestId
                nestSensorRelService.deleteByNestId(String.valueOf(nestId));
            }
            List<NestSensorRelEntity> nestSensorRelEntityList = sensorIdList.stream().map(sId -> {
                NestSensorRelEntity nestSensorRelEntity = new NestSensorRelEntity();
                nestSensorRelEntity.setNestId(nestId);
                nestSensorRelEntity.setSensorId(sId);
                nestSensorRelEntity.setCreatorId(userId);
                return nestSensorRelEntity;
            }).collect(Collectors.toList());
            boolean b = nestSensorRelService.saveBatch(nestSensorRelEntityList);
            if (!b) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_PARAMETER_SAVING_FAILURE.getContent()));
            }
        } else {
            // 删除该机巢绑定的传感器
            //FIXME: wmin修改nestId
            nestSensorRelService.deleteByNestId(String.valueOf(nestId));
        }
        return RestRes.ok();
    }

    /**
     * 获取Component
     *
     * @param nestId
     * @return
     */
    @Override
    public ComponentManager getComponentManager(String nestId) {
        return this.baseNestService.getComponentManagerByNestId(nestId);
    }
}
