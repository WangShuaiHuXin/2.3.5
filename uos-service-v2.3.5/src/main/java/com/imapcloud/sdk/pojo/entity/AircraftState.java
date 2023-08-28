package com.imapcloud.sdk.pojo.entity;

import com.imapcloud.sdk.pojo.constant.FlightModeEnum;
import com.imapcloud.sdk.pojo.constant.GoHomeStateEnum;

import java.util.Collections;
import java.util.List;

/**
 * @author wmin
 * 飞机状态
 */
public class AircraftState {

    private Long updateTimes;

    /**
     * 飞机海拔
     */
    private Double aircraftAltitude;
    /**
     * 初始机头方向
     */
    private Double aircraftBaseHeadDirection;
    /**
     * 下行信号强度
     */
    private Integer aircraftDownLinkSignal;

    /**
     * 上行信号强度
     */
    private Integer aircraftUpLinkSignal;
    /**
     * 飞机飞行多少秒
     */
    private Long aircraftFlyInSecond;
    /**
     * 飞机返航高度
     */
    private Double aircraftGoHomeHeight;
    /**
     * 飞机垂直速度
     */
    private Double aircraftHSpeed;
    /**
     *
     */
    private Double aircraftVSpeed;
    /**
     * 飞机航向
     */
    private Double aircraftHeadDirection;
    /**
     * 飞机位置纬度
     */
    private Double aircraftLocationLatitude;
    /**
     * 飞机位置经度
     */
    private Double aircraftLocationLongitude;
    /**
     * 飞机俯仰
     */
    private Double aircraftPitch;

    /**
     * 飞机旋转
     */
    private Double aircraftRoll;

    /**
     * 飞机视频信号
     */
    private Integer aircraftVideoSignal;
    /**
     * 飞机旋转角度
     */
    private Double aircraftYaw;
    /**
     * 飞行模式
     */
    private FlightModeEnum flightMode;
    /**
     * 返航模式
     */
    private GoHomeStateEnum goHomeState;
    /**
     * home海拔
     */
    private Double homeAltitude;
    /**
     * home位置纬度
     */
    private Double homeLocationLatitude;
    /**
     * home位置经度
     */
    private Double homeLocationLongitude;
    /**
     * 罗盘是否异常
     */
    private Boolean isCompassError;
    /**
     * 卫星数量
     */
    private Integer satelliteCount;
    /**
     * 是否旋转桨叶
     */
    private Boolean areMotorsOn;
    /**
     * 诊断消息
     */
    private List<String> diagnostics;
    /**
     * 飞机超声高度
     */
    private Double aircraftUltrasonicHeightInMeters;

    /**
     * 飞机是否推流
     */
    private Boolean isLiveStreaming;

    /**
     * 推流消息
     */
    private LiveStreamInfo liveStreamInfo;

    private ChannelInterferenceState channelInterferenceState;

    /**
     * 飞机实时位置到home点的距离
     */
    private Double distance;

    /**
     * 飞行提示信息
     */
    private FlightMessage flightMessage = new FlightMessage();

    private Boolean isBackupLanding;

    private Boolean landedOnBackUpPoint = false;

    private Boolean flying = false;

    private AirLinkInfo airLinkInfo = new AirLinkInfo();

    public AircraftState() {
        this.updateTimes = 0L;
        this.aircraftAltitude = 0.0D;
        this.aircraftBaseHeadDirection = 0.0D;
        this.aircraftDownLinkSignal = 0;
        this.aircraftFlyInSecond = 0L;
        this.aircraftGoHomeHeight = 0.0D;
        this.aircraftHSpeed = 0.0D;
        this.aircraftHeadDirection = 0.0D;
        this.aircraftLocationLatitude = 0.0D;
        this.aircraftLocationLongitude = 0.0D;
        this.aircraftPitch = 0.0D;
        this.aircraftRoll = 0.0D;
        this.aircraftUpLinkSignal = 0;
        this.aircraftVSpeed = 0.0D;
        this.aircraftVideoSignal = 0;
        this.aircraftYaw = 0.0D;
        this.flightMode = FlightModeEnum.UNKNOWN;
        this.goHomeState = GoHomeStateEnum.UNKNOWN;
        this.homeAltitude = 0.0D;
        this.homeLocationLatitude = 0.0D;
        this.homeLocationLongitude = 0.0D;
        this.isCompassError = false;
        this.satelliteCount = 0;
        this.areMotorsOn = false;
        this.diagnostics = Collections.emptyList();
        this.aircraftUltrasonicHeightInMeters = 0.0D;
        this.isLiveStreaming = false;
        this.liveStreamInfo = new LiveStreamInfo();
        this.channelInterferenceState = new ChannelInterferenceState();
        this.distance = 0.0;
    }

