package com.imapcloud.nest.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.enums.FocalModeEnum;
import com.imapcloud.nest.enums.TaskModeEnum;
import com.imapcloud.nest.enums.TaskMoldEnum;
import com.imapcloud.nest.mapper.AirLineMapper;
import com.imapcloud.nest.model.*;
import com.imapcloud.nest.pojo.dto.*;
import com.imapcloud.nest.pojo.dto.unifyAirLineDto.GridPhotoParam;
import com.imapcloud.nest.pojo.dto.unifyAirLineDto.UnifyAirLineFormatDto;
import com.imapcloud.nest.service.*;
import com.imapcloud.nest.utils.DataUtil;
import com.imapcloud.nest.utils.DistanceUtil;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.ToolUtil;
import com.imapcloud.nest.utils.airline.*;
import com.imapcloud.nest.utils.fineUtil.FineInspectionZipParseUtil;
import com.imapcloud.nest.utils.fineUtil.FineParseRes;
import com.imapcloud.nest.utils.redis.RedisKeyEnum;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.BaseUavService;
import com.imapcloud.nest.v2.service.PowerComponentService;
import com.imapcloud.nest.v2.service.dto.out.BaseNestInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.CameraParamsOutDTO;
import com.imapcloud.nest.v2.service.dto.out.FpiAirlinePackageParseOutDTO;
import com.imapcloud.sdk.pojo.constant.ActionTypeEnum;
import com.imapcloud.sdk.pojo.constant.HeadingModeEnum;
import com.imapcloud.sdk.pojo.entity.Waypoint;
import com.imapcloud.sdk.pojo.entity.WaypointAction;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 航线表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
@Service
public class AirLineServiceImpl extends ServiceImpl<AirLineMapper, AirLineEntity> implements AirLineService {

    @Autowired
    private MissionService missionService;

    @Autowired
    private MissionParamService missionParamService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private FineInspZipService fineInspZipService;

    @Autowired
    private FineInspTowerService fineInspTowerService;

    @Autowired
    private BaseNestService baseNestService;

    @Autowired
    private BaseUavService baseUavService;


    @Override
    public List<AirLineEntity> listAirLineIdAndName() {
        return baseMapper.selectAllIdAndName();
    }

    @Override
    public List<Waypoint> parseAirLine(Integer missionId, String nestId, Integer mold) {
        MissionEntity missionEntity = missionService.getById(missionId);
        if (missionEntity == null) {
            return Collections.emptyList();
        }
//        CameraParamsOutDTO cameraParam = aircraftService.getCameraParamByNestId(nestId, mold);
        CameraParamsOutDTO cameraParam = baseUavService.getCameraParamByNestId(nestId);
//        NestEntity nestEntity = nestService.getById(nestId);
        BaseNestInfoOutDTO baseNestInfo = baseNestService.getBaseNestInfo(nestId);
        AirLineEntity airLineEntity = this.getById(missionEntity.getAirLineId());
        MissionParamEntity missionParamEntity = missionParamService.getById(missionEntity.getMissionParamId());

        //本地上传、动态规划、变电站规划使用绝对飞行
        //全景采集、线状巡视、航点飞行使用相对飞行
        AirLineParams airLineParams = AirLineParams.instance()
                .airLineType(airLineEntity.getType())
                .nestType(baseNestInfo.getType())
                .startStopAlt(missionParamEntity.getStartStopPointAltitude())
                .nestAltitude(baseNestInfo.getAltitude())
                .airLineJson(airLineEntity.getWaypoints())
                .waypointSpeed((double) missionParamEntity.getSpeed())
                .headingMode(HeadingModeEnum.getInstance(missionParamEntity.getHeadingMode()))
                .focalMode(FocalModeEnum.getInstance(airLineEntity.getFocalMode()));

        if (Objects.nonNull(cameraParam)) {
            airLineParams.focalLengthMin(cameraParam.getFocalLengthMin());
        }

        return AirLineBuildUtil.buildAirLine(airLineParams);
    }

    @Override
    public RestRes importPointCloudAirLine(ImportPcRouteDto routeDto) {
        MultipartFile file = routeDto.getFile();
        Integer airLineType = routeDto.getAirLineType();
        String name = file.getOriginalFilename();
        String nestId = routeDto.getNestId();
        String orgCode = routeDto.getOrgCode();
        if (name != null) {
            int suffixIndex = name.lastIndexOf(".");
            String fileSuffix = name.substring(suffixIndex + 1);
            BaseNestInfoOutDTO baseNestInfo = null;
            if (TaskMoldEnum.NEST.getCode().equals(routeDto.getMold())) {
//                nestEntity = nestService.getById(nestId);
                baseNestInfo = baseNestService.getBaseNestInfo(nestId);
                if (Objects.isNull(baseNestInfo)) {
                    return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_DATABASE_WITH_NEST.getContent()).replace("nestID",nestId));
                }
            }

