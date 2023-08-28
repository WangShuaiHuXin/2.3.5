package com.imapcloud.sdk.manager.mission;


import com.imapcloud.nest.pojo.BuildKmzMissionRes;
import com.imapcloud.nest.v2.dao.po.out.CpsMissionOutPO;
import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.manager.MqttResParam;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.NullParam;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.callback.MissionRunningHandle;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.Constant;
import com.imapcloud.sdk.pojo.entity.Coordinate;
import com.imapcloud.sdk.pojo.entity.Mission;
import com.imapcloud.sdk.pojo.entity.StartMissionParamEntity;
import com.imapcloud.sdk.pojo.entity.Waypoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author wmin
 * 管理类，可以查询任务、上传任务、开启任务等
 */
public class MissionManagerCf {
    private final static String FUNCTION_TOPIC = Constant.MISSION_MANAGER_FUNCTION_TOPIC;
    private Client client;

    public MissionManagerCf(Client client) {
        this.client = client;
    }

    /**
     * 批量查询所有任务,但是没有具体的航点信息
     *
     * @param which
     */
    public MqttResult<Mission> listMission(AirIndexEnum... which) {
        MqttResParam<Mission> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.LIS)
                .code(Constant.MISSION_MANAGER_C1)
                .clazz(Mission.class)
                .which(which);

        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 通过任务id查询一个任务的详情（也就是任务的航点）
     *
     * @param missionId
     */
    public MqttResult<Waypoint> listWaypointByMissionId(String missionId, AirIndexEnum... which) {
        MqttResParam<Waypoint> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.LIS)
                .code(Constant.MISSION_MANAGER_C2)
                .param("missionID", missionId)
                .clazz(Waypoint.class)
                .maxWaitTime(30)
                .which(which);