    public Long getUpdateTimes() {
        return updateTimes;
    }

    public void setUpdateTimes(Long updateTimes) {
        if (updateTimes != null) {
            this.updateTimes = updateTimes;
        }
    }

    public Boolean getIsLiveStreaming() {
        return isLiveStreaming;
    }

    public void setIsLiveStreaming(Boolean isLiveStreaming) {
        if (isLiveStreaming != null) {
            this.isLiveStreaming = isLiveStreaming;
        }

    }

    public Double getAircraftAltitude() {
        return this.aircraftAltitude;
    }

    public void setAircraftAltitude(Double aircraftAltitude) {
        if (aircraftAltitude != null) {
            this.aircraftAltitude = aircraftAltitude;
        }

    }

    public Double getAircraftBaseHeadDirection() {
        return this.aircraftBaseHeadDirection;
    }

    public void setAircraftBaseHeadDirection(Double aircraftBaseHeadDirection) {
        if (aircraftBaseHeadDirection != null) {
            this.aircraftBaseHeadDirection = aircraftBaseHeadDirection;
        }

    }

    public Integer getAircraftDownLinkSignal() {
        return this.aircraftDownLinkSignal;
    }

    public void setAircraftDownLinkSignal(Integer aircraftDownLinkSignal) {
        if (aircraftDownLinkSignal != null) {
            this.aircraftDownLinkSignal = aircraftDownLinkSignal;
        }

    }

    public Long getAircraftFlyInSecond() {
        return this.aircraftFlyInSecond;
    }

    public void setAircraftFlyInSecond(Long aircraftFlyInSecond) {
        if (aircraftFlyInSecond != null) {
            this.aircraftFlyInSecond = aircraftFlyInSecond;
        }

    }

    public Double getAircraftGoHomeHeight() {
        return this.aircraftGoHomeHeight;
    }

    public void setAircraftGoHomeHeight(Double aircraftGoHomeHeight) {
        if (aircraftGoHomeHeight != null) {
            this.aircraftGoHomeHeight = aircraftGoHomeHeight;
        }

    }

    public Double getAircraftHSpeed() {
        return this.aircraftHSpeed;
    }

    public void setAircraftHSpeed(Double aircraftHSpeed) {
        if (aircraftHSpeed != null) {
            this.aircraftHSpeed = aircraftHSpeed;
        }

    }

    public Double getAircraftHeadDirection() {
        return this.aircraftHeadDirection;
    }

    public void setAircraftHeadDirection(Double aircraftHeadDirection) {
        if (aircraftHeadDirection != null) {
            this.aircraftHeadDirection = aircraftHeadDirection;
        }

    }

    public Double getAircraftLocationLatitude() {
        return this.aircraftLocationLatitude;
    }

    public void setAircraftLocationLatitude(Double aircraftLocationLatitude) {
        if (aircraftLocationLatitude != null) {
            this.aircraftLocationLatitude = aircraftLocationLatitude;
        }

    }

    public Double getAircraftLocationLongitude() {
        return this.aircraftLocationLongitude;
    }

    public void setAircraftLocationLongitude(Double aircraftLocationLongitude) {
        if (aircraftLocationLongitude != null) {
            this.aircraftLocationLongitude = aircraftLocationLongitude;
        }

    }

    public Double getAircraftPitch() {
        return this.aircraftPitch;
    }

    public void setAircraftPitch(Double aircraftPitch) {
        if (aircraftPitch != null) {
            this.aircraftPitch = aircraftPitch;
        }

    }

    public Double getAircraftRoll() {
        return this.aircraftRoll;
    }

    public void setAircraftRoll(Double aircraftRoll) {
        if (aircraftRoll != null) {
            this.aircraftRoll = aircraftRoll;
        }

    }

    public Integer getAircraftUpLinkSignal() {
        return this.aircraftUpLinkSignal;
    }

    public void setAircraftUpLinkSignal(Integer aircraftUpLinkSignal) {
        if (aircraftUpLinkSignal != null) {
            this.aircraftUpLinkSignal = aircraftUpLinkSignal;
        }

    }

    public Double getAircraftVSpeed() {
        return this.aircraftVSpeed;
    }

    public void setAircraftVSpeed(Double aircraftVSpeed) {
        if (aircraftVSpeed != null) {
            this.aircraftVSpeed = aircraftVSpeed;
        }

    }

    public Integer getAircraftVideoSignal() {
        return this.aircraftVideoSignal;
    }

    public void setAircraftVideoSignal(Integer aircraftVideoSignal) {
        if (aircraftVideoSignal != null) {
            this.aircraftVideoSignal = aircraftVideoSignal;
        }

    }

