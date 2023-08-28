package com.imapcloud.nest.utils.airline;

import com.alibaba.fastjson.JSONArray;
import com.imapcloud.nest.enums.FocalModeEnum;
import com.imapcloud.sdk.pojo.constant.HeadingModeEnum;

/**
 * 航线解析参数类
 *
 * @author wmin
 */
public class AirLineParams {
    private String airLineJson;
    private Integer airLineType;
    /**
     * 机巢类型，0-固定机巢，1-mini机巢
     */
    private Integer nestType;
    private Double startStopAlt;
    private Double waypointSpeed;
    private Double nestAltitude;


    private JSONArray waypointArray;

    /**
     * 是否使用航点自有的速度（编辑的航线会有速度）
     * 1 - 不给航点添加速度
     * 0 - 给航点添加速度
     */
    private Integer useDefaultSpeed;

    /**
     * 是否使用相对与机巢的高度飞行，如果是，起降点则不用加上机巢的高度
     */
    private Boolean relativeAltitude;

    private Double focalLengthMin;

    private FocalModeEnum focalMode;

    private HeadingModeEnum headingMode;

    public static AirLineParams instance() {
        return new AirLineParams();
    }

    public AirLineParams airLineJson(String airLineJson) {
        this.setAirLineJson(airLineJson);
        return this;
    }

    public AirLineParams airLineType(Integer airLineType) {
        this.setAirLineType(airLineType);
        return this;
    }

    public AirLineParams nestType(Integer nestType) {
        this.setNestType(nestType);
        return this;
    }

    public AirLineParams startStopAlt(Double startStopAlt) {
        this.setStartStopAlt(startStopAlt);
        return this;
    }

    public AirLineParams waypointSpeed(Double waypointSpeed) {
        this.setWaypointSpeed(waypointSpeed);
        return this;
    }

    public AirLineParams nestAltitude(Double nestAltitude) {
        this.setNestAltitude(nestAltitude);
        return this;
    }

    public AirLineParams waypointArray(JSONArray waypointArray) {
        this.setWaypointArray(waypointArray);
        return this;
    }

    public AirLineParams useDefaultSpeed(Integer defaultSpeed) {
        this.setUseDefaultSpeed(defaultSpeed);
        return this;
    }

    public AirLineParams focalLengthMin(Double focalLengthMin) {
        this.setFocalLengthMin(focalLengthMin);
        return this;
    }

    public AirLineParams focalMode(FocalModeEnum focalMode) {
        this.setFocalMode(focalMode);
        return this;
    }

    public AirLineParams headingMode(HeadingModeEnum headingMode) {
        this.setHeadingMode(headingMode);
        return this;
    }

//    public AirLineParams relativeAltitude(Boolean relativeAltitude) {
//        this.setRelativeAltitude(relativeAltitude);
//        return this;
//    }

    public String getAirLineJson() {
        return airLineJson;
    }

    public void setAirLineJson(String airLineJson) {
        this.airLineJson = airLineJson;
    }

    public Integer getAirLineType() {
        return airLineType;
    }

    public void setAirLineType(Integer airLineType) {
        this.airLineType = airLineType;
    }

    public Integer getNestType() {
        return nestType;
    }

    public void setNestType(Integer nestType) {
        this.nestType = nestType;
    }

    public Double getStartStopAlt() {
        return startStopAlt;
    }

    public void setStartStopAlt(Double startStopAlt) {
        this.startStopAlt = startStopAlt;
    }

    public Double getWaypointSpeed() {
        return waypointSpeed;
    }

    public void setWaypointSpeed(Double waypointSpeed) {
        this.waypointSpeed = waypointSpeed;
    }

    public Double getNestAltitude() {
        return nestAltitude;
    }

    public void setNestAltitude(Double nestAltitude) {
        this.nestAltitude = nestAltitude;
    }

    public JSONArray getWaypointArray() {
        return waypointArray;
    }

    public void setWaypointArray(JSONArray waypointArray) {
        this.waypointArray = waypointArray;
    }

    public Integer getUseDefaultSpeed() {
        return useDefaultSpeed;
    }

    public void setUseDefaultSpeed(Integer useDefaultSpeed) {
        this.useDefaultSpeed = useDefaultSpeed;
    }

    public Boolean getRelativeAltitude() {
        return relativeAltitude;
    }

    public void setRelativeAltitude(Boolean relativeAltitude) {
        this.relativeAltitude = relativeAltitude;
    }

    public Double getFocalLengthMin() {
        return focalLengthMin;
    }

    public void setFocalLengthMin(Double focalLengthMin) {
        this.focalLengthMin = focalLengthMin;
    }

    public FocalModeEnum getFocalMode() {
        return focalMode;
    }

    public void setFocalMode(FocalModeEnum focalMode) {
        this.focalMode = focalMode;
    }

    public HeadingModeEnum getHeadingMode() {
        return headingMode;
    }

    public void setHeadingMode(HeadingModeEnum headingMode) {
        this.headingMode = headingMode;
    }
}
