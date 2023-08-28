package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.ITrustedAccessTracer;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.mp.entity.QueryCriteriaDo;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.enums.NestFlowTypeEnum;
import com.imapcloud.nest.sdk.CommonNestStateService;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.DJICameraEnum;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.enums.StreamUseEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.dao.entity.BaseNestEntity;
import com.imapcloud.nest.v2.dao.mapper.BaseNestMapper;
import com.imapcloud.nest.v2.dao.po.NestQueryCriteriaPO;
import com.imapcloud.nest.v2.service.*;
import com.imapcloud.nest.v2.service.converter.BaseNestConverter;
import com.imapcloud.nest.v2.service.dto.in.NestFlowUrlInDTO;
import com.imapcloud.nest.v2.service.dto.in.NestQueryInDTO;
import com.imapcloud.nest.v2.service.dto.out.*;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.DjiTslSnParam;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 机巢信息表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
@Slf4j
@Service
public class BaseNestServiceImpl implements BaseNestService {

    @Resource
    private BaseNestMapper baseNestMapper;

    @Resource
    private BaseMqttBrokerService baseMqttBrokerService;

    @Resource
    private NestAccountService nestAccountService;

    @Resource
    private BaseUavNestRefService baseUavNestRefService;

    @Resource
    private BaseUavService baseUavService;

//    @Resource
//    private MediaStreamService mediaStreamService;

    @Resource
    private UosRegionService uosRegionService;


    @Resource
    private CommonNestStateService commonNestStateService;

    @Resource
    private NestOrgRefService nestOrgRefService;

    @Resource
    private RedisService redisService;

    @Resource
    private UosNestStreamRefService uosNestStreamRefService;

