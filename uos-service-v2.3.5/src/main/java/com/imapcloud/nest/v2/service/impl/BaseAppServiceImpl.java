package com.imapcloud.nest.v2.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.geoai.common.core.bean.ITrustedAccessTracer;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.dao.entity.BaseAppEntity;
import com.imapcloud.nest.v2.dao.mapper.BaseAppMapper;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.rest.UosOrgManager;
import com.imapcloud.nest.v2.service.BaseAppService;
import com.imapcloud.nest.v2.service.BaseUavAppRefService;
import com.imapcloud.nest.v2.service.BaseUavService;
import com.imapcloud.nest.v2.service.dto.in.SaveBaseAppInDTO;
import com.imapcloud.nest.v2.service.dto.in.SaveStreamInDTO;
import com.imapcloud.nest.v2.service.dto.in.SaveUavAppInDTO;
import com.imapcloud.nest.v2.service.dto.in.SaveUavInDTO;
import com.imapcloud.nest.v2.service.dto.out.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 终端信息表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
@Service
public class BaseAppServiceImpl implements BaseAppService {

    @Resource
    private BaseAppMapper baseAppMapper;

    @Resource
    private UosOrgManager uosOrgManager;

//    @Resource
//    private RedisService redisService;

//    @Resource
//    private MediaStreamService mediaStreamService;

    @Resource
    private BaseUavService baseUavService;

    @Resource
    private BaseUavAppRefService baseUavAppRefService;