    public Double getAircraftYaw() {
        return this.aircraftYaw;
    }

    public void setAircraftYaw(Double aircraftYaw) {
        if (aircraftYaw != null) {
            this.aircraftYaw = aircraftYaw;
        }

    }

    public FlightModeEnum getFlightMode() {
        return this.flightMode;
    }

    public void setFlightMode(FlightModeEnum flightMode) {
        if (flightMode != null) {
            this.flightMode = flightMode;
        }

    }

    public Boolean getBackupLanding() {
        return isBackupLanding;
    }

    public void setBackupLanding(Boolean backupLanding) {
        isBackupLanding = backupLanding;
    }

    public GoHomeStateEnum getGoHomeState() {
        return this.goHomeState;
    }

    public void setGoHomeState(GoHomeStateEnum goHomeState) {
        if (goHomeState != null) {
            this.goHomeState = goHomeState;
        }

    }

    public Double getHomeAltitude() {
        return this.homeAltitude;
    }

    public void setHomeAltitude(Double homeAltitude) {
        if (homeAltitude != null) {
            this.homeAltitude = homeAltitude;
        }

    }

    public Double getHomeLocationLatitude() {
        return this.homeLocationLatitude;
    }

    public void setHomeLocationLatitude(Double homeLocationLatitude) {
        if (homeLocationLatitude != null) {
            this.homeLocationLatitude = homeLocationLatitude;
        }

    }

    public Double getHomeLocationLongitude() {
        return this.homeLocationLongitude;
    }

    public void setHomeLocationLongitude(Double homeLocationLongitude) {
        if (homeLocationLongitude != null) {
            this.homeLocationLongitude = homeLocationLongitude;
        }

    }

    public Boolean getIsCompassError() {
        return this.isCompassError;
    }

    public void setIsCompassError(Boolean isCompassError) {
        if (isCompassError != null) {
            this.isCompassError = isCompassError;
        }

    }

    public Integer getSatelliteCount() {
        return this.satelliteCount;
    }

    public void setSatelliteCount(Integer satelliteCount) {
        if (satelliteCount != null) {
            this.satelliteCount = satelliteCount;
        }

    }

    public Boolean getAreMotorsOn() {
        return this.areMotorsOn;
    }

    public void setAreMotorsOn(Boolean areMotorsOn) {
        if (areMotorsOn != null) {
            this.areMotorsOn = areMotorsOn;
        }

    }

    public List<String> getDiagnostics() {
        return this.diagnostics;
    }

    public void setDiagnostics(List<String> diagnostics) {
        if (diagnostics != null) {
            this.diagnostics = diagnostics;
        }

    }

    public Double getAircraftUltrasonicHeightInMeters() {
        return this.aircraftUltrasonicHeightInMeters;
    }

    public void setAircraftUltrasonicHeightInMeters(Double aircraftUltrasonicHeightInMeters) {
        if (aircraftUltrasonicHeightInMeters != null) {
            this.aircraftUltrasonicHeightInMeters = aircraftUltrasonicHeightInMeters;
        }

    }

    public LiveStreamInfo getLiveStreamInfo() {
        return liveStreamInfo;
    }

    public void setLiveStreamInfo(LiveStreamInfo liveStreamInfo) {
        if (liveStreamInfo != null) {
            this.liveStreamInfo = liveStreamInfo;
        }
    }

    public ChannelInterferenceState getChannelInterferenceState() {
        return channelInterferenceState;
    }

    public void setChannelInterferenceState(ChannelInterferenceState channelInterferenceState) {
        if (channelInterferenceState != null) {
            this.channelInterferenceState = channelInterferenceState;
        }
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        if (distance != null) {
            this.distance = distance;
        }
    }

    public Boolean getCompassError() {
        return isCompassError;
    }

    public void setCompassError(Boolean compassError) {
        isCompassError = compassError;
    }

    public FlightMessage getFlightMessage() {
        return flightMessage;
    }

    public void setFlightMessage(FlightMessage flightMessage) {
        this.flightMessage = flightMessage;
    }



    public Boolean getLandedOnBackUpPoint() {
        return landedOnBackUpPoint;
    }

    public void setLandedOnBackUpPoint(Boolean landedOnBackUpPoint) {
        this.landedOnBackUpPoint = landedOnBackUpPoint;
    }

    public Boolean getFlying() {
        return flying;
    }

    public void setFlying(Boolean flying) {
        if (flying != null) {
            this.flying = flying;
        }
    }

    public AirLinkInfo getAirLinkInfo() {
        return airLinkInfo;
    }

    public void setAirLinkInfo(AirLinkInfo airLinkInfo) {
        if(airLinkInfo != null) {
            this.airLinkInfo = airLinkInfo;
        }
    }
}
