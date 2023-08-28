package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.constant.SymbolConstants;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.web.exception.BizParameterException;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.NestGroupStateEnum;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.DriveUseEnum;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.enums.NestDeviceUseEnum;
import com.imapcloud.nest.v2.common.properties.FrpcConfig;
import com.imapcloud.nest.v2.common.properties.GeoaiUosProperties;
import com.imapcloud.nest.v2.dao.entity.BaseUavEntity;
import com.imapcloud.nest.v2.dao.entity.UosNestDeviceRefEntity;
import com.imapcloud.nest.v2.manager.cps.CameraManager;
import com.imapcloud.nest.v2.manager.dataobj.in.BaseNestInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.GimbalAutoFollowDO;
import com.imapcloud.nest.v2.manager.dataobj.in.ListDictItemInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.AccountOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.DictItemInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.VideoPlayInfoOutDO;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import com.imapcloud.nest.v2.manager.feign.BaseServiceClient;
import com.imapcloud.nest.v2.manager.rest.MediaManager;
import com.imapcloud.nest.v2.manager.sql.BaseNestManager;
import com.imapcloud.nest.v2.service.*;
import com.imapcloud.nest.v2.service.converter.UosNestConverter;
import com.imapcloud.nest.v2.service.dto.in.*;
import com.imapcloud.nest.v2.service.dto.out.*;
import com.imapcloud.nest.v2.web.vo.req.NestMediaSaveInfoVO;
import com.imapcloud.nest.v2.web.vo.resp.LivePlayInfoRespVO;
import com.imapcloud.nest.v2.web.vo.resp.NestCodeOperationVO;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.pojo.constant.NestStateEnum;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import com.imapcloud.sdk.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 基站业务接口实现
 *
 * @author Vastfy
 * @date 2022/7/12 14:04
 * @since 1.9.7
 */
@Slf4j
@Service
public class UosNestServiceImpl implements UosNestService {

    @Resource
    private NestService nestService;

    @Resource
    private UosNestFirmwareService uosNestFirmwareService;

    @Resource
    private UosNestStreamRefService uosNestStreamRefService;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private UosRegionService uosRegionService;

    @Resource
    private BaseNestManager baseNestManager;

    @Resource
    private AccountServiceClient accountServiceClient;

    @Resource
    private NestCodeOperationRecordsService nestCodeOperationRecordsService;

    @Resource
    private BaseServiceClient baseServiceClient;

    @Resource
    private CameraManager cameraManager;

    @Resource
    private RedisService redisService;

//    @Resource
//    private MediaStreamService mediaStreamService;
//
//    @Resource
//    private MediaServiceService mediaServiceService;
//
//    @Resource
//    private NodeMediaUtil nodeMediaUtil;

    @Resource
    private UosNestDeviceRefService uosNestDeviceRefService;

    @Resource
    private BaseUavService baseUavService;

    @Resource
    private GeoaiUosProperties geoaiUosProperties;

    @Resource
    private MediaManager mediaManager;

    @Resource
    private UosDeviceService uosDeviceService;

    @Resource
    private BaseUavNestRefService baseUavNestRefService;

    @Override
    public boolean resetCameraSettings(String nestId) {
//        ComponentManager cm = getComponentManager(Integer.valueOf(nestId));
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (Objects.isNull(cm)) {
            return false;
        }
        MqttResult<NullParam> res = cm.getCameraManagerCf().cameraRestoreFactorySet();
        if (res.isSuccess()) {
            return true;
        }
        throw new BizException(String.format("重置基站相机失败：【%s】", res.getMsg()));
    }