    @Override
    public List<AppListInfoOutDTO> listAppInfos() {
        ITrustedAccessTracer trustedAccessTracer = TrustedAccessTracerHolder.get();
        String accountId = trustedAccessTracer.getAccountId();
        String orgCode = trustedAccessTracer.getOrgCode();
        String username = trustedAccessTracer.getUsername();
        if (Objects.isNull(orgCode)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<BaseAppEntity> wrapper = Wrappers.lambdaQuery(BaseAppEntity.class).likeRight(BaseAppEntity::getOrgCode, orgCode);
        List<BaseAppEntity> list = baseAppMapper.selectList(wrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            List<OrgSimpleOutDO> orgInfos = uosOrgManager.listAllOrgInfos();
            Map<String, OrgSimpleOutDO> sysUnitEntityMap = orgInfos.stream().collect(Collectors.toMap(OrgSimpleOutDO::getOrgCode, Function.identity()));
            List<String> appIdList = list.stream().map(BaseAppEntity::getAppId).collect(Collectors.toList());
            List<AppStreamOutDTO> appStreamOutDTOList = baseUavService.listAppStreamsByAppIdList(appIdList);
            List<String> streamIdList = appStreamOutDTOList.stream().map(AppStreamOutDTO::getStreamId).collect(Collectors.toList());
//            List<MediaStreamOutDTO> mediaStreamOutDTOList = mediaStreamService.listStreamInfos(streamIdList);
//            Map<String, String> streamMap = mediaStreamOutDTOList.stream().collect(Collectors.toMap(MediaStreamOutDTO::getStreamId, MediaStreamOutDTO::getStreamPullUrl));
//            Map<String, String> appStreamMap = appStreamOutDTOList.stream().collect(Collectors.toMap(AppStreamOutDTO::getAppId, dto -> streamMap.get(dto.getStreamId())));

            List<AppListInfoOutDTO> dtoList = list.stream().map(ent -> {
                AppListInfoOutDTO dto = AppListInfoOutDTO.builder()
                        .id(ent.getAppId())
                        .name(ent.getName())
                        .showStatus(ent.getShowStatus())
                        .state(-1)
                        .driver(username)
                        .deviceId(ent.getDeviceId())
                        .unitId(ent.getOrgCode())
//                        .pullHttp(appStreamMap.get(ent.getAppId()))
                        .build();
                OrgSimpleOutDO orgSimpleOutDO = sysUnitEntityMap.get(ent.getOrgCode());
                if (Objects.nonNull(orgSimpleOutDO)) {
                    dto.setUnitName(orgSimpleOutDO.getOrgName());
                    dto.setLatitude(orgSimpleOutDO.getLatitude());
                    dto.setLongitude(orgSimpleOutDO.getLongitude());
                }
                return dto;
            }).collect(Collectors.toList());
            return dtoList;
        }
        return Collections.emptyList();
    }

    @Override
    public Boolean setShowStatusByAppId(String appId, Integer showStatus) {
        if (Objects.nonNull(appId)) {
            LambdaUpdateWrapper<BaseAppEntity> wrapper = Wrappers.lambdaUpdate(BaseAppEntity.class).eq(BaseAppEntity::getAppId, appId);
            BaseAppEntity baseAppEntity = new BaseAppEntity();
            baseAppEntity.setShowStatus(showStatus);
            int update = baseAppMapper.update(baseAppEntity, wrapper);
            return update > 0;
        }
        return false;
    }

    @Override
    public Boolean setShowStatusByOrgCode(String orgCode, Integer showStatus) {
        if (Objects.nonNull(orgCode)) {
            LambdaUpdateWrapper<BaseAppEntity> wrapper = Wrappers.lambdaUpdate(BaseAppEntity.class).eq(BaseAppEntity::getOrgCode, orgCode);
            BaseAppEntity baseAppEntity = new BaseAppEntity();
            baseAppEntity.setShowStatus(showStatus);
            int update = baseAppMapper.update(baseAppEntity, wrapper);
            return update > 0;
        }
        return false;
    }

    @Override
    public AppFlowPageOutDTO listAppFlowPage(Integer currentPage, Integer pageSize) {
        Page<BaseAppEntity> page = new Page<>(currentPage, pageSize);
        ITrustedAccessTracer trustedAccessTracer = TrustedAccessTracerHolder.get();
        String orgCode = trustedAccessTracer.getOrgCode();
        LambdaQueryWrapper<BaseAppEntity> wrapper = Wrappers.lambdaQuery(BaseAppEntity.class).eq(BaseAppEntity::getShowStatus, 1)
                .eq(BaseAppEntity::getOrgCode, orgCode)
                .select(BaseAppEntity::getName, BaseAppEntity::getAppId, BaseAppEntity::getDeviceId);

        Page<BaseAppEntity> baseAppPage = baseAppMapper.selectPage(page, wrapper);
        List<BaseAppEntity> records = baseAppPage.getRecords();
        AppFlowPageOutDTO pageOutDTO = AppFlowPageOutDTO.builder()
                .pages(baseAppPage.getPages())
                .size(baseAppPage.getSize())
                .total(baseAppPage.getTotal())
                .current(baseAppPage.getCurrent())
                .build();

        if (CollectionUtil.isNotEmpty(records)) {
            List<String> appIdList = records.stream().map(BaseAppEntity::getAppId).collect(Collectors.toList());
            List<AppStreamOutDTO> appStreamOutDTOList = baseUavService.listAppStreamsByAppIdList(appIdList);
            List<String> streamIdList = appStreamOutDTOList.stream().map(AppStreamOutDTO::getStreamId).collect(Collectors.toList());
//            List<MediaStreamOutDTO> mediaStreamOutDTOList = mediaStreamService.listStreamInfos(streamIdList);
//            Map<String, String> streamUrlMap = mediaStreamOutDTOList.stream().collect(Collectors.toMap(MediaStreamOutDTO::getStreamId, MediaStreamOutDTO::getStreamPullUrl));
//            Map<String, String> appStreamMap = appStreamOutDTOList.stream().collect(Collectors.toMap(AppStreamOutDTO::getAppId, dto -> streamUrlMap.get(dto.getStreamId())));

            List<AppFlowOutDTO> dtoList = records.stream().map(ent -> AppFlowOutDTO.builder()
                            .appId(ent.getAppId())
                            .appName(ent.getName())
                            .state(-1)
//                            .pullHttp(appStreamMap.get(ent.getAppId()))
                            .build())
                    .collect(Collectors.toList());
            pageOutDTO.setRecords(dtoList);
        } else {
            pageOutDTO.setRecords(Collections.emptyList());
        }
        return pageOutDTO;
    }

    @Override
    public String getDeviceIdByAppId(String appId) {
        if (Objects.nonNull(appId)) {
            LambdaQueryWrapper<BaseAppEntity> wrapper = Wrappers.lambdaQuery(BaseAppEntity.class)
                    .eq(BaseAppEntity::getAppId, appId)
                    .select(BaseAppEntity::getDeviceId);
            BaseAppEntity baseAppEntity = baseAppMapper.selectOne(wrapper);
            if (Objects.nonNull(baseAppEntity)) {
                return baseAppEntity.getDeviceId();
            }
        }
        return null;
    }

    @Override
    public String getAppIdByDeviceId(String deviceId) {
        if (Objects.nonNull(deviceId)) {
            LambdaQueryWrapper<BaseAppEntity> wrapper = Wrappers.lambdaQuery(BaseAppEntity.class)
                    .eq(BaseAppEntity::getDeviceId, deviceId)
                    .select(BaseAppEntity::getAppId);
            BaseAppEntity baseAppEntity = baseAppMapper.selectOne(wrapper);
            if (Objects.nonNull(baseAppEntity)) {
                return baseAppEntity.getAppId();
            }
        }
        return null;
    }

    @Override
    public BaseAppInfoOutDTO getAppInfoByAppId(String appId) {
        if (Objects.nonNull(appId)) {
            LambdaQueryWrapper<BaseAppEntity> wrapper = Wrappers.lambdaQuery(BaseAppEntity.class).eq(BaseAppEntity::getAppId, appId);
            BaseAppEntity baseAppEntity = baseAppMapper.selectOne(wrapper);
            Optional<BaseAppInfoOutDTO> opt = Optional.ofNullable(baseAppEntity).map(ent -> BaseAppInfoOutDTO.builder()
                    .appId(ent.getAppId())
                    .name(ent.getName())
                    .orgCode(ent.getOrgCode())
                    .deviceId(ent.getDeviceId())
                    .showStatus(ent.getShowStatus())
                    .build());

            if (opt.isPresent()) {
                return opt.get();
            }
        }
        return null;
    }

    @Override
    public BaseAppInfoOutDTO getAppInfoByDeviceId(String deviceId) {
        if (Objects.nonNull(deviceId)) {
            LambdaQueryWrapper<BaseAppEntity> wrapper = Wrappers.lambdaQuery(BaseAppEntity.class).eq(BaseAppEntity::getDeviceId, deviceId);
            BaseAppEntity baseAppEntity = baseAppMapper.selectOne(wrapper);
            Optional<BaseAppInfoOutDTO> opt = Optional.ofNullable(baseAppEntity).map(ent -> BaseAppInfoOutDTO.builder()
                    .appId(ent.getAppId())
                    .name(ent.getName())
                    .orgCode(ent.getOrgCode())
                    .deviceId(ent.getDeviceId())
                    .showStatus(ent.getShowStatus())
                    .build());

            if (opt.isPresent()) {
                return opt.get();
            }
        }
        return null;
    }

    @Override
    public BaseAppInfoOutDTO getAppInfoByAppName(String appName) {
        if (Objects.nonNull(appName)) {
            LambdaQueryWrapper<BaseAppEntity> wrapper = Wrappers.lambdaQuery(BaseAppEntity.class).eq(BaseAppEntity::getName, appName);
            BaseAppEntity baseAppEntity = baseAppMapper.selectOne(wrapper);
            Optional<BaseAppInfoOutDTO> opt = Optional.ofNullable(baseAppEntity).map(ent -> BaseAppInfoOutDTO.builder()
                    .appId(ent.getAppId())
                    .name(ent.getName())
                    .orgCode(ent.getOrgCode())
                    .deviceId(ent.getDeviceId())
                    .showStatus(ent.getShowStatus())
                    .build());

            if (opt.isPresent()) {
                return opt.get();
            }
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Boolean saveOrUpdateBaseApp(SaveBaseAppInDTO saveBaseAppInDTO) {
        if (Objects.nonNull(saveBaseAppInDTO)) {
            //新增
            if (Objects.isNull(saveBaseAppInDTO.getId())) {
                return saveBaseApp(saveBaseAppInDTO);
            }
            //修改
            if (Objects.nonNull(saveBaseAppInDTO.getId())) {
                return updateBaseApp(saveBaseAppInDTO);
            }
        }

        return true;
    }

    @Override
    public BaseUavAppInfoOutDTO getBaseUavAppInfoByAppId(String appId) {
        if (Objects.isNull(appId)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_QUERY_APP_EXCEPTION.getContent()));
        }
        LambdaQueryWrapper<BaseAppEntity> wrapper = Wrappers.lambdaQuery(BaseAppEntity.class).eq(BaseAppEntity::getAppId, appId);
        BaseAppEntity baseAppEntity = baseAppMapper.selectOne(wrapper);
        if (Objects.isNull(baseAppEntity)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_QUERY_APP_EXCEPTION.getContent()));
        }
        BaseUavInfoOutDTO baseUavInfoOutDTO = baseUavService.getUavInfoByAppId(appId);
        if (Objects.isNull(baseUavInfoOutDTO)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_QUERY_UAV_EXCEPTION.getContent()));
        }