            Map<String, Object> map = new HashMap<>(2);
            TaskOfManageTaskDto taskOfManageTaskDto = new TaskOfManageTaskDto();
            taskOfManageTaskDto.setAirLineType(airLineType);
            if ("data".equals(fileSuffix)) {
                DataUtil.Data data = DataUtil.readDataMultiPartFile(file);
                if (data == null) {
                    return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_DATA_ROUTE_PARSING.getContent()));
                }
                if (data.isMultiMission()) {
                    taskOfManageTaskDto.setMultiMission(true);
                    JSONArray jsonDatas = data.getJsonData();
                    if (jsonDatas == null) {
                        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_ROUTE_RESOLUTION.getContent()));
                    }


                    int pointCount = 0;
                    int photoCount = 0;
                    double predicMiles = 0.0;
                    long predicTime = 0L;
                    double highestAlt = Double.MIN_VALUE;

                    if (TaskMoldEnum.NEST.getCode().equals(routeDto.getMold())) {
                        AirLineParams airLineParams = AirLineParams.instance()
                                .airLineType(airLineType)
                                .nestType(baseNestInfo.getType())
                                .startStopAlt(0.0)
                                .nestAltitude(baseNestInfo.getAltitude())
                                .waypointSpeed(4.0);

                        for (int i = 0; i < jsonDatas.size(); i++) {
                            JSONArray waypointArray = jsonDatas.getJSONArray(i);
                            airLineParams.setWaypointArray(waypointArray);
                            List<Waypoint> waypointList = AirLineBuildUtil.buildAirLine(airLineParams);
                            if (CollectionUtil.isEmpty(waypointList)) {
                                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_ROUTE_RESOLUTION.getContent()));
                            }
                            double tempAirLineHighestAlt = getAirLineHighestAlt(waypointList);
                            highestAlt = Math.max(tempAirLineHighestAlt, highestAlt);
                            Map<String, Object> computeMap = computeWaypointList(waypointList, baseNestInfo.getLongitude(), baseNestInfo.getLatitude(), 4.0, 100);
                            pointCount += (int) computeMap.get("pointCount");
                            photoCount += (int) computeMap.get("photoCount");
                            predicMiles += (double) computeMap.get("predicMiles");
                            predicTime += (long) computeMap.get("predicTime");
                        }
                    }

                    if (TaskMoldEnum.APP.getCode().equals(routeDto.getMold())) {
                        for (int i = 0; i < jsonDatas.size(); i++) {
                            JSONArray waypointArray = jsonDatas.getJSONArray(i);
                            Map<String, Object> computeMap = computeIflyerTerminal(100.0, 4, 2, jsonDatas);
                            double tempAirLineHighestAlt = getAirLineHighestAlt(waypointArray);
                            highestAlt = Math.max(tempAirLineHighestAlt, highestAlt);
                            pointCount += (int) computeMap.get("pointCount");
                            photoCount += (int) computeMap.get("photoCount");
                            predicMiles += (double) computeMap.get("predicMiles");
                            predicTime += (long) computeMap.get("predicTime");
                        }
                    }

                    taskOfManageTaskDto.setPredictFlyTime(predicTime);
                    taskOfManageTaskDto.setAirLineLength(predicMiles);
                    taskOfManageTaskDto.setPhotoCount(photoCount);
                    taskOfManageTaskDto.setPointCount(pointCount);


                    Map<Integer, String> airLineMap = new HashMap<>(jsonDatas.size());
                    for (int i = 0; i < jsonDatas.size(); i++) {
                        JSONArray jsonArray = jsonDatas.getJSONArray(i);
                        // 查询巡检类型
                        putAnalysisType(jsonArray, orgCode);
                        airLineMap.put(i, jsonArray.toJSONString());
                    }

                    map.put("highestAlt", highestAlt + 10);
                    taskOfManageTaskDto.setAirLineMap(airLineMap);
                    map.put("taskDto", taskOfManageTaskDto);
                    taskOfManageTaskDto.setAbsolute(true);
                    return RestRes.ok(map);
                }
                JSONArray waypointArray = data.getJsonData();
                taskOfManageTaskDto.setAbsolute(true);
                return buildTaskOfManageTaskDto(taskOfManageTaskDto, baseNestInfo, airLineType, 4.0, waypointArray, routeDto.getMold());
            }
            if ("kml".equals(fileSuffix)) {
                try {
                    InputStream inputStream = file.getInputStream();
                    ParseKmlResDTO parseKmlResDTO = KmlAirLineUtil.parseKml(inputStream);
                    if (parseKmlResDTO == null) {
                        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_KML_ROUTE.getContent()));
                    }
                    taskOfManageTaskDto.setAbsolute(parseKmlResDTO.getAbsolute());
                    return buildTaskOfManageTaskDto(taskOfManageTaskDto, baseNestInfo, airLineType, 4.0, parseKmlResDTO.getJsonArray(), routeDto.getMold());
                } catch (IOException e) {
                    return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_KML_ROUTE.getContent()));
                }

            }
        }

        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INCORRECT_IMPORT_FORMAT.getContent()));
    }

    @Resource
    private PowerComponentService powerComponentService;
    private void putAnalysisType(JSONArray jsonArray, String orgCode) {
        // 获取航点的台账、部件库，添加识别类型
        List<String> checkpointuuidList = Lists.newLinkedList();
        for (Object o : jsonArray) {
            JSONObject json = (JSONObject) o;
            // 航点ID
            String checkpointuuid = json.getString("checkpointuuid");
            if (StringUtils.isNotBlank(checkpointuuid)) {
                checkpointuuidList.add(checkpointuuid);
            }
        }
        Map<String, List<Integer>> stringListMap = powerComponentService.selectComponentRuleByWaypointIdList(checkpointuuidList, orgCode);
        for (Object o : jsonArray) {
            JSONObject json = (JSONObject) o;
            // 航点ID
            String checkpointuuid = json.getString("checkpointuuid");
            if (StringUtils.isNotBlank(checkpointuuid)) {
                List<Integer> photoPropList = stringListMap.get(checkpointuuid);
                json.put("photoPropList", photoPropList);
            }
        }
    }

    private RestRes buildTaskOfManageTaskDto(TaskOfManageTaskDto taskOfManageTaskDto, BaseNestInfoOutDTO baseNestInfo, Integer airLineType, Double waypointSpeed, JSONArray waypointArray, Integer mold) {
        Map<String, Object> map = new HashMap<>(2);
        taskOfManageTaskDto.setMultiMission(false);
        Map<String, Object> computeMap = null;
        double highestAlt = Double.MIN_VALUE;
        if (TaskMoldEnum.NEST.getCode().equals(mold)) {
            AirLineParams airLineParams = AirLineParams.instance()
                    .airLineType(airLineType)
                    .nestType(baseNestInfo.getType())
                    //通过数据库查出与机巢绑定的起降行高
                    .startStopAlt(0.0)
                    .nestAltitude(baseNestInfo.getAltitude())
                    .waypointSpeed(waypointSpeed)
                    .waypointArray(waypointArray);

            List<Waypoint> waypointList = AirLineBuildUtil.buildAirLine(airLineParams);
            if (CollectionUtil.isEmpty(waypointList)) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_ROUTE_RESOLUTION.getContent()));
            }
            highestAlt = getAirLineHighestAlt(waypointList);
            computeMap = computeWaypointList(waypointList, baseNestInfo.getLongitude(), baseNestInfo.getLatitude(), waypointSpeed, 100);
        } else if (TaskMoldEnum.APP.getCode().equals(mold)) {
            computeMap = computeIflyerTerminal(100.0, 4, 2, waypointArray);
            highestAlt = getAirLineHighestAlt(waypointArray);
        }

        if (CollectionUtil.isEmpty(computeMap)) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_ROUTE_RESOLUTION.getContent()));
        }
        taskOfManageTaskDto.setPredictFlyTime((long) computeMap.get("predicTime"));
        taskOfManageTaskDto.setAirLineLength((double) computeMap.get("predicMiles"));
        taskOfManageTaskDto.setPhotoCount((int) computeMap.get("photoCount"));
        taskOfManageTaskDto.setPointCount((int) computeMap.get("pointCount"));

        Map<Integer, String> airLineMap = new HashMap<>(2);
        airLineMap.put(0, waypointArray.toJSONString());
        taskOfManageTaskDto.setAirLineMap(airLineMap);
        map.put("highestAlt", highestAlt + 10);
        map.put("taskDto", taskOfManageTaskDto);
        return RestRes.ok(map);
    }

    @Override
    public List<AirLineEntity> listPredicMilesAndMergeCountByIdList(List<Integer> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return Collections.emptyList();
        }
        return baseMapper.batchSelectEstimateMilesAndMergeCountByIdList(idList);
    }

    @Override
    public int batchSoftDeleteByIds(List<Integer> idList) {
        if (!CollectionUtils.isEmpty(idList)) {
            return baseMapper.batchSoftDeleteByIds(idList);
        }
        return 0;
    }

    @Override
    public Map<String, Object> computeWaypointList(List<Waypoint> waypointList, Double nestLng, Double nestLat, Double airSpeed, Integer startStopAlt) {
        if (CollectionUtil.isEmpty(waypointList)) {
            return Collections.emptyMap();
        }
        //航线总长度,单位是米
        double sumDistance = 0.0;
        for (int i = 0; i < waypointList.size() - 1; i++) {
            Waypoint p1 = waypointList.get(i);
            Waypoint p2 = waypointList.get(i + 1);
            sumDistance += DistanceUtil.getMercatorDistanceViaLonLat(p1.getWayPointLongitude(), p1.getWayPointLatitude(), p2.getWayPointLongitude(), p2.getWayPointLatitude());
        }

        double startStopTime = (startStopAlt * 2) / airSpeed;

        //降落点到第一个的距离
        Waypoint lastWaypoint = waypointList.get(waypointList.size() - 1);
        Waypoint firstWaypoint = waypointList.get(0);
        double lastDistance = DistanceUtil.getMercatorDistanceViaLonLat(lastWaypoint.getWayPointLongitude(), lastWaypoint.getWayPointLatitude(), nestLng, nestLat);
        double firstDistance = DistanceUtil.getMercatorDistanceViaLonLat(firstWaypoint.getWayPointLongitude(), firstWaypoint.getWayPointLatitude(), nestLng, nestLat);


        double absDistance = Math.abs(startStopAlt - firstWaypoint.getWayPointAltitude()) + Math.abs(startStopAlt - lastWaypoint.getWayPointAltitude());
        sumDistance += (lastDistance + firstDistance + absDistance);

        //拍照数
        int photoCount = waypointList.stream().map(Waypoint::getWaypointActionList).mapToInt(wl -> {
            int count = 0;
            for (WaypointAction wa : wl) {
                if (ActionTypeEnum.START_TAKE_PHOTO.equals(wa.getActionType())) {
                    count++;
                }
            }
            return count;
        }).sum();

        int videoCount = waypointList.stream().map(Waypoint::getWaypointActionList).mapToInt(wl -> {
            int count = 0;
            for (WaypointAction wa : wl) {
                if (ActionTypeEnum.START_RECORD.equals(wa.getActionType())) {
                    count++;
                }
            }
            return count;
        }).sum();

        long videoLength = 0;
        if (videoCount > 0) {
            boolean videoState = false;
            FlyPoint beforePoint = new FlyPoint();
            for (int i = 0; i < waypointList.size(); i++) {
                Waypoint waypoint = waypointList.get(i);
                String wayPointAction = waypoint.getWayPointAction();
                List<Integer> waypointActionList = null;
                if (StrUtil.isNotEmpty(wayPointAction)) {
                    waypointActionList = Arrays.asList(wayPointAction.split(",")).stream().filter(e -> e != null && e.trim().length() > 0).map(e -> Integer.parseInt(e)).collect(Collectors.toList());
                    Double altitude = waypoint.getWayPointAltitude();
                    Double latitude = waypoint.getWayPointLatitude();
                    Double longitude = waypoint.getWayPointLongitude();
                    Double speed = waypoint.getWayPointSpeed();
                    // 结束录制
                    if (waypointActionList.contains(ActionTypeEnum.STOP_RECORD.getValue())) {
                        videoState = false;
                        FlyPoint flyPoint = new FlyPoint(altitude, latitude, longitude, speed);
                        if (ToolUtil.isNotEmpty(beforePoint) && ToolUtil.isNotEmpty(flyPoint)) {
                            double distance = DistanceUtil.getMercatorDistanceViaFlyPoint(beforePoint, flyPoint);
                            videoLength = videoLength + new Double(distance / beforePoint.getWayPointSpeed()).longValue();
                        }
                        // 重置前面点
                        beforePoint = new FlyPoint();
                    }
                    if (videoState) {
                        FlyPoint flyPoint = new FlyPoint(altitude, latitude, longitude, speed);
                        if (ToolUtil.isNotEmpty(beforePoint) && ToolUtil.isNotEmpty(flyPoint)) {
                            double distance = DistanceUtil.getMercatorDistanceViaFlyPoint(beforePoint, flyPoint);
                            videoLength = videoLength + new Double(distance / beforePoint.getWayPointSpeed()).longValue();
                        }
                        // 重置前面点
                        beforePoint = new FlyPoint(altitude, latitude, longitude, speed);
                    }
                    // 开始录制
                    if (waypointActionList.contains(ActionTypeEnum.START_RECORD.getValue())) {
                        videoState = true;
                        beforePoint = new FlyPoint(altitude, latitude, longitude, speed);
                    }

                }
            }
        }
        int pointCount = waypointList.size();
        double flyTime = (sumDistance / airSpeed) + startStopTime + (pointCount * 5) + (absDistance / airSpeed);

        Map<String, Object> map = new HashMap<>(8);
        map.put("predicMiles", sumDistance);
        map.put("predicTime", Math.round(flyTime));
        map.put("photoCount", photoCount);
        //航点数把首尾两点减去，因为在解析航线的时候，已经添加了起降点
        map.put("pointCount", pointCount - 2);
        map.put("videoCount", videoCount);
        map.put("videoLength", videoLength);

        return map;
    }

    public Map<String, Object> computeIflyerTerminal(Double startStopAlt, Integer speed, Integer airLineType, JSONArray jsonArray) {
        if (startStopAlt != null && speed != null && airLineType != null && jsonArray != null) {
            if (airLineType == 2) {
                Integer photoCount = 0;
                Integer videoCount = 0;
                double sumDistance = 0.0;
                for (int i = 0; i < jsonArray.size() - 1; i++) {
                    JSONObject point1 = jsonArray.getJSONObject(i);
                    JSONObject point2 = jsonArray.getJSONObject(i + 1);
                    if (point1.getInteger("waypointType") == 0) {
                        photoCount++;
                    }

                    sumDistance += DistanceUtil.getMercatorDistanceViaLonLat(point1.getDouble("aircraftLocationLongitude"), point1.getDouble("aircraftLocationLatitude"), point2.getDouble("aircraftLocationLongitude"), point2.getDouble("aircraftLocationLatitude"));
                }
                Integer pointCount = jsonArray.size();
                //总长度=航线总长度+2*起降高度
                double totalDistance = sumDistance + 2 * startStopAlt;
                double flyTime = totalDistance / speed + (photoCount * 5);
                Map<String, Object> map = new HashMap<>(8);
                map.put("predicMiles", sumDistance);
                map.put("predicTime", Math.round(flyTime));
                map.put("photoCount", photoCount);
                map.put("pointCount", pointCount);
                map.put("videoCount", videoCount);
                return map;
            }
            if (airLineType == 3) {
                Map<String, Object> map = new HashMap<>(8);
                map.put("predicMiles", 0.0);
                map.put("predicTime", 0L);
                map.put("photoCount", 0);
                map.put("pointCount", 0);
                map.put("videoCount", 0);
                return map;
            }
        }
        return null;
    }

    @Override
    public Integer getAirLineTypeById(Integer id) {
        if (id != null) {
            return baseMapper.selectTypeById(id);
        }
        return 0;
    }

    @Override
    public RestRes listAirLineJsonByTaskId(Integer taskId) {
        String redisKey = RedisKeyEnum.REDIS_KEY.className("AirLineServiceImpl").methodName("listAirLineJsonByTaskId").identity("taskAirLineMap", taskId.toString()).type("map").get();
        Map<String, Object> resMap = new HashMap<>(2);
        Map<Integer, Object> airLineMap = (Map<Integer, Object>) redisService.get(redisKey);
        if (CollectionUtil.isEmpty(airLineMap)) {
            List<MissionEntity> missionEntities = missionService.listMissionByTaskId(taskId);
            if (CollectionUtil.isNotEmpty(missionEntities)) {
                Map<Integer, Integer> missionIdAndAirLineIdMap = missionEntities.stream().collect(Collectors.toMap(MissionEntity::getAirLineId, MissionEntity::getId));
                List<Integer> airLineIdList = missionEntities.stream().map(MissionEntity::getAirLineId).collect(Collectors.toList());
                List<AirLineEntity> airLineEntityList = this.list(new QueryWrapper<AirLineEntity>().in("id", airLineIdList));
                Map<Integer, Object> map = new HashMap<>(airLineIdList.size());
                for (AirLineEntity ale : airLineEntityList) {
                    if (ale.getType() == 1) {
                        map.put(missionIdAndAirLineIdMap.get(ale.getId()), buildUnifyAirLineJson(ale.getOriginalWaypoints()));
                    } else if (ale.getType() == 2 || ale.getType() == 3) {
                        map.put(missionIdAndAirLineIdMap.get(ale.getId()), buildAirLineJson(ale.getWaypoints(), ale.getType()));
                    }
                }
                redisService.set(redisKey, map);
                resMap.put("airLineJson", map);
                return RestRes.ok(resMap);
            }
            return RestRes.err();
        }
        resMap.put("airLineJson", airLineMap);
        return RestRes.ok(resMap);

    }

    @Override
    public Map<String, Object> queryIsCoorTurning(Integer taskId) {
        Map<String, Object> res = new HashMap<>();
        List<MissionEntity> missionEntities = missionService.listMissionByTaskId(taskId);
        if (CollectionUtil.isNotEmpty(missionEntities)) {
//            List<Integer> airLineIdList = missionEntities.stream().map(MissionEntity::getAirLineId).collect(Collectors.toList());
//            List<AirLineEntity> airLineEntityList = this.list(new QueryWrapper<AirLineEntity>().in("id", airLineIdList));
//            if (CollectionUtil.isNotEmpty(airLineEntityList)) {
//                AirLineEntity airLineEntity = airLineEntityList.get(0);
//                String waypoints = airLineEntity.getWaypoints();
//                UnifyAirLineFormatDto unifyAirLineFormatDto = JSONObject.parseObject(waypoints, UnifyAirLineFormatDto.class);
//                TaskModeEnum mode = unifyAirLineFormatDto.getMode();
//                JSONObject gridParamJson = (JSONObject) unifyAirLineFormatDto.getLineConfigs().get(mode.name());
//                GridPhotoParam gridPhotoParam = JSONObject.parseObject(gridParamJson.toJSONString(), GridPhotoParam.class);
//                Integer lineAlt = gridPhotoParam.getLineAlt();
//                Double sideOverlap = gridPhotoParam.getSideOverlap();
//                res.put("lineAlt", lineAlt);
//                res.put("sideOverlap", sideOverlap);
//            }
            MissionEntity missionEntity = missionEntities.get(0);
            Integer missionParamId = missionEntity.getMissionParamId();
            MissionParamEntity missionParamEntity = missionParamService.queryMissionParamInfo(missionParamId);
            Integer isCoorTurning = missionParamEntity.getIsCoorTurning();
            res.put("isCoorTurning", isCoorTurning);
        }
        return res;
    }

    @Override
    public Integer queryAirLinePhotoCount(Integer taskId) {
        List<MissionEntity> missionEntities = missionService.listMissionByTaskId(taskId);
        if (CollectionUtil.isNotEmpty(missionEntities)) {
            List<Integer> airLineIdList = missionEntities.stream().map(MissionEntity::getAirLineId).collect(Collectors.toList());
            List<AirLineEntity> airLineEntityList = this.list(new QueryWrapper<AirLineEntity>().in("id", airLineIdList));
            if (CollectionUtil.isNotEmpty(airLineEntityList)) {
                Integer res = 0;
                for (AirLineEntity airLineEntity : airLineEntityList) {
                    res += airLineEntity.getPhotoCount();
                }
                return res;
            }
        }
        return 0;
    }


    @Override
    public Map<String, Object> computeFlyPointList(List<FlyPoint> flyPointList, Double nestLng, Double nestLat, Double airSpeed, Integer startStopAlt) {
        //航线总长度,单位是米
        double sumDistance = 0.0;
        for (int i = 0; i < flyPointList.size() - 1; i++) {
            FlyPoint p1 = flyPointList.get(i);
            FlyPoint p2 = flyPointList.get(i + 1);
            sumDistance += DistanceUtil.getMercatorDistanceViaLonLat(p1.getAircraftLocationLongitude(), p1.getAircraftLocationLatitude(), p2.getAircraftLocationLongitude(), p2.getAircraftLocationLatitude());
        }

        double startStopTime = (startStopAlt * 2) / airSpeed;

        //降落点到第一个的距离
        FlyPoint lastWaypoint = flyPointList.get(flyPointList.size() - 1);
        FlyPoint firstWaypoint = flyPointList.get(0);
        double lastDistance = DistanceUtil.getMercatorDistanceViaLonLat(lastWaypoint.getAircraftLocationLongitude(), lastWaypoint.getAircraftLocationLatitude(), nestLng, nestLat);
        double firstDistance = DistanceUtil.getMercatorDistanceViaLonLat(firstWaypoint.getAircraftLocationLongitude(), firstWaypoint.getAircraftLocationLatitude(), nestLng, nestLat);
        double absDistance = Math.abs(startStopAlt - firstWaypoint.getAircraftLocationAltitude()) + Math.abs(startStopAlt - lastWaypoint.getAircraftLocationAltitude());
        sumDistance += (lastDistance + firstDistance + absDistance);

        //拍照点数
        long photoCount = flyPointList.stream().filter(p -> p.getWaypointType() == 0).count();

        int pointCount = flyPointList.size();

        double flyTime = (sumDistance / airSpeed) + startStopTime + (pointCount * 5) + (absDistance / airSpeed);

        Map<String, Object> map = new HashMap<>(4);
        map.put("predicMiles", sumDistance);
        map.put("predicTime", Math.round(flyTime));
        map.put("photoCount", (int) photoCount);
        //航点数把首位两个点去掉
        map.put("pointCount", pointCount);
        return map;
    }