    @Override
    public List<RegionNestOutDTO> listGroupByRegion(NestQueryInDTO condition) {
//        List<NestEntity> nestEntities = searchNestFromDb(condition);
        List<NestQueryOutDTO> nestQueryOutDTOList = baseNestService.searchNestFromDb(condition);
        if (!CollectionUtils.isEmpty(nestQueryOutDTOList)) {
            List<String> regionIds = nestQueryOutDTOList.stream().map(NestQueryOutDTO::getRegionId).collect(Collectors.toList());
//            Map<Integer, String> regionNameMap = obtainRegionNameMap(regionIds);
            Map<String, String> regionNameMap = uosRegionService.getRegionNameMap(regionIds);
            Map<String, RegionNestOutDTO> results = new HashMap<>();
//            List<String> nestIds = new ArrayList<>();
            List<String> nestIds = nestQueryOutDTOList.stream().map(NestQueryOutDTO::getNestId).collect(Collectors.toList());
            List<BaseNestUavOutDTO> baseNestUavOutDTOList = baseUavNestRefService.listNestUavIds(nestIds);
            Map<String, List<BaseNestUavOutDTO>> nestUavGroup = Collections.emptyMap();
            if(!CollectionUtils.isEmpty(baseNestUavOutDTOList)){
                nestUavGroup = baseNestUavOutDTOList.stream().collect(Collectors.groupingBy(BaseNestUavOutDTO::getNestId));
            }
            for (NestQueryOutDTO dto : nestQueryOutDTOList) {
//                nestIds.add(dto.getNestId());
                String regionId = dto.getRegionId();
                results.computeIfAbsent(regionId, r -> {
                    RegionNestOutDTO regionNestOutDTO = new RegionNestOutDTO();
                    regionNestOutDTO.setRegionId(regionId);
                    regionNestOutDTO.setRegionName(regionNameMap.getOrDefault(regionId, ""));
                    return regionNestOutDTO;
                });
                RegionNestOutDTO regionNestOutDTO = results.get(regionId);
                if (CollectionUtils.isEmpty(regionNestOutDTO.getNestInfos())) {
                    regionNestOutDTO.setNestInfos(new ArrayList<>());
                }
                NestBasicOutDTO nestBasicOutDTO = new NestBasicOutDTO();
                nestBasicOutDTO.setId(dto.getNestId());
                nestBasicOutDTO.setUuid(dto.getNestUuid());
                nestBasicOutDTO.setName(dto.getNestName());
                nestBasicOutDTO.setType(dto.getNestType());
                nestBasicOutDTO.setState(NestGroupStateEnum.OFF_LINE.getValue());
                nestBasicOutDTO.setBaseState(NestStateEnum.OFF_LINE.getChinese());
                nestBasicOutDTO.setShowStatus(dto.getShowStatus());
                nestBasicOutDTO.setUavIds(nestUavGroup.getOrDefault(dto.getNestId(), Collections.emptyList()).stream().map(BaseNestUavOutDTO::getUavId).collect(Collectors.toList()));
                regionNestOutDTO.getNestInfos().add(nestBasicOutDTO);
            }
            // 批量初始化基站状态
            nestService.asynchronousBatchInitNest(nestIds);
            return new ArrayList<>(results.values());
        }
        return Collections.emptyList();
    }

