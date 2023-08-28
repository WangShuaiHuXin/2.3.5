package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.core.util.DateUtils;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.geoai.common.web.util.ResultUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.imapcloud.nest.enums.AircraftCodeEnum;
import com.imapcloud.nest.pojo.dto.UosNestStreamRefDTO;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.service.SysUnitService;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.DriveUseEnum;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.enums.StreamUseEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.dao.entity.UosNestDeviceRefEntity;
import com.imapcloud.nest.v2.dao.entity.UosNestStreamRefEntity;
import com.imapcloud.nest.v2.manager.cps.GeneralManager;
import com.imapcloud.nest.v2.manager.cps.SystemManager;
import com.imapcloud.nest.v2.manager.dataobj.DeviceInfoDO;
import com.imapcloud.nest.v2.manager.dataobj.in.BaseNestInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.BaseNestOrgRefInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.BaseNestTypeInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.NestParamInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.*;
import com.imapcloud.nest.v2.manager.event.BaseNestTypeChangeEvent;
import com.imapcloud.nest.v2.manager.feign.MediaServiceClient;
import com.imapcloud.nest.v2.manager.feign.OrgServiceClient;
import com.imapcloud.nest.v2.manager.rest.MediaManager;
import com.imapcloud.nest.v2.manager.sql.*;
import com.imapcloud.nest.v2.service.*;
import com.imapcloud.nest.v2.service.dto.in.BaseNestInDTO;
import com.imapcloud.nest.v2.service.dto.out.BaseNestOutDTO;
import com.imapcloud.nest.v2.service.dto.out.BaseNestUavOutDTO;
import com.imapcloud.nest.v2.service.dto.out.UosNestDeviceRefOutDTO;
import com.imapcloud.sdk.pojo.constant.NestSensorEnum;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 基站后台服务
 *
 * @author boluo
 * @date 2022-08-26
 */
@Slf4j
@Service
public class AdminBaseNestServiceImpl implements AdminBaseNestService {

    @Resource
    private BaseNestManager baseNestManager;

    @Resource
    private NestParamManager nestParamManager;

    @Resource
    private BaseNestOrgRefManager baseNestOrgRefManager;

    @Resource
    private RedisService redisService;

    @Resource
    private BaseUavNestRefManager baseUavNestRefManager;

    @Resource
    private BaseUavManager baseUavManager;

    @Resource
    private NestRtkManager nestRtkManager;

    @Resource
    private NestSensorRelManager nestSensorRelManager;

    @Resource
    private MediaStreamManager mediaStreamManager;

    @Resource
    private MediaDeviceManager mediaDeviceManager;

    @Resource
    private MediaDeviceStreamRefManager mediaDeviceStreamRefManager;

    @Resource
    private UosRegionManager uosRegionManager;

    @Resource
    private OrgServiceClient orgServiceClient;

    @Resource
    private UosMqttManager uosMqttManager;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private SystemManager systemManager;

    @Resource
    private GeneralManager generalManager;

    @Resource
    private NestService nestService;

    @Resource
    private SysUnitService sysUnitService;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private ApplicationContext applicationContext;


    @Resource
    private UosNestDeviceRefService uosNestDeviceRefService;

    @Resource
    private UosNestStreamRefService uosNestStreamRefService;

    @Resource
    private MediaManager mediaManager;

    @Resource
    private MediaServiceClient mediaServiceClient;

    @Resource
    private BaseUavNestRefService baseUavNestRefService;

    @Resource
    private BaseNestTypeInfoManager baseNestTypeInfoManager;