    @Override
    public String getNestUuidByNestIdInCache(String nestId) {
        if (Objects.nonNull(nestId)) {
            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.UUID_BY_ID_CACHE_NEW, nestId);
            String nestUuid = (String) redisService.get(redisKey);
            if (Objects.isNull(nestUuid)) {
                nestUuid = baseNestMapper.selectUuidByNestId(nestId);
                if (Objects.nonNull(nestUuid)) {
                    redisService.set(redisKey, nestUuid);
                }
            }
            return nestUuid;
        }
        return null;
    }

    @Override
    public ComponentManager getComponentManagerByNestId(String nestId) {
        String nestUuid = getNestUuidByNestIdInCache(nestId);
        if (Objects.nonNull(nestUuid)) {
            return ComponentManagerFactory.getInstance(nestUuid);
        }
        return null;
    }

    @Override
    public List<MqttInitParamOutDTO> listMqttInitParams(List<String> nestIdList) {
        if (CollectionUtil.isNotEmpty(nestIdList)) {
            List<BaseNestEntity> nestEntList = baseNestMapper.batchSelects(nestIdList);
            if (CollectionUtil.isEmpty(nestEntList)) {
                return Collections.emptyList();
            }
            Map<String, Integer> nestTypeMap = nestEntList.stream().collect(Collectors.toMap(BaseNestEntity::getNestId, BaseNestEntity::getType));
            List<BaseUavInfoOutDTO> baseUavInfoOutDTOS = baseUavService.listUavInfosByNestIds(nestIdList);
            //这里只取大疆类型\pilot基站的遥控器与无人机sn
            Map<String, String> nestRcSnMap = baseUavInfoOutDTOS.stream()
                    .filter(dto -> StrUtil.isNotEmpty(dto.getRcNumber()) && (Objects.equals(NestTypeEnum.DJI_DOCK.getValue(), nestTypeMap.get(dto.getNestId()))
                            || Objects.equals(NestTypeEnum.DJI_PILOT.getValue(), nestTypeMap.get(dto.getNestId()))) )
                    .collect(Collectors.toMap(BaseUavInfoOutDTO::getNestId, BaseUavInfoOutDTO::getRcNumber, (oldValue, newValue) -> newValue));
            Map<String, String> nestUavSnMap = baseUavInfoOutDTOS.stream()
                    .filter(dto -> StrUtil.isNotEmpty(dto.getUavNumber()) && (Objects.equals(NestTypeEnum.DJI_DOCK.getValue(), nestTypeMap.get(dto.getNestId()))
                            || Objects.equals(NestTypeEnum.DJI_PILOT.getValue(), nestTypeMap.get(dto.getNestId()))) )
                    .collect(Collectors.toMap(BaseUavInfoOutDTO::getNestId, BaseUavInfoOutDTO::getUavNumber, (oldValue, newValue) -> newValue));
            Map<String, Integer> nestCameraMap = baseUavInfoOutDTOS.stream()
                    .filter(dto -> StrUtil.isNotEmpty(dto.getCameraName()) && (Objects.equals(NestTypeEnum.DJI_DOCK.getValue(), nestTypeMap.get(dto.getNestId()))
                            || Objects.equals(NestTypeEnum.DJI_PILOT.getValue(), nestTypeMap.get(dto.getNestId()))) )
                    .collect(Collectors.toMap(BaseUavInfoOutDTO::getNestId
                            , dto -> {
                                //M30为52
                                if(DJICameraEnum.M30_CAMERA.getCode().equals(dto.getCameraName())){
                                    return DJICameraEnum.M30_CAMERA.getPayloadEnumValue();
                                }
                                //其他为53
                                return DJICameraEnum.M30T_CAMERA.getPayloadEnumValue();
                            }
                            , (oldValue, newValue) -> newValue));


            List<String> mqttIdList = nestEntList.stream().map(BaseNestEntity::getMqttBrokerId).collect(Collectors.toList());
            List<MqttBrokerInfoOutDTO> mqttBrokerInfoOutDTOS = baseMqttBrokerService.listMqttBrokerInfos(mqttIdList);
            if (CollectionUtil.isNotEmpty(mqttBrokerInfoOutDTOS)) {
                Map<String, MqttBrokerInfoOutDTO> mqttMap = mqttBrokerInfoOutDTOS.stream().collect(Collectors.toMap(MqttBrokerInfoOutDTO::getMqttBrokerId, mqtt -> mqtt));
                List<MqttInitParamOutDTO> paramList = nestEntList.stream()
                        .filter(ne -> Objects.nonNull(mqttMap.get(ne.getMqttBrokerId())))
                        .map(ne -> {
                            String rcSn = nestRcSnMap.get(ne.getNestId());
                            String uavSn = nestUavSnMap.get(ne.getNestId());
                            Integer cameraType = nestCameraMap.get(ne.getNestId());
                            MqttBrokerInfoOutDTO mqttBrokerInfoOutDTO = mqttMap.get(ne.getMqttBrokerId());
                            return MqttInitParamOutDTO.builder()
                                    .nestType(ne.getType())
                                    .nestUuid(ne.getUuid())
                                    .username(mqttBrokerInfoOutDTO.getUsername())
                                    .password(mqttBrokerInfoOutDTO.getPassword())
                                    .mqttBrokerUrl(mqttBrokerInfoOutDTO.getMqttBrokerInnerUrl())
                                    .djiTslSnParam(DjiTslSnParam.builder()
                                            .dockSn(ne.getUuid())
                                            .rcSn(rcSn)
                                            .uavSn(uavSn)
                                            .cameraType(cameraType)
                                            .build())
                                    .build();
                        }).collect(Collectors.toList());
                return paramList;
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<BaseNestListOutDTO> listNestInfos() {
        //0、查询用户与基站关联表
        String visitorId = TrustedAccessTracerHolder.get().getAccountId();
        List<String> nestIdList = nestAccountService.listNestIdByAccountId(visitorId);
        if (CollectionUtil.isEmpty(nestIdList)) {
            return Collections.emptyList();
        }

        //1、查询基站表
        LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class).in(BaseNestEntity::getNestId, nestIdList);
        List<BaseNestEntity> nestEntList = baseNestMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(nestEntList)) {
            return Collections.emptyList();
        }
        //2、查询无人机与基站关联表,查询无人机表
        List<BaseNestUavOutDTO> baseNestUavOutDTOList = baseUavNestRefService.listNestUavIds(nestIdList);
        if (CollectionUtil.isEmpty(baseNestUavOutDTOList)) {
            return Collections.emptyList();
        }
        List<String> uavIdList = baseNestUavOutDTOList.stream().map(BaseNestUavOutDTO::getUavId).collect(Collectors.toList());
        List<BaseUavInfoOutDTO> baseUavInfoOutDTOList = baseUavService.listUavInfos(uavIdList);
        //3、查询流媒体表
        nestEntList = nestEntList.stream().filter(ent -> StrUtil.isNotEmpty(ent.getUuid())).collect(Collectors.toList());
//        List<String> innerStreamIdList = nestEntList.stream().map(BaseNestEntity::getInnerStreamId).filter(Objects::nonNull).collect(Collectors.toList());
//        List<String> outerStreamIdList = nestEntList.stream().map(BaseNestEntity::getOuterStreamId).filter(Objects::nonNull).collect(Collectors.toList());
//        List<String> uavStreamIdList = baseUavInfoOutDTOList.stream().map(BaseUavInfoOutDTO::getStreamId).collect(Collectors.toList());

//        List<MediaStreamOutDTO> innerStreamList = mediaStreamService.listStreamInfos(innerStreamIdList);
//        List<MediaStreamOutDTO> outerStreamList = mediaStreamService.listStreamInfos(outerStreamIdList);
//        List<MediaStreamOutDTO> uavStreamList = mediaStreamService.listStreamInfos(uavStreamIdList);


        //4、查询区域表
        List<String> regionIdList = nestEntList.stream().map(BaseNestEntity::getRegionId).collect(Collectors.toList());
        List<UosRegionInfoOutDTO> uosRegionInfoOutDTOList = uosRegionService.listRegionInfos(regionIdList);
        if (CollectionUtil.isEmpty(uosRegionInfoOutDTOList)) {
            return Collections.emptyList();
        }
        Map<String, String> regionMap = uosRegionInfoOutDTOList.stream().collect(Collectors.toMap(UosRegionInfoOutDTO::getRegionId, UosRegionInfoOutDTO::getRegionName));

        //5、查询基站单位表
        List<NestOrgRefOutDTO> nestOrgRefOutDTOList = nestOrgRefService.listNestOrgRefs(nestIdList, false);

        //6、一巢三机解析无人机推流地址
        Map<String, String> uavNestIdMap = baseNestUavOutDTOList.stream().collect(Collectors.toMap(BaseNestUavOutDTO::getUavId, BaseNestUavOutDTO::getNestId));
        baseUavInfoOutDTOList.forEach(b -> b.setNestId(uavNestIdMap.get(b.getUavId())));
        Map<String, List<BaseUavInfoOutDTO>> nestUavGroup = baseUavInfoOutDTOList.stream().collect(Collectors.groupingBy(BaseUavInfoOutDTO::getNestId));


        //组装数据
        List<BaseNestListOutDTO> nestListDtoList = nestEntList.stream().map(ent -> {
//            String innerStreamUrl = innerStreamList.stream().filter(in -> in.getStreamId().equals(ent.getInnerStreamId())).findFirst().map(MediaStreamOutDTO::getStreamPullUrl).orElse("");
//            String outerStreamUrl = outerStreamList.stream().filter(in -> in.getStreamId().equals(ent.getOuterStreamId())).findFirst().map(MediaStreamOutDTO::getStreamPullUrl).orElse("");
//            String uavStreamUrl = "";
//            Map<String, String> g503PicTranMap = Collections.emptyMap();
//            if (NestTypeEnum.G503.getValue() == ent.getType()) {
//                //G503机型有三个无人机推流地址
//                List<BaseUavInfoOutDTO> baseUavInfoOutDTOS = nestUavGroup.get(ent.getNestId());
//                if (Objects.nonNull(baseUavInfoOutDTOS)) {
//                    g503PicTranMap = new HashMap<>(4);
//                    for (BaseUavInfoOutDTO OutDto : baseUavInfoOutDTOS) {
//                        String uavId = OutDto.getUavId();
//                        String uavStreamId = baseUavInfoOutDTOList.stream().filter(dto -> dto.getUavId().equals(uavId)).findFirst().map(BaseUavInfoOutDTO::getStreamId).orElse(null);
//                        String g503uavStreamUrl = uavStreamList.stream().filter(in -> in.getStreamId().equals(uavStreamId)).findFirst().map(MediaStreamOutDTO::getStreamPullUrl).orElse("");
//                        g503PicTranMap.put(String.valueOf(OutDto.getWhich()), g503uavStreamUrl);
//                    }
//                }
//            } else {
//                String uavId = baseNestUavOutDTOList.stream().filter(dto -> dto.getNestId().equals(ent.getNestId())).findFirst().map(BaseNestUavOutDTO::getUavId).orElse(null);
//                String uavStreamId = baseUavInfoOutDTOList.stream().filter(dto -> dto.getUavId().equals(uavId)).findFirst().map(BaseUavInfoOutDTO::getStreamId).orElse(null);
//                uavStreamUrl = uavStreamList.stream().filter(in -> in.getStreamId().equals(uavStreamId)).findFirst().map(MediaStreamOutDTO::getStreamPullUrl).orElse("");
//            }

            List<String> orgCodeList = nestOrgRefOutDTOList.stream().filter(dto -> Objects.equals(dto.getNestId(), ent.getNestId())).map(NestOrgRefOutDTO::getOrgCode).collect(Collectors.toList());

            BaseNestListOutDTO build = BaseNestListOutDTO.builder()
                    .nestId(ent.getNestId())
                    .nestName(ent.getName())
                    .uuid(ent.getUuid())
                    .alt(ent.getAltitude())
                    .lat(ent.getLatitude())
                    .lon(ent.getLongitude())
                    .aglAltitude(ent.getAglAltitude())
                    .nestAddress(ent.getAddress())
                    .nestStatus(-1)
                    .nestType(ent.getType())
                    .showStatus(ent.getShowStatus())
                    .nestNumber(ent.getNumber())
//                    .picTranUrl(uavStreamUrl)
//                    .innerVideoUrl(innerStreamUrl)
//                    .outerVideoUrl(outerStreamUrl)
                    .unitIds(orgCodeList)
                    .regionId(ent.getRegionId())
                    .regionName(regionMap.get(ent.getRegionId()))
                    .uavIds(nestUavGroup.getOrDefault(ent.getNestId(), Collections.emptyList()).stream().map(BaseUavInfoOutDTO::getUavId).collect(Collectors.toList()))
                    .build();

//            if (StringUtils.hasLength(uavStreamUrl)) {
//                build.setPicTranUrl(uavStreamUrl);
//            }
//
//            if (!CollectionUtils.isEmpty(g503PicTranMap)) {
//                build.setG503PicTranMap(g503PicTranMap);
//            }

            return build;
        }).collect(Collectors.toList());

        return nestListDtoList;
    }

    @Override
    public BaseNestListOutDTO getNestAndRegion(String nestUuid) {
        //1、查询基站表
        LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class).eq(BaseNestEntity::getUuid, nestUuid);
        BaseNestEntity baseNestEntity = baseNestMapper.selectOne(wrapper);
        if (Objects.isNull(baseNestEntity)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_QUERY_NEST.getContent()));
        }
        //2、查询无人机与基站关联表,查询无人机表
        BaseUavInfoOutDTO baseUavInfoOutDTO = baseUavService.getUavInfoByNestId(baseNestEntity.getNestId());

        //3、查询流媒体表