    @Override
    public PageResultInfo<NestExtFirmwareOutDTO> pageNestInfoWithFirmwares(NestExtFirmwareQueryInDTO condition) {

        BaseNestInDO.ListInDO listInDO = new BaseNestInDO.ListInDO();
        listInDO.setUserOrgCode(TrustedAccessTracerHolder.get().getOrgCode());
        listInDO.setOrgCode(condition.getUnitId());
        listInDO.setKeyword(condition.getKeyword());
        listInDO.setPageNo(condition.getPageNo());
        listInDO.setPageSize(condition.getPageSize());
        if(Objects.equals(NestDeviceUseEnum.INNER.getType(), condition.getDeviceUse())){
            listInDO.setShowStatus(1);
            List<Integer> nestTypes = new ArrayList<>();
            nestTypes.add(NestTypeEnum.G600.getValue());
            nestTypes.add(NestTypeEnum.G900.getValue());
            nestTypes.add(NestTypeEnum.G503.getValue());
            nestTypes.add(NestTypeEnum.G900_CHARGE.getValue());
            // 大疆基站现在归类为巢内监控
            nestTypes.add(NestTypeEnum.DJI_DOCK.getValue());
            listInDO.setTypes(nestTypes);
        }
        else if(Objects.equals(NestDeviceUseEnum.OUTER.getType(), condition.getDeviceUse())){
            listInDO.setShowStatus(1);
            List<Integer> nestTypes = new ArrayList<>();
            nestTypes.add(NestTypeEnum.G600.getValue());
            nestTypes.add(NestTypeEnum.S100_V1.getValue());
            nestTypes.add(NestTypeEnum.G900.getValue());
            nestTypes.add(NestTypeEnum.T50.getValue());
            nestTypes.add(NestTypeEnum.S100_V2.getValue());
            nestTypes.add(NestTypeEnum.G503.getValue());
            nestTypes.add(NestTypeEnum.S110_AUTEL.getValue());
            nestTypes.add(NestTypeEnum.S110_MAVIC3.getValue());
            nestTypes.add(NestTypeEnum.G900_CHARGE.getValue());
            listInDO.setTypes(nestTypes);
        }
        long total = baseNestManager.countByCondition(listInDO);
        List<BaseNestOutDO.ListOutDO> rows = null;
        if (total > 0) {
            rows = baseNestManager.selectByCondition(listInDO);
        }
        // 获取基站最新的固件（CPS和MPS）版本信息
        Map<String, List<FirmwarePackageBasicOutDTO>> nestLfvMappings = getNestLfvMappings(rows);
        return PageResultInfo.of(total, rows)
                .map(r -> {
                    NestExtFirmwareOutDTO result = new NestExtFirmwareOutDTO();
                    result.setId(r.getNestId());
                    result.setUuid(r.getUuid());
                    result.setName(r.getName());
                    result.setType(r.getType());
//                    result.setShowStatus(r.getShowStatus());
                    // 设置基站最新固件版本信息
                    if (nestLfvMappings.containsKey(r.getNestId())) {
                        result.setCurFirmwareInfos(nestLfvMappings.get(r.getNestId()));
                    }
                    return result;
                });
    }

