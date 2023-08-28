package com.imapcloud.nest.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.enums.AirLineDefaultSpeedEnum;
import com.imapcloud.nest.model.FixAirLineEntity;
import com.imapcloud.nest.mapper.FixAirLineMapper;
import com.imapcloud.nest.model.NestEntity;
import com.imapcloud.nest.pojo.dto.FixAirLineDto;
import com.imapcloud.nest.pojo.dto.TaskOfManageTaskDto;
import com.imapcloud.nest.service.AirLineService;
import com.imapcloud.nest.service.FixAirLineService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.service.NestService;
import com.imapcloud.nest.utils.DataUtil;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.utils.UuidUtil;
import com.imapcloud.nest.utils.airline.AirLineBuildUtil;
import com.imapcloud.nest.utils.airline.AirLineParams;
import com.imapcloud.nest.utils.redis.RedisKeyEnum;
import com.imapcloud.nest.utils.redis.RedisService;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.dto.out.BaseNestInfoOutDTO;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.mission.MissionManager;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import com.imapcloud.sdk.pojo.entity.Mission;
import com.imapcloud.sdk.pojo.entity.Waypoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * <p>
 * 固定航线表 服务实现类
 * </p>
 *
 * @author wmin
 * @since 2020-10-10
 */
@Service
public class FixAirLineServiceImpl extends ServiceImpl<FixAirLineMapper, FixAirLineEntity> implements FixAirLineService {

    @Autowired
    private NestService nestService;

    @Autowired
    private AirLineService airLineService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private BaseNestService baseNestService;