//        MediaStreamOutDTO mediaStreamOutDTO = mediaStreamService.getStreamInfo(baseUavInfoOutDTO.getStreamId());
//        if (Objects.isNull(mediaStreamOutDTO)) {
//            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_QUERY_FLOW_EXCEPTION.getContent()));
//        }
        Optional<OrgSimpleOutDO> orgInfoOpt = uosOrgManager.getOrgInfo(baseAppEntity.getOrgCode());
        BaseUavAppInfoOutDTO baseUavAppInfoOutDTO = BaseUavAppInfoOutDTO.builder()
                .id(baseAppEntity.getAppId())
                .name(baseAppEntity.getName())
                .deviceId(baseAppEntity.getDeviceId())
                .unitId(baseAppEntity.getOrgCode())
                .aircraftId(baseUavInfoOutDTO.getUavId())
                .aircraftTypeValue(baseUavInfoOutDTO.getType())
                .cameraName(baseUavInfoOutDTO.getCameraName())
//                .pullHttp(mediaStreamOutDTO.getStreamPullUrl())
//                .pushRtmp(mediaStreamOutDTO.getStreamPushUrl())
                .build();

        orgInfoOpt.ifPresent(org -> baseUavAppInfoOutDTO.setUnitName(org.getOrgName()));

        return baseUavAppInfoOutDTO;
    }

    @Override
    public BaseAppPageOutDTO listSysAppByPages(Integer pageNo, Integer pageSize) {
        if (Objects.nonNull(pageNo) && Objects.nonNull(pageSize)) {
            ITrustedAccessTracer trustedAccessTracer = TrustedAccessTracerHolder.get();
            Page<BaseAppEntity> page = new Page<>(pageNo, pageSize);
            LambdaQueryWrapper<BaseAppEntity> wrapper = Wrappers.lambdaQuery(BaseAppEntity.class)
                    .likeRight(BaseAppEntity::getOrgCode, trustedAccessTracer.getOrgCode())
                    .select(BaseAppEntity::getAppId, BaseAppEntity::getName, BaseAppEntity::getOrgCode, BaseAppEntity::getDeviceId, BaseAppEntity::getDeleted);
            Page<BaseAppEntity> pageEnt = baseAppMapper.selectPage(page, wrapper);
            BaseAppPageOutDTO pageDto = BaseAppPageOutDTO.builder()
                    .pages(pageEnt.getPages())
                    .current(pageEnt.getCurrent())
                    .size(pageEnt.getSize())
                    .total(pageEnt.getTotal())
                    .build();


            List<BaseAppEntity> records = pageEnt.getRecords();
            if (CollectionUtil.isNotEmpty(records)) {
                List<String> appIdList = records.stream().map(BaseAppEntity::getAppId).collect(Collectors.toList());
                List<String> orgCodeList = records.stream().map(BaseAppEntity::getOrgCode).collect(Collectors.toList());
                List<OrgSimpleOutDO> orgInfoList = uosOrgManager.listOrgInfos(orgCodeList);
                List<BaseUavInfoOutDTO> baseUavInfoOutDTOList = baseUavService.listUavInfosByAppIds(appIdList);
                if (CollectionUtil.isNotEmpty(baseUavInfoOutDTOList) && CollectionUtil.isNotEmpty(orgInfoList)) {
                    Map<String, String> orgMap = orgInfoList.stream().collect(Collectors.toMap(OrgSimpleOutDO::getOrgCode, OrgSimpleOutDO::getOrgName));
                    Map<String, BaseUavInfoOutDTO> uavMap = baseUavInfoOutDTOList.stream().collect(Collectors.toMap(BaseUavInfoOutDTO::getAppId, dto -> dto));
                    List<String> streamIdList = baseUavInfoOutDTOList.stream().map(BaseUavInfoOutDTO::getStreamId).collect(Collectors.toList());
//                    List<MediaStreamOutDTO> mediaStreamOutDTOList = mediaStreamService.listStreamInfos(streamIdList);
//                    Map<String, MediaStreamOutDTO> streamMap = mediaStreamOutDTOList.stream().collect(Collectors.toMap(MediaStreamOutDTO::getStreamId, dto -> dto));
//                    Map<String, MediaStreamOutDTO> appStreamMap = records.stream().collect(Collectors.toMap(BaseAppEntity::getAppId, ent -> {
//                        BaseUavInfoOutDTO baseUavInfoOutDTO = uavMap.get(ent.getAppId());
//                        if (Objects.nonNull(baseUavInfoOutDTO)) {
//                            return streamMap.get(baseUavInfoOutDTO.getStreamId());
//                        }
//                        return null;
//                    }));

                    List<BaseUavAppInfoOutDTO> newRecords = records.stream().map(ent -> {
                        BaseUavInfoOutDTO uavDto = uavMap.get(ent.getAppId());
//                        MediaStreamOutDTO streamDto = appStreamMap.get(ent.getAppId());
                        return BaseUavAppInfoOutDTO.builder()
                                .id(ent.getAppId())
                                .name(ent.getName())
                                .deviceId(ent.getDeviceId())
                                .unitId(ent.getOrgCode())
                                .unitName(orgMap.get(ent.getOrgCode()))
                                .aircraftId(uavDto == null ? "" : uavDto.getUavId())
                                .aircraftTypeValue(uavDto == null ? "" : uavDto.getType())
                                .cameraName(uavDto == null ? "" : uavDto.getCameraName())
//                                .pushRtmp(streamDto == null ? "" : streamDto.getStreamPushUrl())
//                                .pullHttp(streamDto == null ? "" : streamDto.getStreamPullUrl())
                                .build();
                    }).collect(Collectors.toList());

                    pageDto.setRecords(newRecords);
                }
            } else {
                pageDto.setRecords(Collections.emptyList());
            }
            return pageDto;
        }
        return null;
    }

    @Override
    public Boolean softDeleteSysApp(String appId) {
        if (Objects.nonNull(appId)) {
            BaseUavInfoOutDTO uavInfo = baseUavService.getUavInfoByAppId(appId);
            if (Objects.isNull(uavInfo)) {
                throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DELETE_MOBILE_TERMINAL_FAIL.getContent()));
            }
            //1、删除app
            int appDel = baseAppMapper.updateDeletedByAppId(appId, 1);
            //2、删除uav
            Boolean uavDel = baseUavService.softDeleteUavByUavId(uavInfo.getUavId());
            //3、删除app-uav
            Boolean refDel = baseUavAppRefService.softDeleteRef(appId, uavInfo.getUavId());
            //4、删除stream
