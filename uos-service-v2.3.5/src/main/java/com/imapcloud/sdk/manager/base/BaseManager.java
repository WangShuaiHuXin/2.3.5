package com.imapcloud.sdk.manager.base;

import com.imapcloud.sdk.manager.ClientProxy;
import com.imapcloud.sdk.mqttclient.Client;
import com.imapcloud.sdk.pojo.BaseResult3;
import com.imapcloud.sdk.pojo.callback.AllSubscribeHandle;
import com.imapcloud.sdk.pojo.callback.DynamicTopicHandle;
import com.imapcloud.sdk.pojo.callback.MissionQueueHandle;
import com.imapcloud.sdk.pojo.callback.UserHandle;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.Constant;
import com.imapcloud.sdk.pojo.constant.StatusTopicEnum;
import com.imapcloud.sdk.pojo.entity.*;

import java.util.Objects;

/**
 * 所有监听的主题
 *
 * @author wmin
 */
public class BaseManager {
    private Client client;

    public BaseManager(Client client) {
        this.client = client;
    }

    /**
     * 监听固定机巢的状态
     *
     * @param handle
     */
    public void listenNestState(UserHandle<NestState> handle, AirIndexEnum... airIndexEnums) {
        String topic = Constant.BASE_STATE_SUBSCRIBE_TOPIC;
        if (Objects.nonNull(airIndexEnums) && airIndexEnums.length > 0) {
            AirIndexEnum airIndexEnum = airIndexEnums[0];
            if (AirIndexEnum.ONE.equals(airIndexEnum)) {
                topic = Constant.BASE_STATE_SUBSCRIBE_TOPIC_1;
            }
            if (AirIndexEnum.TWO.equals(airIndexEnum)) {
                topic = Constant.BASE_STATE_SUBSCRIBE_TOPIC_2;
            }
            if (AirIndexEnum.THREE.equals(airIndexEnum)) {
                topic = Constant.BASE_STATE_SUBSCRIBE_TOPIC_3;
            }
        }
        ClientProxy.proxySubscribeGetStatus(this.client, topic, NestState.class, handle, airIndexEnums);
    }

    public void removeListenNestState(AirIndexEnum... airIndexEnums){
        String topic = Constant.BASE_STATE_SUBSCRIBE_TOPIC;
        if (Objects.nonNull(airIndexEnums) && airIndexEnums.length > 0) {
            AirIndexEnum airIndexEnum = airIndexEnums[0];
            if (AirIndexEnum.ONE.equals(airIndexEnum)) {
                topic = Constant.BASE_STATE_SUBSCRIBE_TOPIC_1;
            }
            if (AirIndexEnum.TWO.equals(airIndexEnum)) {
                topic = Constant.BASE_STATE_SUBSCRIBE_TOPIC_2;
            }
            if (AirIndexEnum.THREE.equals(airIndexEnum)) {
                topic = Constant.BASE_STATE_SUBSCRIBE_TOPIC_3;
            }
        }
        ClientProxy.proxySubscribeRemove(this.client, topic,airIndexEnums);
    }