    @Override
    public PageResultInfo<BaseNestOutDTO.ListOutDTO> list(BaseNestInDTO.ListInDTO listInDTO) {

        BaseNestInDO.ListInDO listInDO = new BaseNestInDO.ListInDO();
        listInDO.setUserOrgCode(listInDTO.getUserOrgCode());
        listInDO.setOrgCode(listInDTO.getOrgCode());
        listInDO.setName(listInDTO.getName());
        listInDO.setNumber(listInDTO.getNumber());
        listInDO.setUuid(listInDTO.getUuid());
        listInDO.setType(listInDTO.getType());
        listInDO.setRegionId(listInDTO.getRegionId());
        listInDO.setPageNo(listInDTO.getPageNo());
        listInDO.setPageSize(listInDTO.getPageSize());
        listInDO.setUavCate(listInDTO.getUavCate());
        listInDO.setUavModel(listInDTO.getUavModel());
        listInDO.setUavType(listInDTO.getUavType());

        long condition = baseNestManager.countByCondition(listInDO);
        if (condition == 0) {
            return PageResultInfo.of(0, Collections.emptyList());
        }
        List<BaseNestOutDO.ListOutDO> listOutDOList = baseNestManager.selectByCondition(listInDO);
        if (CollUtil.isEmpty(listOutDOList)) {
            return PageResultInfo.of(0, Collections.emptyList());
        }

        List<BaseNestOutDTO.ListOutDTO> listOutDTOList = Lists.newLinkedList();
        List<String> regionIdList = new ArrayList<>();
        List<String> nestIdList = new ArrayList<>();
        for (BaseNestOutDO.ListOutDO listOutDO : listOutDOList) {
            regionIdList.add(listOutDO.getRegionId());
            nestIdList.add(listOutDO.getNestId());
        }
        // 查询单位
        List<BaseNestOrgRefOutDO> baseNestOrgRefOutDOList = baseNestOrgRefManager.selectByNestIdList(nestIdList);
        Set<String> orgCodeList = baseNestOrgRefOutDOList.stream().map(BaseNestOrgRefOutDO::getOrgCode).collect(Collectors.toSet());
        Map<String, List<BaseNestOrgRefOutDO>> nestIdRefMap = baseNestOrgRefOutDOList.stream().collect(Collectors.groupingBy(BaseNestOrgRefOutDO::getNestId));

        List<UosRegionOutDO> uosRegionOutDOList = uosRegionManager.selectByRegionIdList(regionIdList);
        Map<String, String> regionMap = uosRegionOutDOList.stream()
                .collect(Collectors.toMap(UosRegionOutDO::getRegionId, UosRegionOutDO::getRegionName, (key1, key2) -> key1));

        Result<List<OrgSimpleOutDO>> result = orgServiceClient.listOrgDetails(Lists.newArrayList(orgCodeList));
        List<OrgSimpleOutDO> orgSimpleOutDOList = ResultUtils.getData(result);

        //查找基站关联的无人机信息
        List<BaseNestOutDO.BaseNestUavInfoOutDO> baseNestUavInfoOutDO=baseNestManager.getNestUavInfoByIds(nestIdList);
        Map<String, List<BaseNestOutDO.BaseNestUavInfoOutDO>> stringListMap = baseNestUavInfoOutDO.stream().collect(Collectors.groupingBy(BaseNestOutDO.BaseNestUavInfoOutDO::getNestId));
        Map<String, String> orgMap;
        if (CollUtil.isNotEmpty(orgSimpleOutDOList)) {
            orgMap = orgSimpleOutDOList.stream().collect(Collectors.toMap(OrgSimpleOutDO::getOrgCode, OrgSimpleOutDO::getOrgName, (key1, key2) -> key1));
        } else {
            orgMap = Collections.emptyMap();
        }
        //2、查询无人机与基站关联表,查询无人机表
        Map<String, List<BaseNestUavOutDTO>> nestUavMap = Collections.emptyMap();
        List<BaseNestUavOutDTO> baseNestUavOutDTOList = baseUavNestRefService.listNestUavIds(nestIdList);
        if(!CollectionUtils.isEmpty(baseNestUavOutDTOList)){
            nestUavMap = baseNestUavOutDTOList.stream().collect(Collectors.groupingBy(BaseNestUavOutDTO::getNestId, Collectors.toList()));
        }
        for (BaseNestOutDO.ListOutDO listOutDO : listOutDOList) {
            BaseNestOutDTO.ListOutDTO listOutDTO = new BaseNestOutDTO.ListOutDTO();
            BeanUtils.copyProperties(listOutDO, listOutDTO);
            listOutDTO.setTypeName(NestTypeEnum.getInstance(listOutDO.getType()).getMessage());
            listOutDTO.setRegionName(regionMap.getOrDefault(listOutDO.getRegionId(), ""));

            List<BaseNestOrgRefOutDO> doList = nestIdRefMap.get(listOutDO.getNestId());
            StringJoiner joiner = new StringJoiner(",");
            StringJoiner joinerOrgCode = new StringJoiner(",");
            if (doList != null) {
                for (BaseNestOrgRefOutDO baseNestOrgRefOutDO : doList) {
                    String s = orgMap.get(baseNestOrgRefOutDO.getOrgCode());
                    if (StringUtils.isNotBlank(s)) {
                        joiner.add(s);
                        joinerOrgCode.add(baseNestOrgRefOutDO.getOrgCode());
                    }
                }
            }
            if(!CollectionUtils.isEmpty(nestUavMap) && nestUavMap.containsKey(listOutDO.getNestId())){
                listOutDTO.setUavIds(nestUavMap.get(listOutDO.getNestId()).stream().map(BaseNestUavOutDTO::getUavId).collect(Collectors.toList()));
            }
            listOutDTO.setOrgName(joiner.toString());
            listOutDTO.setOrgCode(joinerOrgCode.toString());
            List<BaseNestOutDO.BaseNestUavInfoOutDO> baseNestUavInfoOutDOS = stringListMap.get(listOutDO.getNestId());
            if(ObjectUtils.isNotEmpty(baseNestUavInfoOutDOS)) {
                List<String> stringList = baseNestUavInfoOutDOS.stream().map(BaseNestOutDO.BaseNestUavInfoOutDO::getType).collect(Collectors.toList());
                listOutDTO.setUavModel(stringList);
            }
            listOutDTOList.add(listOutDTO);
        }
        return PageResultInfo.of(condition, listOutDTOList);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String nestBaseSave(BaseNestInDTO.NestBaseInDTO nestBaseInDTO) {
        String nestId = nestBaseInDTO.getNestId();
        BaseNestInDO.BaseNestEntityInDO baseNestEntityInDO;
        List<BaseNestOrgRefOutDO> haveList;
        if (StringUtils.isNotEmpty(nestId)) {
            List<BaseNestOutDO.BaseNestEntityOutDO> baseNestEntityOutDOList = baseNestManager.selectByNestId(nestBaseInDTO.getNestId());
            if (CollUtil.isEmpty(baseNestEntityOutDOList)) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_EXIST_NESTID.getContent()));
            }
            baseNestEntityInDO = getBaseNestEntityInDO(nestBaseInDTO);
            baseNestEntityInDO.setNestId(nestBaseInDTO.getNestId());
            baseNestManager.updateByNestId(baseNestEntityInDO);
            // 查询基站单位关系
            haveList = baseNestOrgRefManager.selectOneByNestId(nestId);
        } else {
            haveList = Lists.newLinkedList();
            nestId = BizIdUtils.snowflakeIdStr();
            baseNestEntityInDO = getBaseNestEntityInDO(nestBaseInDTO);
            baseNestEntityInDO.setNestId(nestId);
            baseNestManager.insertOne(baseNestEntityInDO);
            // 插入nest_param
            NestParamInDO.NestParamEntityInDO nestParamEntityInDO = new NestParamInDO.NestParamEntityInDO();
            nestParamEntityInDO.setBaseNestId(baseNestEntityInDO.getNestId());
            nestParamEntityInDO.setCreatorId(Long.parseLong(nestBaseInDTO.getAccountId()));
            nestParamManager.insert(nestParamEntityInDO);
        }
        Set<String> orgCodeSet = Sets.newHashSet();
        for (BaseNestOrgRefOutDO baseNestOrgRefOutDO : haveList) {
            if (nestBaseInDTO.getDeleteOrgCodeList().contains(baseNestOrgRefOutDO.getOrgCode())) {
                continue;
            }
            orgCodeSet.add(baseNestOrgRefOutDO.getOrgCode());
        }

        List<BaseNestOrgRefInDO.BaseNestOrgRefEntityInDO> addNestOrgRefEntityInDOList = Lists.newLinkedList();
        for (String s : nestBaseInDTO.getAddOrgCodeList()) {
            if (orgCodeSet.contains(s)) {
                continue;
            }
            orgCodeSet.add(s);
            BaseNestOrgRefInDO.BaseNestOrgRefEntityInDO baseNestOrgRefEntityInDO = new BaseNestOrgRefInDO.BaseNestOrgRefEntityInDO();
            baseNestOrgRefEntityInDO.setNestId(baseNestEntityInDO.getNestId());
            baseNestOrgRefEntityInDO.setOrgCode(s);
            baseNestOrgRefEntityInDO.setAccountId(nestBaseInDTO.getAccountId());
            addNestOrgRefEntityInDOList.add(baseNestOrgRefEntityInDO);
        }
        if (CollUtil.isEmpty(orgCodeSet)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MUST_HAVE_A_UNIT.getContent()));
        }
        // 查看基站的单位
        log.info("#AdminBaseNestServiceImpl.nestBaseSave# nestId={}", nestId);
        nestService.synNestOrg(baseNestEntityInDO.getNestId(), Lists.newArrayList(orgCodeSet));
        log.info("#AdminBaseNestServiceImpl.nestBaseSave# nestId={}", nestId);
        if (CollUtil.isNotEmpty(nestBaseInDTO.getDeleteOrgCodeList())) {
            baseNestOrgRefManager.deleteByNestIdAndOrgCodeList(baseNestEntityInDO.getNestId(), nestBaseInDTO.getDeleteOrgCodeList(), nestBaseInDTO.getAccountId());
        }
        if (CollUtil.isNotEmpty(addNestOrgRefEntityInDOList)) {
            baseNestOrgRefManager.batchInsert(addNestOrgRefEntityInDOList);
        }
        baseNestManager.clearRedisCache(baseNestEntityInDO.getNestId(), nestBaseInDTO.getAccountId());

        return baseNestEntityInDO.getNestId();
    }

    private BaseNestInDO.BaseNestEntityInDO getBaseNestEntityInDO(BaseNestInDTO.NestBaseInDTO nestBaseInDTO) {
        BaseNestInDO.BaseNestEntityInDO baseNestEntityInDO = new BaseNestInDO.BaseNestEntityInDO();
        baseNestEntityInDO.setName(nestBaseInDTO.getName());
        baseNestEntityInDO.setRegionId(nestBaseInDTO.getRegionId());
        baseNestEntityInDO.setAltitude(Optional.ofNullable(nestBaseInDTO.getAltitude())
                .map(BigDecimal::doubleValue)
                .orElseGet(() -> BigDecimal.ZERO.doubleValue()));
        baseNestEntityInDO.setLongitude(Optional.ofNullable(nestBaseInDTO.getLongitude())
                .map(BigDecimal::doubleValue)
                .orElseGet(() -> BigDecimal.ZERO.doubleValue()));
        baseNestEntityInDO.setLatitude(Optional.ofNullable(nestBaseInDTO.getLatitude())
                .map(BigDecimal::doubleValue)
                .orElseGet(() -> BigDecimal.ZERO.doubleValue()));
        baseNestEntityInDO.setAglAltitude(Optional.ofNullable(nestBaseInDTO.getAltitude())
                .orElseGet(() -> BigDecimal.ZERO));
        baseNestEntityInDO.setDeployTime(nestBaseInDTO.getDeployTime());
        baseNestEntityInDO.setAddress(nestBaseInDTO.getAddress() == null ? "" : nestBaseInDTO.getAddress());
        baseNestEntityInDO.setAccountId(nestBaseInDTO.getAccountId());
        return baseNestEntityInDO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void nestNestSave(BaseNestInDTO.NestNestInDTO nestNestInDTO) {

        String value = nestNestInDTO.getNestId();
        String key = String.format("%s:%s:lock:%s", this.getClass().getSimpleName(), "nestNestSave", nestNestInDTO.getNestId());
        try {
            boolean tryLock = redisService.tryLock(key, value, 10, TimeUnit.SECONDS);
            if (!tryLock) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NEST_INFORMATION_IS_BEING_UPDATED.getContent()));
            }
            // 基站UUID不能重复
            List<BaseNestOutDO.BaseNestEntityOutDO> baseNestEntityOutDOList = baseNestManager.selectByUuid(nestNestInDTO.getUuid());
            if (CollUtil.isNotEmpty(baseNestEntityOutDOList)) {
                long count = baseNestEntityOutDOList.stream()
                        .filter(bean -> !nestNestInDTO.getNestId().equals(bean.getNestId())).count();
                if (count > 0) {
                    throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ALREADY_EXIST_NESTID.getContent()));
                }
            }
            // 基站编号不能重复
            baseNestEntityOutDOList = baseNestManager.selectByNumber(nestNestInDTO.getNumber());
            if (CollUtil.isNotEmpty(baseNestEntityOutDOList)) {
                long count = baseNestEntityOutDOList.stream()
                        .filter(bean -> !nestNestInDTO.getNestId().equals(bean.getNestId())).count();
                if (count > 0) {
                    throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ALREADY_EXIST_NESTNUM.getContent()));
                }
            }

            BaseNestInDO.BaseNestEntityInDO baseNestEntityInDO = new BaseNestInDO.BaseNestEntityInDO();
            baseNestEntityInDO.setNestId(nestNestInDTO.getNestId());
            baseNestEntityInDO.setUuid(nestNestInDTO.getUuid());
            baseNestEntityInDO.setType(nestNestInDTO.getType());
            baseNestEntityInDO.setNumber(nestNestInDTO.getNumber());
            baseNestEntityInDO.setMqttBrokerId(nestNestInDTO.getMqttBrokerId());
            baseNestManager.updateByNestId(baseNestEntityInDO);
            // 发起事件，如果是修改基站类型，则清空无人机信息、无人机图传推流信息
            BaseNestInDO.BaseNestEntityInDO originBaseNestEntityInDO = new BaseNestInDO.BaseNestEntityInDO();
            originBaseNestEntityInDO.setNestId(nestNestInDTO.getNestId());
            originBaseNestEntityInDO.setType(baseNestEntityOutDOList.stream()
                    .filter(bean -> nestNestInDTO.getNestId().equals(bean.getNestId()))
                    .map(BaseNestOutDO.BaseNestEntityOutDO::getType)
                    .findFirst()
                    .orElseGet(() -> -1));
            this.applicationContext.publishEvent(new BaseNestTypeChangeEvent(originBaseNestEntityInDO));
            baseNestManager.clearRedisCache(baseNestEntityInDO.getNestId(), nestNestInDTO.getAccountId());
        } finally {
            redisService.releaseLock(key, value);
        }
    }

    private Pair<List<String>, List<String>> getOrgNameInfo(String nestId) {

        List<BaseNestOrgRefOutDO> baseNestOrgRefOutDOList = baseNestOrgRefManager.selectOneByNestId(nestId);
        if (CollUtil.isNotEmpty(baseNestOrgRefOutDOList)) {
            List<String> orgCodeList = baseNestOrgRefOutDOList.stream().map(BaseNestOrgRefOutDO::getOrgCode).collect(Collectors.toList());
            Result<List<OrgSimpleOutDO>> listResult = orgServiceClient.listOrgDetails(orgCodeList);

            List<String> orgNameList = null;
            List<OrgSimpleOutDO> data = ResultUtils.getData(listResult);
            if (data != null) {
                orgNameList = data.stream().map(OrgSimpleOutDO::getOrgName).collect(Collectors.toList());
            }
            return Pair.of(orgCodeList, orgNameList == null ? Collections.emptyList() : orgNameList);
        }
        return Pair.of(Collections.emptyList(), Collections.emptyList());
    }

    private BaseNestOutDTO.NestBaseOutDTO toNestBaseOutDTO(BaseNestOutDO.BaseNestEntityOutDO baseNestEntityOutDO) {
        // 查询region信息
        UosRegionOutDO uosRegionOutDO = uosRegionManager.selectOneByRegionId(baseNestEntityOutDO.getRegionId());

        BaseNestOutDTO.NestBaseOutDTO nestBaseOutDTO = new BaseNestOutDTO.NestBaseOutDTO();
        nestBaseOutDTO.setName(baseNestEntityOutDO.getName());
        nestBaseOutDTO.setDeployTime(baseNestEntityOutDO.getDeployTime());
        nestBaseOutDTO.setLatitude(baseNestEntityOutDO.getLatitude());
        nestBaseOutDTO.setLongitude(baseNestEntityOutDO.getLongitude());
        nestBaseOutDTO.setAltitude(baseNestEntityOutDO.getAltitude());
        nestBaseOutDTO.setAglAltitude(baseNestEntityOutDO.getAglAltitude());
        nestBaseOutDTO.setAddress(baseNestEntityOutDO.getAddress());
        nestBaseOutDTO.setRegionId(baseNestEntityOutDO.getRegionId());
        nestBaseOutDTO.setRegionName("");
        if (uosRegionOutDO != null) {
            nestBaseOutDTO.setRegionName(uosRegionOutDO.getRegionName());
        }
        // 获取单位信息
        Pair<List<String>, List<String>> codeNamePair = getOrgNameInfo(baseNestEntityOutDO.getNestId());
        nestBaseOutDTO.setOrgCodeList(codeNamePair.getKey());
        nestBaseOutDTO.setOrgNameList(codeNamePair.getValue());
        return nestBaseOutDTO;
    }

    private String getMqttName(String mqttBrokerId) {
        UosMqttOutDO uosMqttOutDO = uosMqttManager.selectOneByMqttBrokerId(mqttBrokerId);
        if (uosMqttOutDO != null) {
            return uosMqttOutDO.getMqttName();
        }
        return "";
    }

    private BaseNestOutDTO.NestInfoOutDTO toNestInfoOutDTO(BaseNestOutDO.BaseNestEntityOutDO baseNestEntityOutDO) {

        BaseNestOutDTO.NestInfoOutDTO nestInfoOutDTO = new BaseNestOutDTO.NestInfoOutDTO();
        nestInfoOutDTO.setUuid(baseNestEntityOutDO.getUuid());
        nestInfoOutDTO.setNumber(baseNestEntityOutDO.getNumber());
        nestInfoOutDTO.setType(baseNestEntityOutDO.getType() == null ? null : baseNestEntityOutDO.getType().toString());
        nestInfoOutDTO.setTypeName(MessageUtils.getMessage(NestTypeEnum.getInstance(baseNestEntityOutDO.getType()).getKey()));
        nestInfoOutDTO.setMqttBrokerId(baseNestEntityOutDO.getMqttBrokerId());
        // 查询mqtt信息
        nestInfoOutDTO.setMqttBrokerName(getMqttName(baseNestEntityOutDO.getMqttBrokerId()));
        return nestInfoOutDTO;
    }

    private BaseNestOutDTO.UavInfoOutDTO toUavInfoOutDTO(BaseUavOutDO.BaseUavEntityOutDO baseUavEntityOutDO, String nestId) {
        BaseNestOutDTO.UavInfoOutDTO uavInfoOutDTO = new BaseNestOutDTO.UavInfoOutDTO();
        // 查询RTK
        NestRtkOutDO.NestRtkEntityOutDO nestRtkEntityOutDO = nestRtkManager.selectByNestId(nestId);
        // 查询传感器
        List<NestSensorRelOutDO.NestSensorRelEntityOutDO> nestSensorRelEntityOutDOList = nestSensorRelManager.selectListByNestId(nestId);
        // 查询无人机流信息
        //MediaStreamOutDO mediaStreamOutDO = mediaStreamManager.selectOneByStreamId(baseUavEntityOutDO.getStreamId());
        try {
            Result<PushStreamInfoOutDO> streamInfoRs = mediaServiceClient.fetchPushStreamInfo(baseUavEntityOutDO.getStreamId());
            if(streamInfoRs.isOk()) {
                uavInfoOutDTO.setUavPullUrl(streamInfoRs.getData().getPullUrl());
                uavInfoOutDTO.setPushRtmp(streamInfoRs.getData().getPushUrl());
                uavInfoOutDTO.setPushRtmp(streamInfoRs.getData().getPushUrl());
                uavInfoOutDTO.setStreamId(streamInfoRs.getData().getStreamId());
            }
        }catch (Exception e) {
            log.error("获取无人机流信息失败，streamId = {}", baseUavEntityOutDO.getStreamId());
        }

        uavInfoOutDTO.setRegisterCode(baseUavEntityOutDO.getRegisterCode());
        uavInfoOutDTO.setTakeoffWeight(baseUavEntityOutDO.getTakeoffWeight());
        uavInfoOutDTO.setUavId(baseUavEntityOutDO.getUavId());
        uavInfoOutDTO.setWhich(baseUavEntityOutDO.getWhich());
        uavInfoOutDTO.setCameraName(baseUavEntityOutDO.getCameraName());
        uavInfoOutDTO.setCameraId(baseUavEntityOutDO.getCameraName());
        uavInfoOutDTO.setUavNumber(baseUavEntityOutDO.getUavNumber());
        uavInfoOutDTO.setRcNumber(baseUavEntityOutDO.getRcNumber());
        uavInfoOutDTO.setType(baseUavEntityOutDO.getType());
        uavInfoOutDTO.setTypeName(MessageUtils.getMessage(AircraftCodeEnum.getInstance(baseUavEntityOutDO.getType()).getKey()));

        //中科天网
        uavInfoOutDTO.setUavPro(baseUavEntityOutDO.getUavPro());
        uavInfoOutDTO.setUavName(baseUavEntityOutDO.getUavName());
        uavInfoOutDTO.setUavType(baseUavEntityOutDO.getUavType());
        uavInfoOutDTO.setUavPattern(baseUavEntityOutDO.getUavPattern());
        uavInfoOutDTO.setUavSn(baseUavEntityOutDO.getUavSn());
        if (nestRtkEntityOutDO != null) {

            uavInfoOutDTO.setRtkEnable(nestRtkEntityOutDO.rtkEnable());
            uavInfoOutDTO.setExpireTime("");
            if (nestRtkEntityOutDO.getExpireTime() != null) {
                uavInfoOutDTO.setExpireTime(nestRtkEntityOutDO.getExpireTime().format(DateUtils.DATE_FORMATTER_OF_CN));
            }
        }

        if (CollUtil.isNotEmpty(nestSensorRelEntityOutDOList)) {
            List<String> sensorIdList = Lists.newLinkedList();
            List<String> sensorNameList = Lists.newLinkedList();
            for (NestSensorRelOutDO.NestSensorRelEntityOutDO nestSensorRelEntityOutDO : nestSensorRelEntityOutDOList) {
                sensorIdList.add(nestSensorRelEntityOutDO.getSensorId().toString());
                NestSensorEnum nestSensorEnumByCode = NestSensorEnum.getNestSensorEnumByCode(nestSensorRelEntityOutDO.getSensorId());
                if (nestSensorEnumByCode != NestSensorEnum.DEFAULT) {
                    sensorNameList.add(MessageUtils.getMessage(nestSensorEnumByCode.getKey()));
                }
            }
            uavInfoOutDTO.setSensorNameList(sensorNameList);
            uavInfoOutDTO.setSensorIdList(sensorIdList);
        } else {
            uavInfoOutDTO.setSensorNameList(Collections.emptyList());
            uavInfoOutDTO.setSensorIdList(Collections.emptyList());
        }
        return uavInfoOutDTO;
    }


    private void toDeviceInfoOutDTO(BaseNestOutDTO.NestDetailOutDTO nestDetailOutDTO, String nestId) {
        List<UosNestDeviceRefOutDTO> list = uosNestDeviceRefService.findByNestId(nestId);
        if(CollectionUtil.isEmpty(list)){
            return;
        }
        for (UosNestDeviceRefOutDTO dto : list) {
            if(Objects.equals(dto.getDeviceUse(), DriveUseEnum.OUTSIDE.getCode())) {
                BaseNestOutDTO.WatchDeviceInfo watchDeviceInfo = new BaseNestOutDTO.WatchDeviceInfo();
                watchDeviceInfo.setDeviceCode(dto.getDeviceId());
                watchDeviceInfo.setChannelCode(getChannelCode(dto.getDeviceId()));
                assembleUrl(watchDeviceInfo, nestId, DriveUseEnum.OUTSIDE.getCode());
                nestDetailOutDTO.getMediaInfo().setOutsideDevice(watchDeviceInfo);
            }else if(Objects.equals(dto.getDeviceUse(), DriveUseEnum.INSIDE.getCode())) {
                BaseNestOutDTO.WatchDeviceInfo watchDeviceInfo = new BaseNestOutDTO.WatchDeviceInfo();
                watchDeviceInfo.setDeviceCode(dto.getDeviceId());
                watchDeviceInfo.setChannelCode(getChannelCode(dto.getDeviceId()));
                assembleUrl(watchDeviceInfo, nestId, DriveUseEnum.INSIDE.getCode());
                nestDetailOutDTO.getMediaInfo().setInsideDevice(watchDeviceInfo);
            }
        }
    }

    /**
     * 根据streamId获取推拉流信息
     * @param watchDeviceInfo
     * @param nestId
     * @param streamUse
     */
    private void assembleUrl(BaseNestOutDTO.WatchDeviceInfo watchDeviceInfo, String nestId, Integer streamUse) {
        UosNestStreamRefDTO uosNestStreamRefDTO = uosNestStreamRefService.findByNestId(nestId, streamUse);
        if(Objects.isNull(uosNestStreamRefDTO)) {
            return;
        }
        try {
            //获取通道ID
            Result<PushStreamInfoOutDO> result = mediaServiceClient.fetchPushStreamInfo(uosNestStreamRefDTO.getStreamId());
            if(result.isOk() && result.getData() != null) {
                watchDeviceInfo.setPushStreamUrl(result.getData().getPushUrl());
                watchDeviceInfo.setPullStreamUrl(result.getData().getPullUrl());
            }
        }catch (Exception e) {
            log.error("streamId:{}, 获取流信息失败:{}", uosNestStreamRefDTO.getStreamId(), e);
        }
    }

    private String getChannelCode(String deviceId) {
        try {
            //获取通道ID
            Result<DeviceInfoOutDO> result = mediaServiceClient.findByDeviceId(deviceId);
            return result.getData().getChannelCode();
        }catch (Exception e) {
            log.error("deviceId:{}, 获取ChannelCode失败:{}", deviceId, e);
        }
        return null;
    }

