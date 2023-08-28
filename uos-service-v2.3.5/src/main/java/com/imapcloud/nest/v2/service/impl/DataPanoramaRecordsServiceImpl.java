package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.mp.entity.PagingRestrictDo;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.enums.TaskModeEnum;
import com.imapcloud.nest.mapper.MissionRecordsMapper;
import com.imapcloud.nest.model.AirLineEntity;
import com.imapcloud.nest.model.MissionPhotoEntity;
import com.imapcloud.nest.model.SysTagEntity;
import com.imapcloud.nest.model.TaskEntity;
import com.imapcloud.nest.service.AirLineService;
import com.imapcloud.nest.service.MissionPhotoService;
import com.imapcloud.nest.service.SysTagService;
import com.imapcloud.nest.service.TaskService;
import com.imapcloud.nest.v2.common.enums.LenTypeEnum;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.common.utils.DownLoadUtils;
import com.imapcloud.nest.v2.dao.po.in.MissionRecordsCriteriaPO;
import com.imapcloud.nest.v2.dao.po.out.MissionRecordsOutPO;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.DataPanoramaRecordsService;
import com.imapcloud.nest.v2.service.NestAccountService;
import com.imapcloud.nest.v2.service.converter.DataPanoramaRecordsConverter;
import com.imapcloud.nest.v2.service.dto.in.DataPanoramaRecordsInDTO;
import com.imapcloud.nest.v2.service.dto.in.PanoramaLocationDTO;
import com.imapcloud.nest.v2.service.dto.out.DataPanoramaRecordsOutDTO;
import com.imapcloud.nest.v2.service.dto.out.MissionPhotoPointOutDTO;
import com.imapcloud.nest.v2.service.dto.out.TaskOutDTO;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaPointServiceImpl.java
 * @Description DataPanoramaPointServiceImpl
 * @createTime 2022年09月22日 14:16:00
 */
@Slf4j
@Service
public class DataPanoramaRecordsServiceImpl implements DataPanoramaRecordsService {

    @Resource
    private MissionRecordsMapper missionRecordsMapper;

    @Resource
    private SysTagService sysTagService;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private AirLineService airLineService;

    @Resource
    private MissionPhotoService missionPhotoService;

    @Resource
    private NestAccountService nestAccountService;

    @Resource
    private TaskService taskService;