    @Override
    public List<String> listOnLineUsernames(String nestId) {
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        List<String> accountIds = ChannelService.listOnLineAccountIds(nestUuid);
        Result<List<AccountOutDO>> result = accountServiceClient.listAccountInfos(accountIds);
        if (Objects.nonNull(result)) {
            List<AccountOutDO> data = result.getData();
            if (CollectionUtil.isNotEmpty(data)) {
                return data.stream().map(outDo -> {
                    String name = outDo.getName();
                    if (StrUtil.isEmpty(name)) {
                        name = outDo.getAccount();
                    }
                    return name;
                }).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public PageResultInfo<NestCodeOperationVO> listNestCodeOperationRecords(String nestId, Integer currPage, Integer pageSize) {
        PageResultInfo<NestCodeOperationOutDTO> dtoPageResultInfo = nestCodeOperationRecordsService.listRecordsPage(nestId, currPage, pageSize);
        PageResultInfo<NestCodeOperationVO> voPageResultInfo = new PageResultInfo<>();
        voPageResultInfo.setTotal(dtoPageResultInfo.getTotal());
        List<NestCodeOperationOutDTO> records = dtoPageResultInfo.getRecords();
        if (CollectionUtil.isEmpty(records)) {
            voPageResultInfo.setRecords(Collections.emptyList());
            return voPageResultInfo;
        }
        DateTimeFormatter dfDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<String> accountIdList = records.stream().map(NestCodeOperationOutDTO::getCreatorId).distinct().collect(Collectors.toList());
        List<String> nestCodeList = records.stream().map(NestCodeOperationOutDTO::getNestCode).distinct().collect(Collectors.toList());
        //批量查询词典操作
        //TODO 添加缓存
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String language = request.getHeader("Accept-Language");
        String dictCode = "NEST_CODE_TYPE";
        if (StringUtils.hasText(language) && "en-US".equals(language)) dictCode = "NEST_CODE_TYPE_EN";
        Result<List<DictItemInfoOutDO>> baseResult = baseServiceClient.listDictItemInfo(ListDictItemInfoInDO.builder().dictCode(dictCode).itemValues(nestCodeList).build());
        if (Objects.isNull(baseResult)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_QUERY_BASE_STATION_COMMAND_DICTIONARY_ITEMS.getContent()));
        }
        Map<String, String> dictItemMap = baseResult.getData().stream().collect(Collectors.toMap(DictItemInfoOutDO::getItemValue, DictItemInfoOutDO::getItemName));
        //TODO 添加缓存
        Result<List<AccountOutDO>> accountResult = accountServiceClient.listAccountInfos(accountIdList);
        Map<String, String> accountMap = accountResult.getData().stream().collect(Collectors.toMap(AccountOutDO::getAccountId, d -> {
            String name = d.getName();
            if (StrUtil.isEmpty(name)) {
                name = d.getAccount();
            }
            return name;
        }));

        List<NestCodeOperationVO> newRecords = records.stream().map(dto -> {
            String nestCodeName = dictItemMap.get(dto.getNestCode());
            String username = accountMap.get(dto.getCreatorId());
            String operationTime = dfDateTime.format(dto.getCreatorTime());
            return NestCodeOperationVO.builder()
                    .nestCodeName(nestCodeName)
                    .username(username)
                    .operationTime(operationTime)
                    .build();
        }).collect(Collectors.toList());

        voPageResultInfo.setRecords(newRecords);
        return voPageResultInfo;
    }

    @Override
    public Boolean startGimbalAutoFollow(GimbalAutoFollowDTO dto) {
        GimbalAutoFollowDO gimbalAutoFollowDO = UosNestConverter.INSTANCES.gimbalAutoFollowVO2GimbalAutoFollowDO(dto);
        return cameraManager.startGimbalAutoFollow(gimbalAutoFollowDO);
    }

    @Override
    public Boolean cancelGimbalAutoFollow(String nestId) {
        return cameraManager.cancelGimbalAutoFollow(nestId);
    }

    @Override
    public Boolean exitGimbalAutoFollow(String nestId) {
        if (Objects.nonNull(nestId)) {
            String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
            if (Objects.nonNull(nestUuid)) {
                String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_FOLLOW_MODE, nestUuid);
                long decr = redisService.decr(redisKey);
                if (decr < 0) {
                    redisService.set(redisKey, 0);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean execGimbalAutoFollow(String nestId) {
        if (Objects.nonNull(nestId)) {
            String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
            if (Objects.nonNull(nestUuid)) {
                String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.AUTO_FOLLOW_MODE, nestUuid);
                redisService.incr(redisKey);
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean gimbalAutoFollowReset(GimbalAutoFollowResetDTO dto) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(dto.getNestId());
        if (Objects.nonNull(cm)) {
            //云台控制
            MqttResult<NullParam> res = cm.getCameraManagerCf().setCameraAngle(dto.getPitchAngle(), dto.getYamAngle());
            if (!res.isSuccess()) {
                throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RESET_LENS_FAILED.getContent()) + res.getMsg());
            }
            //相机变焦,如果是G600，则不调
            if (!NestTypeEnum.G600.equals(cm.getNestType())) {
                MqttResult<NullParam> res2 = cm.getCameraManagerCf().setPhotoZoomRatio(dto.getRatio());
                if (!res2.isSuccess()) {
                    throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RESET_LENS_FAILED.getContent()) + res2.getMsg());
                }
                return true;
            }
            return true;
        }
        return false;
    }

    @Override
    public Integer getCameraVideoSource(String nestId) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (Objects.nonNull(cm)) {
            //获取相机视频源
            MqttResult<Integer> res = cm.getCameraManagerCf().getCameraSource();
            return res.getRes();
        }
        return null;
    }

//    @Override
//    public Result<Object> resetCameraStream(String nestId) {
//        BaseNestInfoOutDTO baseNestInfo = baseNestService.getBaseNestInfo(nestId);
//        ArrayList<String> streamIds = new ArrayList<>();
//        Integer type = baseNestInfo.getType();
//        //G600、G900、G503、G900充电有巢内
//        if (type == NestTypeEnum.G600.getValue()
//                || type == NestTypeEnum.G900.getValue()
//                || type == NestTypeEnum.G503.getValue()
//                || type == NestTypeEnum.G900_CHARGE.getValue()) {
//            String innerStreamId = baseNestInfo.getInnerStreamId();
//            if (Objects.nonNull(innerStreamId)) {
//                streamIds.add(innerStreamId);
//            }
//        }
//        String outerStreamId = baseNestInfo.getOuterStreamId();
//        if (Objects.nonNull(outerStreamId)) {
//            streamIds.add(outerStreamId);
//        }
//        List<MediaStreamOutDTO> mediaStreamOutDTOS = mediaStreamService.listStreamInfos(streamIds);
//        if (CollectionUtils.isEmpty(mediaStreamOutDTOS)) {
//            return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NEST_MEDIA_STREAM_NOT_FOUND.getContent()));//未查询到流地址
//        }
//        List<MediaServiceEntity> serviceEntities = mediaServiceService.getDomainsByType(MediaServiceTypeEnum.MEDIA_PUSH.getVal());
//        if (CollectionUtils.isEmpty(serviceEntities)) {
//            return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NEST_MEDIA_SERVICE_NOT_FOUND.getContent()));//未查询到流服务器
//        }
//
//        for (MediaStreamOutDTO stream : mediaStreamOutDTOS) {
//            String streamPushUrl = stream.getStreamPushUrl();
//            if (streamPushUrl == null || streamPushUrl.isEmpty()) {
//                return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NEST_MEDIA_PUSH_NOT_SETUP.getContent()));//未配置推流地址
//            }
//            String[] split = streamPushUrl.trim().split("/");
//            String domain = split[2];
//            String host = "http://" + domain + ":8000";
//            String app = split[3];
//            String name = split[4];
//            if (name.contains("?")) {
//                name = name.split("\\?")[0];
//            }
//            Optional<MediaServiceEntity> first = serviceEntities.stream().filter(service -> service.getDomain().equals(domain)).findFirst();
//            if (first.isPresent()) {
//                String token = first.get().getToken();
//                if (token == null || token.isEmpty()) {
//                    log.info("监控服务器:{} 连接密钥异常", first.get().getDomain());
//                    return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NEST_MEDIA_SERVICE_ERROR_TOKEN.getContent()));//监控服务器连接密钥异常
//                }
//                NmsRes nmsRes = nodeMediaUtil.getStream(app, name, token, host);
//                if (nmsRes.getCode().equals(nodeMediaUtil.CODE_SUCCESS)) {
//                    Object data = nmsRes.getData();
//                    JSONObject result = JSONUtil.parseObj(data);
//                    JSONObject appObj = result.getJSONObject(app);
//                    if (appObj != null) {
//                        JSONObject nameObj = appObj.getJSONObject(name);
//                        if (nameObj != null) {
//                            JSONObject publisher = nameObj.getJSONObject("publisher");
//                            if (publisher != null) {
//                                log.info("监控服务器:{}/{} 流在线", app, name);
//                                String id = publisher.getStr("id");
//                                NmsRes delStream = nodeMediaUtil.delStream(id, token, host);
//                                if (delStream.getCode().equals(nodeMediaUtil.CODE_SUCCESS)) {
//                                    log.info("监控服务器:{}/{} 删除流成功", app, name);
//                                } else if (delStream.getCode().equals(nodeMediaUtil.CODE_NOT_FOUND)) {
//                                    log.info("监控服务器:{}/{} 删除流不在线", app, name);
//                                    return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NEST_MEDIA_STREAM_OFFLINE.getContent()));//流不存在
//                                } else {
//                                    log.error("监控服务器:{}/{} 删除流异常", app, name);
//                                    return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NEST_MEDIA_STREAM_DEL_ERR.getContent()));//删除流异常
//                                }
//                            }
//                        }
//                    }
//                } else {
//                    return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NEST_MEDIA_STREAM_OFFLINE.getContent()));//流不存在
//                }
//            } else {
//                log.error("未匹配到推流服务:{}/{}", app, name);
//                return Result.error(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NEST_MEDIA_SERVICE_NOT_MATCH.getContent()));//未匹配到推流服务
//            }
//        }
//        try {
//            Thread.sleep(2000L);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//        return Result.ok();
//    }