        return ClientProxy.getMqttResult(mrp);

    }

    /**
     * 通过任务id查询一个任务详情，返回的航线是json
     *
     * @param missionId
     * @param airIndexEnum
     */
    public MqttResult<String> listWaypointJsonByMissionId(String missionId, AirIndexEnum... airIndexEnum) {
        MqttResParam<String> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.MISSION_MANAGER_C2)
                .param("missionID", missionId)
                .clazz(String.class)
                .which(airIndexEnum);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 上传任务
     *
     * @param mission 要上传的任务
     * @return 返回上传任务的ID
     */
    public MqttResult<String> uploadMission(Mission mission, AirIndexEnum... airIndexEnum) {
        Map<String, Object> missionMap = mission2map(mission);
        MqttResParam<String> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.MISSION_MANAGER_C3)
                .param(missionMap)
                .clazz(String.class)
                .key("missionID")
                .maxWaitTime(30)
                .which(airIndexEnum);
        return ClientProxy.getMqttResult(mrp);
    }

    public MqttResult<String> uploadKmzMission(BuildKmzMissionRes.KzmMission kzmMission, AirIndexEnum... airIndexEnum) {
        Map<String, Object> paramMap = new HashMap<>(4);
        paramMap.put("missionID", kzmMission.getMissionID());
        paramMap.put("name", kzmMission.getName());
        paramMap.put("kmzUrl", kzmMission.getKmzUrl());
        paramMap.put("kmzMD5", kzmMission.getKmzMD5());
        MqttResParam<String> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.MISSION_MANAGER_C3)
                .param(paramMap)
                .clazz(String.class)
                .key("missionID")
                .maxWaitTime(30)
                .which(airIndexEnum);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 通过指定任务ID删除指定任务
     *
     * @param missionId 任务ID
     * @return
     */
    public MqttResult<NullParam> deleteMission(String missionId, AirIndexEnum... airIndexEnum) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.MISSION_MANAGER_C4)
                .param("missionID", missionId)
                .which(airIndexEnum);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 开启missionId指定的任务
     *
     * @param startMissionParamEntity
     * @param airIndexEnum
     */
    public MqttResult<NullParam> startMission(Integer flyType,StartMissionParamEntity startMissionParamEntity, AirIndexEnum... airIndexEnum) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("missionID", startMissionParamEntity.getMissionUuid());
        // 获取数据方式的参数（媒体文件回传）
        Map<String, Object> missionDataSync = new HashMap<>(3);
        missionDataSync.put("mode", startMissionParamEntity.getMode());
        Map<String, Object> uploadParams = new HashMap<>(8);
        uploadParams.put("url", startMissionParamEntity.getUploadUrl());
        uploadParams.put("chunkInitUrl", startMissionParamEntity.getChunkInitUrl());
        uploadParams.put("chunkCombineUrl", startMissionParamEntity.getChunkCombineUrl());
        uploadParams.put("chunkSyncUrl", startMissionParamEntity.getChunkSyncUrl());
        uploadParams.put("uploadByChunks", startMissionParamEntity.getUploadByChunks());
        // @since 2.2.3，新版文件上传参数
        uploadParams.put("v2SmallFileUploadUrl", startMissionParamEntity.getOssConfig().getUploadUrl());
        uploadParams.put("v2ChunkUpload", startMissionParamEntity.getUploadByChunks());
        uploadParams.put("v2ChunkInitUrl", startMissionParamEntity.getOssConfig().getChunkInitUrl());
        uploadParams.put("v2ChunkUploadUrl", startMissionParamEntity.getOssConfig().getChunkUploadUrl());
        uploadParams.put("v2ChunkSize", startMissionParamEntity.getOssConfig().getChunkSize().toBytes());
        uploadParams.put("v2ChunkSyncUrl", startMissionParamEntity.getChunkSyncUrl());
        uploadParams.put("v2ChunkCombineCallbackUrl", startMissionParamEntity.getOssConfig().getChunkComposeNotifyUrl());
        uploadParams.put("v2AuthUrl", startMissionParamEntity.getOssConfig().getAuthUrl());
        missionDataSync.put("uploadParams", uploadParams);
        missionDataSync.put("flyType", flyType);
        param.put("missionDataSync", missionDataSync);
        param.put("disableRtkInMission", startMissionParamEntity.getDisableRtkInMission());

        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.MISSION_MANAGER_C6)
                .param(param)
                .maxWaitTime(30)
                .which(airIndexEnum);

        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 回调监听
     *
     * @param handle
     */
    public void listenMissionRunning(MissionRunningHandle handle, AirIndexEnum airIndexEnum) {
        ClientProxy.putMissionRunningHandle(this.client, handle, airIndexEnum);
    }

    /**
     * 暂停任务
     *
     * @param airIndexEnum
     */
    public MqttResult<NullParam> pauseMission(AirIndexEnum... airIndexEnum) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.MISSION_MANAGER_C10)
                .which(airIndexEnum);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 继续任务
     *
     * @param airIndexEnum
     */
    public MqttResult<NullParam> continueMission(Boolean breakPoint, AirIndexEnum... airIndexEnum) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.MISSION_MANAGER_C11)
                .param("breakPoint", Objects.isNull(breakPoint) || breakPoint)
                .maxWaitTime(10)
                .which(airIndexEnum);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 停止任务
     *
     * @param airIndexEnum
     */
    public MqttResult<NullParam> stopMission(AirIndexEnum... airIndexEnum) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.MISSION_MANAGER_C12)
                .which(airIndexEnum);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 终止一切飞行动作（任务、动作、返航），执行后飞机终止动作并悬停
     *
     * @param airIndexEnum
     */
    public MqttResult<NullParam> endAllProcess(AirIndexEnum... airIndexEnum) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.MISSION_MANAGER_C14)
                .which(airIndexEnum);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 终止启动流程
     *
     * @param airIndexEnum
     */
    public MqttResult<NullParam> endStartUpProcess(AirIndexEnum... airIndexEnum) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.MISSION_MANAGER_C15)
                .which(airIndexEnum);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 终止结束流程
     *
     * @param airIndexEnum
     */
    public MqttResult<NullParam> endFinishProcess(AirIndexEnum... airIndexEnum) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.MISSION_MANAGER_C16)
                .which(airIndexEnum);
        return ClientProxy.getMqttResult(mrp);
    }


    public MqttResult<NullParam> deleteAllMission(AirIndexEnum... airIndexEnum) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.MISSION_MANAGER_C19)
                .which(airIndexEnum);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置默认备降点坐标
     *
     * @param latitude
     * @param longitude
     * @param airIndexEnum
     */
    public MqttResult<NullParam> setDefaultBackLandPoint(Double latitude, Double longitude, AirIndexEnum... airIndexEnum) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("latitude", latitude);
        param.put("longitude", longitude);

        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.MISSION_MANAGER_C22)
                .param(param)
                .which(airIndexEnum);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取默认备降点坐标
     *
     * @param airIndexEnum
     */
    public MqttResult<Coordinate> getDefaultBackLandPoint(AirIndexEnum... airIndexEnum) {
        MqttResParam<Coordinate> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(Constant.MISSION_MANAGER_C23)
                .clazz(Coordinate.class)
                .which(airIndexEnum);

        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 设置自动前往默认备降点飞行高度
     * 高度为相对基站起飞点高度，无人机会按照设置高度飞去备降点，如果当前高度比设置高度高，则按照当前高度飞去备降点。
     *
     * @param altitude
     * @param airIndexEnum
     */
    public MqttResult<NullParam> setAutoGoToDefaultBackLandPointAlt(Double altitude, AirIndexEnum... airIndexEnum) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(Constant.MISSION_MANAGER_C25)
                .param("altitude", altitude)
                .which(airIndexEnum);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取自动前往默认备降点飞行高度
     * 高度为相对基站起飞点高度，无人机会按照设置高度飞去备降点，如果当前高度比设置高度高，则按照当前高度飞去备降点。
     *
     * @param airIndexEnum
     */
    public MqttResult<Double> getAutoGoToDefaultBackLandPointAlt(AirIndexEnum... airIndexEnum) {
        MqttResParam<Double> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.MISSION_MANAGER_C26)
                .clazz(Double.class)
                .key("altitude")
                .which(airIndexEnum);

        return ClientProxy.getMqttResult(mrp);

    }

    /**
     * 设置是否前往备降点的功能
     *
     * @param airIndexEnum
     */
    public MqttResult<NullParam> setAutoGoToDefaultBackLandPointFun(boolean enable, AirIndexEnum... airIndexEnum) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("enable", enable);
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.MISSION_MANAGER_C27)
                .param("enable", enable)
                .which(airIndexEnum);

        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取是否开启前往备降点的功能
     *
     * @param airIndexEnum
     */
    public MqttResult<Boolean> getAutoGoToDefaultBackLandPointFun(AirIndexEnum... airIndexEnum) {
        MqttResParam<Boolean> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.ATT)
                .code(Constant.MISSION_MANAGER_C28)
                .key("enable")
                .clazz(Boolean.class)
                .which(airIndexEnum);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 立即前往默认备降点
     * 任务、返航、降落都会终止，如果有设置默认备降点，则会直接按给定的飞行高度前往备降点，如果当前高度比给定高度高，则按照当前高度飞去备降点。
     *
     * @param altitude
     * @param airIndexEnum
     */
    public MqttResult<NullParam> immediatelyGoToDefaultBackLandPoint(Double altitude, AirIndexEnum... airIndexEnum) {
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.MISSION_MANAGER_C24)
                .param("altitude", altitude)
                .which(airIndexEnum);

        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 立即前往指定备降点
     * 任务、返航、降落都会终止，直接按给定的飞行高度前往指定坐标备降点，如果当前高度比给定高度高，则按照当前高度飞去备降点。
     *
     * @param coordinate
     * @param airIndexEnum
     */
    public MqttResult<NullParam> immediatelyGoToGoToDesignBackLandPoint(Coordinate coordinate, AirIndexEnum... airIndexEnum) {
        Map<String, Object> param = new HashMap<>(4);
        param.put("altitude", coordinate.getAltitude());
        param.put("latitude", coordinate.getLatitude());
        param.put("longitude", coordinate.getLongitude());
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.MISSION_MANAGER_C13)
                .param(param)
                .which(airIndexEnum);

        return ClientProxy.getMqttResult(mrp);
    }

    private Map<String, Object> mission2map(Mission mission) {
        Map<String, Object> map = new HashMap<>();
        map.put("missionID", mission.getMissionID());
        map.put("name", mission.getName());
        if (mission.getHeadingMode() != null) {
            map.put("headingMode", mission.getHeadingMode().getValue());
        }
        if (mission.getFlightPathMode() != null) {
            map.put("flightPathMode", mission.getFlightPathMode().getValue());
        }
        if (mission.getFinishAction() != null) {
            map.put("finishAction", mission.getFinishAction().getValue());
        }
        if (mission.getGotoFirstWaypointMode() != null) {
            map.put("gotoFirstWaypointMode", mission.getGotoFirstWaypointMode().getValue());
        }
        if (mission.getAutoFlightSpeed() > 0) {
            map.put("autoFlightSpeed", (double) mission.getAutoFlightSpeed());
        }
        if (mission.getIntelligentMission() != null) {
            map.put("intelligentMission", mission.getIntelligentMission());
        }
        if (mission.getPoiLatitude() != null && mission.getPoiLongitude() != null) {
            map.put("poiLatitude", mission.getPoiLatitude());
            map.put("poiLongitude", mission.getPoiLongitude());
        }
        map.put("relativeAltitude", mission.getRelativeAltitude());
        map.put("mission", mission.getMission());
        map.put("useHomeSeaLevelInRtkUnable", mission.getUseHomeSeaLevelInRtkUnable());
        return map;
    }

    /**
     * 设置备降点
     *
     * @param longitude
     * @param latitude
     * @return
     */
    public MqttResult<NullParam> setAlternateLandingPosition(Double longitude, Double latitude) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("latitude", latitude);
        param.put("longitude", longitude);
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.MISSION_MANAGER_C22)
                .param(param)
                .which(AirIndexEnum.DEFAULT);
        return ClientProxy.getMqttResult(mrp);
    }


    /**
     * 设置前往备降点高度
     *
     * @param altitude
     * @return
     */
    public MqttResult<NullParam> setAlternateLandingAltitude(Double altitude) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("altitude", altitude);
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.MISSION_MANAGER_C25)
                .param(param)
                .which(AirIndexEnum.DEFAULT);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 开启自动前往备降点功能
     *
     * @param enable
     * @return
     */
    public MqttResult<NullParam> setAlternateLandingStatus(Boolean enable) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("enable", enable);
        MqttResParam<NullParam> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.NUL)
                .code(Constant.MISSION_MANAGER_C27)
                .param(param)
                .which(AirIndexEnum.DEFAULT);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取设置的备降点信息
     *
     * @return
     */
    public MqttResult<CpsMissionOutPO.CpsMissionPositionOutPO> getAlternateLandingPosition() {
        MqttResParam<CpsMissionOutPO.CpsMissionPositionOutPO> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(Constant.MISSION_MANAGER_C23)
                .clazz(CpsMissionOutPO.CpsMissionPositionOutPO.class)
                .which(AirIndexEnum.DEFAULT);
        return ClientProxy.getMqttResult(mrp);
    }

    /**
     * 获取前往备降点高度
     * 高度为相对基站起飞点高度，无人机会按照设置高度飞去备降点，如果当前高度比设置高度高，则按照当前高度飞去备降点。
     *
     * @return
     */
    public MqttResult<CpsMissionOutPO.CpsMissionAltitudeOutPO> getAlternateLandingAltitude() {
        MqttResParam<CpsMissionOutPO.CpsMissionAltitudeOutPO> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(Constant.MISSION_MANAGER_C26)
                .clazz(CpsMissionOutPO.CpsMissionAltitudeOutPO.class)
                .which(AirIndexEnum.DEFAULT);
        return ClientProxy.getMqttResult(mrp);
    }

    /*是否开启自动前往备降点*/
    public MqttResult<CpsMissionOutPO.CpsMissionEnableOutPO> getAlternateLandingEnable() {
        MqttResParam<CpsMissionOutPO.CpsMissionEnableOutPO> mrp = MqttResParam.getInstance(this.client, FUNCTION_TOPIC);
        mrp.type(MqttResParam.Type.OBJ)
                .code(Constant.MISSION_MANAGER_C28)
                .clazz(CpsMissionOutPO.CpsMissionEnableOutPO.class)
                .which(AirIndexEnum.DEFAULT);
        return ClientProxy.getMqttResult(mrp);
    }
}