//        List<String> streamIdList = Arrays.asList(baseNestEntity.getInnerStreamId(), baseNestEntity.getOuterStreamId(), baseUavInfoOutDTO.getStreamId());
//        List<MediaStreamOutDTO> mediaStreamOutDTOList = mediaStreamService.listStreamInfos(streamIdList);
//        String innerStreamUrl = "";
//        String outerStreamUrl = "";
//        String uavStreamUrl = "";
//        if (CollectionUtil.isNotEmpty(mediaStreamOutDTOList)) {
//            innerStreamUrl = mediaStreamOutDTOList.stream()
//                    .filter(dto -> dto.getStreamId().equals(baseNestEntity.getInnerStreamId()))
//                    .findFirst()
//                    .map(MediaStreamOutDTO::getStreamPullUrl)
//                    .orElse("");
//
//            outerStreamUrl = mediaStreamOutDTOList.stream()
//                    .filter(dto -> dto.getStreamId().equals(baseNestEntity.getOuterStreamId()))
//                    .findFirst()
//                    .map(MediaStreamOutDTO::getStreamPullUrl)
//                    .orElse("");
//
//            uavStreamUrl = mediaStreamOutDTOList.stream()
//                    .filter(dto -> dto.getStreamId().equals(baseUavInfoOutDTO.getStreamId()))
//                    .findFirst()
//                    .map(MediaStreamOutDTO::getStreamPullUrl)
//                    .orElse("");
//        }

        //4、查询区域表
        UosRegionQueryInfoOutDTO regionDTO = uosRegionService.queryRegionInfo(baseNestEntity.getRegionId());

        //5、查询基站单位表
        return BaseNestListOutDTO.builder()
                .nestName(baseNestEntity.getName())
                .nestId(baseNestEntity.getNestId())
                .uuid(baseNestEntity.getUuid())
                .nestType(baseNestEntity.getType())
                .nestAddress(baseNestEntity.getAddress())
                .nestStatus(-1)
                .showStatus(baseNestEntity.getShowStatus())
                .nestNumber(baseNestEntity.getNumber())
                .alt(baseNestEntity.getAltitude())
                .lat(baseNestEntity.getLatitude())
                .lon(baseNestEntity.getLongitude())
                .regionId(baseNestEntity.getRegionId())
                .regionName(regionDTO == null ? "未知" : regionDTO.getRegionName())