    @Override
    public RestRes importCloudAirLine(MultipartFile file, Integer nestId, Integer type) {
        String filename = file.getOriginalFilename();
        int i = filename.indexOf(".");
        String name = filename.substring(0, i);

        if ("application/json".equals(file.getContentType())) {
            String fileJson = multipartFileToStr(file);
            JSONObject jsonObject = JSONObject.parseObject(fileJson);
            JSONObject airLineData = jsonObject.getJSONObject("airLineData");
            Integer airLineType = jsonObject.getInteger("airLineType");
            String missionStr = airLineData.getString("mission");

            try {
                if (fileJson != null) {
                    FixAirLineEntity fixAirLineEntity = new FixAirLineEntity();
                    fixAirLineEntity.setNestId(nestId);
                    fixAirLineEntity.setName(name);
                    fixAirLineEntity.setType(airLineType);
                    fixAirLineEntity.setWaypoints(missionStr);
                    fixAirLineEntity.setMultiMission(false);
                    boolean res = this.save(fixAirLineEntity);
                    if (res) {
                        return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_ROUTE_IMPORT.getContent()));
                    }
                }
            } catch (Exception e) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_ROUTE_IMPORT.getContent()));
            }
        } else {
            DataUtil.Data data = DataUtil.readDataMultiPartFile(file);
            if (data != null) {
                FixAirLineEntity fixAirLineEntity = new FixAirLineEntity();
                fixAirLineEntity.setNestId(nestId);
                fixAirLineEntity.setName(name);
                fixAirLineEntity.setType(2);
                fixAirLineEntity.setWaypoints(data.getJsonData().toJSONString());
                fixAirLineEntity.setMultiMission(data.isMultiMission());
                boolean res = this.save(fixAirLineEntity);
                if (res) {
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_ROUTE_IMPORT.getContent()));
                }
            }
        }


        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_ROUTE_IMPORT.getContent()));
    }

    @Override
    public RestRes listCloudAirLine(Integer nestId) {
        List<FixAirLineEntity> list = baseMapper.batchSelectIdAndNameByNestId(nestId);
        if (CollectionUtil.isNotEmpty(list)) {
            List<Map<String, Object>> missionList = list.stream().sorted(Comparator.comparing(FixAirLineEntity::getModifyTime)).map(fale -> {
                Map<String, Object> missionMap = new HashMap<>(2);
                missionMap.put("name", fale.getName());
                missionMap.put("missionId", fale.getId());
                missionMap.put("modifyTime", fale.getModifyTime());
                return missionMap;
            }).collect(Collectors.toList());
            Map<String, Object> resMap = new HashMap<>(2);
            resMap.put("missionList", missionList);
            return RestRes.ok(resMap);
        }
        return RestRes.ok();
    }

    @Override
    public RestRes findCloudAirLineDetail(Integer id) {
        FixAirLineEntity fixAirLineEntity = this.getById(id);
        Map<String, Object> resMap = new HashMap<>(2);
        HashMap<Object, Object> missionDetail = new HashMap<>(4);
        missionDetail.put("name", fixAirLineEntity.getName());
        missionDetail.put("type", fixAirLineEntity.getType());
        missionDetail.put("waypoints", fixAirLineEntity.getWaypoints());
        missionDetail.put("multiMission", fixAirLineEntity.getMultiMission());
        resMap.put("missionDetail", missionDetail);
        return RestRes.ok(resMap);
    }


    @Override
    public RestRes choiceAirLine(Integer missioId, String nestId) {
        FixAirLineEntity fixAirLineEntity = this.getById(missioId);
        if (fixAirLineEntity != null) {
//            NestEntity nestEntity = nestService.getById(nestId);
//            if (nestEntity == null) {
//                return RestRes.err("没有ID=" + nestId + "的数据库");
//            }
            BaseNestInfoOutDTO baseNestInfo = baseNestService.getBaseNestInfo(nestId);
            if(Objects.isNull(baseNestInfo)) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NO_DATABASE_WITH_NEST.getContent()).replace("nestID",nestId));
            }
            Map<String, Object> map = new HashMap<>(2);
            TaskOfManageTaskDto taskOfManageTaskDto = new TaskOfManageTaskDto();
            taskOfManageTaskDto.setAirLineType(fixAirLineEntity.getType());
            taskOfManageTaskDto.setName(fixAirLineEntity.getName());
            taskOfManageTaskDto.setWaypointList(fixAirLineEntity.getWaypoints());
            Double defaultSpeed = AirLineDefaultSpeedEnum.getDefaultSpeedByType(fixAirLineEntity.getType());
            //点云导出的航线
            if (fixAirLineEntity.getMultiMission()) {
                taskOfManageTaskDto.setMultiMission(true);
                JSONArray jsonDatas = JSONArray.parseArray(fixAirLineEntity.getWaypoints());
                if (jsonDatas == null) {
                    return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_ROUTE_RESOLUTION.getContent()));
                }
                AirLineParams airLineParams = AirLineParams.instance()
                        .airLineType(fixAirLineEntity.getType())
                        .nestType(baseNestInfo.getType())
                        .startStopAlt(0.0) //TODO 起降高度到时候从nest_param表查询出来
                        .nestAltitude(baseNestInfo.getAltitude())
                        .waypointSpeed(defaultSpeed);
                int pointCount = 0;
                int photoCount = 0;
                double predicMiles = 0.0;
                long predicTime = 0L;


                for (int i = 0; i < jsonDatas.size(); i++) {
                    JSONArray waypointArray = jsonDatas.getJSONArray(i);
                    airLineParams.setWaypointArray(waypointArray);
                    List<Waypoint> waypointList = AirLineBuildUtil.buildAirLine(airLineParams);
                    Map<String, Object> computeMap = airLineService.computeWaypointList(waypointList, baseNestInfo.getLongitude(), baseNestInfo.getLatitude(), defaultSpeed, 100);
                    pointCount += (int) computeMap.get("pointCount");
                    photoCount += (int) computeMap.get("photoCount");
                    predicMiles += (double) computeMap.get("predicMiles");
                    predicTime += (long) computeMap.get("predicTime");

                }

                taskOfManageTaskDto.setPredictFlyTime(predicTime);
                taskOfManageTaskDto.setAirLineLength(predicMiles);
                taskOfManageTaskDto.setPhotoCount(photoCount);
                taskOfManageTaskDto.setPointCount(pointCount);

                Map<Integer, String> airLineMap = new HashMap<>(jsonDatas.size());

                for (int i = 0; i < jsonDatas.size(); i++) {
                    airLineMap.put(i, jsonDatas.getJSONArray(i).toJSONString());
                }

                taskOfManageTaskDto.setAirLineMap(airLineMap);
                map.put("taskDto", taskOfManageTaskDto);
                return RestRes.ok(map);

            } else {
                //易飞导出的航线
                taskOfManageTaskDto.setMultiMission(false);
                JSONArray waypointArray = JSONArray.parseArray(fixAirLineEntity.getWaypoints());
                AirLineParams airLineParams = null;
                if (fixAirLineEntity.getType() == 2) {
                    airLineParams = AirLineParams.instance()
                            .airLineType(fixAirLineEntity.getType())
                            .nestType(baseNestInfo.getType())
                            //通过数据库查出与机巢绑定的起降行高
                            .startStopAlt(0.0)
                            .nestAltitude(baseNestInfo.getAltitude())
                            .waypointSpeed(defaultSpeed)
                            .waypointArray(waypointArray);

                } else if (fixAirLineEntity.getType() == 3) {
                    airLineParams = AirLineParams.instance()
                            .airLineType(fixAirLineEntity.getType())
                            .nestType(baseNestInfo.getType())
                            //通过数据库查出与机巢绑定的起降行高
                            .startStopAlt(0.0)
                            .nestAltitude(baseNestInfo.getAltitude())
                            .waypointSpeed(defaultSpeed)
                            .airLineJson(fixAirLineEntity.getWaypoints());
                }
                List<Waypoint> waypointList = AirLineBuildUtil.buildAirLine(airLineParams);
                if (CollectionUtil.isEmpty(waypointList)) {
                    return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_ROUTE_RESOLUTION.getContent()));
                }
                Map<String, Object> computeMap = airLineService.computeWaypointList(waypointList, baseNestInfo.getLongitude(), baseNestInfo.getLatitude(), defaultSpeed, 100);

                taskOfManageTaskDto.setPredictFlyTime((long) computeMap.get("predicTime"));
                taskOfManageTaskDto.setAirLineLength((double) computeMap.get("predicMiles"));
                taskOfManageTaskDto.setPhotoCount((int) computeMap.get("photoCount"));
                taskOfManageTaskDto.setPointCount((int) computeMap.get("pointCount"));

                Map<Integer, String> airLineMap = new HashMap<>(2);
                airLineMap.put(0, waypointArray.toJSONString());
                taskOfManageTaskDto.setAirLineMap(airLineMap);

                map.put("taskDto", taskOfManageTaskDto);

                return RestRes.ok(map);

            }
        }

        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_FIND_CORRESPONDING_ROUTE.getContent()));
    }

    @Override
    public RestRes listMissionFromNestByNestId(String nestId) {
//        String nestUuid = nestService.getUuidById(nestId);
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        Map<String, Object> map = new HashMap<>(2);
        List<Mission> missionList = listMissionFromNestCache(nestUuid);
        if (CollectionUtil.isNotEmpty(missionList)) {
            List<Map<String, Object>> missions = missionList.stream().map(m -> {
                Map<String, Object> missionMap = new HashMap<>(2);
                missionMap.put("name", m.getName());
                missionMap.put("missionId", m.getMissionID());
                return missionMap;
            }).collect(Collectors.toList());
            map.put("missionList", missions);
            return RestRes.ok(map);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_FIND_CORRESPONDING_MISSION.getContent()));
    }

    @Override
    public RestRes findMissionDetailFromNestByMissionId(String nestId, String missionId) {
//        NestEntity nestEntity = nestService.getById(nestId);
        BaseNestInfoOutDTO baseNestInfo = baseNestService.getBaseNestInfo(nestId);
        if (Objects.nonNull(baseNestInfo)) {
            Map<String, Object> resMap = new HashMap<>(2);
            String missionStr = selectMissionDetailCache(baseNestInfo.getUuid(), missionId);
            List<Mission> missions = listMissionFromNestCache(baseNestInfo.getUuid());
            if (CollectionUtil.isEmpty(missions) || StrUtil.isEmpty(missionStr)) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_VIEW_MISSION_DETAIL.getContent()));
            }
            AirLineParams airLineParams = AirLineParams.instance()
                    .airLineType(3)
                    .nestType(baseNestInfo.getType())
                    .startStopAlt(0.0) //起降高度到时候从nest_param表查询出来
                    .nestAltitude(baseNestInfo.getAltitude())
                    .airLineJson(missionStr)
                    .waypointSpeed(4.0);
            List<Waypoint> waypointList = AirLineBuildUtil.buildAirLine(airLineParams);
            Map<String, Object> computeMap = airLineService.computeWaypointList(waypointList, baseNestInfo.getLongitude(), baseNestInfo.getLatitude(), 2.0, 100);
            //去掉waypointActionList字段
            waypointList.forEach(waypoint -> waypoint.setWaypointActionList(null));

            TaskOfManageTaskDto taskOfManageTaskDto = new TaskOfManageTaskDto();
            taskOfManageTaskDto.setPredictFlyTime((long) computeMap.get("predicTime"));
            taskOfManageTaskDto.setAirLineLength((double) computeMap.get("predicMiles"));
            taskOfManageTaskDto.setPhotoCount((int) computeMap.get("photoCount"));
            taskOfManageTaskDto.setPointCount((int) computeMap.get("pointCount"));

            Optional<Mission> first = missions.stream().filter(m -> m.getMissionID().equals(missionId)).findFirst();
            if (first.isPresent()) {
                Mission mission = first.get();
                //从机巢里面传出来的已经有起降点了，因此去掉就好了
                waypointList.remove(0);
                waypointList.remove(waypointList.size() - 1);
                mission.setMission(waypointList);
                taskOfManageTaskDto.setMissionDetail(mission);
            }

            //机巢航线类型为3
            taskOfManageTaskDto.setAirLineType(3);

            resMap.put("taskDto", taskOfManageTaskDto);
            return RestRes.ok(resMap);
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_VIEW_MISSION_DETAIL.getContent()));
    }

    @Override
    public Mission exportAirLineStr(String nestId, String missionId) {
//        String nestUuid = nestService.getUuidById(nestId);
        String nestUuid = baseNestService.getNestUuidByNestIdInCache(nestId);
        List<Mission> missions = listMissionFromNestCache(nestUuid);
        String missionStr = selectMissionDetailCache(nestUuid, missionId);
        if (CollectionUtil.isNotEmpty(missions)) {
            Optional<Mission> first = missions.stream().filter(m -> m.getMissionID().equals(missionId)).findFirst();
            if (first.isPresent()) {
                Mission mission = first.get();
                List<Waypoint> waypoints = JSONArray.parseArray(missionStr, Waypoint.class);
                mission.setMission(waypoints);
                return mission;
            }
        }
        return null;
    }

    @Override
    public Integer softDelFixAirLine(Integer id) {
        return baseMapper.updateDeletedById(id, 1);
    }

    @Override
    public RestRes uploadAirLineToNest(Integer nestId, Integer type, MultipartFile file) {

        String filename = file.getOriginalFilename();
        int i = filename.indexOf(".");
        String uploadFilename = filename.substring(0, i);

        NestEntity nest = nestService.getById(nestId);
        ComponentManager cm = ComponentManagerFactory.getInstance(nest.getUuid());

        Mission mission = null;
        if (cm != null) {
            if ("application/json".equals(file.getContentType())) {
                String fileJson = multipartFileToStr(file);
                JSONObject jsonObject = JSONObject.parseObject(fileJson);
                Integer airLineType1 = jsonObject.getInteger("airLineType");
                if (airLineType1 == 3) {
                    try {
                        mission = jsonObject.getObject("airLineData", Mission.class);
                        mission.setMissionID(UuidUtil.createNoBar());
                        mission.setName(uploadFilename);
                    } catch (Exception e) {
                        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_JSON_FILE.getContent()));
                    }
                } else if (airLineType1 == 2) {
                    JSONObject airLineData = jsonObject.getJSONObject("airLineData");
                    JSONArray jsonData = airLineData.getJSONArray("mission");
                    AirLineParams airLineParams = AirLineParams.instance()
                            .airLineType(airLineType1)
                            .waypointArray(jsonData)
                            .nestAltitude(nest.getAltitude())
                            .startStopAlt(0.0)
                            .nestType(nest.getType())
                            .waypointSpeed(4.0);
                    List<Waypoint> waypoints = AirLineBuildUtil.buildAirLine(airLineParams);
                    mission = new Mission();
                    mission.setMission(waypoints);
                    mission.setMissionID(UuidUtil.createNoBar());
                    mission.setName(uploadFilename);
                    mission.setRelativeAltitude(false);
                }

            } else {
                DataUtil.Data data = DataUtil.readDataMultiPartFile(file);
                if (data != null) {
                    JSONArray jsonData = data.getJsonData();
                    AirLineParams airLineParams = AirLineParams.instance()
                            .airLineType(2)
                            .waypointArray(jsonData)
                            .nestAltitude(nest.getAltitude())
                            .startStopAlt(0.0)
                            .nestType(nest.getType())
                            .waypointSpeed(4.0);

                    List<Waypoint> waypoints = AirLineBuildUtil.buildAirLine(airLineParams);
                    if (CollectionUtil.isEmpty(waypoints)) {
                        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_ROUTE_UPLOAD.getContent()));
                    }
                    mission = new Mission();
                    mission.setMission(waypoints);
                    mission.setMissionID(UuidUtil.createNoBar());
                    mission.setName(uploadFilename);
                    mission.setRelativeAltitude(false);
                }
            }
            if (mission == null) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_ROUTE_UPLOAD.getContent()));
            }

            MissionManager missionManager = cm.getMissionManager();
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            missionManager.uploadMission(mission, (result, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(true);
                } else {
                    future.complete(false);
                }
            });
            try {
                if (future.get(10, TimeUnit.SECONDS)) {
                    clearNestAirLineRedisCache(nest.getUuid());
                    return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_ROUTE_UPLOAD.getContent()));
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_NEST_ROUTE_UPLOAD.getContent()));
            }
        }

        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_FIND_UNITINFO_SERVICE_ROUTE_UPLOAD.getContent()));
    }

    @Override
    public RestRes uploadNestAirLineToCloudAirLine(Map<String, String> missionMap, Integer nestId) {
        NestEntity nest = nestService.getById(nestId);
        if (CollectionUtil.isNotEmpty(missionMap)) {
            Set<Map.Entry<String, String>> entries = missionMap.entrySet();
            List<FixAirLineEntity> fixAirLineEntityList = new ArrayList<>(entries.size());
            for (Map.Entry<String, String> entry : entries) {
                String missionStr = selectMissionDetailCache(nest.getUuid(), entry.getKey());
                FixAirLineEntity fixAirLineEntity = new FixAirLineEntity();
                fixAirLineEntity.setWaypoints(missionStr);
                fixAirLineEntity.setMultiMission(false);
                fixAirLineEntity.setName(entry.getValue());
                fixAirLineEntity.setNestId(nestId);
                fixAirLineEntity.setType(3);
                fixAirLineEntityList.add(fixAirLineEntity);
            }
            boolean b = this.saveBatch(fixAirLineEntityList);

            if (b) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_NEST_ROUTE_UPLOADED_TO_SERVER.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_NEST_ROUTE_UPLOADED_TO_SERVER.getContent()));
    }

    @Override
    public Integer batchSoftDelete(List<Integer> idList) {
        if (CollectionUtil.isNotEmpty(idList)) {
            return baseMapper.batchUpdateDeletedByIdList(idList);
        }
        return 0;
    }

    @Override
    @Transactional
    public RestRes copyCloudAirLine(Integer cloudAirLineId) {
        FixAirLineEntity fixAirLineEntity = this.getById(cloudAirLineId);
        int newCopyCount = fixAirLineEntity.getCopyCount() + 1;
        fixAirLineEntity.setCopyCount(newCopyCount);
        boolean updateRes = this.updateById(fixAirLineEntity);

        FixAirLineEntity fixAirLineEntity1 = new FixAirLineEntity();
        fixAirLineEntity1.setName(fixAirLineEntity.getName() + "_副本" + newCopyCount);
        fixAirLineEntity1.setCopyCount(0);
        fixAirLineEntity1.setType(fixAirLineEntity.getType());
        fixAirLineEntity1.setNestId(fixAirLineEntity.getNestId());
        fixAirLineEntity1.setWaypoints(fixAirLineEntity.getWaypoints());
        fixAirLineEntity1.setMultiMission(fixAirLineEntity.getMultiMission());
        boolean saveRes = this.save(fixAirLineEntity1);

        if (updateRes && saveRes) {
            return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_CLOUD_ROUTE_REPLICATION.getContent()));
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_CLOUD_ROUTE_REPLICATION.getContent()));
    }

    @Override
    public RestRes updateCloudAirLine(FixAirLineDto dto) {
        if (ObjectUtil.isNotNull(dto)) {
            FixAirLineEntity fixAirLineEntity = new FixAirLineEntity();
            BeanUtil.copyProperties(dto, fixAirLineEntity);
            boolean updateRes = this.updateById(fixAirLineEntity);
            if (updateRes) {
                return RestRes.ok(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUCCESS_UPDATE.getContent()));
            }
        }
        return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_UPDATE.getContent()));
    }


    private String multipartFileToStr(MultipartFile file) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()));
            StringBuilder fileSb = new StringBuilder();
            String str;
            while ((str = br.readLine()) != null) {
                fileSb.append(str);
            }
            return fileSb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private List<Mission> listMissionFromNestCache(String nestUuid) {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            NestTypeEnum nestType = cm.getNestType();
            if (NestTypeEnum.G503.equals(nestType)) {
                List<Mission> missions = new ArrayList<>();
                MqttResult<Mission> res1 = cm.getMissionManagerCf().listMission(AirIndexEnum.ONE);
                MqttResult<Mission> res2 = cm.getMissionManagerCf().listMission(AirIndexEnum.TWO);
                MqttResult<Mission> res3 = cm.getMissionManagerCf().listMission(AirIndexEnum.THREE);
                if (res1.isSuccess()) {
                    missions.addAll(res1.getResList());
                }
                if (res2.isSuccess()) {
                    missions.addAll(res2.getResList());
                }
                if (res3.isSuccess()) {
                    missions.addAll(res3.getResList());
                }
                return missions;
            } else {
                MqttResult<Mission> res = cm.getMissionManagerCf().listMission();
                if (res.isSuccess()) {
                    return res.getResList();
                }
            }
        }
        return Collections.emptyList();
    }

    private String selectMissionDetailCache(String nestUuid, String missionId) {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            MissionManager missionManager = cm.getMissionManager();
            CompletableFuture<String> future = new CompletableFuture<>();
            missionManager.listWaypointJsonByMissionId(missionId, (missionStr, isSuccess, errMsg) -> {
                if (isSuccess) {
                    future.complete(missionStr);
                }
            });

            try {
                return future.get(10, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                return "";
            }
        }
        return "";
    }

    private void clearNestAirLineRedisCache(String nestUuid) {
        String redisKey = RedisKeyEnum.REDIS_KEY.className("FixAirLineServiceImpl").methodName("listMissionFromNest").identity("nestId", nestUuid).type("String").get();
        redisService.del(redisKey);
    }

}