    @Override
    public Boolean saveNestMediaInfo(String nestId, NestMediaSaveInfoVO body) {
        String insideDeviceCode = body.getInsideDeviceCode();
        String outsideDeviceCode = body.getOutsideDeviceCode();
        Boolean resNestDeviceRef = saveNestDeviceRef(nestId, insideDeviceCode, outsideDeviceCode);
        Boolean resNestStreamRef = saveNestStreamRef(nestId, body);
        return resNestDeviceRef && resNestStreamRef;
    }

    @Override
    public LivePlayInfoRespVO playNestLive(String nestId, Integer deviceUse) {
        if(!StringUtils.hasText(nestId)){
            throw new BizParameterException("基站ID不能为空");
        }
        Optional<NestDeviceUseEnum> match = NestDeviceUseEnum.findMatch(deviceUse);
        if(!match.isPresent()){
            throw new BizException("基站设备用途无效");
        }
        BaseNestInfoOutDTO baseNestInfo = baseNestService.getBaseNestInfo(nestId);
        if(Objects.isNull(baseNestInfo)){
            throw new BizException("基站信息不存在");
        }
        VideoPlayInfoOutDO videoPlayInfoOutDO = null;
        // 查询基站设备信息
        String deviceCode = uosDeviceService.getDeviceCodeByNestId(nestId, deviceUse);
        if(StringUtils.hasText(deviceCode)){
            // 设备点播
            videoPlayInfoOutDO = mediaManager.playGbDevice(deviceCode);
        }
        // 查询基站推流信息
        if(Objects.isNull(videoPlayInfoOutDO)){
            String pushStreamId = uosNestStreamRefService.getStreamIdByNestId(nestId, deviceUse);
            if(StringUtils.hasText(pushStreamId)){
                // 推流点播
                videoPlayInfoOutDO = mediaManager.playPushStream(pushStreamId);
            }
        }
        if(Objects.nonNull(videoPlayInfoOutDO)){
            LivePlayInfoRespVO respVO = new LivePlayInfoRespVO();
            respVO.setApp(videoPlayInfoOutDO.getApp());
            respVO.setStream(videoPlayInfoOutDO.getStream());
            respVO.setRtsp(videoPlayInfoOutDO.getRtsp());
            respVO.setRtmp(videoPlayInfoOutDO.getRtmp());
            respVO.setHttp(videoPlayInfoOutDO.getHttp());
            respVO.setHttps(videoPlayInfoOutDO.getHttps());
            respVO.setWs(videoPlayInfoOutDO.getWs());
            respVO.setWss(videoPlayInfoOutDO.getWss());
            return respVO;
        }
        throw new BizException("基站未设置设备或推流信息");
    }