//                .innerVideoUrl(innerStreamUrl)
//                .outerVideoUrl(outerStreamUrl)
//                .picTranUrl(uavStreamUrl)
                .uavIds(Collections.singletonList(baseUavInfoOutDTO.getUavId()))
                .build();
    }

    @Override
    public boolean setShowStatusByNestId(String nestId, Integer showStatus) {
        if (Objects.nonNull(nestId) && Objects.nonNull(showStatus)) {
            LambdaUpdateWrapper<BaseNestEntity> wrapper = Wrappers.lambdaUpdate(BaseNestEntity.class).eq(BaseNestEntity::getNestId, nestId);
            BaseNestEntity baseNestEntity = new BaseNestEntity();
            baseNestEntity.setShowStatus(showStatus);
            int res = baseNestMapper.update(baseNestEntity, wrapper);
            return res > 0;
        }
        return false;
    }

    @Override
    public boolean setShowStatusByRegionId(String regionId, Integer showStatus) {
        if (Objects.nonNull(regionId) && Objects.nonNull(showStatus)) {
            LambdaUpdateWrapper<BaseNestEntity> wrapper = Wrappers.lambdaUpdate(BaseNestEntity.class).eq(BaseNestEntity::getRegionId, regionId);
            BaseNestEntity baseNestEntity = new BaseNestEntity();
            baseNestEntity.setShowStatus(showStatus);
            baseNestEntity.setShowStatus(showStatus);
            int res = baseNestMapper.update(baseNestEntity, wrapper);
            return res > 0;
        }
        return false;
    }

    @Override
    public List<NestFlowUrlOutDTO> listNestFlowUrls(NestFlowUrlInDTO dto) {
        if (Objects.nonNull(dto)) {
            if (CollectionUtil.isEmpty(dto.getNestIdList())) {
                return Collections.emptyList();
            }
            if (NestFlowTypeEnum.INSIDE_URL.equals(dto.getNestFlowTypeEnum())) {
                return listNestInnerFlowUrls(dto.getNestIdList());
            }

            if (NestFlowTypeEnum.OUTSIDE_URL.equals(dto.getNestFlowTypeEnum())) {
                return listNestOuterFlowUrls(dto.getNestIdList());
            }

            if (NestFlowTypeEnum.RTMP_URL.equals(dto.getNestFlowTypeEnum())) {
                return listUavFlowUrls(dto.getNestIdList());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public String getNestNameByUuid(String nestUuid) {
        if (Objects.nonNull(nestUuid)) {
            LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class).eq(BaseNestEntity::getUuid, nestUuid).select(BaseNestEntity::getName);
            BaseNestEntity baseNestEntity = baseNestMapper.selectOne(wrapper);
            if (Objects.nonNull(baseNestEntity)) {
                return baseNestEntity.getName();
            }
        }
        return null;
    }

    @Override
    public String getNestNumberByUuid(String nestUuid) {
        if (Objects.nonNull(nestUuid)) {
            LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class).eq(BaseNestEntity::getUuid, nestUuid).select(BaseNestEntity::getNumber);
            BaseNestEntity baseNestEntity = baseNestMapper.selectOne(wrapper);
            if (Objects.nonNull(baseNestEntity)) {
                return baseNestEntity.getNumber();
            }
        }
        return null;
    }

    @Override
    public boolean setNestNumberByUuid(String nestUuid, String nestNumber) {
        if (Objects.nonNull(nestUuid)) {
            LambdaUpdateWrapper<BaseNestEntity> wrapper = Wrappers.lambdaUpdate(BaseNestEntity.class).eq(BaseNestEntity::getUuid, nestUuid);
            BaseNestEntity baseNestEntity = new BaseNestEntity();
            baseNestEntity.setNumber(nestNumber);
            int update = baseNestMapper.update(baseNestEntity, wrapper);
            return update > 0;
        }
        return false;
    }

    @Override
    public StartMissionNestInfoOutDTO getStartMissionParamByMissionId(Integer missionId) {
        if (Objects.nonNull(missionId)) {
            BaseNestEntity baseNestEntity = baseNestMapper.selectNestByMissionId(missionId);
            if (Objects.nonNull(baseNestEntity)) {
                StartMissionNestInfoOutDTO dto = StartMissionNestInfoOutDTO.builder()
                        .nestType(baseNestEntity.getType())
                        .nestId(baseNestEntity.getNestId())
                        .nestUuid(baseNestEntity.getUuid())
                        .build();

                return dto;
            }
        }
        return null;
    }

    @Override
    public List<NestQueryOutDTO> searchNestFromDb(NestQueryInDTO condition) {
        ITrustedAccessTracer trustedAccessTracer = TrustedAccessTracerHolder.get();
        String accountId = trustedAccessTracer.getAccountId();
        List<BaseNestEntity> baseNestEntities = baseNestMapper.selectNestByKeyword(Long.valueOf(accountId), condition.getKeyword());
        if (CollectionUtil.isNotEmpty(baseNestEntities)) {
            List<NestQueryOutDTO> collect = baseNestEntities.stream().map(BaseNestConverter.INSTANCES::convertNestQueryOutDTO).collect(Collectors.toList());
            return collect;
        }
        return Collections.emptyList();
    }

    @Override
    public Long countTotalByCondition(QueryCriteriaDo<NestQueryCriteriaPO> queryCriteriaPO) {

        return null;
    }

    @Override
    public NestTypeEnum getNestTypeByUuidCache(String nestUuid) {
        if (StringUtils.hasLength(nestUuid)) {
            String redisKey = RedisKeyConstantList.NEST_TYPE;
            Integer nestType = (Integer) redisService.hGet(redisKey, nestUuid);
            if (Objects.isNull(nestType)) {
                LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class)
                        .eq(BaseNestEntity::getUuid, nestUuid)
                        .eq(BaseNestEntity::getDeleted, false)
                        .select(BaseNestEntity::getType);
                BaseNestEntity baseNestEntity = baseNestMapper.selectOne(wrapper);
                if (Objects.nonNull(baseNestEntity)) {
                    nestType = baseNestEntity.getType();
                    if (Objects.nonNull(nestType) && nestType != -1) {
                        redisService.hSet(redisKey, nestUuid, nestType);
                    }
                }
            }
            return NestTypeEnum.getInstance(nestType);
        }
        return NestTypeEnum.UNKNOWN;
    }

    @Override
    public NestTypeEnum getNestTypeByNestIdCache(String nestId) {
        if (StringUtils.hasLength(nestId)) {
            LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class)
                    .eq(BaseNestEntity::getNestId, nestId)
                    .select(BaseNestEntity::getUuid);

            BaseNestEntity baseNestEntity = baseNestMapper.selectOne(wrapper);
            if (Objects.nonNull(baseNestEntity) && Objects.nonNull(baseNestEntity.getUuid())) {
                return getNestTypeByUuidCache(baseNestEntity.getUuid());
            }
        }
        return NestTypeEnum.UNKNOWN;
    }

    @Override
    public NestTypeEnum getNestType(String nestId) {
        if (StringUtils.hasLength(nestId)) {
            LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class)
                    .eq(BaseNestEntity::getNestId, nestId)
                    .select(BaseNestEntity::getType);

            BaseNestEntity baseNestEntity = baseNestMapper.selectOne(wrapper);
            return NestTypeEnum.getInstance(baseNestEntity.getType());
        }
        return NestTypeEnum.UNKNOWN;
    }

    /**
     * 根据nestId查询基站类型
     *
     * @param nestIdList
     * @return
     */
    @Override
    public Map<String, Integer> getNestTypeMap(List<String> nestIdList) {
        LambdaQueryWrapper queryWrapper = Wrappers.lambdaQuery(BaseNestEntity.class)
                .in(BaseNestEntity::getNestId , nestIdList)
                .select(BaseNestEntity::getNestId , BaseNestEntity::getType);
        List<BaseNestEntity> baseNestEntityList = this.baseNestMapper.selectList(queryWrapper);
        Map<String, Integer> nestIdToTypeMap = baseNestEntityList.stream().collect(Collectors.toMap(BaseNestEntity::getNestId
                ,BaseNestEntity::getType
                ,(oldValue,newValue)-> newValue));
        return nestIdToTypeMap;
    }

    @Override
    public BaseNestInfoOutDTO findNestByUavId(String uavId) {
        return baseNestMapper.findNestByUavId(uavId);
    }

    @Override
    public String findDJIStreamId(String nestId) {
        return baseNestMapper.findDJIStreamId(nestId);
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
                if (StrUtil.isNotEmpty(nestUuid)) {
                    LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class).eq(BaseNestEntity::getUuid, nestUuid).select(BaseNestEntity::getMaintenanceStatus);
                    BaseNestEntity baseNestEntity = baseNestMapper.selectOne(wrapper);
                    if (Objects.nonNull(baseNestEntity)) {
                        maintenanceState = baseNestEntity.getMaintenanceStatus();
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
    public Integer getMaintenanceStateByNestId(String nestId) {
        if (Objects.nonNull(nestId)) {
            LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class).eq(BaseNestEntity::getNestId, nestId).select(BaseNestEntity::getMaintenanceStatus);
            BaseNestEntity baseNestEntity = baseNestMapper.selectOne(wrapper);
            if (Objects.nonNull(baseNestEntity)) {
                return baseNestEntity.getMaintenanceStatus();
            }
        }
        return 0;
    }

    @Override
    public String getNestNameByUuidInCache(String uuid) {
        if (uuid != null) {
            String nestName = (String) redisService.hGet(RedisKeyConstantList.NEST_UUID_NAME_MAP_KEY, uuid);
            if (nestName == null) {
                nestName = this.getNestNameByUuid(uuid);
                if (nestName != null) {
                    redisService.hSet(RedisKeyConstantList.NEST_UUID_NAME_MAP_KEY, uuid, nestName);
                }
            }
            return nestName;
        }
        return null;
    }

    @Override
    public double getNestAltCache(String nestUuid) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.NEST_ALT_CACHE, nestUuid);
        Double nestAlt = (Double) redisService.get(redisKey);
        if (nestAlt == null) {
            LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class)
                    .eq(BaseNestEntity::getUuid, nestUuid)
                    .select(BaseNestEntity::getAltitude);
            BaseNestEntity baseNestEntity = baseNestMapper.selectOne(wrapper);

            if (Objects.nonNull(baseNestEntity)) {
                nestAlt = baseNestEntity.getAltitude();
                if (Objects.nonNull(nestAlt)) {
                    redisService.set(redisKey, nestAlt);
                    return nestAlt;
                }
            }
            redisService.set(redisKey, 0.0D, 10);
        }
        return nestAlt == null ? 0 : nestAlt;
    }

    @Override
    public Map<String, String> getNestNameMap(List<String> nestIdList) {
        if (CollectionUtil.isNotEmpty(nestIdList)) {
            LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class)
                    .in(BaseNestEntity::getNestId, nestIdList)
                    .select(BaseNestEntity::getNestId, BaseNestEntity::getName);

            List<BaseNestEntity> baseNestEntities = baseNestMapper.selectList(wrapper);
            if (CollectionUtil.isNotEmpty(baseNestEntities)) {
                Map<String, String> map = baseNestEntities.stream().collect(Collectors.toMap(BaseNestEntity::getNestId, BaseNestEntity::getName));
                return map;
            }
        }
        return Collections.emptyMap();
    }

    @Override
    public Map<String, String> getAllNestNameMap() {
        LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class)
                .select(BaseNestEntity::getNestId, BaseNestEntity::getName);

        List<BaseNestEntity> baseNestEntities = baseNestMapper.selectList(wrapper);
        if (CollectionUtil.isNotEmpty(baseNestEntities)) {
            Map<String, String> map = baseNestEntities.stream().collect(Collectors.toMap(BaseNestEntity::getNestId, BaseNestEntity::getName));
            return map;
        }
        return Collections.emptyMap();
    }

    @Override
    public List<BatchOperNestOutDTO> listBatchOperParam(List<String> nestIdList) {
        if (CollectionUtil.isNotEmpty(nestIdList)) {
            LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class)
                    .in(BaseNestEntity::getNestId, nestIdList)
                    .select(BaseNestEntity::getUuid, BaseNestEntity::getNestId, BaseNestEntity::getName, BaseNestEntity::getType);

            List<BaseNestEntity> baseNestEntities = baseNestMapper.selectList(wrapper);

            if (CollectionUtil.isNotEmpty(baseNestEntities)) {
                List<BatchOperNestOutDTO> collect = baseNestEntities.stream().map(ent -> BatchOperNestOutDTO.builder()
                        .nestId(ent.getNestId())
                        .nestUuid(ent.getUuid())
                        .nestType(ent.getType())
                        .build()).collect(Collectors.toList());
                return collect;
            }

        }
        return Collections.emptyList();
    }

    @Override
    public StartMissionQueueNestInfoOutDTO getStartMissionQueueNestInfoByNestId(String nestId) {
        if (Objects.nonNull(nestId)) {
            LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class)
                    .eq(BaseNestEntity::getNestId, nestId)
                    .select(BaseNestEntity::getNestId, BaseNestEntity::getName, BaseNestEntity::getType, BaseNestEntity::getUuid);

            BaseNestEntity baseNestEntity = baseNestMapper.selectOne(wrapper);
            Optional<StartMissionQueueNestInfoOutDTO> opt = Optional.ofNullable(baseNestEntity).map(ent -> StartMissionQueueNestInfoOutDTO.builder()
                    .nestId(ent.getNestId())
                    .nestUuid(ent.getUuid())
                    .nestType(ent.getType())
                    .nestName(ent.getName())
                    .build());
            if (opt.isPresent()) {
                return opt.get();
            }
        }
        return null;
    }

    @Override
    public StartMissionQueueNestInfoOutDTO getStartMissionQueueNestInfoByUuid(String nestUuid) {
        if (Objects.nonNull(nestUuid)) {
            LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class)
                    .eq(BaseNestEntity::getUuid, nestUuid)
                    .select(BaseNestEntity::getNestId, BaseNestEntity::getName, BaseNestEntity::getType, BaseNestEntity::getUuid);

            BaseNestEntity baseNestEntity = baseNestMapper.selectOne(wrapper);
            Optional<StartMissionQueueNestInfoOutDTO> opt = Optional.ofNullable(baseNestEntity).map(ent -> StartMissionQueueNestInfoOutDTO.builder()
                    .nestId(ent.getNestId())
                    .nestUuid(ent.getUuid())
                    .nestType(ent.getType())
                    .nestName(ent.getName())
                    .build());
            if (opt.isPresent()) {
                return opt.get();
            }
        }
        return null;
    }