    /**
     * 监听飞机状态
     *
     * @param handle
     */
    public void listenAircraftState(UserHandle<AircraftState> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.AIRCRAFT_STATE_SUBSCRIBE_TOPIC, AircraftState.class, handle);
    }

    /**
     * 监听云无人机状态
     *
     * @param handle
     */
    public void listenCloudCrownAircraft(UserHandle<CloudCrownAircraftState> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.AIRCRAFT_STATE_SUBSCRIBE_TOPIC, CloudCrownAircraftState.class, handle);
    }


    /**
     * 监听RTK状态，只支持固定机巢
     *
     * @param handle
     */
    public void listenRtkState(UserHandle<RTKState> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.RTK_STATE_SUBSCRIBE_TOPIC, RTKState.class, handle);
    }

    /**
     * 监听飞机电池状态，一代机巢
     *
     * @param handle
     */
    public void listenAircraftBatteryStateCPSV1(UserHandle<AircraftBatteryStateCPSV1> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.AIRCRAFT_BATTERY_STATE_SUBSCRIBE_TOPIC, AircraftBatteryStateCPSV1.class, handle);
    }

    /**
     * 监听飞机电池状态，二代机巢
     *
     * @param handle
     */
    public void listenAircraftBatteryStateCPSV2(UserHandle<AircraftBatteryStateCPSV2> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.AIRCRAFT_BATTERY_STATE_SUBSCRIBE_TOPIC, AircraftBatteryStateCPSV2.class, handle);
    }

    /**
     * 监听机巢电池状态
     *
     * @param handle
     */
    public void listenNestBatteryState(UserHandle<NestBatteryState> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.NEST_BATTERY_STATE_SUBSCRIBE_TOPIC, NestBatteryState.class, handle);
    }

    /**
     * 监听云台状态
     *
     * @param handle
     */
    public void listenGimbalState(UserHandle<GimbalState> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.GIMBAL_STATE_SUBSCRIBE_TOPIC, GimbalState.class, handle);
    }

    /**
     * 监听任务状态，所有机巢通用
     *
     * @param handle
     */
    public void listenMissionState(UserHandle<MissionState> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.MISSION_STATE_SUBSCRIBE_TOPIC, MissionState.class, handle);
    }

    /**
     * 监听航点状态，一代机巢
     *
     * @param handle
     */
    public void listenWaypointStateCPSV1(UserHandle<WaypointStateCPSV1> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.WAYPOINT_MISSION_STATE_SUBSCRIBE_TOPIC, WaypointStateCPSV1.class, handle);
    }

    /**
     * 监听航点状态，二代机巢
     *
     * @param handle
     */
    public void listenWaypointStateCPSV2(UserHandle<WaypointState> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.WAYPOINT_MISSION_STATE_SUBSCRIBE_TOPIC, WaypointState.class, handle);
    }

    /**
     * 监听固定机巢电机状态
     *
     * @param handle
     */
    public void listenFixNestMotorState(UserHandle<FixMotorState> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.MOTOR_SUBSCRIBE_TOPIC, FixMotorState.class, handle);
    }

    /**
     * 监听迷你机巢电机状态
     *
     * @param handle
     */
    public void listenMiniNestMotorState(UserHandle<MiniMotorState> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.MOTOR_SUBSCRIBE_TOPIC, MiniMotorState.class, handle);
    }

    /**
     * 监听m300电机状态
     *
     * @param handle
     */
    public void listenM300NestMotorBaseState(UserHandle<MotorBaseState> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.MOTOR_BASE_SUBSCRIBE_TOPIC, MotorBaseState.class, handle);
    }

    /**
     * 监听电机驱动器的状态,只支持固定机巢
     *
     * @param handle
     */
    public void listenMotorDriverState(UserHandle<MotorDriverState> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.MOTOR_DRIVE_STATE_SUBSCRIBE_TOPIC, MotorDriverState.class, handle);
    }

    /**
     * 监听固定机巢温控系统状态
     *
     * @param handle
     */
    public void listenFixNestTemperatureState(UserHandle<FixTemperatureState> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.TEMPERATURE_STATE_SUBSCRIBE_TOPIC, FixTemperatureState.class, handle);
    }

    /**
     * 监听迷你机巢温控系统状态
     *
     * @param handle
     */
    public void listenMiniNestTemperatureState(UserHandle<MiniTemperatureState> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.TEMPERATURE_STATE_SUBSCRIBE_TOPIC, MiniTemperatureState.class, handle);
    }

    /**
     * 监听流媒体状态
     *
     * @param handle
     */
    public void listenMediaState(UserHandle<MediaState> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC, MediaState.class, handle);
    }

    /**
     * 监听mini机巢流媒体状态
     *
     * @param handle
     */
    public void listenMiniMediaState(UserHandle<MiniMediaState> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC, MiniMediaState.class, handle);
    }

    /**
     * 监听气象系统的状态
     *
     * @param handle
     */
    public void listenAerographState(UserHandle<AerographState> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.AEROGRAPH_STATE_SUBSCRIBE_TOPIC, AerographState.class, handle);
    }


    /**
     * 监听所有的主题
     *
     * @param handle
     */
    public void listenAllSubscribeTopic(AllSubscribeHandle handle) {
        ClientProxy.proxySubscribeAllTopicGetStatus(this.client, handle);
    }

    /**
     * 多媒体系统V2状态
     *
     * @param handle
     */
    public void listenMediaStateV2(UserHandle<MediaStateV2> handle,AirIndexEnum... airIndexEnums) {
        String topic = Constant.MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC;
        if (Objects.nonNull(airIndexEnums) && airIndexEnums.length > 0) {
            AirIndexEnum airIndexEnum = airIndexEnums[0];
            if (AirIndexEnum.ONE.equals(airIndexEnum)) {
                topic = Constant.MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC_1;
            }
            if (AirIndexEnum.TWO.equals(airIndexEnum)) {
                topic = Constant.MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC_2;
            }
            if (AirIndexEnum.THREE.equals(airIndexEnum)) {
                topic = Constant.MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC_3;
            }
        }
        ClientProxy.proxySubscribeGetStatus(this.client, topic, MediaStateV2.class, handle,airIndexEnums);
    }

    public void removeListMediaStateV2(AirIndexEnum... airIndexEnums) {
        ClientProxy.proxySubscribeRemove(this.client, Constant.MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC,airIndexEnums);
    }

    /**
     * 多媒体系统V2,监听下载状态（无人机-> 机巢）
     *
     * @param handle
     */
    public void listenMediaDownloadState(UserHandle<BaseResult3> handle,AirIndexEnum... airIndexEnums) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.MEDIA_DOWNLOAD_RESULT_TOPIC, BaseResult3.class, handle,airIndexEnums);
    }

    /**
     * 多媒体系统V2,监听上传状态（机巢-> 图片服务器）
     *
     * @param handle
     */
    public void listenMediaUploadState(UserHandle<BaseResult3> handle,AirIndexEnum... airIndexEnums) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.MEDIA_UPLOAD_RESULT_TOPIC, BaseResult3.class, handle,airIndexEnums);
    }


    /**
     * 多媒体系统V2,监听下载上传状态 手工同步
     *
     * @param handle
     */
    public void listenMediaManualState(UserHandle<BaseResult3> handle,AirIndexEnum... airIndexEnums) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.MEDIA_MANUAL_RESULT_TOPIC, BaseResult3.class, handle,airIndexEnums);
    }

    /**
     * 多媒体系统V2 ，去除监听手工同步
     * @param airIndexEnums
     */
    public void removeListMediaManualState(AirIndexEnum... airIndexEnums) {
        ClientProxy.proxySubscribeRemove(this.client, Constant.MEDIA_MANUAL_RESULT_TOPIC,airIndexEnums);
    }

    /**
     * 获取上传音频
     *
     * @param handle
     */
    public void listenAccessoryState(UserHandle<AccessoryStatus> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.ACCESSORY_STATUS_SUBSCRIBE_TOPIC, AccessoryStatus.class, handle);
    }

    /**
     * 监听日志包上传状态
     *
     * @param handle
     */
    public void listenNestLogUploadState(UserHandle<NestLogUploadState> handle,AirIndexEnum... airIndexEnums) {
        String topic = Constant.NEST_LOG_UPLOAD_PROCESS;
        if (Objects.nonNull(airIndexEnums) && airIndexEnums.length > 0) {
            AirIndexEnum airIndexEnum = airIndexEnums[0];
            if (AirIndexEnum.ONE.equals(airIndexEnum)) {
                topic = Constant.NEST_LOG_UPLOAD_PROCESS_1;
            }
            if (AirIndexEnum.TWO.equals(airIndexEnum)) {
                topic = Constant.NEST_LOG_UPLOAD_PROCESS_2;
            }
            if (AirIndexEnum.THREE.equals(airIndexEnum)) {
                topic = Constant.NEST_LOG_UPLOAD_PROCESS_3;
            }
        }
        ClientProxy.proxySubscribeGetStatus(this.client, topic, NestLogUploadState.class, handle,airIndexEnums);
    }

    /**
     * 更新CPS状态
     *
     * @param handle
     */
    public void listenCpsDownloadAndInstallState(UserHandle<CpsUpdateState> handle,AirIndexEnum... airIndexEnums) {
        String topic = Constant.NEST_CPS_UPDATE_PROCESS;
        if (Objects.nonNull(airIndexEnums) && airIndexEnums.length > 0) {
            AirIndexEnum airIndexEnum = airIndexEnums[0];
            if (AirIndexEnum.ONE.equals(airIndexEnum)) {
                topic = Constant.NEST_CPS_UPDATE_PROCESS_1;
            }
            if (AirIndexEnum.TWO.equals(airIndexEnum)) {
                topic = Constant.NEST_CPS_UPDATE_PROCESS_2;
            }
            if (AirIndexEnum.THREE.equals(airIndexEnum)) {
                topic = Constant.NEST_CPS_UPDATE_PROCESS_3;
            }
        }
        ClientProxy.proxySubscribeGetStatus(this.client, topic, CpsUpdateState.class, handle,airIndexEnums);
    }

    /**
     * 云冠告警信息
     *
     * @param handle
     */
    public void listenCloudCrownAlarmInfo(UserHandle<CloudCrownAlarmInfo> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.CLOUD_CROWN_ALARM_INFO_TOPIC, CloudCrownAlarmInfo.class, handle);
    }

    /**
     * 监听RC状态
     *
     * @param handle
     */
    public void listenRcStatus(UserHandle<RcState> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.RC_STATUS_SUBSCRIBE_TOPIC, RcState.class, handle);
    }

    /**
     * 监听任务运行过程，包括主题/status/base（nestState），/status/mission，启动任务的callback，/status/aircraft (diagnostics)
     */
    public void listenNestMissionProgress(MissionQueueHandle handle) {
        ClientProxy.proxySubscribeMissionQueueGetStatus(this.client, handle);
    }

    public String listenDynamicTopic(DynamicTopicHandle handle, StatusTopicEnum... topicEnums) {
        return ClientProxy.proxySubscribeDynamicTopicGetStatus(this.client, handle, topicEnums);
    }

    /**
     * 监听迷你机巢温控系统状态
     *
     * @param handle
     */
    public void listenAircraftInPlaceState(UserHandle<G900AircraftInPlaceState> handle) {
        ClientProxy.proxySubscribeGetStatus(this.client, Constant.AIRCRAFT_IN_PLACE_SUBSCRIBE_TOPIC, G900AircraftInPlaceState.class, handle);
    }

    public void removeDynamicTopic(String key) {
        ClientProxy.proxyRemoveDynamicTopic(this.client, key);
    }

}