    @Override
    public String getNestDeviceSettingUrl(String nestId, String lanIp) {
        ComponentManager cm = baseNestService.getComponentManagerByNestId(nestId);
        if (Objects.isNull(cm)) {
            throw new BizException("基站已离线");
        }
        FrpcConfig frpcConfig = geoaiUosProperties.getFrpc();
        // 优先从redis获取
        String nestPortKey = "geoai:uos:frpc:" + nestId + ":" + DigestUtils.md5DigestAsHex(lanIp.getBytes()).toUpperCase();
        Integer currentPort = (Integer) redisService.get(nestPortKey);
        boolean debugEnabled = log.isDebugEnabled();
        if(debugEnabled){
            log.debug("缓存中获取到基站[{}]内网IP[{}]的映射访问端口为：{}", nestId, lanIp, currentPort);
        }
        String portPoolKey = "geoai:uos:frpc:proxy";
        boolean fromCache = true;
        if(Objects.isNull(currentPort)){
            fromCache = false;
            currentPort = (Integer) redisService.get(portPoolKey);
            if(debugEnabled){
                log.debug("缓存中未获取到基站[{}]内网IP[{}]的映射访问地址，尝试从端口池获取端口：{}", nestId, lanIp, currentPort);
            }
        }
        if(Objects.isNull(currentPort) || currentPort > frpcConfig.getProxyPortMax()){
            // 重置
            currentPort = frpcConfig.getProxyPortMin();
            if(debugEnabled){
                log.debug("端口池未初始化或端口段已达到最大值，重置端口池初始端口：{}",currentPort);
            }
        }
        DeviceSettingParamInDTO param = new DeviceSettingParamInDTO();
        DeviceSettingParamInDTO.FrpcServer frpcServer = new DeviceSettingParamInDTO.FrpcServer();
        frpcServer.setServerAddr(frpcConfig.getServerHost());
        frpcServer.setServerPort(frpcConfig.getServerPort());
        frpcServer.setToken(frpcConfig.getServerPassword());
        param.setCommon(frpcServer);
        DeviceSettingParamInDTO.FrpcClient frpcClient = new DeviceSettingParamInDTO.FrpcClient();
        frpcClient.setName(nestId + "_frpc");
        frpcClient.setLocalIp(lanIp);
        // 由服务端分配
        frpcClient.setRemotePort(currentPort);
        param.setProxy_addes(Collections.singletonList(frpcClient));
        MqttResult<NullParam> res = cm.getGeneralManagerCf().startLanPenetration(param, (int) (frpcConfig.getAcquireTimeout().toMillis() / 1000));
        if (!res.isSuccess()) {
            log.error("内网穿透地址获取失败 ==> {}", res);
            throw new BizException("内网穿透地址获取失败");
        }
        boolean set1 = redisService.set(nestPortKey, currentPort, frpcConfig.getExpiredTimeout().toMinutes(), TimeUnit.MINUTES);
        if(debugEnabled){
            log.debug("更新到基站[{}]内网IP[{}]的映射访问端口：{}，操作结果：{}==>{}", nestId, lanIp, currentPort, nestPortKey, set1);
        }
        // 第一次时需要初始化，更新端口池值
        if(!fromCache){
            // 更新redis值
            boolean set = redisService.set(portPoolKey, (currentPort + 1));
            if(debugEnabled){
                log.debug("更新端口池最新端口为：{}，操作结果：{}", currentPort + 1, set);
            }
        }
        return frpcConfig.getServerSchema() + "://" + frpcConfig.getServerHost() + SymbolConstants.COLON + currentPort;
    }