//    @Override
//    public CreateRelayParamOutDTO getCreateRelayParamCache(String nestId, Integer uavWhich) {
//        if (Objects.nonNull(nestId)) {
//            String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.CREATE_RELAY_PRAM, nestId);
//            CreateRelayParamOutDTO dto = (CreateRelayParamOutDTO) redisService.get(redisKey);
//            if (Objects.isNull(dto)) {
//                LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class).eq(BaseNestEntity::getNestId, nestId)
//                        .select(BaseNestEntity::getName, BaseNestEntity::getUuid);
//                BaseNestEntity baseNestEntity = baseNestMapper.selectOne(wrapper);
//                String uavStreamId = baseUavService.getUavStreamIdByNestId(nestId, AirIndexEnum.getInstance(uavWhich));
//                String uavStreamPullUrl = mediaStreamService.getStreamPullUrl(uavStreamId);
//                if (Objects.nonNull(baseNestEntity) && Objects.nonNull(uavStreamId)) {
//                    dto = CreateRelayParamOutDTO.builder()
//                            .nestName(baseNestEntity.getName())
//                            .nestUuid(baseNestEntity.getUuid())
//                            .uavStreamUrl(uavStreamPullUrl)
//                            .build();
//                    redisService.set(redisKey, dto, 30, TimeUnit.MINUTES);
//                }
//            }
//            return dto;
//        }
//        return null;
//    }

    @Override
    public BaseNestLocationOutDTO getNestLocation(String nestId) {
        if (Objects.nonNull(nestId)) {
            LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class).eq(BaseNestEntity::getNestId, nestId)
                    .select(BaseNestEntity::getAltitude, BaseNestEntity::getLatitude, BaseNestEntity::getLongitude);
            BaseNestEntity baseNestEntity = baseNestMapper.selectOne(wrapper);
            Optional<BaseNestLocationOutDTO> opt = Optional.ofNullable(baseNestEntity).map(ent -> BaseNestLocationOutDTO.builder()
                    .alt(ent.getAltitude())
                    .lat(ent.getLatitude())
                    .lng(ent.getLongitude())
                    .build());
            if (opt.isPresent()) {
                return opt.get();
            }
        }
        return null;
    }

    @Override
    public DynamicAirLineOutDTO getDynamicAirLineParam(String nestId) {
        if (Objects.nonNull(nestId)) {
            LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class).eq(BaseNestEntity::getNestId, nestId)
                    .select(BaseNestEntity::getAltitude, BaseNestEntity::getLatitude, BaseNestEntity::getLongitude, BaseNestEntity::getPlanCode);
            BaseNestEntity baseNestEntity = baseNestMapper.selectOne(wrapper);
            Optional<DynamicAirLineOutDTO> opt = Optional.ofNullable(baseNestEntity).map(ent -> DynamicAirLineOutDTO.builder()
                    .alt(ent.getAltitude())
                    .lat(ent.getLatitude())
                    .lng(ent.getLongitude())
                    .planCode(ent.getPlanCode())
                    .build());
            if (opt.isPresent()) {
                return opt.get();
            }
        }
        return null;
    }

    @Override
    public BaseNestInfoOutDTO getBaseNestInfo(String nestId) {
        if (Objects.nonNull(nestId)) {
            LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class).eq(BaseNestEntity::getNestId, nestId);
            BaseNestEntity baseNestEntity = baseNestMapper.selectOne(wrapper);

            Optional<BaseNestInfoOutDTO> opt = Optional.ofNullable(baseNestEntity).map(BaseNestConverter.INSTANCES::convertNestInfoOutDTO);
            if (opt.isPresent()) {
                return opt.get();
            }
        }
        return null;
    }

    @Override
    public BaseNestInfoOutDTO getBaseNestInfoByNestUuid(String nestUuid) {
        if (Objects.nonNull(nestUuid)) {
            LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class).eq(BaseNestEntity::getUuid, nestUuid);
            BaseNestEntity baseNestEntity = baseNestMapper.selectOne(wrapper);

            Optional<BaseNestInfoOutDTO> opt = Optional.ofNullable(baseNestEntity).map(BaseNestConverter.INSTANCES::convertNestInfoOutDTO);
            if (opt.isPresent()) {
                return opt.get();
            }
        }
        return null;
    }

    @Override
    public Boolean setPlanCodeByNestId(String nestId, String planCode) {
        if (Objects.nonNull(nestId) && Objects.nonNull(planCode)) {
            BaseNestEntity baseNestEntity = new BaseNestEntity();
            baseNestEntity.setPlanCode(planCode);
            LambdaUpdateWrapper<BaseNestEntity> wrapper = Wrappers.lambdaUpdate(BaseNestEntity.class).eq(BaseNestEntity::getNestId, nestId);
            int update = baseNestMapper.update(baseNestEntity, wrapper);
            return update > 0;
        }
        return false;
    }

    @Override
    public List<String> listAllNestUuids() {
        LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class).select(BaseNestEntity::getUuid);
        List<BaseNestEntity> baseNestEntities = baseNestMapper.selectList(wrapper);
        if (CollectionUtil.isNotEmpty(baseNestEntities)) {
            List<String> uuidList = baseNestEntities.stream().map(BaseNestEntity::getUuid).collect(Collectors.toList());
            return uuidList;
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> listAllNestIds() {
        LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class).select(BaseNestEntity::getNestId);
        List<BaseNestEntity> baseNestEntities = baseNestMapper.selectList(wrapper);
        if (CollectionUtil.isNotEmpty(baseNestEntities)) {
            List<String> idList = baseNestEntities.stream().map(BaseNestEntity::getNestId).collect(Collectors.toList());
            return idList;
        }
        return Collections.emptyList();
    }

    @Override
    public Boolean setMaintenanceStateByNestId(Integer state, String nestId) {
        if (Objects.nonNull(state) && Objects.nonNull(nestId)) {
            LambdaUpdateWrapper<BaseNestEntity> wrapper = Wrappers.lambdaUpdate(BaseNestEntity.class).eq(BaseNestEntity::getNestId, nestId);
            BaseNestEntity baseNestEntity = new BaseNestEntity();
            baseNestEntity.setMaintenanceStatus(state);
            int update = baseNestMapper.update(baseNestEntity, wrapper);
            return update > 0;
        }
        return false;
    }

    @Override
    public String getNestIdByNestUuid(String nestUuid) {
        if (Objects.nonNull(nestUuid)) {
            LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class)
                    .eq(BaseNestEntity::getUuid, nestUuid)
                    .select(BaseNestEntity::getNestId);

            BaseNestEntity baseNestEntity = baseNestMapper.selectOne(wrapper);
            if (Objects.nonNull(baseNestEntity)) {
                return baseNestEntity.getNestId();
            }
        }
        return null;
    }


    @Override
    public List<NestSimpleOutDTO> listNestInfos(Collection<String> nestIds) {
        if (!CollectionUtils.isEmpty(nestIds)) {
            LambdaQueryWrapper<BaseNestEntity> condition = Wrappers.lambdaQuery(BaseNestEntity.class)
                    .in(BaseNestEntity::getNestId, nestIds)
                    .select(BaseNestEntity::getName, BaseNestEntity::getUuid, BaseNestEntity::getNestId, BaseNestEntity::getNumber, BaseNestEntity::getType);
            List<BaseNestEntity> entities = baseNestMapper.selectList(condition);
            if (!CollectionUtils.isEmpty(entities)) {
                return entities.stream()
                        .map(r -> {
                            NestSimpleOutDTO ns = new NestSimpleOutDTO();
                            ns.setId(r.getNestId());
                            ns.setName(r.getName());
                            ns.setNestNumber(r.getNumber());
                            ns.setNestUuid(r.getUuid());
                            ns.setType(r.getType());
                            return ns;
                        })
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }


    /**
     * 批量获取基站信息
     *
     * @return 基站简要信息
     */
    @Override
    public List<NestSimpleOutDTO> listAllNestInfos() {
        LambdaQueryWrapper<BaseNestEntity> condition = Wrappers.lambdaQuery(BaseNestEntity.class)
                .select(BaseNestEntity::getNestId, BaseNestEntity::getType);
        List<BaseNestEntity> entities = baseNestMapper.selectList(condition);
        if (!CollectionUtils.isEmpty(entities)) {
            return entities.stream()
                    .map(r -> {
                        NestSimpleOutDTO ns = new NestSimpleOutDTO();
                        ns.setId(r.getNestId());
                        ns.setType(r.getType());
                        return ns;
                    })
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<BaseNestInfoOutDTO> listBaseNestInfosByOrgCode(String orgCode) {
        if (Objects.nonNull(orgCode)) {
            List<BaseNestEntity> baseNestEntities = baseNestMapper.selectByOrgCode(orgCode);
            if (CollectionUtil.isNotEmpty(baseNestEntities)) {
                return baseNestEntities.stream().map(BaseNestConverter.INSTANCES::convertNestInfoOutDTO).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public BaseNestInfoOutDTO getBaseNestInfoByUuid(String nestUuid) {
        if (Objects.nonNull(nestUuid)) {
            LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class).eq(BaseNestEntity::getUuid, nestUuid);
            BaseNestEntity baseNestEntity = baseNestMapper.selectOne(wrapper);

            Optional<BaseNestInfoOutDTO> opt = Optional.ofNullable(baseNestEntity).map(BaseNestConverter.INSTANCES::convertNestInfoOutDTO);
            if (opt.isPresent()) {
                //根据nestId获取流信息
                String nestId = baseNestEntity.getNestId();
                opt.get().setInnerStreamId(uosNestStreamRefService.getStreamIdByNestId(nestId, StreamUseEnum.INSIDE.getCode()));
                opt.get().setOuterStreamId(uosNestStreamRefService.getStreamIdByNestId(nestId, StreamUseEnum.OUTSIDE.getCode()));
                return opt.get();
            }
        }
        return null;
    }


    @Override
    public List<String> listAllUuidsCache() {
        String redisKey = RedisKeyConstantList.ALL_NEST_UUID;
        List<String> uuidList = (List<String>) redisService.get(redisKey);
        if (CollectionUtil.isEmpty(uuidList)) {
            LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class).select(BaseNestEntity::getUuid);
            List<BaseNestEntity> baseNestEntities = baseNestMapper.selectList(wrapper);
            if (CollectionUtil.isNotEmpty(baseNestEntities)) {
                uuidList = baseNestEntities.stream().map(BaseNestEntity::getUuid).filter(StrUtil::isNotEmpty).collect(Collectors.toList());
                redisService.set(redisKey, uuidList);
            }
        }
        return uuidList;
    }

    private List<NestFlowUrlOutDTO> listNestInnerFlowUrls(List<String> nestIdList) {
        //1、查询基站表
        LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class)
                .in(BaseNestEntity::getNestId, nestIdList)
                .eq(BaseNestEntity::getShowStatus,1);
        List<BaseNestEntity> nestEntList = baseNestMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(nestEntList)) {
            return Collections.emptyList();
        }
//        List<String> innerStreamIdList = nestEntList.stream().map(BaseNestEntity::getInnerStreamId).collect(Collectors.toList());
//        List<MediaStreamOutDTO> innerStreamList = mediaStreamService.listStreamInfos(innerStreamIdList);
//        Map<String, String> innerStreamMap = innerStreamList.stream().filter(bean -> !StringUtils.isEmpty(bean.getStreamPullUrl()))
//                .collect(Collectors.toMap(MediaStreamOutDTO::getStreamId, MediaStreamOutDTO::getStreamPullUrl));
        return nestEntList.stream().map(ent -> {
//            String innerStreamUrl = innerStreamMap.get(ent.getInnerStreamId());
            NestFlowUrlOutDTO build = NestFlowUrlOutDTO.builder()
                    .nestId(ent.getNestId())
                    .nestUuid(ent.getUuid())
                    .nestName(ent.getName())
                    .type(ent.getType())
//                    .innerStreamUrl(innerStreamUrl)
                    .build();
            return build;
        }).collect(Collectors.toList());
    }

    private List<NestFlowUrlOutDTO> listNestOuterFlowUrls(List<String> nestIdList) {
        //1、查询基站表
        LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class)
                .in(BaseNestEntity::getNestId, nestIdList)
                .eq(BaseNestEntity::getShowStatus,1);
        List<BaseNestEntity> nestEntList = baseNestMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(nestEntList)) {
            return Collections.emptyList();
        }
//        List<String> outerStreamIdList = nestEntList.stream().map(BaseNestEntity::getOuterStreamId).collect(Collectors.toList());
//        List<MediaStreamOutDTO> outerStreamList = mediaStreamService.listStreamInfos(outerStreamIdList);
//        Map<String, String> outerStreamMap = outerStreamList.stream().filter(e->!StringUtils.isEmpty(e.getStreamPullUrl())).collect(Collectors.toMap(MediaStreamOutDTO::getStreamId, MediaStreamOutDTO::getStreamPullUrl));
        return nestEntList.stream().map(ent -> {
//            String outerStreamUrl = outerStreamMap.get(ent.getOuterStreamId());
            NestFlowUrlOutDTO build = NestFlowUrlOutDTO.builder()
                    .nestId(ent.getNestId())
                    .nestUuid(ent.getUuid())
                    .nestName(ent.getName())
                    .type(ent.getType())
//                    .outerStreamUrl(outerStreamUrl)
                    .build();
            return build;
        }).collect(Collectors.toList());
    }

    private List<NestFlowUrlOutDTO> listUavFlowUrls(List<String> nestIdList) {

        log.info("#BaseNestServiceImpl.listUavFlowUrls# nestIdList={}", JSONUtil.toJsonStr(nestIdList));
        //1、查询基站表
        LambdaQueryWrapper<BaseNestEntity> wrapper = Wrappers.lambdaQuery(BaseNestEntity.class)
                .in(BaseNestEntity::getNestId, nestIdList)
                .eq(BaseNestEntity::getShowStatus,1);
        List<BaseNestEntity> nestEntList = baseNestMapper.selectList(wrapper);
        if (CollectionUtil.isEmpty(nestEntList)) {
            return Collections.emptyList();
        }

        //2、查询无人机表
        List<BaseUavInfoOutDTO> baseUavInfoOutDTOList = baseUavService.listUavInfosByNestIds(nestIdList);
//        Map<String, String> nestUavStreamIdMap = baseUavInfoOutDTOList.stream().collect(Collectors.toMap(BaseUavInfoOutDTO::getNestId, BaseUavInfoOutDTO::getStreamId));
        Map<String, List<BaseUavInfoOutDTO>> nestUavStreamGroup = baseUavInfoOutDTOList.stream().collect(Collectors.groupingBy(BaseUavInfoOutDTO::getNestId));


        //3、查询流媒体表
//        List<String> uavStreamIdList = baseUavInfoOutDTOList.stream().map(BaseUavInfoOutDTO::getStreamId).collect(Collectors.toList());
//        List<MediaStreamOutDTO> uavStreamList = mediaStreamService.listStreamInfos(uavStreamIdList);
//        Map<String, String> uavStreamMap = uavStreamList.stream().filter(bean -> !StringUtils.isEmpty(bean.getStreamPullUrl()))
//                .collect(Collectors.toMap(MediaStreamOutDTO::getStreamId, MediaStreamOutDTO::getStreamPullUrl));

//        log.info("#BaseNestServiceImpl.listUavFlowUrls# uavStreamMap={}", JSONUtil.toJsonStr(uavStreamMap));
        return nestEntList.stream().filter(bean -> nestUavStreamGroup.get(bean.getNestId()) != null)
                .map(ent -> {
            List<BaseUavInfoOutDTO> baseUavInfoOutDTOS = nestUavStreamGroup.get(ent.getNestId());
            List<NestFlowUrlOutDTO> list = new ArrayList<>();
            for(BaseUavInfoOutDTO out : baseUavInfoOutDTOS) {
//                String uavStreamUrl = uavStreamMap.get(out.getStreamId());
                NestFlowUrlOutDTO build = NestFlowUrlOutDTO.builder()
                        .nestId(ent.getNestId())
                        .nestUuid(ent.getUuid())
                        .nestName(ent.getName())
                        .type(ent.getType())
//                        .uavStreamUrl(uavStreamUrl)
                        .build();
                list.add(build);
            }

            return list;
        }).flatMap(Collection::stream).collect(Collectors.toList());
    }

}
