package com.imapcloud.nest.sdk;

import cn.hutool.core.collection.CollectionUtil;
import com.imapcloud.nest.sdk.converter.DjiCommonConverter;
import com.imapcloud.nest.sdk.converter.DjiLiveStateConverter;
import com.imapcloud.nest.sdk.listener.DjiEventListener;
import com.imapcloud.nest.utils.spring.SpringContextUtils;
import com.imapcloud.nest.v2.common.enums.DJILivePayloadEnum;
import com.imapcloud.nest.v2.manager.dataobj.in.DjiFileUploadCallBackDO;
import com.imapcloud.nest.v2.manager.dataobj.in.DjiLiveStreamDO;
import com.imapcloud.nest.v2.manager.event.DJIFileUploadCallBackEvent;
import com.imapcloud.nest.v2.manager.event.DJILiveStreamEvent;
import com.imapcloud.sdk.manager.ComponentManager;
import com.imapcloud.sdk.manager.ComponentManagerFactory;
import com.imapcloud.sdk.manager.dji.DjiDockManagerCf;
import com.imapcloud.sdk.manager.dji.DjiUavManagerCf;
import com.imapcloud.sdk.manager.general.enums.PushStreamMode;
import com.imapcloud.sdk.pojo.constant.AirIndexEnum;
import com.imapcloud.sdk.pojo.constant.MissionCommonStateEnum;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import com.imapcloud.sdk.pojo.djido.*;
import com.imapcloud.sdk.pojo.entity.*;
import com.imapcloud.sdk.utils.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * @author wmin
 */
@Slf4j
public class CommonNestState {

    private NestTypeEnum nestType;
    /**
     * 机巢uuid
     */
    private String nestUuid;

//    private Integer nestType;

    /**
     * 上一次使用的最后一块电池序号
     */
    private Integer aircraftLastUseBatteryIndex = -1;

    private Double cameraZoomRatio = 1.0;

    private ExtraParam extraParam = new ExtraParam();
    /**
     * 机巢状态
     */
    private NestState nestState = new NestState();
    private NestState nestState1 = new NestState();
    private NestState nestState2 = new NestState();
    private NestState nestState3 = new NestState();

    /**
     * 无人机状态
     */
    private AircraftState aircraftState = new AircraftState();
    private AircraftState aircraftState1 = new AircraftState();
    private AircraftState aircraftState2 = new AircraftState();
    private AircraftState aircraftState3 = new AircraftState();

    private AircraftState ccAircraftState = new AircraftState();
    /**
     * RTK状态
     */
    private RTKState rtkState = new RTKState();
    private RTKState rtkState1 = new RTKState();
    private RTKState rtkState2 = new RTKState();
    private RTKState rtkState3 = new RTKState();
    /**
     * 云台状态
     */
    private GimbalState gimbalState = new GimbalState();
    private GimbalState gimbalState1 = new GimbalState();
    private GimbalState gimbalState2 = new GimbalState();
    private GimbalState gimbalState3 = new GimbalState();
    /**
     * 无人机电池状态
     */
    private AircraftBatteryStateCPSV2 aircraftBatteryState = new AircraftBatteryStateCPSV2();
    private AircraftBatteryStateCPSV2 aircraftBatteryState1 = new AircraftBatteryStateCPSV2();
    private AircraftBatteryStateCPSV2 aircraftBatteryState2 = new AircraftBatteryStateCPSV2();
    private AircraftBatteryStateCPSV2 aircraftBatteryState3 = new AircraftBatteryStateCPSV2();
    private AircraftBatteryStateCPSV2 ccAircraftBatteryState = new AircraftBatteryStateCPSV2();
    /**
     * 固定机巢电池状态
     */
    private NestBatteryState nestBatteryState = new NestBatteryState();

    /**
     * mini机巢电池状态
     */
    private MiniNestBatteryState miniNestBatteryState = new MiniNestBatteryState();

    /**
     * M300机巢电池状态
     */
    private M300NestBatteryState m300NestBatteryState = new M300NestBatteryState();

    private G503NestBatteryState g503NestBatteryState = new G503NestBatteryState();

    /**
     * 任务状态
     */
    private MissionState missionState = new MissionState();
    private MissionState missionState1 = new MissionState();
    private MissionState missionState2 = new MissionState();
    private MissionState missionState3 = new MissionState();
    /**
     * 航点状态
     */
    private WaypointState waypointState = new WaypointState();
    private WaypointState waypointState1 = new WaypointState();
    private WaypointState waypointState2 = new WaypointState();
    private WaypointState waypointState3 = new WaypointState();
    /**
     * 固定电机状态
     */
    private FixMotorState fixMotorState = new FixMotorState();
    /**
     * mini电机状态
     */
    private MiniMotorState miniMotorState = new MiniMotorState();

    /**
     * m300电机状态（旧）
     */
    private M300MotorState m300MotorState = new M300MotorState();
    /**
     * m300电机状态（新）
     */
    private MotorBaseState motorBaseState = new MotorBaseState();
    /**
     * 电机驱动状态
     */
    private MotorDriverState motorDriverState = new MotorDriverState();
    /**
     * 固定机巢温度状态
     */
    private FixTemperatureState fixTemperatureState = new FixTemperatureState();
    /**
     * 迷你机巢空调状态V1
     */
    private MiniTemperatureState miniTemperatureState = new MiniTemperatureState();

    /**
     * 迷你机巢空调状态V2
     */
    private MiniAcStateV2 miniAcStateV2 = new MiniAcStateV2();

    /**
     * M300基站温度状态
     */
    private M300TemperatureState m300TemperatureState = new M300TemperatureState();
    /**
     * 多媒体状态
     */
    private MediaStateV2 mediaState = new MediaStateV2();
    private MediaStateV2 mediaState1 = new MediaStateV2();
    private MediaStateV2 mediaState2 = new MediaStateV2();
    private MediaStateV2 mediaState3 = new MediaStateV2();
    /**
     * 气象站状态
     */
    private AerographState aerographState = new AerographState();

    private DjiCommonDO<DjiDockPropertyOsdDO> djiDockPropertyOsdDO = new DjiCommonDO<>();

    /**
     * pilot
     */
    private DjiCommonDO<DjiPilotPropertyOsdDO> djiPilotPropertyOsdDO = new DjiCommonDO<>();

    private DjiCommonDO<DjiDockPropertyStateDO> djiDockPropertyStateDO = new DjiCommonDO<>();

    /**
     * 直播能力
     */
    private DjiCommonDO<DjiDockLiveCapacityStateDO> djiDockLiveCapacityStateDO = new DjiCommonDO<>();