//    private BaseNestOutDTO.DeviceInfoOutDTO toDeviceInfoOutDTO(BaseNestOutDO.BaseNestEntityOutDO baseNestEntityOutDO) {
//
//        if (StringUtils.isAnyBlank(baseNestEntityOutDO.getInnerStreamId(), baseNestEntityOutDO.getOuterStreamId())) {
//            return null;
//        }
//        List<String> streamIdList = Lists.newArrayList(baseNestEntityOutDO.getInnerStreamId(), baseNestEntityOutDO.getOuterStreamId());
//        List<MediaStreamOutDO> mediaStreamOutDOList = mediaStreamManager.selectByStreamIdList(streamIdList);
//        List<MediaDeviceStreamRefOutDO> listByStreamIdList = mediaDeviceStreamRefManager.getListByStreamIdList(streamIdList);
//        if (CollUtil.isEmpty(listByStreamIdList)) {
//            return null;
//        }
//
//        Map<String, MediaDeviceStreamRefOutDO> streamDeviceMap = listByStreamIdList.stream()
//                .collect(Collectors.toMap(MediaDeviceStreamRefOutDO::getStreamId, bean -> bean, (key1, key2) -> key1));
//        List<MediaDeviceOutDO.MediaDeviceEntityOutDO> mediaDeviceEntityOutDOList = mediaDeviceManager.selectByDeviceIdList(listByStreamIdList.stream().map(MediaDeviceStreamRefOutDO::getDeviceId).collect(Collectors.toList()));
//        Map<String, MediaDeviceOutDO.MediaDeviceEntityOutDO> collect = mediaDeviceEntityOutDOList.stream().collect(Collectors.toMap(MediaDeviceOutDO.MediaDeviceEntityOutDO::getDeviceId, bean -> bean, (key1, key2) -> key1));
//
//        Map<String, MediaStreamOutDO> stringMediaStreamOutDOMap = mediaStreamOutDOList.stream().collect(Collectors.toMap(MediaStreamOutDO::getStreamId, bean -> bean, (key1, key2) -> key1));
//        BaseNestOutDTO.DeviceInfoOutDTO deviceInfoOutDTO = new BaseNestOutDTO.DeviceInfoOutDTO();
//
//        MediaDeviceOutDO.MediaDeviceEntityOutDO outer = getMediaDeviceEntityOutDO(baseNestEntityOutDO.getOuterStreamId(), streamDeviceMap, collect);
//        if (outer != null) {
//
//            deviceInfoOutDTO.setOuterMac(outer.getDeviceMac());
//        }
//        MediaStreamOutDO outerStreamInfo = stringMediaStreamOutDOMap.get(baseNestEntityOutDO.getOuterStreamId());
//        if (outerStreamInfo != null) {
//            deviceInfoOutDTO.setOuterPushUrl(outerStreamInfo.getStreamPushUrl());
//            deviceInfoOutDTO.setOuterPullUrl(outerStreamInfo.getStreamPullUrl());
//        }
//
//        MediaDeviceOutDO.MediaDeviceEntityOutDO inner = getMediaDeviceEntityOutDO(baseNestEntityOutDO.getInnerStreamId(), streamDeviceMap, collect);
//        if (inner != null) {
//            deviceInfoOutDTO.setInnerMac(inner.getDeviceMac());
//        }
//        MediaStreamOutDO mediaStreamOutDO = stringMediaStreamOutDOMap.get(baseNestEntityOutDO.getInnerStreamId());
//        if (mediaStreamOutDO != null) {
//            deviceInfoOutDTO.setInnerPullUrl(mediaStreamOutDO.getStreamPullUrl());
//        }
//        return deviceInfoOutDTO;
//    }

    private MediaDeviceOutDO.MediaDeviceEntityOutDO getMediaDeviceEntityOutDO(String streamId, Map<String, MediaDeviceStreamRefOutDO> streamDeviceMap, Map<String, MediaDeviceOutDO.MediaDeviceEntityOutDO> collect) {
        MediaDeviceStreamRefOutDO deviceStreamRefOutDO = streamDeviceMap.get(streamId);
        if (deviceStreamRefOutDO != null) {
            return collect.get(deviceStreamRefOutDO.getDeviceId());
        }
        return null;
    }

    @Override
    public BaseNestOutDTO.NestDetailOutDTO nestDetail(String nestId) {

        List<BaseNestOutDO.BaseNestEntityOutDO> baseNestEntityOutDOList = baseNestManager.selectByNestId(nestId);
        if (CollUtil.isEmpty(baseNestEntityOutDOList)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_EXIST_NEST.getContent()));
        }
        BaseNestOutDO.BaseNestEntityOutDO baseNestEntityOutDO = baseNestEntityOutDOList.get(0);
        BaseNestOutDTO.NestDetailOutDTO nestDetailOutDTO = new BaseNestOutDTO.NestDetailOutDTO();
        nestDetailOutDTO.setNestId(nestId);
        nestDetailOutDTO.setBaseInfo(toNestBaseOutDTO(baseNestEntityOutDO));
        nestDetailOutDTO.setNestInfo(toNestInfoOutDTO(baseNestEntityOutDO));

        // 查询无人机信息
        List<BaseUavNestRefOutDO.EntityOutDO> entityOutDOList = baseUavNestRefManager.selectListByNestId(nestId);
        if (CollUtil.isNotEmpty(entityOutDOList)) {

            List<BaseUavOutDO.BaseUavEntityOutDO> baseUavEntityOutDOList = baseUavManager.selectListByUavIdList(entityOutDOList.stream().map(BaseUavNestRefOutDO.EntityOutDO::getUavId).collect(Collectors.toList()));
            if (CollectionUtil.isNotEmpty(baseUavEntityOutDOList)) {
                nestDetailOutDTO.setUavInfoList(baseUavEntityOutDOList.stream().map(x -> toUavInfoOutDTO(x, nestId)).collect(Collectors.toList()));
            }
        }

        // 获取流媒体信息
        BaseNestOutDTO.MediaInfo mediaInfo = toMediaInfoOutDTO(nestId);
        nestDetailOutDTO.setMediaInfo(mediaInfo);

        // 查询设备信息
        //nestDetailOutDTO.setDeviceInfo(toDeviceInfoOutDTO(baseNestEntityOutDO));
        try {
            toDeviceInfoOutDTO(nestDetailOutDTO, nestId);
        }catch (Exception e) {
            log.error("获取设备信息异常：{}", e);
        }

        //监控信息
        assembleMonitor(nestId, nestDetailOutDTO);
        return nestDetailOutDTO;
    }


    /**
     * 大疆监控信息
     */
    private void assembleMonitor(String nestId, BaseNestOutDTO.NestDetailOutDTO nestDetailOutDTO) {
        List<UosNestStreamRefDTO> list = uosNestStreamRefService.listByNestId(nestId);
        if(Objects.isNull(list)) {
            return;
        }
        BaseNestOutDTO.DeviceInfoOutDTO deviceInfoOutDTO = new BaseNestOutDTO.DeviceInfoOutDTO();
        for (UosNestStreamRefDTO dto : list) {
            PushStreamInfoOutDO outDO = null;
            try {
                Result<PushStreamInfoOutDO> result = mediaServiceClient.fetchPushStreamInfo(dto.getStreamId());
                if(result.isOk() && result.getData() != null) {
                    outDO = result.getData();
                }else {
                    continue;
                }
            }catch (Exception e) {
                log.error("streamId:{}, 获取流信息失败:{}", dto.getStreamId(), e);
                continue;
             }

            if(Objects.equals(dto.getStreamUse(), StreamUseEnum.OUTSIDE.getCode())) {
                deviceInfoOutDTO.setOuterPushUrl(outDO.getPushUrl());
                deviceInfoOutDTO.setOuterPullUrl(outDO.getPullUrl());
            }else if(Objects.equals(dto.getStreamUse(), StreamUseEnum.INSIDE.getCode())) {
                deviceInfoOutDTO.setInnerPushUrl(outDO.getPushUrl());
                deviceInfoOutDTO.setInnerPullUrl(outDO.getPullUrl());
                deviceInfoOutDTO.setMonitorServerId(outDO.getServerId());
                deviceInfoOutDTO.setDjiMonitorStreamId(outDO.getStreamId());
            }
        }
        nestDetailOutDTO.setDeviceInfo(deviceInfoOutDTO);
    }


    /**
     * 获取流媒体信息
     */
    private BaseNestOutDTO.MediaInfo toMediaInfoOutDTO(String nestId) {

        BaseNestOutDTO.MediaInfo res = new BaseNestOutDTO.MediaInfo();
        List<BaseNestOutDTO.UavPushStreamInfo> uavPushStreamInfo = getUavPushStreamInfo(nestId);
        res.setUavPushStreamInfos(uavPushStreamInfo);

        return res;
    }

    @Override
    public BaseNestOutDTO.VersionOutDTO getVersion(String nestId, boolean clearCache) {

        BaseNestOutDTO.VersionOutDTO versionOutDTO = new BaseNestOutDTO.VersionOutDTO();
        versionOutDTO.setSysVersion(geoaiUosProperties.getVersion());

        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.VERSION_INFO_KEY, nestId);
        if (clearCache) {
            redisService.del(redisKey);
        }
        String mpsVersion = (String) redisService.hGet(redisKey, "mpsVersion");
        if (mpsVersion == null) {
            try {
                mpsVersion = systemManager.getMpsVersion(nestId);
            } catch (Exception e) {
                mpsVersion = "";
            }
            redisService.hSet(redisKey, "mpsVersion", mpsVersion, 3600);
        }
        List<String> cpsVersionList = (List<String>) redisService.hGet(redisKey, "cpsVersion");
        if (cpsVersionList == null) {
            try {
                cpsVersionList = this.getCpsVersionList(nestId);
            } catch (Exception e) {
                cpsVersionList = CollectionUtil.newArrayList("");
            }
            redisService.hSet(redisKey, "cpsVersion", cpsVersionList, 3600);
        }
        versionOutDTO.setCpsVersionList(cpsVersionList);
        versionOutDTO.setMpsVersion(mpsVersion);

        GeneralManagerOutDO.ComponentSerialNumberOutDO outDO = (GeneralManagerOutDO.ComponentSerialNumberOutDO) redisService.hGet(redisKey, "ComponentSerialNumberOutDO");
        if (outDO == null) {
            try {
                outDO = generalManager.listComponentSerialNumber(nestId);
            } catch (Exception e) {
                outDO = new GeneralManagerOutDO.ComponentSerialNumberOutDO();
                outDO.setCameraVersion("");
                outDO.setFcVersion("");
                outDO.setRcVersion("");
                outDO.setBatteryVersion("");
            }
            redisService.hSet(redisKey, "ComponentSerialNumberOutDO", outDO, 3600);
        }
        versionOutDTO.setCameraVersion(outDO.getCameraVersion());
        versionOutDTO.setFcVersion(outDO.getFcVersion());
        versionOutDTO.setRcVersion(outDO.getRcVersion());
        versionOutDTO.setBatteryVersion(outDO.getBatteryVersion());
        return versionOutDTO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean delete(String nestId, String accountId, String userName) {
        // 删除 基本信息 基站信息
        List<BaseNestOutDO.BaseNestEntityOutDO> baseNestEntityOutDOList = baseNestManager.selectByNestId(nestId);
        if (CollUtil.isEmpty(baseNestEntityOutDOList)) {
            return true;
        }
        BaseNestOutDO.BaseNestEntityOutDO baseNestEntityOutDO = baseNestEntityOutDOList.get(0);

        baseNestManager.deleteByNestId(nestId, accountId);
        nestParamManager.deleteByNestId(nestId);
        baseNestOrgRefManager.deleteByNestId(nestId, accountId);
        List<String> streamIdList = Lists.newLinkedList();

        // 无人机信息
        List<BaseUavNestRefOutDO.EntityOutDO> entityOutDOList = baseUavNestRefManager.selectListByNestId(nestId);
        if (CollUtil.isNotEmpty(entityOutDOList)) {
            List<String> uavIdList = entityOutDOList.stream().map(BaseUavNestRefOutDO.EntityOutDO::getUavId).collect(Collectors.toList());
            List<BaseUavOutDO.BaseUavEntityOutDO> baseUavEntityOutDOList = baseUavManager.selectListByUavIdList(uavIdList);
            List<String> collect = baseUavEntityOutDOList.stream().map(BaseUavOutDO.BaseUavEntityOutDO::getStreamId).collect(Collectors.toList());
            streamIdList.addAll(collect);

            baseUavManager.deleteByUavIdList(uavIdList, accountId);
            baseUavNestRefManager.deleteByNestId(nestId, accountId);

            nestRtkManager.deleteByNestId(nestId);
            nestSensorRelManager.deleteByNestId(nestId);
        }

        List<UosNestStreamRefDTO> list = uosNestStreamRefService.listByNestId(nestId);
        CompletableFuture.runAsync(()->{
            if(CollectionUtil.isNotEmpty(list)) {
                List<String> streamIds = list.stream().map(UosNestStreamRefDTO::getStreamId).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(streamIds)) {
                    mediaServiceClient.deleteStreamInfo(streamIds);
                }
            }
        }).whenComplete((v, e) -> {
            if (e == null) {
                log.info(">>>>>> 删除推流信息成功：nestId = {}", nestId);
            }
        }).exceptionally((e)->{
            log.error(">>>>>> 删除推流信息异常：nestId = {}", nestId, e);
            return null;
        });

        uosNestStreamRefService.deleteNestStreamRef(nestId);
        uosNestDeviceRefService.deleteNestDeviceRef(nestId);
        baseNestManager.clearDeleteRedisCache(userName);
        return true;
    }

    @Override
    public List<BaseNestOutDTO.CameraInfoOutDTO> getCameraInfo(String nestId) {

        List<BaseNestOutDTO.CameraInfoOutDTO> cameraInfoOutDTOList = Lists.newLinkedList();
        List<GeneralManagerOutDO.NestCameraInfoOutDO> nestCameraInfoOutDOList = generalManager.listNestCameraInfos(nestId);
        for (GeneralManagerOutDO.NestCameraInfoOutDO nestCameraInfoOutDO : nestCameraInfoOutDOList) {
            BaseNestOutDTO.CameraInfoOutDTO cameraInfoOutDTO = new BaseNestOutDTO.CameraInfoOutDTO();
            BeanUtils.copyProperties(nestCameraInfoOutDO, cameraInfoOutDTO);
            cameraInfoOutDTOList.add(cameraInfoOutDTO);
        }
        return cameraInfoOutDTOList;
    }

    @Override
    public List<Pair<String, String>> queryOrgInfoByNestId(String nestId) {

        List<Pair<String, String>> result = Lists.newLinkedList();

        List<BaseNestOrgRefOutDO> baseNestOrgRefOutDOList = baseNestOrgRefManager.selectOneByNestId(nestId);
        if (CollUtil.isEmpty(baseNestOrgRefOutDOList)) {
            return result;
        }
        List<String> orgCodeList = baseNestOrgRefOutDOList.stream().map(BaseNestOrgRefOutDO::getOrgCode).collect(Collectors.toList());
        // 查询父单位
        Set<String> allOrgCodeSet = Sets.newHashSet();
        allOrgCodeSet.addAll(orgCodeList);
        for (String orgCode : orgCodeList) {
            allOrgCodeSet.addAll(sysUnitService.getSuperiorOrgCodes(orgCode));
        }

        Result<List<OrgSimpleOutDO>> listResult = orgServiceClient.listOrgDetails(Lists.newArrayList(allOrgCodeSet));
        List<OrgSimpleOutDO> data = ResultUtils.getData(listResult);
        if (data != null) {
            for (OrgSimpleOutDO orgSimpleOutDO : data) {
                result.add(Pair.of(orgSimpleOutDO.getOrgCode(), orgSimpleOutDO.getOrgName()));
            }
        }
        return result;
    }

    /**
     * 获取CPS版本信息
     *
     * @param nestId
     * @return
     */
    public List<String> getCpsVersionList(String nestId) {
        List<String> cpsVersionList = CollectionUtil.newArrayList();
        //判断是什么基站，如果是一巢三机，需要查询三次
        NestTypeEnum nestTypeEnum = this.baseNestService.getNestTypeByNestIdCache(nestId);
        if (NestTypeEnum.G503.getValue() == nestTypeEnum.getValue()) {
            //后续应由接口可以查询不同基站无人机数量
            for (int i = 1; i <= 3; i++) {
                cpsVersionList.add(systemManager.getCpsVersion(nestId, i));
            }
        } else {
            cpsVersionList.add(systemManager.getCpsVersion(nestId, 0));
        }
        return cpsVersionList;
    }

    /**
     * 获取无人机推拉流详情
     */
    private List<BaseNestOutDTO.UavPushStreamInfo> getUavPushStreamInfo(String nestId) {
        List<BaseNestOutDTO.UavPushStreamInfo> uavInfoList = Lists.newArrayList();
        List<BaseUavNestRefOutDO.EntityOutDO> uavList = baseUavNestRefManager.selectListByNestId(nestId);
        for (BaseUavNestRefOutDO.EntityOutDO uavEntity : uavList) {
            String streamId = uavEntity.getStreamId();
            String uavId = uavEntity.getUavId();
            PushStreamInfoOutDO streamInfoDO = mediaManager.fetchPushStreamInfo(streamId);
            BaseNestOutDTO.UavPushStreamInfo uavInfo = new BaseNestOutDTO.UavPushStreamInfo();
            uavInfo.setUavId(uavId);
            uavInfo.setServerId(streamInfoDO.getServerId());
            uavInfo.setStreamId(streamInfoDO.getStreamId());
            uavInfo.setPushStreamUrl(streamInfoDO.getPushUrl());
            uavInfo.setPullStreamUrl(streamInfoDO.getPullUrl());
            uavInfoList.add(uavInfo);
        }
        return uavInfoList;
    }

    /**
     * 获取监控设备信息
     */
    private void setDeviceInfo(String nestId, BaseNestOutDTO.WatchDeviceInfo insideDevice, BaseNestOutDTO.WatchDeviceInfo outsideDevice) {
        List<UosNestDeviceRefEntity> nestDeviceRefEntityList = uosNestDeviceRefService.listNestDeviceRef(nestId);
        for (UosNestDeviceRefEntity entity : nestDeviceRefEntityList) {
            Integer deviceUse = entity.getDeviceUse();
            String deviceCode = entity.getDeviceId();
            DeviceInfoDO deviceInfoDO = mediaManager.queryGbDeviceInfos(deviceCode);
            if (Objects.equals(deviceUse, DriveUseEnum.INSIDE.getCode())) {
                if (!ObjectUtils.isEmpty(deviceInfoDO)) {
                    insideDevice.setDeviceCode(deviceInfoDO.getDeviceCode());
                    insideDevice.setChannelCode(deviceInfoDO.getChannelCode());

                }
            } else if (Objects.equals(deviceUse, DriveUseEnum.OUTSIDE.getCode())) {
                if (!ObjectUtils.isEmpty(deviceInfoDO)) {
                    outsideDevice.setDeviceCode(deviceInfoDO.getDeviceCode());
                    outsideDevice.setChannelCode(deviceInfoDO.getChannelCode());
                }
            }
        }
    }

    /**
     * 获取监控推拉流
     */
    private void setPushStreamInfo(String nestId, BaseNestOutDTO.WatchDeviceInfo insideDevice, BaseNestOutDTO.WatchDeviceInfo outsideDevice) {
        List<UosNestStreamRefEntity> nestStreamRefEntityList = uosNestDeviceRefService.listNestStreamRef(nestId);
        for (UosNestStreamRefEntity entity : nestStreamRefEntityList) {
            String streamId = entity.getStreamId();
            Integer streamUse = entity.getStreamUse();
            PushStreamInfoOutDO streamInfoDO = mediaManager.fetchPushStreamInfo(streamId);
            if (Objects.equals(streamUse, DriveUseEnum.INSIDE.getCode())) {
                if (!ObjectUtils.isEmpty(streamInfoDO)) {
                    insideDevice.setPullStreamUrl(streamInfoDO.getPullUrl());
                    insideDevice.setPushStreamUrl(streamInfoDO.getPushUrl());
                }
            } else if (Objects.equals(streamUse, DriveUseEnum.OUTSIDE.getCode())) {
                if (!ObjectUtils.isEmpty(streamInfoDO)) {
                    outsideDevice.setPullStreamUrl(streamInfoDO.getPullUrl());
                    outsideDevice.setPushStreamUrl(streamInfoDO.getPushUrl());
                }
            }
        }
    }

    @Override
    public void nestBattery(BaseNestInDTO.BatteryInDTO batteryInDTO) {

        if (batteryInDTO.getAlarmCircleNum() == null || batteryInDTO.getForbiddenCircleNum() == null) {
            return;
        }
        NestParamInDO.BatteryInDO batteryInDO = new NestParamInDO.BatteryInDO();
        BeanUtils.copyProperties(batteryInDTO, batteryInDO);
        nestParamManager.updateBatteryByNestId(batteryInDO);
    }

    @Override
    public BaseNestOutDTO.BatteryDetailOutDTO nestBatteryDetail(String nestId) {

        BaseNestOutDTO.BatteryDetailOutDTO batteryDetailOutDTO = new BaseNestOutDTO.BatteryDetailOutDTO();
        List<NestParamOutDO> nestParamOutDOList = nestParamManager.selectListByNestIdCollection(Lists.newArrayList(nestId));
        if (CollUtil.isEmpty(nestParamOutDOList)) {
            return batteryDetailOutDTO;
        }
        BeanUtils.copyProperties(nestParamOutDOList.get(0), batteryDetailOutDTO);
        return batteryDetailOutDTO;
    }

    @Override
    public void nestTypeEdit(BaseNestInDTO.NestTypeInDTO nestTypeInDTO) {

        BaseNestTypeInfoInDO baseNestTypeInfoInDO = new BaseNestTypeInfoInDO();
        BeanUtils.copyProperties(nestTypeInDTO, baseNestTypeInfoInDO);
        baseNestTypeInfoManager.edit(baseNestTypeInfoInDO);
    }

    @Override
    public List<BaseNestOutDTO.NestTypeOutDTO> nestTypeDetail() {

        List<BaseNestTypeInfoOutDO> typeInfoOutDOList = baseNestTypeInfoManager.selectAll();

        List<BaseNestOutDTO.NestTypeOutDTO> nestTypeOutDTOList = Lists.newLinkedList();
        for (BaseNestTypeInfoOutDO baseNestTypeInfoOutDO : typeInfoOutDOList) {
            BaseNestOutDTO.NestTypeOutDTO outDTO = new BaseNestOutDTO.NestTypeOutDTO();
            BeanUtils.copyProperties(baseNestTypeInfoOutDO, outDTO);
            nestTypeOutDTOList.add(outDTO);
        }
        return nestTypeOutDTOList;
    }

    @Override
    public BaseNestInDTO.AdminNestTypeCountOutDto getTypeCount(BaseNestInDTO.ListInDTO listInDTO) {
        BaseNestInDTO.AdminNestTypeCountOutDto  dto=new BaseNestInDTO.AdminNestTypeCountOutDto();
        BaseNestInDO.ListInDO listInDO = new BaseNestInDO.ListInDO();
        listInDO.setUserOrgCode(listInDTO.getUserOrgCode());
        listInDO.setOrgCode(listInDTO.getOrgCode());
        listInDO.setName(listInDTO.getName());
        listInDO.setNumber(listInDTO.getNumber());
        listInDO.setUuid(listInDTO.getUuid());
        listInDO.setType(listInDTO.getType());
        listInDO.setRegionId(listInDTO.getRegionId());
        listInDO.setPageNo(listInDTO.getPageNo());
        listInDO.setPageSize(listInDTO.getPageSize());
        listInDO.setUavCate(listInDTO.getUavCate());
        listInDO.setUavModel(listInDTO.getUavModel());
        listInDO.setUavType(listInDTO.getUavType());

        long condition = baseNestManager.countByCondition(listInDO);
        if (condition == 0) {
            return dto;
        }
        List<BaseNestOutDO.ListOutDO> listOutDOList = baseNestManager.selectAllCondition(listInDO);
        if (CollUtil.isEmpty(listOutDOList)) {
            return dto;
        }
        List<String> collect = listOutDOList.stream().map(BaseNestOutDO.ListOutDO::getNestId).collect(Collectors.toList());
        List<BaseNestOutDO.BaseNestUavInfoOutDO> nestUavInfoByIds = baseNestManager.getNestUavInfoByIds(collect);
        dto.setNestCount(listOutDOList.size());
        dto.setUavCount(nestUavInfoByIds.size());
        List<BaseNestInDTO.AdminNestTypeMapOutDTO> outDTOS=new ArrayList<>();
        nestUavInfoByIds.stream().collect(Collectors.groupingBy(BaseNestOutDO.BaseNestUavInfoOutDO::getType)).forEach((k,v)->{
            BaseNestInDTO.AdminNestTypeMapOutDTO  outDTO=new BaseNestInDTO.AdminNestTypeMapOutDTO();
            outDTO.setUavType(k);
            outDTO.setValue(v.size());
            outDTOS.add(outDTO);
        });
        dto.setInfos(outDTOS);
        return dto;
    }
}
