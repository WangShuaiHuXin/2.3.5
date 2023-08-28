package com.imapcloud.nest.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.core.bean.ITrustedAccessTracer;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.netty.ws.ChannelService;
import com.imapcloud.nest.enums.AppWebSocketTopicEnum;
import com.imapcloud.nest.mapper.SysAppMapper;
import com.imapcloud.nest.model.SysAppEntity;
import com.imapcloud.nest.pojo.dto.SaveSysAppDTO;
import com.imapcloud.nest.pojo.dto.SysAppDto;
import com.imapcloud.nest.pojo.dto.VisibleAppFlowParam;
import com.imapcloud.nest.service.SysAppService;
import com.imapcloud.nest.utils.*;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisKeyEnum;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.rest.UosOrgManager;
import com.imapcloud.nest.v2.service.BaseAppService;
import com.imapcloud.nest.v2.service.dto.in.SaveBaseAppInDTO;
import com.imapcloud.nest.v2.service.dto.out.AppFlowPageOutDTO;
import com.imapcloud.nest.v2.service.dto.out.AppListInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.BaseAppInfoOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 终端信息表 服务实现类
 * </p>
 *
 * @author kings
 * @since 2020-10-26
 */
@Service
@Slf4j
public class SysAppServiceImpl extends ServiceImpl<SysAppMapper, SysAppEntity> implements SysAppService {

    @Resource
    private SysAppMapper sysAppMapper;

    @Resource
    private RedisService redisService;

    @Resource
    private UosOrgManager uosOrgManager;

    @Resource
    private BaseAppService baseAppService;

    @Override
    public IPage<SysAppEntity> listSysAppByPages(Integer pageNo, Integer pageSize, SysAppEntity sysAppEntity) {
        if (pageNo != null && pageSize != null) {
            try {
                IPage<SysAppEntity> page = new Page<>(pageNo, pageSize);
                QueryWrapper<SysAppEntity> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("deleted", false);
                IPage<SysAppEntity> sysAppEntityIPage = sysAppMapper.selectPage(page, queryWrapper);

                // 查询用户单位信息
                String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
                queryWrapper.likeRight("org_code", orgCode);

                if (sysAppEntityIPage != null && !CollectionUtil.isEmpty(sysAppEntityIPage.getRecords())) {
                    Map<String, String> unitIdNameMap = getOrgInfoMappings();
                    sysAppEntityIPage.getRecords().forEach(it -> {
                        String orgName = unitIdNameMap.get(it.getOrgCode());
                        if (StringUtils.hasText(orgName)) {
                            it.setUnitName(orgName);
                        }
                    });
                }


                return sysAppEntityIPage;
            } catch (Exception e) {
                e.printStackTrace();
                log.info("分页出现问题： {}", e);
                return null;
            }
        }

        return null;
    }

    private Map<String, String> getOrgInfoMappings() {
        List<OrgSimpleOutDO> orgInfos = uosOrgManager.listAllOrgInfos();
        Map<String, String> unitIdNameMap = Collections.emptyMap();
        if (!CollectionUtils.isEmpty(orgInfos)) {
            unitIdNameMap = orgInfos.stream().collect(Collectors.toMap(OrgSimpleOutDO::getOrgCode, OrgSimpleOutDO::getOrgName));
        }
        return unitIdNameMap;
    }

    @Override
    public SysAppDto getInfoById(Integer id) {
        if (id != null) {
//            SysAppDto sysAppDto = new SysAppDto();
//            SysAppEntity sysAppEntity = sysAppMapper.getInfoById(id);
//            if (Objects.nonNull(sysAppEntity)) {
//                Optional<OrgSimpleOutDO> optional = uosOrgManager.getOrgInfo(sysAppEntity.getOrgCode());
//                optional.ifPresent(r -> sysAppEntity.setUnitName(r.getOrgName()));
//            }
//            AircraftEntity aircraftEntity = aircraftService.lambdaQuery().eq(AircraftEntity::getAppId, id).eq(AircraftEntity::getDeleted, false).one();
//            BeanUtils.copyProperties(sysAppEntity, sysAppDto);
//            if (aircraftEntity != null) {
//                sysAppDto.setCameraName(aircraftEntity.getCameraName());
//                sysAppDto.setAircraftId(aircraftEntity.getId());
//                sysAppDto.setAircraftTypeValue(aircraftEntity.getTypeValue());
//                sysAppDto.setAircraftCode(aircraftEntity.getCode());
//            }
//            return sysAppDto;
        }
        return null;
    }