    private Map<String, List<FirmwarePackageBasicOutDTO>> getNestLfvMappings(List<BaseNestOutDO.ListOutDO> rows) {
        if (!CollectionUtils.isEmpty(rows)) {
            Set<String> nestIds = rows.stream()
                    .map(BaseNestOutDO.ListOutDO::getNestId)
                    .collect(Collectors.toSet());
            return uosNestFirmwareService.getNestLatestFirmwareVersionInfos(nestIds);
        }
        return Collections.emptyMap();
    }


    /**
     * 保存基站——设备信息
     */
    private Boolean saveNestDeviceRef(String nestId, String insideDeviceCode, String outsideDeviceCode) {
        // 保存基站——设备信息
        List<UosNestDeviceRefEntity> entityList = uosNestDeviceRefService.listNestDeviceRef(nestId);
        if (!CollectionUtils.isEmpty(entityList)) {
            uosNestDeviceRefService.deleteNestDeviceRef(nestId);
        }
        List<UosNestDeviceRefEntity> newEntityList = Lists.newArrayList();
        if (StringUtils.hasText(insideDeviceCode)) {
            newEntityList.add(insertNestDeviceRef(nestId, insideDeviceCode, DriveUseEnum.INSIDE.getCode()));
        }
        if (StringUtils.hasText(outsideDeviceCode)) {
            newEntityList.add(insertNestDeviceRef(nestId, outsideDeviceCode, DriveUseEnum.OUTSIDE.getCode()));
        }
        if (!CollectionUtils.isEmpty(newEntityList)) {
            return uosNestDeviceRefService.insertNestDeviceRef(newEntityList);
        }
        return false;
    }

//    /**
//     * 保存基站——无人机图传地址
//     */
//    private Boolean saveNestStreamRef2(String nestId, List<String> uavPushStreamList) {
//        if (!CollectionUtils.isEmpty(uavPushStreamList)) {
//            String accountId = TrustedAccessTracerHolder.get().getAccountId();
//            int which = uavPushStreamList.size() > 1 ? 1 : 0;
//            for (String pushUrl : uavPushStreamList) {
//                baseUavService.setPushUrl(nestId, pushUrl, accountId, which);
//                which += 1;
//            }
//        }
//        return true;
//    }