    /**
     * 任务列表-分页查询（当前用户拥有的基站下面的巡检记录）
     *
     * @param recordsInDTO
     * @return
     */
    @Override
    public PageResultInfo<DataPanoramaRecordsOutDTO.RecordsPageOutDTO> queryPointRecordsPage(DataPanoramaRecordsInDTO.RecordsInDTO recordsInDTO) {
        List<DataPanoramaRecordsOutDTO.RecordsPageOutDTO> results = new ArrayList<>();
        long total = 0L;
        //获取该用户拥有的单位全
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        String startTime = ObjectUtil.isEmpty(recordsInDTO.getStartTime()) ? "" : LocalDateTime.of(recordsInDTO.getStartTime()
                , LocalTime.of(00, 00, 00)).toString(),
                endTime = ObjectUtil.isEmpty(recordsInDTO.getEndTime()) ? "" : LocalDateTime.of(recordsInDTO.getEndTime()
                        , LocalTime.of(23, 59, 59)).toString();
        Map<String, String> tagIdToMap = new HashMap<>(8), baseIdToNameMap = new HashMap<>(8);
        //拼装数据
        MissionRecordsCriteriaPO po = MissionRecordsCriteriaPO.builder()
                .startTime(startTime)
                .endTime(endTime)
                .baseNestId(recordsInDTO.getBaseNestId())
                .visibleOrgCode(orgCode)
                .build();
        //查询条件下总数
        total = this.missionRecordsMapper.countRecordsByCondition(po);
        if (total > 0) {
            //分页查询基础数据
            List<MissionRecordsOutPO> outPOList = this.missionRecordsMapper.selectRecordsByCondition(po, PagingRestrictDo.getPagingRestrict(recordsInDTO));
            //查询标签
            List<String> tagIds = outPOList.stream().map(MissionRecordsOutPO::getTagId).distinct().collect(Collectors.toList());
            tagIdToMap = getTagMap(tagIds);

            //查询基站名
            List<String> baseNestIds = outPOList.stream().map(MissionRecordsOutPO::getBaseNestId).distinct().collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(baseNestIds)) {
                baseIdToNameMap = this.baseNestService.getNestNameMap(baseNestIds);
            }

            Map<String, String> finalTagIdToMap = tagIdToMap;
            Map<String, String> finalBaseIdToNameMap = baseIdToNameMap;
            results = outPOList.stream().map(DataPanoramaRecordsConverter.INSTANCES::convertPage)
                    .map(x -> {
                        x.setTagName(StringUtils.hasText(x.getTagId()) ? finalTagIdToMap.get(x.getTagId()) : "");
                        x.setBaseNestName(StringUtils.hasText(x.getBaseNestId()) ? finalBaseIdToNameMap.get(x.getBaseNestId()) : "");
                        return x;
                    })
                    .collect(Collectors.toList());
        }
        return PageResultInfo.of(total, results);
    }

    /**
     * 任务列表- 根据架次记录查询航点信息
     * <p>
     * {
     * "mode": "PANORAMA",
     * "mapConfigs": {
     * "points": [
     * {
     * "location": {
     * "airPointId":,
     * "airPointIndex":,
     * "panoName":,
     * "lng": 112.94200000390536,
     * "lat": 23.048785370563532,
     * "alt": 70
     * }
     * }]
     * },
     * "lineConfigs": {
     * "PANORAMA": {
     * "returnMode": "LINE",
     * "takeOffLandAlt": 75,
     * "speed": 15,
     * "autoFlightSpeed": 15,
     * "lineAlt": 0,
     * "resolution": 0,
     * "byname": "",
     * "photoPropList": [ ]
     * }
     * }
     * }
     *
     * @param missionRecordsId
     * @return
     */
    @Override
    public List<DataPanoramaRecordsOutDTO.AirPointOutDTO> queryAirPoint(String missionRecordsId, String taskId) {
        List<DataPanoramaRecordsOutDTO.AirPointOutDTO> airPointOutDTOList = new ArrayList<>();
        if (StringUtils.isEmpty(missionRecordsId) && StringUtils.isEmpty(taskId)) {
            return airPointOutDTOList;
        }
        //根据架次记录ID查询
        String airLineId = this.missionRecordsMapper.getAirLineId(missionRecordsId, taskId);
        if (StringUtils.isEmpty(airLineId)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_FIND_CORRESPONDING_ROUTE.getContent()));
        }
        AirLineEntity airLineEntity = this.airLineService.getById(airLineId);
        if (ObjectUtil.isNull(airLineEntity)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_FIND_CORRESPONDING_ROUTE.getContent()));
        }
        //航线类型不是全景报错
        if (!TaskModeEnum.PANORAMA.getValue().equals(airLineEntity.getType())) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THE_QUERY_ROUTE_IS_NOT_A_PANORAMIC_ROUTE_PLEASE_CHECK.getContent()));
        }
        String airPointJson = airLineEntity.getWaypoints();
        List<PanoramaLocationDTO> panoramaLocationDTOList = getLocationByWaypoints(airPointJson);
        if (CollectionUtil.isEmpty(panoramaLocationDTOList)) {
            return airPointOutDTOList;
        }
        //只返回有了航点ID的数据
        airPointOutDTOList = panoramaLocationDTOList.stream()
                .filter(x -> StringUtils.hasText(x.getAirPointId()))
                .map(DataPanoramaRecordsConverter.INSTANCES::convert)
                .collect(Collectors.toList());
        return airPointOutDTOList;
    }

    /**
     * 任务列表- 根据架次记录以及航点查找照片源数据 全量
     *
     * @param picInDTO
     * @return
     */
    @Override
    public List<DataPanoramaRecordsOutDTO.PicOutDTO> queryPointRecordsPic(DataPanoramaRecordsInDTO.PicInDTO picInDTO) {
        List<DataPanoramaRecordsOutDTO.PicOutDTO> picOutDTOs = new ArrayList<>();
        //如果航点ID不为空，默认按航点ID进行匹配
        Integer startIndex = 0, endIndex = 0;
        if (StringUtils.hasText(picInDTO.getAirPointId())) {
            //TODO 后续需要新增按航点ID分类照片的功能
        } else {
            //按航点序号进行匹配
            if (StringUtils.isEmpty(picInDTO.getAirPointIndex())) {
                if (log.isDebugEnabled()) {
                    log.debug("【queryPointRecordsPic】:pointIndexId is null ");
                }
                return picOutDTOs;
            }
            //固定31张 ,全景航线，一个点会被拆分成10个点，每个点三个拍照动作，其中第一个点还会多一张，从上往下的拍照，共计31张
            //根据是否可除25除尽判断是2.4.0之后的全景还是之前的全景
            List<MissionPhotoEntity> allPhotoByMissionRecordsId = this.missionPhotoService.getAllPhotoByMissionRecordsId(Integer.valueOf(picInDTO.getMissionRecordsId()));
            if (CollectionUtil.isNotEmpty(allPhotoByMissionRecordsId)) {
                if (allPhotoByMissionRecordsId.size() % 25 == 0) {
                    startIndex = 25 * (picInDTO.getAirPointIndex() - 1) + 1;
                    endIndex = 25 * (picInDTO.getAirPointIndex());
                } else {
                    startIndex = 31 * (picInDTO.getAirPointIndex() - 1) + 1;
                    endIndex = 31 * (picInDTO.getAirPointIndex());
                }
            }
            //御三的按照时间进行排序查询
            List<MissionPhotoPointOutDTO>   missionPhotoPointOutDTOS = this.missionPhotoService.getPhotoByPoint(startIndex, endIndex, picInDTO.getMissionRecordsId());
            if (CollectionUtil.isNotEmpty(missionPhotoPointOutDTOS)) {
                //判断是什么基槽类型，G900取广角；G600、S100等其他机型取普通可见光
                Integer lenType = LenTypeEnum.NORMAL_TYPE.getType();
                NestTypeEnum nestTypeEnum = getNestType(missionPhotoPointOutDTOS.get(0).getMissionId());
                //基站类型为G900，查询广角镜头，其他情况查询普通可见光
                if (nestTypeEnum.getValue() == NestTypeEnum.G900.getValue() || nestTypeEnum.getValue() == NestTypeEnum.G900_CHARGE.getValue()) {
                    lenType = LenTypeEnum.WIDE_TYPE.getType();
                }
                Integer finalLenType = lenType;
                //lenType有值，则用lenType进行判断
                if (judgePointPic(missionPhotoPointOutDTOS)) {
                    picOutDTOs = missionPhotoPointOutDTOS.stream()
                            .filter(x -> finalLenType.equals(x.getLenType()))
                            .map(DataPanoramaRecordsConverter.INSTANCES::convert)
                            .map(x -> {
                                x.setAirPointId(picInDTO.getAirPointId());
                                x.setAirPointIndex(picInDTO.getAirPointIndex());
                                return x;
                            })
                            .sorted(Comparator.comparing(DataPanoramaRecordsOutDTO.PicOutDTO::getWaypointsIndex))
                            .collect(Collectors.toList());
                    return picOutDTOs;
                }

                //需要判断是广角的照片-存在部分历史数据命名无规则，无法判断 ; G900查询广角，其他的查询普通可见光，普通可见光没有命名规则
                picOutDTOs = missionPhotoPointOutDTOS.stream()
                        .filter(x -> {
                            boolean bol = true;
                            //G900广角
                            if (nestTypeEnum.getValue() == NestTypeEnum.G900.getValue() || nestTypeEnum.getValue() == NestTypeEnum.G900_CHARGE.getValue()) {
                                bol = x.getPhotoUrl().endsWith("WIDE.jpg") || x.getPhotoUrl().endsWith("W.jpg");

                            } else {
                                //其他非G900，取普通可见光，普通可见光无命名规则，只能用排除法
                                boolean wideBol = !x.getPhotoUrl().endsWith("WIDE.jpg") && !x.getPhotoUrl().endsWith("W.jpg");
                                boolean zoomBol = !x.getPhotoUrl().endsWith("ZOOM.jpg") && !x.getPhotoUrl().endsWith("Z.jpg");
                                boolean thumBol = !x.getPhotoUrl().endsWith("THUM.jpg") && !x.getPhotoUrl().endsWith("T.jpg");
                                bol = wideBol && zoomBol && thumBol;
                            }
                            return bol;
                        })
                        .map(DataPanoramaRecordsConverter.INSTANCES::convert)
                        .map(x -> {
                            x.setAirPointId(picInDTO.getAirPointId());
                            x.setAirPointIndex(picInDTO.getAirPointIndex());
                            return x;
                        })
                        .sorted(Comparator.comparing(DataPanoramaRecordsOutDTO.PicOutDTO::getWaypointsIndex))
                        .collect(Collectors.toList());
            }
        }
        return picOutDTOs;
    }

    /**
     * 获取基站信息
     *
     * @return
     */
    private NestTypeEnum getNestType(String missionId) {
        //获取NestId
        TaskEntity taskEntity = this.taskService.getByMissionId(Integer.parseInt(missionId));
        if (taskEntity == null) {
            throw new BusinessException(String.format("getNestType:根据missionId->{} 查询不到Task记录", missionId));
        }
        if (StringUtils.isEmpty(taskEntity.getBaseNestId())) {
            throw new BusinessException(String.format("getNestType:baseNestId为空！查询不到基站类型", taskEntity.getBaseNestId()));
        }
        NestTypeEnum nestTypeEnum = this.baseNestService.getNestTypeByNestIdCache(taskEntity.getBaseNestId());
        return nestTypeEnum;
    }

    /**
     * 任务列表-根据架次记录以及全景点打包下载照片源数据
     *
     * @param picInDTO
     * @param response
     * @return
     */
    @Override
    public void downloadPointRecordsPic(DataPanoramaRecordsInDTO.PicInDTO picInDTO, HttpServletResponse response) {
        List<DataPanoramaRecordsOutDTO.PicOutDTO> picOutDTOList = this.queryPointRecordsPic(picInDTO);
        List<String> photoUrls = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(picOutDTOList)) {
            photoUrls = picOutDTOList.stream()
                    .map(DataPanoramaRecordsOutDTO.PicOutDTO::getPhotoUrl)
                    .collect(Collectors.toList());
            if (CollectionUtil.isEmpty(photoUrls)) {
                log.info("downloadPointRecordsPic: size -> 0");
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_THERE_IS_NO_DATA_THAT_CAN_BE_EXPORTED.getContent()));
            }
            //任务架次记录
            Integer missionRecordsId = Integer.parseInt(picInDTO.getMissionRecordsId());
            String missionName = this.missionRecordsMapper.selectMissionName(missionRecordsId);
            Integer flyIndex = this.missionRecordsMapper.selectFlyIndex(missionRecordsId);
            DownLoadUtils.downloadZipWithPip(photoUrls, String.format("%s-%d_第%d航点全景图片",missionName,flyIndex,picInDTO.getAirPointIndex()), response);
        }
    }

    /**
     * 任务列表- 查询航线信息
     *
     * @return
     */
    @Override
    public List<DataPanoramaRecordsOutDTO.TaskOutDTO> queryPanoramaTask() {
        List<DataPanoramaRecordsOutDTO.TaskOutDTO> taskOutDTOList = new ArrayList<>();
        //获取该用户拥有的单位全
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        //获取用户拥有的基站权限
        List<String> baseNestIds = this.nestAccountService.listNestIdByAccountId(TrustedAccessTracerHolder.get().getAccountId());
        log.info("queryPanoramaTask:baseNestIds -> {} , orgCode - > {} ", baseNestIds, orgCode);
        //根据基站查询对应的全景航线任务
        List<TaskOutDTO> taskOutDTOs = this.taskService.queryTaskByNest(baseNestIds, TaskModeEnum.PANORAMA.getValue());
        if (CollectionUtil.isEmpty(taskOutDTOs)) {
            return taskOutDTOList;
        }
        taskOutDTOList = taskOutDTOs.stream()
                //筛选当前登录人的单位
                .filter(x -> orgCode.equals(x.getOrgCode()))
                .map(DataPanoramaRecordsConverter.INSTANCES::convert)
                .collect(Collectors.toList());
        return taskOutDTOList;
    }

    /**
     * 根据航线中json字段解析出航点信息
     *
     * @param waypointJson
     * @return
     */
    @Override
    public List<PanoramaLocationDTO> getLocationByWaypoints(String waypointJson) {
        List<PanoramaLocationDTO> panoramaLocationDTOList = new ArrayList<>();
        if (StringUtils.isEmpty(waypointJson)) {
            return panoramaLocationDTOList;
        }
        Map<String, JSONObject> map = (Map) JSONUtil.parse(waypointJson);
        String mapConfigs = map.get("mapConfigs").toString();
        if (StringUtils.isEmpty(mapConfigs)) {
            if (log.isDebugEnabled()) {
                log.debug("【getLocationByWaypoints】mapConfigs : null");
            }
            return panoramaLocationDTOList;
        }
        Map<String, JSONArray> configMap = (Map) JSONUtil.parse(mapConfigs);
        JSONArray jsonArray = configMap.get("points");
        if (StringUtils.isEmpty(jsonArray)) {
            if (log.isDebugEnabled()) {
                log.debug("【getLocationByWaypoints】points : null");
            }
            return panoramaLocationDTOList;
        }
        for (int i = 0; i < jsonArray.size(); i++) {
            panoramaLocationDTOList.add(JSONUtil.toBean(jsonArray.getJSONObject(i).get("location").toString(), PanoramaLocationDTO.class));
        }
        return panoramaLocationDTOList;
    }

    /**
     * 获取tag
     *
     * @param tagIds
     * @return
     */
    private Map<String, String> getTagMap(List<String> tagIds) {
        Map<String, String> returnMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(tagIds)) {
            LambdaQueryWrapper<SysTagEntity> condition = Wrappers.lambdaQuery(SysTagEntity.class)
                    .in(SysTagEntity::getId, tagIds)
                    .select(SysTagEntity::getId, SysTagEntity::getName);
            List<SysTagEntity> sysTagEntityList = this.sysTagService.getBaseMapper().selectList(condition);
            returnMap = sysTagEntityList.stream()
                    .collect(Collectors.toMap(x -> x.getId().toString()
                            , SysTagEntity::getName
                            , (oldValue, newValue) -> newValue
                    ));
        }
        return returnMap;
    }

    /**
     * 判断是否可以通过lenType,只要有不等于-1的值，代表这里的数据已经不是历史数据
     *
     * @return
     */
    public boolean judgePointPic(List<MissionPhotoPointOutDTO> missionPhotoPointOutDTOS) {
        if (missionPhotoPointOutDTOS.stream().anyMatch(x -> x.getLenType() != null && x.getLenType() != -1)) {
            return true;
        }
        return false;
    }


}