//            Boolean streamDel = mediaStreamService.softDeleteStreamByStreamId(uavInfo.getStreamId());
            return appDel > 0 && uavDel && refDel /*&& streamDel*/;
        }
        return false;
    }

    @Override
    public Boolean updateAppShowStatusByAppId(String appId, Integer showStatus) {
        if (Objects.nonNull(appId) && Objects.nonNull(showStatus)) {
            int i = baseAppMapper.updateShowStatusByAppId(appId, showStatus);
            return i > 0;
        }
        return false;
    }

    @Override
    public Boolean updateAppShowStatusByOrgCode(String orgCode, Integer showStatus) {
        if (Objects.nonNull(orgCode) && Objects.nonNull(showStatus)) {
            int i = baseAppMapper.updateShowStatusByOrgCode(orgCode, showStatus);
            return i > 0;
        }
        return false;
    }

    private Boolean saveBaseApp(SaveBaseAppInDTO saveBaseAppInDTO) {
        ITrustedAccessTracer trustedAccessTracer = TrustedAccessTracerHolder.get();
        //1、保存app
        BaseAppEntity baseAppEntity = new BaseAppEntity();
        baseAppEntity.setAppId(BizIdUtils.snowflakeIdStr());
        baseAppEntity.setOrgCode(saveBaseAppInDTO.getUnitId());
        baseAppEntity.setDeviceId(saveBaseAppInDTO.getDeviceId());
        baseAppEntity.setName(saveBaseAppInDTO.getName());
        baseAppEntity.setCreatorId(trustedAccessTracer.getAccountId());
        baseAppEntity.setModifierId(trustedAccessTracer.getAccountId());
        int insert = baseAppMapper.insert(baseAppEntity);

        if (insert <= 0) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SAVING_APP_MESSAGE_EXCEPTION.getContent()));
        }

        //2、保存流媒体
