package com.imapcloud.sdk.manager.mission;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.manager.MqttResParam;
import com.imapcloud.sdk.manager.MqttResult;
import com.imapcloud.sdk.manager.icrest.entity.DestinationParam;
import com.imapcloud.sdk.manager.icrest.entity.DistancParam;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.BaseResult3;
import com.imapcloud.sdk.pojo.callback.MissionRunningHandle;
import com.imapcloud.sdk.pojo.callback.ProxyHandle;
import com.imapcloud.sdk.pojo.callback.UserHandle;
import com.imapcloud.sdk.pojo.constant.*;
import com.imapcloud.sdk.pojo.entity.Coordinate;
import com.imapcloud.sdk.pojo.entity.Mission;
import com.imapcloud.sdk.pojo.entity.Waypoint;
import com.imapcloud.sdk.utils.JSONUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author wmin
 * 管理类，可以查询任务、上传任务、开启任务等
 */

@Deprecated
public class MissionManager {
    private final static String FUNCTION_TOPIC = Constant.MISSION_MANAGER_FUNCTION_TOPIC;
    private Client client;

    public MissionManager(Client client) {
        this.client = client;
    }

    /**
     * 批量查询所有任务,但是没有具体的航点信息
     *
     * @param handle
     */
    public void listMission(UserHandle<List<Mission>> handle) {
        ProxyHandle<List<Mission>> ph = new ProxyHandle<List<Mission>>() {
            @Override
            public void success(List<Mission> missions, String msg) {
                handle.handle(missions, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishParamList(this.client, FUNCTION_TOPIC, Constant.MISSION_MANAGER_C1, null, ph, Mission.class);
    }


    /**
     * 通过任务id查询一个任务的详情（也就是任务的航点）
     *
     * @param missionId
     */
    public void listWaypointByMissionId(String missionId, UserHandle<List<Waypoint>> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("missionID", missionId);
        ProxyHandle<List<Waypoint>> ph = new ProxyHandle<List<Waypoint>>() {
            @Override
            public void success(List<Waypoint> waypoints, String msg) {
                handle.handle(waypoints, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishParamList(this.client, FUNCTION_TOPIC, Constant.MISSION_MANAGER_C2, param, ph, Waypoint.class);
    }


    public MqttResult<Waypoint> listWaypointByMissionId(String missionId) {
        MqttResParam<Waypoint> mrParam = new MqttResParam<>();
        Map<String, Object> param = new HashMap<>(2);
        param.put("missionID", missionId);
        mrParam.setParam(param);
        mrParam.setType(MqttResParam.Type.LIS);
        mrParam.setClient(this.client);
        mrParam.setCode(Constant.MISSION_MANAGER_C2);
        mrParam.setClazz(Waypoint.class);
        mrParam.setTopic(FUNCTION_TOPIC);
        mrParam.setKey(null);
        return ClientProxy.getMqttResult(mrParam);
    }

    /**
     * 通过任务id查询一个任务详情，返回的航线是json
     *
     * @param missionId
     * @param handle
     */
    public void listWaypointJsonByMissionId(String missionId, UserHandle<String> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("missionID", missionId);
        ProxyHandle<BaseResult3> ph = new ProxyHandle<BaseResult3>() {
            @Override
            public void success(BaseResult3 br3, String msg) {
                String waypointJson = br3.getParam();
                handle.handle(waypointJson, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishPayload(this.client, FUNCTION_TOPIC, Constant.MISSION_MANAGER_C2, param, ph);
    }

    /**
     * 上传任务
     *
     * @param mission 要上传的任务
     * @return 返回上传任务的ID
     */
    public void uploadMission(Mission mission, UserHandle<String> handle) {
        ProxyHandle<String> ph = new ProxyHandle<String>() {
            @Override
            public void success(String s, String msg) {
                handle.handle(s, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        Map<String, Object> missionMap = mission2map(mission);
        ClientProxy.proxyPublishParamOne(this.client, FUNCTION_TOPIC, Constant.MISSION_MANAGER_C3, missionMap, ph, "missionID", String.class);
    }

    /**
     * 通过指定任务ID删除指定任务
     *
     * @param missionId 任务ID
     * @return
     */
    public void deleteMission(String missionId, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("missionID", missionId);
        getResultBoolean(Constant.MISSION_MANAGER_C4, handle, param);
    }

    /**
     * 开启missionId指定的任务
     *
     * @param missionId
     * @param handle
     */
    public void startMission(Integer flyType,String missionId, Integer mode, String uploadUrl, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("missionID", missionId);
        // 获取数据方式的参数（媒体文件回传）
        Map<String, Object> missionDataSync = new HashMap<>(2);
        missionDataSync.put("mode", mode);
        missionDataSync.put("uploadUrl", uploadUrl);
        /**
         * 海康威视定制化
         */
        missionDataSync.put("flyType",flyType);
        param.put("missionDataSync", missionDataSync);
        getResultBoolean(Constant.MISSION_MANAGER_C6, handle, param);
    }

    /**
     * 回调监听
     *
     * @param handle
     */
    public void listenMissionRunning(MissionRunningHandle handle,AirIndexEnum airIndexEnum) {
        ClientProxy.putMissionRunningHandle(this.client, handle,airIndexEnum);
    }

    public void removeListenMissionRunning(AirIndexEnum airIndexEnum) {
        ClientProxy.rmMissionRunningHandle(this.client, airIndexEnum);
    }


    /**
     * 暂停任务
     *
     * @param handle
     */
    public void pauseMission(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.MISSION_MANAGER_C10, handle, null);
    }

    /**
     * 继续任务
     *
     * @param handle
     */
    public void continueMission(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.MISSION_MANAGER_C11, handle, null);
    }

    /**
     * 停止任务
     *
     * @param handle
     */
    public void stopMission(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.MISSION_MANAGER_C12, handle, null);
    }

    /**
     * 终止一切飞行动作（任务、动作、返航），执行后飞机终止动作并悬停
     *
     * @param handle
     */
    public void endAllProcess(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.MISSION_MANAGER_C14, handle, null);
    }

    /**
     * 终止启动流程
     *
     * @param handle
     */
    public void endStartUpProcess(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.MISSION_MANAGER_C15, handle, null);
    }

    public boolean endStartUpProcessFuture() {
        CompletableFuture<Boolean> cf = new CompletableFuture<>();
        getResultBoolean(Constant.MISSION_MANAGER_C15, (result, isSuccess, errMsg) -> {
            if (isSuccess) {
                cf.complete(result);
            } else {
                cf.complete(false);
            }
        }, null);
        try {
            if (cf.get(10, TimeUnit.SECONDS)) {
                return true;
            }
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return false;
        }
        return false;
    }

    /**
     * 终止结束流程
     *
     * @param handle
     */
    public void endFinishProcess(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.MISSION_MANAGER_C16, handle, null);
    }


    public void deleteAllMission(UserHandle<Boolean> handle) {
        getResultBoolean(Constant.MISSION_MANAGER_C19, handle, null);
    }

    /**
     * 设置默认备降点坐标
     *
     * @param latitude
     * @param longitude
     * @param handle
     */
    public void setDefaultBackLandPoint(Double latitude, Double longitude, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("latitude", latitude);
        param.put("longitude", longitude);
        getResultBoolean(Constant.MISSION_MANAGER_C22, handle, param);
    }

    /**
     * 获取默认备降点坐标
     *
     * @param handle
     */
    public void getDefaultBackLandPoint(UserHandle<Coordinate> handle) {
        ProxyHandle<BaseResult3> ph = new ProxyHandle<BaseResult3>() {
            @Override
            public void success(BaseResult3 br3, String msg) {
                String param = br3.getParam();
                Coordinate coordinate = JSONUtil.parseObject(param, Coordinate.class);
                handle.handle(coordinate, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishPayload(client, FUNCTION_TOPIC, Constant.MISSION_MANAGER_C23, null, ph);
    }

    /**
     * 设置自动前往默认备降点飞行高度
     * 高度为相对基站起飞点高度，无人机会按照设置高度飞去备降点，如果当前高度比设置高度高，则按照当前高度飞去备降点。
     *
     * @param altitude
     * @param handle
     */
    public void setAutoGoToDefaultBackLandPointAlt(Double altitude, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("altitude", altitude);
        getResultBoolean(Constant.MISSION_MANAGER_C25, handle, param);
    }

    /**
     * 获取自动前往默认备降点飞行高度
     * 高度为相对基站起飞点高度，无人机会按照设置高度飞去备降点，如果当前高度比设置高度高，则按照当前高度飞去备降点。
     *
     * @param handle
     */
    public void getAutoGoToDefaultBackLandPointAlt(UserHandle<Double> handle) {
        ProxyHandle<BaseResult3> ph = new ProxyHandle<BaseResult3>() {
            @Override
            public void success(BaseResult3 br3, String msg) {
                if (br3.getParam() != null) {
                    String param = br3.getParam();
                    JSONObject jsonObject = JSON.parseObject(param);
                    Double altitude = jsonObject.getDouble("altitude");
                    handle.handle(altitude, true, msg);
                } else {
                    handle.handle(0.0, true, msg);
                }

            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishPayload(this.client, FUNCTION_TOPIC, Constant.MISSION_MANAGER_C26, null, ph);
    }

    /**
     * 设置是否前往备降点的功能
     *
     * @param handle
     */
    public void setAutoGoToDefaultBackLandPointFun(boolean enable, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("enable", enable);
        getResultBoolean(Constant.MISSION_MANAGER_C27, handle, param);
    }

    /**
     * 获取是否开启前往备降点的功能
     *
     * @param handle
     */
    public void getAutoGoToDefaultBackLandPointFun(UserHandle<Boolean> handle) {
        ProxyHandle<Boolean> ph = new ProxyHandle<Boolean>() {
            @Override
            public void success(Boolean enable, String msg) {
                handle.handle(enable, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishParamOne(this.client, FUNCTION_TOPIC, Constant.MISSION_MANAGER_C28, null, ph, "enable", Boolean.class);
    }

    /**
     * 立即前往默认备降点
     * 任务、返航、降落都会终止，如果有设置默认备降点，则会直接按给定的飞行高度前往备降点，如果当前高度比给定高度高，则按照当前高度飞去备降点。
     *
     * @param altitude
     * @param handle
     */
    public void immediatelyGoToDefaultBackLandPoint(Double altitude, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("altitude", altitude);
        getResultBoolean(Constant.MISSION_MANAGER_C24, handle, param);
    }

    /**
     * 立即前往指定备降点
     * 任务、返航、降落都会终止，直接按给定的飞行高度前往指定坐标备降点，如果当前高度比给定高度高，则按照当前高度飞去备降点。
     *
     * @param coordinate
     * @param handle
     */
    public void immediatelyGoToGoToDesignBackLandPoint(Coordinate coordinate, UserHandle<Boolean> handle) {
        Map<String, Object> param = new HashMap<>(4);
        param.put("altitude", coordinate.getAltitude());
        param.put("latitude", coordinate.getLatitude());
        param.put("longitude", coordinate.getLongitude());
        getResultBoolean(Constant.MISSION_MANAGER_C13, handle, param);
    }

    /**
     * 垂直调整高度
     *
     * @param altitude
     */
    public void oneDistanceTakeOff(float altitude) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("altitude", altitude);
        ClientProxy.proxyPublishNone(this.client, FUNCTION_TOPIC, Constant.MISSION_MANAGER_C31, param);
    }

    /**
     * 指定距离平移飞行
     *
     * @param distancParam
     */
    public void controlDistanceTakeOff(DistancParam distancParam) {
        Map<String, Object> param = new HashMap<>(4);
        param.put("speed", distancParam.getSpeedFactor());
        param.put("distance", distancParam.getTargetDistance());
        param.put("direction", distancParam.getDirection());
        param.put("heading", distancParam.getTargetYaw());
        ClientProxy.proxyPublishNone(this.client, FUNCTION_TOPIC, Constant.MISSION_MANAGER_C32, param);
    }

    /**
     * 指点飞行
     *
     * @param destinationParam
     */
    public void controlDestinationTakeOff(DestinationParam destinationParam) {
        Map<String, Object> param = new HashMap<>(2);
        param.put("latitude", destinationParam.getLatTarget());
        param.put("longitude", destinationParam.getLngTarget());
        param.put("altitude", destinationParam.getHeightTarget());
        param.put("speed", destinationParam.getSpeed());
        ClientProxy.proxyPublishNone(this.client, FUNCTION_TOPIC, Constant.MISSION_MANAGER_C33, param);
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

        map.put("relativeAltitude", mission.getRelativeAltitude());
        map.put("mission", mission.getMission());
        map.put("useHomeSeaLevelInRtkUnable", mission.getUseHomeSeaLevelInRtkUnable());
        return map;
    }

    private void getResultBoolean(String code, UserHandle<Boolean> handle, Map<String, Object> param) {
        ProxyHandle<Boolean> ph = new ProxyHandle<Boolean>() {
            @Override
            public void success(Boolean aBoolean, String msg) {
                handle.handle(aBoolean, true, msg);
            }

            @Override
            public void error(boolean isError, String msg) {
                if (isError) {
                    handle.handle(null, false, msg);
                }
            }
        };
        ClientProxy.proxyPublishBool(this.client, FUNCTION_TOPIC, code, param, ph);
    }
}