    @Override
    public List<SysAppEntity> listByUnitId(String unitId) {
        return this.listByOrgCode(unitId);
    }

    @Override
    public List<SysAppEntity> listByOrgCode(String orgCode) {
        if (!StringUtils.hasText(orgCode)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<SysAppEntity> condition = Wrappers.lambdaQuery(SysAppEntity.class)
                .likeRight(SysAppEntity::getOrgCode, orgCode)
                .eq(SysAppEntity::getDeleted, false);
        return baseMapper.selectList(condition);
    }

    @Override
    public RestRes listSysAppByUnitId() {
        ITrustedAccessTracer trustedAccessTracer = TrustedAccessTracerHolder.get();
        Map<String, Object> map = new HashMap<>(2);
        List<AppListInfoOutDTO> list = baseAppService.listAppInfos();
        if (CollectionUtil.isNotEmpty(list)) {
            Set<String> deviceIdSet = list.stream().map(AppListInfoOutDTO::getDeviceId).collect(Collectors.toSet());
            String redisKey = RedisKeyEnum.REDIS_KEY.className("SysAppController").methodName("listSysAppByUnitId").identity("account", trustedAccessTracer.getUsername()).type("list").get();
            redisService.set(redisKey, deviceIdSet);

            // 缓存移动终端id，名称，拉流地址，推流地址到redis
            list.forEach(dto -> {
                setAircraftStreamToRedis(dto.getName(), dto.getDeviceId(), dto.getPullHttp());
            });
            map.put("appList", list);
        } else {
            map.put("appList", Collections.EMPTY_LIST);
        }

        return RestRes.ok(map);

//        // 查询用户单位信息
//        String orgCode = VisitorDetailsContext.getVisitorDetails().getVisitorOrgId();
//        String account = VisitorDetailsContext.getVisitorDetails().getVisitorCode();
//        String name = VisitorDetailsContext.getVisitorDetails().getVisitorName();
//        //里面是有判空的
//        List<SysAppEntity> list = this.listByOrgCode(orgCode);
//
//        Map<String, Object> map = new HashMap<>(2);
//        List<String> unitIds = list.stream().map(SysAppEntity::getOrgCode).distinct().collect(Collectors.toList());
//        if (CollectionUtil.isNotEmpty(unitIds)) {
//            List<OrgSimpleOutDO> orgInfos = uosOrgManager.listAllOrgInfos();
//            Map<String, OrgSimpleOutDO> sysUnitEntityMap = orgInfos.stream().collect(Collectors.toMap(OrgSimpleOutDO::getOrgCode, Function.identity()));
//
//            Set<String> deviceIdSet = list.stream().map(SysAppEntity::getDeviceId).collect(Collectors.toSet());
//            String redisKey = RedisKeyEnum.REDIS_KEY.className("SysAppController").methodName("listSysAppByUnitId").identity("account", account).type("list").get();
//            redisService.set(redisKey, deviceIdSet);
//            List<AppListInfoDto> appListInfoDtos = list.stream().map(se -> {
//                String deviceId = se.getDeviceId();
//                String pullHttp = se.getPullHttp();
//                AppListInfoDto aliDto = new AppListInfoDto();
//                aliDto.setId(se.getId());
//                aliDto.setName(se.getName());
//                aliDto.setShowStatus(se.getShowStatus());
//                aliDto.setState(-1);
//                aliDto.setDriver(name);
//                aliDto.setDeviceId(deviceId);
//                aliDto.setPullHttp(pullHttp);
//                aliDto.setUnitId(se.getOrgCode());
//                OrgSimpleOutDO orgSimple = sysUnitEntityMap.get(se.getOrgCode());
//                if(Objects.nonNull(orgSimple)){
//                    aliDto.setUnitName(orgSimple.getOrgName());
//                    aliDto.setLatitude(orgSimple.getLatitude());
//                    aliDto.setLongitude(orgSimple.getLongitude());
//                }
//                // 缓存移动终端id，名称，拉流地址，推流地址到redis
//                setAircraftStreamToRedis(aliDto, deviceId, pullHttp);
//                return aliDto;
//            }).collect(Collectors.toList());
//            map.put("appList", appListInfoDtos);
//            return RestRes.ok(map);
//        } else {
//            map.put("appList", Collections.EMPTY_LIST);
//            return RestRes.ok(map);
//        }
    }

    @Override
    public RestRes updateSysAppShowStatus(Integer appId, String unitId, Integer showStatus) {
        if (appId != null) {
            SysAppEntity appEntity = this.getById(appId);
            if (appEntity == null) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_EXIST_MOBILE_TERMINAL.getContent()));
            }
            appEntity.setShowStatus(showStatus);
            return this.updateById(appEntity) ? RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_CHANGE_MOBILE_TERMINAL.getContent())) : RestRes.err();
        }
        if (unitId != null) {
            Optional<OrgSimpleOutDO> optional = uosOrgManager.getOrgInfo(unitId);
            if (!optional.isPresent()) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_EXIST_MOBILE_TERMINAL.getContent()));
            }
            return this.lambdaUpdate().set(SysAppEntity::getShowStatus, showStatus).eq(SysAppEntity::getOrgCode, unitId).update() ? RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_CURRENT_MOBILE_TERMINAL_MONITORING_CHANGE_STATUS_SUCCESSFULLY.getContent())) : RestRes.err();
        }
        return RestRes.err();
    }

    @Override
    public RestRes getTxDefaultLive(String appId) {
        Map result = TxStreamUtil.getDefault(appId);
        return RestRes.ok(result);
    }

    @Override
    public RestRes listAppFlowUrl(Integer currentPage, Integer pageSize) {
        AppFlowPageOutDTO appFlowPageOutDTO = baseAppService.listAppFlowPage(currentPage, pageSize);
        Map<String, Object> data = new HashMap<>(8);
        data.put("current", appFlowPageOutDTO.getCurrent());
        data.put("pages", appFlowPageOutDTO.getPages());
        data.put("size", appFlowPageOutDTO.getSize());
        data.put("total", appFlowPageOutDTO.getTotal());
        data.put("records", appFlowPageOutDTO.getRecords());
        return RestRes.ok(data);
//        Page<SysAppEntity> page = new Page<>(currentPage, pageSize);
//
//        // 查询用户单位信息
//        String orgCode = VisitorDetailsContext.getVisitorDetails().getVisitorOrgId();
//        LambdaQueryChainWrapper<SysAppEntity> selectWrapper = this.lambdaQuery()
//                .eq(SysAppEntity::getDeleted, 0)
//                .eq(SysAppEntity::getShowStatus, 1)
//                .apply("pull_http is not null and pull_http != '' and pull_http LIKE 'http://%'")
//                .select(SysAppEntity::getName, SysAppEntity::getId, SysAppEntity::getPullHttp, SysAppEntity::getDeviceId);
//
//
//        if (StringUtils.hasText(orgCode)) {
//            selectWrapper.eq(SysAppEntity::getOrgCode, orgCode);
//        }
//
//        Map<String, Object> data = new HashMap<>(8);
//
//        Page<SysAppEntity> appPage = selectWrapper.page(page);
//
//        List<SysAppEntity> appList = appPage.getRecords();
//
//        if (CollectionUtil.isNotEmpty(appList)) {
//            Map<String, Integer> appStateMap = ChannelService.getAppStateMap();
//            List<Map<String, Object>> maps = new ArrayList<>(appList.size());
//            for (SysAppEntity app : appList) {
//                Map<String, Object> appMap = new HashMap<>(4);
//                appMap.put("appName", app.getName());
//                appMap.put("appId", app.getId());
//                Integer state = appStateMap.get(app.getDeviceId());
//                appMap.put("appStatus", state == null ? -1 : state);
//                appMap.put("pullHttp", app.getPullHttp());
//                maps.add(appMap);
//            }
//            data.put("records", maps);
//        } else {
//            data.put("records", Collections.emptyList());
//
//        }
//
//        data.put("current", appPage.getCurrent());
//        data.put("pages", appPage.getPages());
//        data.put("size", appPage.getSize());
//        data.put("total", appPage.getTotal());
//
//        return RestRes.ok(data);
    }

    @Override
    public RestRes setVisibleAppFlow(VisibleAppFlowParam visibleAppFlowParam) {
        String appId = visibleAppFlowParam.getAppId();
        String unitId = visibleAppFlowParam.getUnitId();
        Integer state = visibleAppFlowParam.getState();
        boolean update = false;
        if (appId != null) {
//            update = this.lambdaUpdate()
//                    .set(SysAppEntity::getShowStatus, state)
//                    .eq(SysAppEntity::getId, appId)
//                    .update();
            update = baseAppService.setShowStatusByAppId(appId, state);
        }
        if (unitId != null) {
//            update = this.lambdaUpdate()
//                    .set(SysAppEntity::getShowStatus, state)
//                    .eq(SysAppEntity::getUnitId, unitId)
//                    .update();
            update = baseAppService.setShowStatusByOrgCode(unitId, state);
        }
        if (update) {
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_SET_UP.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SETTING_FAILED.getContent()));
    }

    @Override
    public RestRes addOrUpdateApp(SaveSysAppDTO sysAppDto) {
        String deviceId = sysAppDto.getDeviceId();
        String name = sysAppDto.getName();
        String appId = sysAppDto.getId();
        boolean add = Objects.isNull(appId);
//        SysAppEntity appEntity = this.lambdaQuery().eq(SysAppEntity::getDeviceId, deviceId).eq(SysAppEntity::getDeleted, false).one();
        BaseAppInfoOutDTO appInfo = baseAppService.getAppInfoByDeviceId(deviceId);
        if (add) {
            if (Objects.nonNull(appInfo)) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ALREADY_EXIST_MOBILE_TERMINAL_ID.getContent()) + appInfo.getName());
            }
        } else {
            if (Objects.nonNull(appInfo) && !appInfo.getAppId().equals(appId)) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ALREADY_EXIST_MOBILE_TERMINAL_ID.getContent()) + appInfo.getName());
            }
        }