    /**
     * 图传状态
     */
    private DjiCommonDO<DjiDockLiveStateDO> djiDockLiveStateDO = new DjiCommonDO<>();

    /**
     * 直播videoId
     */
    private DjiDockLiveIdDO djiDockLiveIdDO = new DjiDockLiveIdDO();

    private DjiCommonDO<DjiUavPropertyOsdDO> djiUavPropertyDO = new DjiCommonDO<>();

    private DjiCommonDO<FlightTaskProgressDO> flightTaskProgressDO = new DjiCommonDO<>();

    /**
     * 云冠无人机
     */
    private CloudCrownAircraftState cloudCrownAircraftState = new CloudCrownAircraftState();

    private WsStatus wsStatus = new WsStatus();

    private CameraState cameraState = new CameraState();
    private CameraState cameraState1 = new CameraState();
    private CameraState cameraState2 = new CameraState();
    private CameraState cameraState3 = new CameraState();

    private RcState rcState = new RcState();
    private RcState rcState1 = new RcState();
    private RcState rcState2 = new RcState();
    private RcState rcState3 = new RcState();

    private G900AircraftInPlaceState g900AircraftInPlaceState = new G900AircraftInPlaceState();

    private CpsUpdateState cpsUpdateState = new CpsUpdateState();
    private CpsUpdateState cpsUpdateState1 = new CpsUpdateState();
    private CpsUpdateState cpsUpdateState2 = new CpsUpdateState();
    private CpsUpdateState cpsUpdateState3 = new CpsUpdateState();

    public CommonNestState(String nestUuid, Integer nestType) {
        this.nestUuid = nestUuid;
        refreshNestState(nestUuid);
    }

