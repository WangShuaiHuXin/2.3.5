package com.imapcloud.sdk.pojo.entity;


import com.imapcloud.sdk.pojo.constant.FinishActionEnum;
import com.imapcloud.sdk.pojo.constant.FlightPathModeEnum;
import com.imapcloud.sdk.pojo.constant.GoFirstWaypointModeEnum;
import com.imapcloud.sdk.pojo.constant.HeadingModeEnum;

import java.util.List;

/**
 * @author wmin
 * 任务类，封装任务
 */
public class Mission {
    private String missionID;
    private String name;
    private int autoFlightSpeed;
    private GoFirstWaypointModeEnum gotoFirstWaypointMode;
    private FinishActionEnum finishAction;
    private HeadingModeEnum headingMode;
    private FlightPathModeEnum flightPathMode;
    /**
     * 指定的执行时间，如果值为null,就表示任务立即开始，推荐设置值为null
     */
    private String executeTime;

    /**
     * 是否是相对飞机的起飞点高度飞行
     */
    private Boolean relativeAltitude;

    /**
     * 非RTK基站是否飞绝对海拔高度任务
     */
    private Boolean useHomeSeaLevelInRtkUnable;

    /**
     * 环绕兴趣点纬度，设置了该数值机头回一直指向该坐标，仅当航线模式为4时有效
     */
    private Double poiLatitude;
    /**
     * 环绕兴趣点纬度，设置了该数值机头回一直指向该坐标，仅当航线模式为4时有效
     */
    private Double poiLongitude;

    /**
     * 是否为航点任务V2【仅支持G900基站】
     */
    private Boolean intelligentMission;

    private List<Waypoint> mission;


    public String getMissionID() {
        return missionID;
    }

    public void setMissionID(String missionID) {
        this.missionID = missionID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAutoFlightSpeed() {
        return autoFlightSpeed;
    }

    public void setAutoFlightSpeed(int autoFlightSpeed) {
        this.autoFlightSpeed = autoFlightSpeed;
    }

    public GoFirstWaypointModeEnum getGotoFirstWaypointMode() {
        return gotoFirstWaypointMode;
    }

    public void setGotoFirstWaypointMode(GoFirstWaypointModeEnum gotoFirstWaypointMode) {
        this.gotoFirstWaypointMode = gotoFirstWaypointMode;
    }

    public FinishActionEnum getFinishAction() {
        return finishAction;
    }

    public void setFinishAction(FinishActionEnum finishAction) {
        this.finishAction = finishAction;
    }

    public HeadingModeEnum getHeadingMode() {
        return headingMode;
    }

    public void setHeadingMode(HeadingModeEnum headingMode) {
        this.headingMode = headingMode;
    }

    public FlightPathModeEnum getFlightPathMode() {
        return flightPathMode;
    }

    public void setFlightPathMode(FlightPathModeEnum flightPathMode) {
        this.flightPathMode = flightPathMode;
    }

    public String getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(String executeTime) {
        this.executeTime = executeTime;
    }

    public List<Waypoint> getMission() {
        return mission;
    }

    public void setMission(List<Waypoint> mission) {
        this.mission = mission;
    }

    public Boolean getRelativeAltitude() {
        return relativeAltitude;
    }

    public void setRelativeAltitude(Boolean relativeAltitude) {
        this.relativeAltitude = relativeAltitude;
    }

    public Boolean getUseHomeSeaLevelInRtkUnable() {
        return useHomeSeaLevelInRtkUnable;
    }

    public void setUseHomeSeaLevelInRtkUnable(Boolean useHomeSeaLevelInRtkUnable) {
        this.useHomeSeaLevelInRtkUnable = useHomeSeaLevelInRtkUnable;
    }

    public Double getPoiLatitude() {
        return poiLatitude;
    }

    public void setPoiLatitude(Double poiLatitude) {
        this.poiLatitude = poiLatitude;
    }

    public Double getPoiLongitude() {
        return poiLongitude;
    }

    public void setPoiLongitude(Double poiLongitude) {
        this.poiLongitude = poiLongitude;
    }

    public Boolean getIntelligentMission() {
        return intelligentMission;
    }

    public void setIntelligentMission(Boolean intelligentMission) {
        this.intelligentMission = intelligentMission;
    }

    @Override
    public String toString() {
        return "Mission{" +
                "missionID='" + missionID + '\'' +
                ", name='" + name + '\'' +
                ", autoFlightSpeed=" + autoFlightSpeed +
                ", gotoFirstWaypointMode=" + gotoFirstWaypointMode +
                ", finishAction=" + finishAction +
                ", headingMode=" + headingMode +
                ", flightPathMode=" + flightPathMode +
                ", executeTime='" + executeTime + '\'' +
                ", relativeAltitude=" + relativeAltitude +
                ", useHomeSeaLevelInRtkUnable=" + useHomeSeaLevelInRtkUnable +
                ", poiLatitude=" + poiLatitude +
                ", poiLongitude=" + poiLongitude +
                ", mission=" + mission +
                '}';
    }
}
