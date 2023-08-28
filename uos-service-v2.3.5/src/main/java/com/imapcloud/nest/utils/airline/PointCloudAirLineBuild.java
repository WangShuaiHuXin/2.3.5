package com.imapcloud.nest.utils.airline;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.enums.FocalModeEnum;
import com.imapcloud.nest.utils.DistanceUtil;
import com.imapcloud.nest.utils.JacksonUtil;
import com.imapcloud.sdk.pojo.constant.ActionTypeEnum;
import com.imapcloud.sdk.pojo.constant.HeadingModeEnum;
import com.imapcloud.sdk.pojo.constant.NestTypeEnum;
import com.imapcloud.sdk.pojo.constant.RotateAircraftTurnModeEnum;
import com.imapcloud.sdk.pojo.entity.Waypoint;

import java.util.*;

/**
 * 点云航线构建
 *
 * @author wmin
 */
public class PointCloudAirLineBuild implements IAirLineBuild {
    private final static int PHOTO_POINT = 0;
    private final static int ASSIST_POINT = 1;
    private final static int NO_ACTION_POINT = 2;

    @Override
    public List<Waypoint> buildAirLine(AirLineParams param) {

        JSONArray waypointArray = param.getWaypointArray();
        if (waypointArray == null) {
            String airLineJson = param.getAirLineJson();
            waypointArray = JSONArray.parseArray(airLineJson);
        }

        Deque<Waypoint> waypointDeque = new ArrayDeque<>();

        JSONObject point0 = waypointArray.getJSONObject(0);

        /**
         *  设置起飞点，如果是mini机巢(没有RTK)，起飞点高度是绝对高度，如果是固定机巢（有RTK）,高度是相对高度，则需要加入当前机巢得海拔高度
         */
        double startStopAlt = param.getStartStopAlt();

        Waypoint waypoint0 = setLngLatAltSpeed(point0, param.getWaypointSpeed());
        pointSetHeading(waypoint0, point0.getDouble("heading"), param.getHeadingMode());
        waypoint0.setWayPointAltitude(startStopAlt);
        waypoint0.addAction(ActionTypeEnum.GIMBAL_PITCH, 0);
        waypointDeque.add(waypoint0);

        //设置第一个航点
        JSONObject point1 = waypointArray.getJSONObject(0);
        Integer speed1 = point1.getInteger("speed");
        Waypoint waypoint1 = setLngLatAltSpeed(point1, speed1 != null && speed1 != 0 ? speed1 : param.getWaypointSpeed());
        pointSetHeading(waypoint1, point1.getDouble("heading"), param.getHeadingMode());
        pointAddAction(waypoint1, point1, param.getFocalLengthMin(), param.getFocalMode(), param.getHeadingMode());
        pointSetTurnMode(waypoint1,point1.getString("turnMode"));
        String byname1 = point1.getString("byname");
        String photoPropList1 = point1.getString("photoPropList");
        setBynameAndPhotoPropList(waypoint1, byname1, photoPropList1);
        waypointDeque.add(waypoint1);

        for (int i = 1; i < waypointArray.size(); i++) {
            JSONObject point = waypointArray.getJSONObject(i);
            Double speed = point.getDouble("speed");
            Waypoint waypoint = setLngLatAltSpeed(point, speed != null && speed != 0 ? speed.intValue() : param.getWaypointSpeed());
            pointSetHeading(waypoint, point.getDouble("heading"), param.getHeadingMode());
            pointSetTurnMode(waypoint,point.getString("turnMode"));
            //获取列表中的最后一个点进行比较
            Waypoint waypointLast = waypointDeque.removeLast();
            //判断距离是否大于0.6，如果大于0.6，则把新的点添加到航点列表，否则把新的航点动作添加到
            if (DistanceUtil.checkWaypointDistance(waypoint, waypointLast)) {
                waypointDeque.addLast(waypointLast);
                pointAddAction(waypoint, point, param.getFocalLengthMin(), param.getFocalMode(), param.getHeadingMode());
                String byname = point.getString("byname");
                String photoPropList = point.getString("photoPropList");
                setBynameAndPhotoPropList(waypoint, byname, photoPropList);
                waypointDeque.addLast(waypoint);

            } else {
                //在末尾航点添加动作
                pointAddAction(waypointLast, point, param.getFocalLengthMin(), param.getFocalMode(), param.getHeadingMode());
                String byname = point.getString("byname");
                String photoPropList = point.getString("photoPropList");
                setBynameAndPhotoPropList(waypointLast, byname, photoPropList);
                waypointDeque.addLast(waypointLast);
            }

        }

        //设置降落点
        JSONObject pointLast = waypointArray.getJSONObject(waypointArray.size() - 1);
        Waypoint waypointStop = setLngLatAltSpeed(pointLast, param.getWaypointSpeed());
        pointSetHeading(waypointStop, pointLast.getDouble("heading"), param.getHeadingMode());
        waypointStop.setWayPointAltitude(startStopAlt);
        waypointStop.addAction(ActionTypeEnum.GIMBAL_PITCH, 0);
        String bynameLast = pointLast.getString("byname");
        String photoPropListLast = pointLast.getString("photoPropList");
        setBynameAndPhotoPropList(waypointStop, bynameLast, photoPropListLast);
        waypointDeque.addLast(waypointStop);

        return new ArrayList<>(waypointDeque);
    }