    public void refreshNestState(String nestUuid) {
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            NestTypeEnum nestType = cm.getNestType();
            if (NestTypeEnum.UNKNOWN.equals(nestType)) {
                return;
            }
            this.nestType = nestType;
            if (NestTypeEnum.DJI_DOCK.equals(nestType)) {
                //初始化大疆基站状态
                djiRefreshNestState(cm);
                DjiEventListener djiEventListener = new DjiEventListener();
                djiEventListener.refreshNestState(nestUuid);
            } else if(NestTypeEnum.DJI_PILOT.equals(nestType)){
                //初始化大疆遥控器状态
                djiPilotRefreshNestState(cm);
            }else {
                //初始化中科云图基站状态
                geoRefreshNestState(cm);
            }
        }
    }

    public ExtraParam getExtraParam() {
        return extraParam;
    }

    public void setExtraParam(ExtraParam extraParam) {
        this.extraParam = extraParam;
    }

    public String getNestUuid() {
        return nestUuid;
    }

    public NestState getNestState(AirIndexEnum... airIndex) {
        if (Objects.nonNull(airIndex) && airIndex.length > 0) {
            switch (airIndex[0]) {
                case DEFAULT:
                    return nestState;
                case ONE:
                    return nestState1;
                case TWO:
                    return nestState2;
                case THREE:
                    return nestState3;
            }
        }
        return nestState;
    }


    public AircraftState getAircraftState(AirIndexEnum... airIndex) {
        if (!nestState.getRemoteControllerConnected()) {
            aircraftState = new AircraftState();
        }
        if (Objects.nonNull(airIndex) && airIndex.length > 0) {
            switch (airIndex[0]) {
                case DEFAULT:
                    return aircraftState;
                case ONE:
                    return aircraftState1;
                case TWO:
                    return aircraftState2;
                case THREE:
                    return aircraftState3;
            }
        }
        return aircraftState;
    }

    public CameraState getCameraState(AirIndexEnum... airIndex) {
        if (!nestState.getRemoteControllerConnected()) {
            cameraState = new CameraState();
        }
        if (Objects.nonNull(airIndex) && airIndex.length > 0) {
            switch (airIndex[0]) {
                case DEFAULT:
                    return cameraState;
                case ONE:
                    return cameraState1;
                case TWO:
                    return cameraState2;
                case THREE:
                    return cameraState3;
            }
        }
        return cameraState;
    }

    public RcState getRcState(AirIndexEnum... airIndex) {
        if (!nestState.getRemoteControllerConnected()) {
            rcState = new RcState();
        }
        if (Objects.nonNull(airIndex) && airIndex.length > 0) {
            switch (airIndex[0]) {
                case DEFAULT:
                    return rcState;
                case ONE:
                    return rcState1;
                case TWO:
                    return rcState2;
                case THREE:
                    return rcState3;
            }
        }
        return rcState;
    }

    public G900AircraftInPlaceState getG900AircraftInPlaceState() {
        //如果不需要一直推，可以不需要这个条件
//        if (!nestState.getRemoteControllerConnected()) {
//            g900AircraftInPlaceState = new G900AircraftInPlaceState();
//        }
        return g900AircraftInPlaceState;
    }


    public AircraftState getCcAircraftState() {
        return ccAircraftState;
    }

    public RTKState getRtkState(AirIndexEnum... airIndex) {
        if (!nestState.getRemoteControllerConnected()) {
            rtkState = new RTKState();
        }
        if (Objects.nonNull(airIndex) && airIndex.length > 0) {
            switch (airIndex[0]) {
                case DEFAULT:
                    return rtkState;
                case ONE:
                    return rtkState1;
                case TWO:
                    return rtkState2;
                case THREE:
                    return rtkState3;
            }
        }
        return rtkState;
    }

    public GimbalState getGimbalState(AirIndexEnum... airIndex) {
        if (!nestState.getRemoteControllerConnected()) {
            gimbalState = new GimbalState();
        }
        if (Objects.nonNull(airIndex) && airIndex.length > 0) {
            switch (airIndex[0]) {
                case DEFAULT:
                    return gimbalState;
                case ONE:
                    return gimbalState1;
                case TWO:
                    return gimbalState2;
                case THREE:
                    return gimbalState3;
            }
        }
        return gimbalState;
    }

    public AircraftBatteryStateCPSV2 getAircraftBatteryState(AirIndexEnum... airIndex) {
        if (!nestState.getAircraftConnected() || !nestState.getRemoteControllerConnected()) {
            aircraftBatteryState = new AircraftBatteryStateCPSV2();
        }
        if (Objects.nonNull(airIndex) && airIndex.length > 0) {
            switch (airIndex[0]) {
                case DEFAULT:
                    return aircraftBatteryState;
                case ONE:
                    return aircraftBatteryState1;
                case TWO:
                    return aircraftBatteryState2;
                case THREE:
                    return aircraftBatteryState3;
            }
        }
        return aircraftBatteryState;
    }

    public AircraftBatteryStateCPSV2 getCcAircraftBatteryState() {
        return ccAircraftBatteryState;
    }

    public NestBatteryState getNestBatteryState() {
        return nestBatteryState;
    }

    public MiniNestBatteryState getMiniNestBatteryState() {
        return miniNestBatteryState;
    }

    public MissionState getMissionState(AirIndexEnum... airIndex) {
        if (Objects.nonNull(airIndex) && airIndex.length > 0) {
            switch (airIndex[0]) {
                case DEFAULT:
                    return missionState;
                case ONE:
                    return missionState1;
                case TWO:
                    return missionState2;
                case THREE:
                    return missionState3;
            }
        }
        return missionState;
    }

    public WaypointState getWaypointState(AirIndexEnum... airIndex) {
        if (Objects.nonNull(airIndex) && airIndex.length > 0) {
            switch (airIndex[0]) {
                case DEFAULT:
                    return waypointState;
                case ONE:
                    return waypointState1;
                case TWO:
                    return waypointState2;
                case THREE:
                    return waypointState3;
            }
        }
        return waypointState;
    }

    public FixMotorState getFixMotorState() {
        return fixMotorState;
    }

    public MiniMotorState getMiniMotorState() {
        return miniMotorState;
    }

    public MotorDriverState getMotorDriverState() {
        return motorDriverState;
    }

    public FixTemperatureState getFixTemperatureState() {
        return fixTemperatureState;
    }

    public MiniTemperatureState getMiniTemperatureState() {
        return miniTemperatureState;
    }

    public MiniAcStateV2 getMiniAcStateV2() {
        return miniAcStateV2;
    }

    public MediaStateV2 getMediaState(AirIndexEnum... airIndex) {
        if (Objects.nonNull(airIndex) && airIndex.length > 0) {
            switch (airIndex[0]) {
                case DEFAULT:
                    return mediaState;
                case ONE:
                    return mediaState1;
                case TWO:
                    return mediaState2;
                case THREE:
                    return mediaState3;
            }
        }
        return mediaState;
    }

    public AerographState getAerographState() {
        return aerographState;
    }

    public WsStatus getWsStatus() {
        return wsStatus;
    }

    public Integer getAircraftLastUseBatteryIndex() {
        return aircraftLastUseBatteryIndex;
    }

    public M300MotorState getM300MotorState() {
        return m300MotorState;
    }

    public MotorBaseState getMotorBaseState() {
        return motorBaseState;
    }

    public M300TemperatureState getM300TemperatureState() {
        return m300TemperatureState;
    }

    public M300NestBatteryState getM300NestBatteryState() {
        return m300NestBatteryState;
    }

    public G503NestBatteryState getG503NestBatteryState() {
        return g503NestBatteryState;
    }

    public Double getCameraZoomRatio() {
        return cameraZoomRatio;
    }

    public void setCameraZoomRatio(Double cameraZoomRatio) {
        this.cameraZoomRatio = cameraZoomRatio;
    }

    public CloudCrownAircraftState getCloudCrownAircraftState() {
        return cloudCrownAircraftState;
    }

    public DjiCommonDO<DjiDockPropertyOsdDO> getDjiDockPropertyOsdDO() {
        return djiDockPropertyOsdDO;
    }

    public DjiCommonDO<DjiPilotPropertyOsdDO> getDjiPilotPropertyOsdDO() {
        return djiPilotPropertyOsdDO;
    }

    public DjiCommonDO<DjiDockPropertyStateDO> getDjiDockPropertyStateDO() {
        return djiDockPropertyStateDO;
    }

    public DjiCommonDO<DjiDockLiveCapacityStateDO> getDjiDockLiveCapacityStateDO() {
        return djiDockLiveCapacityStateDO;
    }

    public DjiCommonDO<DjiDockLiveStateDO> getDjiDockLiveStateDO() {
        return djiDockLiveStateDO;
    }

    public DjiDockLiveIdDO getDjiDockLiveIdDO() {
        return djiDockLiveIdDO;
    }

    public DjiCommonDO<DjiUavPropertyOsdDO> getDjiUavPropertyDO() {
        return djiUavPropertyDO;
    }

    public DjiCommonDO<FlightTaskProgressDO> getFlightTaskProgressDO() {
        return flightTaskProgressDO;
    }

    public void clearFlightTaskProgressDOOfData() {
        this.flightTaskProgressDO.setData(null);
    }


    public CpsUpdateState getCpsUpdateState(AirIndexEnum... airIndex) {
        if (Objects.nonNull(airIndex) && airIndex.length > 0) {
            switch (airIndex[0]) {
                case DEFAULT:
                    if (cpsUpdateState == null || cpsUpdateState.isExpire()) {
                        return new CpsUpdateState();
                    }
                    return cpsUpdateState;
                case ONE:
                    if (cpsUpdateState1 == null || cpsUpdateState1.isExpire()) {
                        return new CpsUpdateState();
                    }
                    return cpsUpdateState1;
                case TWO:
                    if (cpsUpdateState2 == null || cpsUpdateState2.isExpire()) {
                        return new CpsUpdateState();
                    }
                    return cpsUpdateState2;
                case THREE:
                    if (cpsUpdateState3 == null || cpsUpdateState3.isExpire()) {
                        return new CpsUpdateState();
                    }
                    return cpsUpdateState3;
            }
        }
        if (cpsUpdateState == null || cpsUpdateState.isExpire()) {
            return new CpsUpdateState();
        }
        return cpsUpdateState;
    }

    public void setCommonCameraZoomRatio(Double zoomRatio) {
        if (Objects.nonNull(zoomRatio)) {
            this.setCameraZoomRatio(zoomRatio);
        }
    }

    public NestTypeEnum getNestType() {
        return nestType;
    }

    private void updateNestInfoDtoByNestBatteryState(NestBatteryState batteryState) {
        String[] bdi4;
        if (batteryState.getNestBattery1State() != 0) {
            bdi4 = this.getBatteryDetailInfo(batteryState.getNestBattery1State());
            if (this.isNestUsing(bdi4[0])) {
                this.aircraftLastUseBatteryIndex = 0;
            }
        }

        if (batteryState.getNestBattery2State() != 0) {
            bdi4 = this.getBatteryDetailInfo(batteryState.getNestBattery2State());
            if (this.isNestUsing(bdi4[0])) {
                this.aircraftLastUseBatteryIndex = 1;
            }

        }

        if (batteryState.getNestBattery3State() != 0) {
            bdi4 = this.getBatteryDetailInfo(batteryState.getNestBattery3State());
            if (this.isNestUsing(bdi4[0])) {
                this.aircraftLastUseBatteryIndex = 2;
            }
        }

        if (batteryState.getNestBattery4State() != 0) {
            bdi4 = this.getBatteryDetailInfo(batteryState.getNestBattery4State());
            if (this.isNestUsing(bdi4[0])) {
                this.aircraftLastUseBatteryIndex = 3;
            }
        }


    }

    private boolean isNestUsing(String state) {
        return "03".equals(state);
    }


    /**
     * 获取电池详细信息
     * 00: 初始状态
     * 01: 正在充电
     * 02: 电池已充满
     * 03: 电池正在使用
     *
     * @param batteryState
     * @return 返回数组[状态, 电量]
     */
    private String[] getBatteryDetailInfo(int batteryState) {
        return dec2hex(batteryState);
    }

    private String[] dec2hex(int state) {
        StringBuilder hex = new StringBuilder(Integer.toHexString(state));

        if (hex.length() < 4) {
            int tmp = 4 - hex.length();

            for (int i = 0; i < tmp; i++) {
                hex.insert(0, "0");
            }
        }

        return new String[]{hex.substring(0, 2), String.valueOf(Integer.parseInt(hex.substring(2, 4), 16))};
    }


    private void handleCallback(String nestUuid, MissionState missionState, AirIndexEnum airIndexEnum) {
        //TODO
        ComponentManager cm = ComponentManagerFactory.getInstance(nestUuid);
        if (cm != null) {
            MissionCommonStateEnum currentState = missionState.getCurrentState();
            if (MissionCommonStateEnum.COMPLETING.equals(currentState)) {
                cm.getMissionManager().removeListenMissionRunning(airIndexEnum);
            }

        }
    }

    private void djiRefreshNestState(ComponentManager cm) {
        this.djiDockPropertyOsdDO.setData(new DjiDockPropertyOsdDO());
        this.djiUavPropertyDO.setData(new DjiUavPropertyOsdDO());
        this.djiDockPropertyStateDO.setData(new DjiDockPropertyStateDO());
        this.flightTaskProgressDO.setData(new FlightTaskProgressDO());

        this.djiDockLiveCapacityStateDO.setData(new DjiDockLiveCapacityStateDO());
        this.djiDockLiveStateDO.setData(new DjiDockLiveStateDO());

        DjiDockManagerCf djiDockManagerCf = cm.getDjiDockManagerCf();
        djiDockManagerCf.listenOsd((result, isSuccess, errMsg) -> {
            if (isSuccess && Objects.nonNull(result)) {
                DjiCommonConverter.INSTANCES.dockOsdDOUpdateAFromB(result, this.djiDockPropertyOsdDO);
                DjiCommonConverter.INSTANCES.dockOsdDOUpdateAFromB(result.getData(), this.djiDockPropertyOsdDO.getData());
                this.djiDockPropertyOsdDO.getData().setLastTimestamp(System.currentTimeMillis());
            }
        });

        djiDockManagerCf.listenState(((result, isSuccess, errMsg) -> {
            if (isSuccess && Objects.nonNull(result)) {
                DjiCommonConverter.INSTANCES.dockStateDOUpdateAFromB(result, this.djiDockPropertyStateDO);
                DjiCommonConverter.INSTANCES.dockStateDOUpdateAFromB(result.getData(), this.djiDockPropertyStateDO.getData());
                DjiDockLiveCapacityStateDO.LiveCapacity oldLiveCapacity = this.djiDockLiveCapacityStateDO.getData()==null?null:this.djiDockLiveCapacityStateDO.getData().getLiveCapacity();
                if(result.getData()!=null && result.getData().getLiveStatus()!=null){
                    //直播状态
                    DjiLiveStateConverter.INSTANCES.dockStateDOUpdateAFromB(result.getData(), this.djiDockLiveStateDO.getData());
                }


                //拥有直播能力，直接开启图传
                if(result.getData()!=null && result.getData().getLiveCapacity()!=null){
                    //直播能力
                    DjiLiveStateConverter.INSTANCES.dockStateDOUpdateAFromB(result.getData(), this.djiDockLiveCapacityStateDO.getData());
                    //记录videoId
                    recordVideoId();
//                    DJILiveStreamEvent
                    SpringContextUtils.getApplicationContext().publishEvent(new DJILiveStreamEvent(
                            new DjiLiveStreamDO()
                                    .setLiveCapacity(this.djiDockLiveCapacityStateDO.getData().getLiveCapacity())
                                    .setOldLiveCapacity(oldLiveCapacity)
                                    .setNestUuid(result.getGateway())
                    ));
                }
            }
        }));

        DjiUavManagerCf djiUavManagerCf = cm.getDjiUavManagerCf();
        djiUavManagerCf.listenOsd((result, isSuccess, errMsg) -> {
            if (isSuccess && Objects.nonNull(result)) {
                DjiCommonConverter.INSTANCES.uavOsdDOUpdateAFromB(result, this.djiUavPropertyDO);
                DjiCommonConverter.INSTANCES.uavOsdDOUpdateAFromB(result.getData(), this.djiUavPropertyDO.getData());
                this.djiUavPropertyDO.getData().setLastTimestamp(System.currentTimeMillis());
            }
        });

        djiDockManagerCf.listenFlightTaskProgressOfEvents((result, isSuccess, errMsg) -> {
            DjiCommonConverter.INSTANCES.flightTaskProgressDOUpdateAFromB(result, this.flightTaskProgressDO);
        });

        log.info("注册大疆媒体监听");
        //初始化连接的时候，就开启监听
        djiDockManagerCf.listenFileUploadCallBackOfEvents(((result, isSuccess, errMsg) -> {
            DjiFileUploadCallBackDO djiFileUploadCallBackDO = new DjiFileUploadCallBackDO();
            SpringContextUtils.getApplicationContext().publishEvent(new DJIFileUploadCallBackEvent(djiFileUploadCallBackDO.setDoDjiCommonDO(result)
                    .setNestTypeEnum(nestType)
                    .setSuccess(isSuccess)
                    .setErrMsg(errMsg)));
        }));
    }

    private void djiPilotRefreshNestState(ComponentManager cm) {
        this.djiDockPropertyOsdDO.setData(new DjiDockPropertyOsdDO());
        this.djiPilotPropertyOsdDO.setData(new DjiPilotPropertyOsdDO());
        //无人机
        this.djiUavPropertyDO.setData(new DjiUavPropertyOsdDO());
//        this.djiDockPropertyStateDO.setData(new DjiDockPropertyStateDO());
//        this.flightTaskProgressDO.setData(new FlightTaskProgressDO());

        this.djiDockLiveCapacityStateDO.setData(new DjiDockLiveCapacityStateDO());
        this.djiDockLiveStateDO.setData(new DjiDockLiveStateDO());

        DjiDockManagerCf djiDockManagerCf = cm.getDjiDockManagerCf();

        djiDockManagerCf.listenPilotOsd((result, isSuccess, errMsg) -> {
            if (isSuccess && Objects.nonNull(result)) {
                DjiCommonConverter.INSTANCES.pilotOsdDOUpdateAFromB(result, this.djiPilotPropertyOsdDO);
                DjiCommonConverter.INSTANCES.pilotOsdDOUpdateAFromB(result.getData(), this.djiPilotPropertyOsdDO.getData());
                this.djiPilotPropertyOsdDO.getData().setLastTimestamp(System.currentTimeMillis());
            }
        });

        djiDockManagerCf.listenState(((result, isSuccess, errMsg) -> {
            if (isSuccess && Objects.nonNull(result)) {
                DjiCommonConverter.INSTANCES.dockStateDOUpdateAFromB(result, this.djiDockPropertyStateDO);
                DjiCommonConverter.INSTANCES.dockStateDOUpdateAFromB(result.getData(), this.djiDockPropertyStateDO.getData());
                DjiDockLiveCapacityStateDO.LiveCapacity oldLiveCapacity = this.djiDockLiveCapacityStateDO.getData()==null?null:this.djiDockLiveCapacityStateDO.getData().getLiveCapacity();
                if(result.getData()!=null && result.getData().getLiveStatus()!=null){
                    //直播状态
                    DjiLiveStateConverter.INSTANCES.dockStateDOUpdateAFromB(result.getData(), this.djiDockLiveStateDO.getData());
                }


                //拥有直播能力，直接开启图传
                if(result.getData()!=null && result.getData().getLiveCapacity()!=null){
                    //直播能力
                    DjiLiveStateConverter.INSTANCES.dockStateDOUpdateAFromB(result.getData(), this.djiDockLiveCapacityStateDO.getData());
                    //记录videoId
                    recordVideoId();
//                    DJILiveStreamEvent
                    SpringContextUtils.getApplicationContext().publishEvent(new DJILiveStreamEvent(
                            new DjiLiveStreamDO()
                                    .setLiveCapacity(this.djiDockLiveCapacityStateDO.getData().getLiveCapacity())
                                    .setOldLiveCapacity(oldLiveCapacity)
                                    .setNestUuid(result.getGateway())
                    ));
                }
            }
        }));

        DjiUavManagerCf djiUavManagerCf = cm.getDjiUavManagerCf();
        djiUavManagerCf.listenOsd((result, isSuccess, errMsg) -> {
            if (isSuccess && Objects.nonNull(result)) {
                DjiCommonConverter.INSTANCES.uavOsdDOUpdateAFromB(result, this.djiUavPropertyDO);
                DjiCommonConverter.INSTANCES.uavOsdDOUpdateAFromB(result.getData(), this.djiUavPropertyDO.getData());
                this.djiUavPropertyDO.getData().setLastTimestamp(System.currentTimeMillis());
            }
        });

//        djiDockManagerCf.listenFlightTaskProgressOfEvents((result, isSuccess, errMsg) -> {
//            DjiCommonConverter.INSTANCES.flightTaskProgressDOUpdateAFromB(result, this.flightTaskProgressDO);
//        });
    }

    /**
     * 记录videoId
     */
    private void recordVideoId(){
        if(djiDockLiveIdDO == null){
            djiDockLiveIdDO = new DjiDockLiveIdDO();
        }
        if(this.djiDockPropertyStateDO.getData() == null || this.djiDockPropertyStateDO.getData().getLiveCapacity() ==null){
            return;
        }
        DjiDockPropertyStateDO.LiveCapacity liveCapacity = this.djiDockPropertyStateDO.getData().getLiveCapacity();
        List<DjiDockPropertyStateDO.Device> deviceList = Optional.ofNullable(liveCapacity)
                .map(DjiDockPropertyStateDO.LiveCapacity::getDeviceList)
                .orElseGet(()->new ArrayList<>());
        if(CollectionUtil.isEmpty(deviceList)){
            return;
        }

        if(StringUtils.isEmpty(djiDockLiveIdDO.getDockVideoId())){
            djiDockLiveIdDO.setDockVideoId(getVideoId(deviceList,DJILivePayloadEnum.DJI_DOCK_LIVE.getCode()));
        }
        if(StringUtils.isEmpty(djiDockLiveIdDO.getAircraftSelfVideoId())){
            djiDockLiveIdDO.setAircraftSelfVideoId(getVideoId(deviceList,DJILivePayloadEnum.AIR_CRAFT_SELF.getCode()));
        }
        if(StringUtils.isEmpty(djiDockLiveIdDO.getAircraftLiveVideoId())){
            djiDockLiveIdDO.setAircraftLiveVideoId(getVideoId(deviceList,DJILivePayloadEnum.AIR_CRAFT_LIVE.getCode()));
        }
    }

    /**
     * 基站 0 ， 0 ，0 无人机机身 1 ， 0 ，0 无人机图传 1 ， 1 ，0
     * @param deviceList
     * @param payloadIndex
     * @return
     */
    public String getVideoId(List<DjiDockPropertyStateDO.Device> deviceList , Integer payloadIndex){
        int deviceIndex = 0
                ,cameraIndex = 0
                ,videoIndex = 0;
        if(DJILivePayloadEnum.AIR_CRAFT_SELF.getCode().equals(payloadIndex)){
            deviceIndex = 1;
            cameraIndex = 0;
            videoIndex = 0;
        }else if(DJILivePayloadEnum.AIR_CRAFT_LIVE.getCode().equals(payloadIndex)){
            deviceIndex = 1;
            cameraIndex = 1;
            videoIndex = 0;
        }else if(DJILivePayloadEnum.DJI_DOCK_LIVE.getCode().equals(payloadIndex)){
            deviceIndex = 0;
            cameraIndex = 0;
            videoIndex = 0;
        }

        if(CollectionUtil.isEmpty(deviceList) || deviceList.size() <= deviceIndex){
            return "";
        }
        DjiDockPropertyStateDO.Device device = deviceList.get(deviceIndex);
        List<DjiDockPropertyStateDO.Camera> cameraList = device.getCameraList();
        if(CollectionUtil.isEmpty(cameraList) || cameraList.size() <= cameraIndex){
            return "";
        }
        DjiDockPropertyStateDO.Camera camera = cameraList.get(cameraIndex);
        List<DjiDockPropertyStateDO.Video> videoList = camera.getVideoList();
        if(CollectionUtil.isEmpty(videoList) || videoList.size() <= videoIndex){
            return "";
        }
        return String.format("%s/%s/%s",device.getSn() , camera.getCameraIndex() , videoList.get(videoIndex).getVideoIndex());
    }

    /**
     * 检查电池使用次数，小于1或者是null的返回true
     *
     * @param num 全国矿工工会
     * @return boolean
     */
    private boolean checkNum(Integer num) {
        return num == null || num < 1;
    }

    private void geoRefreshNestState(ComponentManager cm) {
        cm.getBaseManager().listenAllSubscribeTopic((en, payload) -> {
           /* if(log.isDebugEnabled()){
                log.debug("cm.getBaseManager().listenAllSubscribeTopic ==> en={},payload={}", en, payload);
            }*/
            switch (en) {
                /**
                 * 监听fix,mini机巢状态监听m300机巢状态
                 */
                case BASE_STATE_SUBSCRIBE_TOPIC:
                    this.nestState = JSONUtil.parseObject(payload, NestState.class);
                    break;
                case BASE_STATE_SUBSCRIBE_TOPIC_1:
                    this.nestState1 = JSONUtil.parseObject(payload, NestState.class);
                    break;
                case BASE_STATE_SUBSCRIBE_TOPIC_2:
                    this.nestState2 = JSONUtil.parseObject(payload, NestState.class);
                    break;
                case BASE_STATE_SUBSCRIBE_TOPIC_3:
                    this.nestState3 = JSONUtil.parseObject(payload, NestState.class);
                    break;

                /**
                 * 监听飞机状态
                 */
                case AIRCRAFT_STATE_SUBSCRIBE_TOPIC:
                    if (Objects.equals(NestTypeEnum.I_CREST2, this.nestType)) {
                        this.ccAircraftState = JSONUtil.parseObject(payload, AircraftState.class);
                        if (this.ccAircraftState != null) {
                            this.ccAircraftState.setUpdateTimes(System.currentTimeMillis());
                        }
                    } else {
                        this.aircraftState = JSONUtil.parseObject(payload, AircraftState.class);
                    }
                    break;
                case AIRCRAFT_STATE_SUBSCRIBE_TOPIC_1:
                    this.aircraftState1 = JSONUtil.parseObject(payload, AircraftState.class);
                    break;
                case AIRCRAFT_STATE_SUBSCRIBE_TOPIC_2:
                    this.aircraftState2 = JSONUtil.parseObject(payload, AircraftState.class);
                    break;
                case AIRCRAFT_STATE_SUBSCRIBE_TOPIC_3:
                    this.aircraftState3 = JSONUtil.parseObject(payload, AircraftState.class);
                    break;

                /**
                 * 监听RTK状态
                 */
                case RTK_STATE_SUBSCRIBE_TOPIC:
                    this.rtkState = JSONUtil.parseObject(payload, RTKState.class);
                    break;
                case RTK_STATE_SUBSCRIBE_TOPIC_1:
                    this.rtkState1 = JSONUtil.parseObject(payload, RTKState.class);
                    break;
                case RTK_STATE_SUBSCRIBE_TOPIC_2:
                    this.rtkState2 = JSONUtil.parseObject(payload, RTKState.class);
                    break;
                case RTK_STATE_SUBSCRIBE_TOPIC_3:
                    this.rtkState3 = JSONUtil.parseObject(payload, RTKState.class);
                    break;
                /**
                 * 监听云台状态
                 */
                case GIMBAL_STATE_SUBSCRIBE_TOPIC:
                    this.gimbalState = JSONUtil.parseObject(payload, GimbalState.class);
                    break;
                case GIMBAL_STATE_SUBSCRIBE_TOPIC_1:
                    this.gimbalState1 = JSONUtil.parseObject(payload, GimbalState.class);
                    break;
                case GIMBAL_STATE_SUBSCRIBE_TOPIC_2:
                    this.gimbalState2 = JSONUtil.parseObject(payload, GimbalState.class);
                    break;
                case GIMBAL_STATE_SUBSCRIBE_TOPIC_3:
                    this.gimbalState3 = JSONUtil.parseObject(payload, GimbalState.class);
                    break;

                /**
                 * 飞机电池状态
                 */
                case AIRCRAFT_BATTERY_STATE_SUBSCRIBE_TOPIC:
                    this.aircraftBatteryState = JSONUtil.parseObject(payload, AircraftBatteryStateCPSV2.class);
                    if (this.aircraftBatteryState != null) {
                        this.miniNestBatteryState.setChargeCount(this.aircraftBatteryState.getAircraftBatteryNumberOfDischarges());
                    }
                    break;
                case AIRCRAFT_BATTERY_STATE_SUBSCRIBE_TOPIC_1:
                    if (Objects.equals(NestTypeEnum.I_CREST2, this.nestType)) {
                        this.ccAircraftBatteryState = JSONUtil.parseObject(payload, AircraftBatteryStateCPSV2.class);
                    } else {
                        this.aircraftBatteryState1 = JSONUtil.parseObject(payload, AircraftBatteryStateCPSV2.class);
                        if (this.aircraftBatteryState1 != null) {
                            this.miniNestBatteryState.setChargeCount(this.aircraftBatteryState1.getAircraftBatteryNumberOfDischarges());
                        }
                    }
                    break;
                case AIRCRAFT_BATTERY_STATE_SUBSCRIBE_TOPIC_2:
                    this.aircraftBatteryState2 = JSONUtil.parseObject(payload, AircraftBatteryStateCPSV2.class);
                    if (this.aircraftBatteryState2 != null) {
                        this.miniNestBatteryState.setChargeCount(this.aircraftBatteryState2.getAircraftBatteryNumberOfDischarges());
                    }
                    break;
                case AIRCRAFT_BATTERY_STATE_SUBSCRIBE_TOPIC_3:
                    this.aircraftBatteryState3 = JSONUtil.parseObject(payload, AircraftBatteryStateCPSV2.class);
                    if (this.aircraftBatteryState3 != null) {
                        this.miniNestBatteryState.setChargeCount(this.aircraftBatteryState3.getAircraftBatteryNumberOfDischarges());
                    }
                    break;

                /**
                 * 机巢电池状态
                 */
                case NEST_BATTERY_STATE_SUBSCRIBE_TOPIC:
                    if (Objects.equals(NestTypeEnum.G600, this.nestType)) {

                        NestBatteryState preNestBatteryState = this.nestBatteryState;
                        this.nestBatteryState = JSONUtil.parseObject(payload, NestBatteryState.class);
                        updateNestInfoDtoByNestBatteryState(this.nestBatteryState);
                        // 缓存电池使用次数
                        if (preNestBatteryState != null) {
                            if (checkNum(this.nestBatteryState.getNestBattery1NumOfDischarged())) {
                                this.nestBatteryState.setNestBattery1NumOfDischarged(preNestBatteryState.getNestBattery1NumOfDischarged());
                            }
                            if (checkNum(this.nestBatteryState.getNestBattery2NumOfDischarged())) {
                                this.nestBatteryState.setNestBattery2NumOfDischarged(preNestBatteryState.getNestBattery2NumOfDischarged());
                            }
                            if (checkNum(this.nestBatteryState.getNestBattery3NumOfDischarged())) {
                                this.nestBatteryState.setNestBattery3NumOfDischarged(preNestBatteryState.getNestBattery3NumOfDischarged());
                            }
                            if (checkNum(this.nestBatteryState.getNestBattery4NumOfDischarged())) {
                                this.nestBatteryState.setNestBattery4NumOfDischarged(preNestBatteryState.getNestBattery4NumOfDischarged());
                            }
                        }
                    }

                    if (Objects.equals(NestTypeEnum.S110_AUTEL, this.nestType) ||
                        Objects.equals(NestTypeEnum.S110_MAVIC3, this.nestType) ||
                            Objects.equals(NestTypeEnum.S100_V2, this.nestType) ||
                            Objects.equals(NestTypeEnum.S100_V1, this.nestType)
                    ) {
                        // 获取上次的充电次数
                        MiniNestBatteryState temp = this.miniNestBatteryState;
                        this.miniNestBatteryState = JSONUtil.parseObject(payload, MiniNestBatteryState.class);
                        if (this.miniNestBatteryState != null && temp != null) {
                            this.miniNestBatteryState.setChargeCount(temp.getChargeCount());
                        }
                    }

                    if (Objects.equals(NestTypeEnum.G900, this.nestType) || Objects.equals(NestTypeEnum.G900_CHARGE,this.nestType)) {
                        this.m300NestBatteryState = JSONUtil.parseObject(payload, M300NestBatteryState.class);
                    }

                    if (Objects.equals(NestTypeEnum.G503, this.nestType)) {
                        this.g503NestBatteryState = JSONUtil.parseObject(payload, G503NestBatteryState.class);
                    }
                    break;

                /**
                 * 任务状态监听
                 */
                case MISSION_STATE_SUBSCRIBE_TOPIC:
                    this.missionState = JSONUtil.parseObject(payload, MissionState.class);
                    handleCallback(nestUuid, missionState, AirIndexEnum.DEFAULT);
                    break;
                case MISSION_STATE_SUBSCRIBE_TOPIC_1:
                    this.missionState1 = JSONUtil.parseObject(payload, MissionState.class);
                    handleCallback(nestUuid, missionState1, AirIndexEnum.ONE);
                    break;
                case MISSION_STATE_SUBSCRIBE_TOPIC_2:
                    this.missionState2 = JSONUtil.parseObject(payload, MissionState.class);
                    handleCallback(nestUuid, missionState2, AirIndexEnum.TWO);
                    break;
                case MISSION_STATE_SUBSCRIBE_TOPIC_3:
                    this.missionState3 = JSONUtil.parseObject(payload, MissionState.class);
                    handleCallback(nestUuid, missionState3, AirIndexEnum.THREE);
                    break;
                /**
                 * 航点状态监听
                 */
                case WAYPOINT_MISSION_STATE_SUBSCRIBE_TOPIC:
                    this.waypointState = JSONUtil.parseObject(payload, WaypointState.class);
                    break;
                case WAYPOINT_MISSION_STATE_SUBSCRIBE_TOPIC_1:
                    this.waypointState1 = JSONUtil.parseObject(payload, WaypointState.class);
                    break;
                case WAYPOINT_MISSION_STATE_SUBSCRIBE_TOPIC_2:
                    this.waypointState2 = JSONUtil.parseObject(payload, WaypointState.class);
                    break;
                case WAYPOINT_MISSION_STATE_SUBSCRIBE_TOPIC_3:
                    this.waypointState3 = JSONUtil.parseObject(payload, WaypointState.class);
                    break;
                /**
                 * 固定机巢电机状态
                 */
                case MOTOR_SUBSCRIBE_TOPIC:
                    if (Objects.equals(NestTypeEnum.G600, this.nestType)) {
                        this.fixMotorState = JSONUtil.parseObject(payload, FixMotorState.class);
                    }
                    if (Objects.equals(NestTypeEnum.S100_V1, this.nestType) ||
                            Objects.equals(NestTypeEnum.S100_V2, this.nestType)
                    ) {
                        this.miniMotorState = JSONUtil.parseObject(payload, MiniMotorState.class);
                    }
                    break;
                /**
                 * m300新状态
                 */
                case MOTOR_BASE_SUBSCRIBE_TOPIC:
                    if (Objects.equals(NestTypeEnum.G503, this.nestType) ||
                            Objects.equals(NestTypeEnum.S110_AUTEL, this.nestType) ||
                            Objects.equals(NestTypeEnum.S110_MAVIC3, this.nestType) ||
                            Objects.equals(NestTypeEnum.G900, this.nestType) ||
                            Objects.equals(NestTypeEnum.G900_CHARGE, this.nestType)
                    ) {
                        this.motorBaseState = JSONUtil.parseObject(payload, MotorBaseState.class);
                        if (Objects.isNull(this.getMotorBaseState())) {
                            System.out.println(nestUuid);
                        }
                    }
                    break;
                /**
                 * 驱动器状态
                 */
                case MOTOR_DRIVE_STATE_SUBSCRIBE_TOPIC:
                    this.motorDriverState = JSONUtil.parseObject(payload, MotorDriverState.class);
                    break;
                /**
                 * 固定温控系统状态
                 */
                case TEMPERATURE_STATE_SUBSCRIBE_TOPIC:
                    if (Objects.equals(NestTypeEnum.G600, this.nestType)) {
                        this.fixTemperatureState = JSONUtil.parseObject(payload, FixTemperatureState.class);
                        break;
                    }
                    if (Objects.equals(NestTypeEnum.S100_V1, this.nestType) ||
                            Objects.equals(NestTypeEnum.S100_V2, this.nestType) ||
                            Objects.equals(NestTypeEnum.S110_AUTEL, this.nestType) ||
                            Objects.equals(NestTypeEnum.S110_MAVIC3, this.nestType)
                    ) {
                        this.miniTemperatureState = JSONUtil.parseObject(payload, MiniTemperatureState.class);
                        this.miniAcStateV2 = JSONUtil.parseObject(payload, MiniAcStateV2.class);
                        break;
                    }
                    if (Objects.equals(NestTypeEnum.G900,this.nestType) ||
                            Objects.equals(NestTypeEnum.G900_CHARGE,this.nestType) ||
                            Objects.equals(NestTypeEnum.G503,this.nestType)) {
                        this.m300TemperatureState = JSONUtil.parseObject(payload, M300TemperatureState.class);
                    }
                    break;
                case MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC:
                    this.mediaState = JSONUtil.parseObject(payload, MediaStateV2.class);
                    break;
                case MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC_1:
                    this.mediaState1 = JSONUtil.parseObject(payload, MediaStateV2.class);
                    break;
                case MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC_2:
                    this.mediaState2 = JSONUtil.parseObject(payload, MediaStateV2.class);
                    break;
                case MEDIA_FILE_DOWNLOAD_STATE_SUBSCRIBE_TOPIC_3:
                    this.mediaState3 = JSONUtil.parseObject(payload, MediaStateV2.class);
                    break;

                case WS_STATE_SUBSCRIBE_TOPIC:
                    this.wsStatus = JSONUtil.parseObject(payload, WsStatus.class);
                    break;
                case CAMERA_STATE_SUBSCRIBE_TOPIC:
                    this.cameraState = JSONUtil.parseObject(payload, CameraState.class);
                    break;
                case CAMERA_STATE_SUBSCRIBE_TOPIC_1:
                    this.cameraState1 = JSONUtil.parseObject(payload, CameraState.class);
                    break;
                case CAMERA_STATE_SUBSCRIBE_TOPIC_2:
                    this.cameraState2 = JSONUtil.parseObject(payload, CameraState.class);
                    break;
                case CAMERA_STATE_SUBSCRIBE_TOPIC_3:
                    this.cameraState3 = JSONUtil.parseObject(payload, CameraState.class);
                    break;

                case RC_STATE_SUBSCRIBE_TOPIC:
                    this.rcState = JSONUtil.parseObject(payload, RcState.class);
                    break;
                case RC_STATE_SUBSCRIBE_TOPIC_1:
                    this.rcState1 = JSONUtil.parseObject(payload, RcState.class);
                    break;
                case RC_STATE_SUBSCRIBE_TOPIC_2:
                    this.rcState2 = JSONUtil.parseObject(payload, RcState.class);
                    break;
                case RC_STATE_SUBSCRIBE_TOPIC_3:
                    this.rcState3 = JSONUtil.parseObject(payload, RcState.class);
                    break;
                case SYSTEM_UPDATE_STATUS_SUBSCRIBE_TOPIC:
                    this.cpsUpdateState = JSONUtil.parseObject(payload, CpsUpdateState.class);
                    this.cpsUpdateState.setUpdateLastTime(System.currentTimeMillis());
                    break;
                case SYSTEM_UPDATE_STATUS_SUBSCRIBE_TOPIC_1:
                    this.cpsUpdateState1 = JSONUtil.parseObject(payload, CpsUpdateState.class);
                    this.cpsUpdateState1.setUpdateLastTime(System.currentTimeMillis());
                    break;
                case SYSTEM_UPDATE_STATUS_SUBSCRIBE_TOPIC_2:
                    this.cpsUpdateState2 = JSONUtil.parseObject(payload, CpsUpdateState.class);
                    this.cpsUpdateState2.setUpdateLastTime(System.currentTimeMillis());
                    break;
                case SYSTEM_UPDATE_STATUS_SUBSCRIBE_TOPIC_3:
                    this.cpsUpdateState3 = JSONUtil.parseObject(payload, CpsUpdateState.class);
                    this.cpsUpdateState3.setUpdateLastTime(System.currentTimeMillis());
                    break;
                case AIRCRAFT_IN_PLACE_SUBSCRIBE_TOPIC:
                    this.g900AircraftInPlaceState = JSONUtil.parseObject(payload, G900AircraftInPlaceState.class);
                    break;
                default:
                    break;
            }
        });
    }


    public static class ExtraParam {
        private PushStreamMode pushStreamMode = PushStreamMode.UNKNOWN;
        private PushStreamMode pushStreamMode1 = PushStreamMode.UNKNOWN;
        private PushStreamMode pushStreamMode2 = PushStreamMode.UNKNOWN;
        private PushStreamMode pushStreamMode3 = PushStreamMode.UNKNOWN;

        public PushStreamMode getPushStreamMode(AirIndexEnum... airIndexEnums) {
            if (Objects.nonNull(airIndexEnums) && airIndexEnums.length > 0) {
                AirIndexEnum airIndexEnum = airIndexEnums[0];
                if (AirIndexEnum.DEFAULT.equals(airIndexEnum)) {
                    return Objects.nonNull(this.pushStreamMode) ? this.pushStreamMode : PushStreamMode.UNKNOWN;
                }
                if (AirIndexEnum.ONE.equals(airIndexEnum)) {
                    return  Objects.nonNull(this.pushStreamMode1) ? this.pushStreamMode1 : PushStreamMode.UNKNOWN;
                }
                if (AirIndexEnum.TWO.equals(airIndexEnum)) {
                    return Objects.nonNull(this.pushStreamMode2) ? this.pushStreamMode2 : PushStreamMode.UNKNOWN;
                }
                if (AirIndexEnum.THREE.equals(airIndexEnum)) {
                    return Objects.nonNull(this.pushStreamMode3) ? this.pushStreamMode3 : PushStreamMode.UNKNOWN;
                }
            }
            return Objects.nonNull(this.pushStreamMode) ? this.pushStreamMode : PushStreamMode.UNKNOWN;
        }

        public void setPushStreamMode(PushStreamMode pushStreamMode, AirIndexEnum... airIndexEnums) {
            if (Objects.isNull(airIndexEnums) || airIndexEnums.length == 0) {
                this.pushStreamMode = pushStreamMode;
                return;
            }
            AirIndexEnum airIndexEnum = airIndexEnums[0];
            if (AirIndexEnum.DEFAULT.equals(airIndexEnum)) {
                if(Objects.nonNull(pushStreamMode)) {
                    this.pushStreamMode = pushStreamMode;
                }
            }
            if (AirIndexEnum.ONE.equals(airIndexEnum)) {
                if(Objects.nonNull(pushStreamMode)) {
                    this.pushStreamMode1 = pushStreamMode;
                }
            }
            if (AirIndexEnum.TWO.equals(airIndexEnum)) {
                if(Objects.nonNull(pushStreamMode)) {
                    this.pushStreamMode2 = pushStreamMode;
                }
            }
            if (AirIndexEnum.THREE.equals(airIndexEnum)) {
                if(Objects.nonNull(pushStreamMode)) {
                    this.pushStreamMode3 = pushStreamMode;
                }
            }
        }
    }


}