    /**
     * 保存基站——无人机图传地址
     */
    private Boolean saveNestStreamRef(String nestId, NestMediaSaveInfoVO mediaSaveInfoVO) {
        //无人机
        List<NestMediaSaveInfoVO.UavMeta> uavMetaList = mediaSaveInfoVO.getUavMetaList();
        if(CollectionUtil.isNotEmpty(uavMetaList)) {
            List<BaseUavEntity> entityList = new ArrayList<>();
            for (NestMediaSaveInfoVO.UavMeta uavMeta : uavMetaList) {
                BaseUavEntity baseUavEntity = new BaseUavEntity();
                baseUavEntity.setUavId(uavMeta.getUavId());
                baseUavEntity.setStreamId(uavMeta.getStreamId());
                entityList.add(baseUavEntity);
            }
            //回写streamId
            baseUavService.updateStreamIdByUavId(entityList);
        }

        String djiMonitorStreamId = mediaSaveInfoVO.getDjiMonitorStreamId();
        BaseNestInfoOutDTO baseNestInfoOutDTO = baseNestService.getBaseNestInfo(nestId);
        if(baseNestInfoOutDTO == null) {
            log.error("找不到基站信息:{}", nestId);
            throw new BizException("找不到基站信息");
        }
        if(StringUtil.isNotEmpty(djiMonitorStreamId)
                && Objects.equals(baseNestInfoOutDTO.getType(), NestTypeEnum.DJI_DOCK.getValue())) {
            //大疆监控属于巢内监控-streamUse为0
            uosNestStreamRefService.updateStreamIdByNestId(nestId, djiMonitorStreamId, 0);
        }
        return true;
    }


    private UosNestDeviceRefEntity insertNestDeviceRef(String nestId, String deviceId, int deviceUse) {
        UosNestDeviceRefEntity entity = new UosNestDeviceRefEntity();
        entity.setNestId(nestId);
        entity.setDeviceId(deviceId);
        entity.setDeviceUse(deviceUse);
        entity.setCreatedTime(LocalDateTime.now());
        entity.setModifiedTime(LocalDateTime.now());
        entity.setCreatorId(TrustedAccessTracerHolder.get().getAccountId());
        entity.setModifierId(TrustedAccessTracerHolder.get().getAccountId());
        return entity;
    }
}