//        SaveStreamInDTO saveStreamInDTO = SaveStreamInDTO.builder()
//                .streamPullUrl(saveBaseAppInDTO.getPullHttp())
//                .streamPushUrl(saveBaseAppInDTO.getPushRtmp())
//                .build();
//        String streamId = mediaStreamService.saveOrUpdateStream(saveStreamInDTO);
//        if (Objects.isNull(streamId)) {
//            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SAVING_STREAMING_MEDIA_EXCEPTION.getContent()));
//        }

        //3、保存无人机
        SaveUavInDTO saveUavInDTO = SaveUavInDTO.builder()
                .cameraName(saveBaseAppInDTO.getCameraName())
                .type(saveBaseAppInDTO.getAircraftTypeValue())
//                .streamId(streamId)
                .build();
        String uavId = baseUavService.saveOrUpdateUav(saveUavInDTO);
        if (Objects.isNull(uavId)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SAVING_UAV_EXCEPTION.getContent()));
        }

        //4、保存无人机与app的关联关系
        SaveUavAppInDTO saveUavAppInDTO = SaveUavAppInDTO.builder()
                .appId(baseAppEntity.getAppId())
                .uavId(uavId)
                .build();

        Boolean aBoolean = baseUavAppRefService.saveUavAppRef(saveUavAppInDTO);
        if (!aBoolean) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SAVING_UAV_APP__RELATIONSHIP_EXCEPTION.getContent()));
        }
        return true;
    }

    private Boolean updateBaseApp(SaveBaseAppInDTO saveBaseAppInDTO) {
        ITrustedAccessTracer trustedAccessTracer = TrustedAccessTracerHolder.get();
        //1、保存app
        BaseAppEntity baseAppEntity = new BaseAppEntity();
        baseAppEntity.setAppId(saveBaseAppInDTO.getId());
        baseAppEntity.setOrgCode(saveBaseAppInDTO.getUnitId());
        baseAppEntity.setDeviceId(saveBaseAppInDTO.getDeviceId());
        baseAppEntity.setName(saveBaseAppInDTO.getName());
        baseAppEntity.setModifierId(trustedAccessTracer.getAccountId());

        LambdaUpdateWrapper<BaseAppEntity> wrapper = Wrappers.lambdaUpdate(BaseAppEntity.class).eq(BaseAppEntity::getAppId, saveBaseAppInDTO.getId());
        int update = baseAppMapper.update(baseAppEntity, wrapper);
        if (update <= 0) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SAVING_APP_MESSAGE_EXCEPTION.getContent()));
        }

        //2、保存流媒体
        //查询无人机的streamId
        String uavStreamId = baseUavService.getUavStreamIdByAppId(saveBaseAppInDTO.getId());
        if (Objects.isNull(uavStreamId)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_QUERY_STREAMID_EXCEPTION.getContent()));
        }
        SaveStreamInDTO saveStreamInDTO = SaveStreamInDTO.builder()
                .streamId(uavStreamId)
                .streamPullUrl(saveBaseAppInDTO.getPullHttp())
                .streamPushUrl(saveBaseAppInDTO.getPushRtmp())
                .build();
//        String streamId = mediaStreamService.saveOrUpdateStream(saveStreamInDTO);
//        if (Objects.isNull(streamId)) {
//            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SAVING_STREAMING_MEDIA_EXCEPTION.getContent()));
//        }

        //3、保存无人机
        SaveUavInDTO saveUavInDTO = SaveUavInDTO.builder()
                .uavId(saveBaseAppInDTO.getAircraftId())
                .cameraName(saveBaseAppInDTO.getCameraName())
                .type(saveBaseAppInDTO.getAircraftTypeValue())
//                .streamId(streamId)
                .build();
        String uavId = baseUavService.saveOrUpdateUav(saveUavInDTO);
        if (Objects.isNull(uavId)) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SAVING_UAV_EXCEPTION.getContent()));
        }
        return true;
    }
}