//        SysAppEntity sysAppEntity = this.getOne(new QueryWrapper<SysAppEntity>().eq("name", name).eq("deleted", false));
        BaseAppInfoOutDTO appInfo2 = baseAppService.getAppInfoByAppName(name);
        if (add) {
            if (Objects.nonNull(appInfo2)) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ALREADY_EXIST_MOBILE_TERMINAL.getContent()));
            }
        } else {
            if (Objects.nonNull(appInfo2) && !appInfo2.getAppId().equals(appId)) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ALREADY_EXIST_MOBILE_TERMINAL.getContent()));
            }
        }

//        SysAppEntity newSysAppEntity = new SysAppEntity();
//        BeanUtils.copyProperties(sysAppDto, newSysAppEntity);
//        newSysAppEntity.setCreatorId(Long.valueOf(VisitorDetailsContext.getVisitorDetails().getVisitorId()));
//        newSysAppEntity.setModifyTime(LocalDateTime.now());
//        boolean b = this.saveOrUpdate(newSysAppEntity);
//        appId = newSysAppEntity.getId();
//
//        // 增加对nms转发的操作，校验rtmpUrl为腾讯云地址时，主动添加relay任务到nms
////        nodeMediaUtil.createOneRelayStream(sysAppDto.getPullHttp(), sysAppDto.getName());
//
//        if (sysAppDto.getAircraftTypeValue() != null) {
//            //保存无人机信息
//            AircraftEntity aircraftEntity = new AircraftEntity();
//            aircraftEntity.setId(sysAppDto.getAircraftId());
//            aircraftEntity.setAppId(appId);
//            AircraftCodeEnum instance = AircraftCodeEnum.getInstance(String.valueOf(sysAppDto.getAircraftTypeValue()));
//            aircraftEntity.setCode(instance.getCode());
//            aircraftEntity.setTypeValue(sysAppDto.getAircraftTypeValue());
//            aircraftEntity.setCameraName(sysAppDto.getCameraName());
//            aircraftService.saveOrUpdate(aircraftEntity);
//        }
        SaveBaseAppInDTO saveBaseAppInDTO = SaveBaseAppInDTO.builder()
                .aircraftId(sysAppDto.getAircraftId())
                .aircraftTypeValue(sysAppDto.getAircraftTypeValue())
                .cameraName(sysAppDto.getCameraName())
                .deviceId(sysAppDto.getDeviceId())
                .id(sysAppDto.getId())
                .name(sysAppDto.getName())
                .pullHttp(sysAppDto.getPullHttp())
                .pushRtmp(sysAppDto.getPushRtmp())
                .unitId(sysAppDto.getUnitId())
                .build();
        Boolean b = baseAppService.saveOrUpdateBaseApp(saveBaseAppInDTO);
        if (b) {
            return RestRes.ok(add ? MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MOBILE_TERMINAL_ADDED_SUCCESSFULLY.getContent()) : MessageUtils.getMessage(MessageEnum.GEOAI_UOS_MOBILE_TERMINAL_MODIFIED_SUCCESSFULLY.getContent()));
        }
        return RestRes.err(add ? MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_ADD_MOBILE_TERMINAL.getContent()) : MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_MODIFY_THE_MOBILE_TERMINAL.getContent()));
    }

    @Override
    public RestRes getAppLocalRoute(String appId) {
//        SysAppEntity sysAppEntity = this.lambdaQuery().eq(SysAppEntity::getId, appId)
//                .eq(SysAppEntity::getDeleted, false)
//                .select(SysAppEntity::getDeviceId)
//                .one();
        String deviceId = baseAppService.getDeviceIdByAppId(appId);
        if (Objects.nonNull(deviceId)) {
            String cacheKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.APP_LOCAL_ROUTE, deviceId);
            String routeStr = (String) redisService.get(cacheKey);
            if (routeStr != null) {
                List<Map> route = JacksonUtil.json2Array(routeStr, List.class, Map.class);
                return RestRes.ok("appLocalRoute", route);
            }
        }

        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_GET.getContent()));
    }

    @Override
    public RestRes setAppPushStream(String appId, Boolean enable) {
//        SysAppEntity sysAppEntity = this.lambdaQuery()
//                .eq(SysAppEntity::getId, appId)
//                .eq(SysAppEntity::getDeleted, 0)
//                .select(SysAppEntity::getDeviceId)
//                .one();
//
//        if (sysAppEntity == null) {
//            return RestRes.err("没有该机巢,设置推流成功");
//        }
//        String deviceId = sysAppEntity.getDeviceId();
        String deviceId = baseAppService.getDeviceIdByAppId(appId);
        if (Objects.isNull(deviceId)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_DRONE_NEST_SET_UP_PUSH_FLOW_SUCCESS.getContent()));
        }
        Map<String, Object> map = new HashMap<>(2);
        map.put("dronePush", enable);
        String msg = AppWebSocketRes.instance().token("1111").messageType(AppWebSocketTopicEnum.SET_APP_PUSH_STREAM.getValue()).messageBody(map).toJSONString();
        ChannelService.sendMessageByType7Channel(deviceId, msg);
        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_SET_UP_PUSH_FLOW.getContent()));
    }

    private boolean isStandardFlowUrl(String flowUrl) {
        return StrUtil.isNotEmpty(flowUrl) && flowUrl.startsWith("http://");
    }


    /**
     * 缓存移动终端id，名称，拉流地址，推流地址到redis
     *
     * @param appName
     * @param deviceId
     * @param pullHttp
     */
    private void setAircraftStreamToRedis(String appName, String deviceId, String pullHttp) {
        // 把设备id，拉流地址缓存到redis
        String AppDeviceInfoRedisKey = RedisKeyEnum.REDIS_KEY.className("SysAppController").methodName("AppDeviceInfo").identity("deviceId", deviceId).type("map").get();
        Map<String, Object> redisMap = new HashMap<>();
        redisMap.put("name", appName);
        redisMap.put("deviceId", deviceId);
        redisMap.put("pullHttp", pullHttp);
        String pullRtmp = "";
        if (ToolUtil.isNotEmpty(pullHttp)) {
            String pull = pullHttp.startsWith("https") && pullHttp.contains(".") ? pullHttp.substring(5, pullHttp.lastIndexOf(".")) : (pullHttp.startsWith("http") && pullHttp.contains(".") ? pullHttp.substring(4, pullHttp.lastIndexOf(".")) : null);
            if (ToolUtil.isNotEmpty(pull)) {
                pullRtmp = "rtmp" + pull;
                redisMap.put("pullRtmp", pullRtmp);
            }
        }
        redisMap.put("pullRtmp", pullRtmp);
        redisMap.put("pushAIRtmp", "rtmp://112.94.68.148:1935/live/AI" + deviceId);
        redisService.set(AppDeviceInfoRedisKey, redisMap);
    }
}