//    @Transactional
//    @Override
//    public RestRes uploadFineInspectionZip(String zipPath, String originalFilename) {
//        FineParseRes fineParseRes = FineInspectionZipParseUtil.uploadAdnParseZip(zipPath, originalFilename);
//        if (fineParseRes == null || fineParseRes.getFineInspZipEntity() == null || fineParseRes.getFineInspTowerEntityList() == null) {
//            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FINE_PATROL_PACKAGE_PARSING_ERROR.getContent()));
//        }
//        FineInspZipEntity fineInspZipEntity = fineParseRes.getFineInspZipEntity();
//        //TODO 需要写一个定时检查在空闲的时间去删除无用的数据
//        boolean res1 = fineInspZipService.save(fineInspZipEntity);
//        List<FineInspTowerEntity> fineInspTowerEntityList = fineParseRes.getFineInspTowerEntityList();
//        for (FineInspTowerEntity fineInspTowerEntity : fineInspTowerEntityList) {
//            fineInspTowerEntity.setFineInspZipId(fineInspZipEntity.getId());
//        }
//
//        //TODO 需要写一个定时检查在空闲的时间去删除无用的数据
//        boolean res2 = fineInspTowerService.saveBatch(fineInspTowerEntityList);
//
//        FineParseResDto fineParseResDto = new FineParseResDto();
//        fineParseResDto.setZipId(fineInspZipEntity.getId());
//        fineParseResDto.setZipName(fineInspZipEntity.getZipName());
//        List<Map<String, Object>> towerList = fineInspTowerEntityList.stream().map(t -> {
//            Map<String, Object> map = new HashMap<>(2);
//            map.put("towerId", t.getId());
//            map.put("towerName", t.getTowerName());
//            map.put("position", Arrays.asList(t.getTowerLng(), t.getTowerLat(), t.getTowerAlt()));
//            return map;
//        }).collect(Collectors.toList());
//        fineParseResDto.setTowerList(towerList);
//
//
//        Map<String, Object> resMap = new HashMap<>(2);
//        resMap.put("fineParseResDto", fineParseResDto);
//        return res1 && res2 ? RestRes.ok(resMap) : RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_SAVE_PARSED_DATA.getContent()));
//    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String saveFpiPoleTowerInfo(final FpiAirlinePackageParseOutDTO data) {
        FineInspZipEntity fineInspZipEntity = new FineInspZipEntity();
        fineInspZipEntity.setZipName(data.getZipName());
        fineInspZipEntity.setOriginZipUrl("");
        fineInspZipService.save(fineInspZipEntity);
        if(!CollectionUtils.isEmpty(data.getTowerList())){
            List<FineInspTowerEntity> list = data.getTowerList().stream()
                    .map(r -> {
                        FineInspTowerEntity fineInspTowerEntity = new FineInspTowerEntity();
                        fineInspTowerEntity.setFineInspZipId(fineInspZipEntity.getId());
                        fineInspTowerEntity.setTowerName(r.getTowerName());
                        fineInspTowerEntity.setPointCloudFileUrl(r.getTxtUrl());
                        fineInspTowerEntity.setRouteFileUrl(r.getDataUrl());
                        if(!CollectionUtils.isEmpty(r.getPosition()) && r.getPosition().size() == 3){
                            fineInspTowerEntity.setTowerLng(Double.parseDouble(r.getPosition().get(0)));
                            fineInspTowerEntity.setTowerLat(Double.parseDouble(r.getPosition().get(1)));
                            fineInspTowerEntity.setTowerAlt(Double.parseDouble(r.getPosition().get(2)));
                        }
                        return fineInspTowerEntity;
                    })
                    .collect(Collectors.toList());
            fineInspTowerService.saveBatch(list);
            Map<String, Integer> collect = list.stream().collect(Collectors.toMap(FineInspTowerEntity::getTowerName, FineInspTowerEntity::getId));
            data.getTowerList().forEach(r -> r.setTowerId(collect.getOrDefault(r.getTowerName(), null)));
        }
        return fineInspZipEntity.getId().toString();
    }

    @Override
    public RestRes requestPointCloudAndRoute(Integer towerId) {
        FineInspTowerEntity fineInspTowerEntity = fineInspTowerService.getById(towerId);
        List<List<Double>> pcList = FineInspectionZipParseUtil.parseTxt(fineInspTowerEntity.getPointCloudFileUrl(), fineInspTowerEntity.getTowerLat(), fineInspTowerEntity.getTowerLng());
        if (pcList == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_READ_POINT_CLOUD_DATA.getContent()));
        }
        DataUtil.Data data = FineInspectionZipParseUtil.parseData(fineInspTowerEntity.getRouteFileUrl());
        if (data == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_READ_ROUTE_DATA.getContent()));
        }
        Map<String, Object> resMap = new HashMap<>(2);
        resMap.put("pcList", pcList);
        List<PointCloudWaypoint> pointCloudWaypoints = ComputePcWaypointUtil.jsonArray2OpcList(data.getJsonData());
        ComputePcWaypointUtil.Res res = ComputePcWaypointUtil.compute(pointCloudWaypoints);
        List<PointCloudWaypoint> pointCloudWaypoints1 = ComputePcWaypointUtil.handleByNameAndSpeed(pointCloudWaypoints);
        resMap.put("routeRes", res);
        resMap.put("route", pointCloudWaypoints1);
        return RestRes.ok(resMap);
    }

    @Override
    public AirLineEntity getPicCountAndVideoCountAndLen(Integer missionId) {
        if (missionId == null) {
            return null;
        }
        return baseMapper.selectPhotoCountAndVideoCountAndLen(missionId);
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
                    Double wayPointAltitude = jsonObject.getDouble("wayPointAltitude");
                    Map<String, Object> airLineMap = new HashMap<>(2);
                    airLineMap.put("lat", wayPointLatitude);
                    airLineMap.put("lng", wayPointLongitude);
                    airLineMap.put("alt", wayPointAltitude);
                    list.add(airLineMap);
                }
            } else if (type == 2) {
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Double wayPointLatitude = jsonObject.getDouble("aircraftLocationLatitude");
                    Double wayPointLongitude = jsonObject.getDouble("aircraftLocationLongitude");
                    Double wayPointAltitude = jsonObject.getDouble("aircraftLocationAltitude");
                    Map<String, Object> airLineMap = new HashMap<>(2);
                    airLineMap.put("lat", wayPointLatitude);
                    airLineMap.put("lng", wayPointLongitude);
                    airLineMap.put("alt", wayPointAltitude);
                    list.add(airLineMap);
                }
            }
            return list;
        }
        return Collections.emptyList();
    }


    private List<Map<String, Object>> buildUnifyAirLineJson(String unifyAirLineJson) {
        if (unifyAirLineJson == null) {
            return Collections.emptyList();
        }

        List<Waypoint> waypointList = JSONArray.parseArray(unifyAirLineJson, Waypoint.class);
        List<Map<String, Object>> list = new ArrayList<>(waypointList.size());
        for (Waypoint point : waypointList) {
            Map<String, Object> airLineMap = new HashMap<>(2);
            airLineMap.put("lat", point.getWayPointLatitude());
            airLineMap.put("lng", point.getWayPointLongitude());
            airLineMap.put("alt", point.getWayPointAltitude());
            list.add(airLineMap);
        }
        return list;
    }


    private double getAirLineHighestAlt(List<Waypoint> waypointList) {
        //要去掉首尾点，因为添加了默认的起降点
        if (CollectionUtil.isNotEmpty(waypointList)) {
            double airLineHighest = waypointList.get(1).getWayPointAltitude();
            for (int i = 2; i < waypointList.size() - 1; i++) {
                Double temp = waypointList.get(i).getWayPointAltitude();
                if (temp > airLineHighest) {
                    airLineHighest = temp;
                }
            }
            return airLineHighest;
        }
        return 0.0;
    }

    private double getAirLineHighestAlt(JSONArray jsonArray) {
        if (CollectionUtil.isNotEmpty(jsonArray)) {
            double airLineHighest = jsonArray.getJSONObject(0).getDouble("aircraftLocationAltitude");
            for (int i = 1; i < jsonArray.size(); i++) {
                Double temp = jsonArray.getJSONObject(0).getDouble("aircraftLocationAltitude");
                if (temp > airLineHighest) {
                    airLineHighest = temp;
                }
            }
            return airLineHighest;
        }
        return 0.0;
    }

}