    private Waypoint setLngLatAltSpeed(JSONObject point, double speed) {
        Double lat = point.getDouble("aircraftLocationLatitude");
        Double lng = point.getDouble("aircraftLocationLongitude");
        Double alt = point.getDouble("aircraftLocationAltitude");
        return new Waypoint(lat, lng, alt, speed);
    }

    /**
     * 给航点添加动作
     *
     * @param waypoint
     * @param point
     */
    private void pointAddAction(Waypoint waypoint, JSONObject point, Double focalLengthMin, FocalModeEnum focalMode, HeadingModeEnum headingMode) {
        if (NO_ACTION_POINT == point.getInteger("waypointType")) {
            return;
        }

        if (point.getInteger("focalLength") != null && point.getInteger("focalLength") > 0) {
            waypoint.addAction(ActionTypeEnum.CAMERA_ZOOM, point.getInteger("focalLength"));
        }

        //如果不是USING_WAYPOINT_HEADING模式，则不添加旋转机头动作
        if (!HeadingModeEnum.USING_WAYPOINT_HEADING.equals(headingMode)) {
            waypoint.addAction(ActionTypeEnum.ROTATE_AIRCRAFT, point.getInteger("aircraftYaw"));
        }

        Double gimbalPitch = point.getDouble("gimbalPitch");
        int gimbalPitchInt = gimbalPitch == null ? 0 : gimbalPitch.intValue();
        int newGimbalPitch = gimbalPitchInt <= 0 && gimbalPitchInt >= -90 ? gimbalPitchInt : gimbalPitchInt - 90;
        waypoint.addAction(ActionTypeEnum.GIMBAL_PITCH, newGimbalPitch);

        if (PHOTO_POINT == point.getInteger("waypointType")) {
            waypoint.addAction(ActionTypeEnum.START_TAKE_PHOTO, point.getInteger("waypointType"));
        } else if (ASSIST_POINT == point.getInteger("waypointType")) {
            waypoint.addAction(ActionTypeEnum.STAY, 1000);
        }

    }


    private void setBynameAndPhotoPropList(Waypoint waypoint, String byname, String photoPropList) {
        if (StrUtil.isNotBlank(byname)) {
            waypoint.setByname(byname);
        }
        if (StrUtil.isNotBlank(photoPropList)) {
            waypoint.setPhotoPropList(JacksonUtil.json2Array(photoPropList, List.class, Integer.class));
        }

    }

    private void pointSetHeading(Waypoint waypoint, Double heading, HeadingModeEnum headingMode) {
        if (HeadingModeEnum.USING_WAYPOINT_HEADING.equals(headingMode)) {
            if (heading != null) {
                waypoint.setHeading(heading);
            }
        }
    }

    private void pointSetTurnMode(Waypoint waypoint, String turnModeStr) {
        if (turnModeStr != null) {
            RotateAircraftTurnModeEnum turnMode = RotateAircraftTurnModeEnum.valueOf(turnModeStr);
            waypoint.setTurnMode(turnMode);
        }
    }
}
