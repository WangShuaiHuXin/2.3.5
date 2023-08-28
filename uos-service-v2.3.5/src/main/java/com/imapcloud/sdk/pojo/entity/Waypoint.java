package com.imapcloud.sdk.pojo.entity;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.annotation.JSONField;
import com.imapcloud.sdk.pojo.constant.ActionTypeEnum;
import com.imapcloud.sdk.pojo.constant.RotateAircraftTurnModeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wmin
 * 航点类
 */
public class Waypoint {
    //航点纬度
    private Double wayPointLatitude;
    //航点经度
    private Double wayPointLongitude;
    //航点海拔高度
    private Double wayPointAltitude;
    //航点速度
    private Double wayPointSpeed;
    //航点行动列表
    private String wayPointAction;
    //航点行动参数
    private String wayPointActionParam;

    //航点的动作（动作类型和动作参数）
    @JSONField(serialize = false)
    private List<WaypointAction> waypointActionList;

    //航点航向
    private Double heading;

    //拍照时间间隔
    private Float shootPhotoTimeInterval;

    //拍照距离间隔
    private Float shootPhotoDistanceInterval;

    /**
     * 旋转半径，对应曲线飞行模式
     */
    private Double cornerRadiusInMeters;

    /**
     * 转机头方式
     */
    private RotateAircraftTurnModeEnum turnMode;


    @JSONField(serialize = false)
    private String byname;

    @JSONField(serialize = false)
    private List<Integer> photoPropList;

    @JSONField(serialize = false)
    private List<String> bynameList;

    public Waypoint() {
        this.waypointActionList = new ArrayList<>();
    }

    public Waypoint(Double wayPointLatitude, Double wayPointLongitude, Double wayPointAltitude, Double wayPointSpeed) {
        this.wayPointLatitude = wayPointLatitude;
        this.wayPointLongitude = wayPointLongitude;
        this.wayPointAltitude = wayPointAltitude;
        this.wayPointSpeed = wayPointSpeed;
        this.waypointActionList = new ArrayList<>();
    }
    public Waypoint(Double wayPointLatitude, Double wayPointLongitude) {
        this.wayPointLatitude = wayPointLatitude;
        this.wayPointLongitude = wayPointLongitude;
        this.waypointActionList = new ArrayList<>();
    }
    public Waypoint(Double wayPointLatitude, Double wayPointLongitude, Double wayPointAltitude) {
        this.wayPointLatitude = wayPointLatitude;
        this.wayPointLongitude = wayPointLongitude;
        this.wayPointAltitude = wayPointAltitude;
        this.waypointActionList = new ArrayList<>();
    }

    public Double getWayPointLatitude() {
        return wayPointLatitude;
    }

    public void setWayPointLatitude(Double wayPointLatitude) {
        this.wayPointLatitude = wayPointLatitude;
    }

    public Double getWayPointLongitude() {
        return wayPointLongitude;
    }

    public void setWayPointLongitude(Double wayPointLongitude) {
        this.wayPointLongitude = wayPointLongitude;
    }

    public Double getWayPointAltitude() {
        return wayPointAltitude;
    }

    public void setWayPointAltitude(Double wayPointAltitude) {
        this.wayPointAltitude = wayPointAltitude;
    }

    public Double getWayPointSpeed() {
        return wayPointSpeed;
    }

    public void setWayPointSpeed(Double wayPointSpeed) {
        this.wayPointSpeed = wayPointSpeed;
    }

    public List<WaypointAction> getWaypointActionList() {
        return waypointActionList;
    }

    public void setWaypointActionList(List<WaypointAction> waypointActionList) {
        this.waypointActionList = waypointActionList;
    }

    public Double getHeading() {
        return heading;
    }

    public void setHeading(Double heading) {
        this.heading = heading;
    }

    public Float getShootPhotoTimeInterval() {
        return shootPhotoTimeInterval;
    }

    public void setShootPhotoTimeInterval(Float shootPhotoTimeInterval) {
        this.shootPhotoTimeInterval = shootPhotoTimeInterval;
    }

    public Float getShootPhotoDistanceInterval() {
        return shootPhotoDistanceInterval;
    }

    public void setShootPhotoDistanceInterval(Float shootPhotoDistanceInterval) {
        this.shootPhotoDistanceInterval = shootPhotoDistanceInterval;
    }

    public String getWayPointAction() {
        return wayPointAction;
    }

    public void setWayPointAction(String wayPointAction) {
        this.wayPointAction = wayPointAction;
    }

    public String getWayPointActionParam() {
        return wayPointActionParam;
    }

    public void setWayPointActionParam(String wayPointActionParam) {
        this.wayPointActionParam = wayPointActionParam;
    }

    public String getByname() {
        return byname;
    }

    public void setByname(String byname) {
        this.byname = byname;
    }

    public List<String> getBynameList() {
        return bynameList;
    }

    public void setBynameList(List<String> bynameList) {
        this.bynameList = bynameList;
    }

    public List<Integer> getPhotoPropList() {
        return photoPropList;
    }

    public void setPhotoPropList(List<Integer> photoPropList) {
        this.photoPropList = photoPropList;
    }

    public Double getCornerRadiusInMeters() {
        return cornerRadiusInMeters;
    }

    public void setCornerRadiusInMeters(Double cornerRadiusInMeters) {
        this.cornerRadiusInMeters = cornerRadiusInMeters;
    }

    public RotateAircraftTurnModeEnum getTurnMode() {
        return turnMode;
    }

    public void setTurnMode(RotateAircraftTurnModeEnum turnMode) {
        this.turnMode = turnMode;
    }

    public Waypoint addAction(ActionTypeEnum actionTypeEnum, Integer actionParam) {
        WaypointAction wa = new WaypointAction();
        wa.setActionType(actionTypeEnum);
        wa.setActionParam(actionParam);
        this.waypointActionList.add(wa);
        return this;
    }

    public Waypoint addActions(List<WaypointAction> waypointActionList) {
        if (CollectionUtil.isNotEmpty(waypointActionList)) {
            if (this.getWaypointActionList() == null) {
                this.setWaypointActionList(new ArrayList<>());
            }
            this.getWaypointActionList().addAll(waypointActionList);
        }
        return this;
    }


    @Override
    public String toString() {
        return "Waypoint{" +
                "wayPointLatitude=" + wayPointLatitude +
                ", wayPointLongitude=" + wayPointLongitude +
                ", wayPointAltitude=" + wayPointAltitude +
                ", wayPointSpeed=" + wayPointSpeed +
                ", actionList=" + waypointActionList +
                ", heading=" + heading +
                ", shootPhotoTimeInterval=" + shootPhotoTimeInterval +
                ", shootPhotoDistanceInterval=" + shootPhotoDistanceInterval +
                '}';
    }
}
