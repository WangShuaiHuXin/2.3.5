package com.imapcloud.nest.utils.airline;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.enums.FocalModeEnum;
import com.imapcloud.sdk.pojo.constant.ActionTypeEnum;
import com.imapcloud.sdk.pojo.entity.Waypoint;
import com.imapcloud.sdk.pojo.entity.WaypointAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 基站上航线格式解析
 *
 * @author wmin
 */
public class NestAirLineBuild implements IAirLineBuild {


    @Override
    public List<Waypoint> buildAirLine(AirLineParams param) {

        String airLineJson = param.getAirLineJson();

        List<Waypoint> waypointList = JSONArray.parseArray(airLineJson, Waypoint.class);

        if (CollectionUtil.isEmpty(waypointList)) {
            return Collections.emptyList();
        }

        /**
         * 新的航点列表，包含起降点
         */
        List<Waypoint> newWaypointList = new ArrayList<>(waypointList.size() + 2);
        /**
         *  设置起飞点，如果是mini机巢(没有RTK)，起飞点高度是绝对高度，如果是固定机巢（有RTK）,高度是相对高度，则需要加入当前机巢得海拔高度
         */
        double startStopAlt = param.getStartStopAlt();


        Waypoint waypoint1 = waypointList.get(0);

        Waypoint startWaypoint = new Waypoint();
        startWaypoint.setWayPointLongitude(waypoint1.getWayPointLongitude());
        startWaypoint.setWayPointLatitude(waypoint1.getWayPointLatitude());
        startWaypoint.setWayPointAltitude(startStopAlt);
        startWaypoint.setWayPointSpeed(param.getWaypointSpeed());

        newWaypointList.add(startWaypoint);

        for (int i = 0; i < waypointList.size(); i++) {
            Waypoint waypoint = waypointList.get(i);
            String wayPointActionStr = waypoint.getWayPointAction();
            String wayPointActionParamStr = waypoint.getWayPointActionParam();
            if ("[]".equals(wayPointActionStr) || "[]".equals(wayPointActionParamStr)) {
                continue;
            }
            JSONArray wayPointActionJson = JSONArray.parseArray("[" + wayPointActionStr + "]");
            JSONArray wayPointActionParamJson = JSONArray.parseArray("[" + wayPointActionParamStr + "]");
            List<WaypointAction> waypointActions = new ArrayList<>(wayPointActionJson.size());
            for (int j = 0; j < wayPointActionJson.size(); j++) {
                WaypointAction waypointAction = new WaypointAction();
                waypointAction.setActionType(ActionTypeEnum.getInstance(wayPointActionJson.getInteger(j)));
                waypointAction.setActionParam(wayPointActionParamJson.getInteger(j));
                waypointActions.add(waypointAction);
            }
            waypoint.setWaypointActionList(waypointActions);
        }

        newWaypointList.addAll(waypointList);

        Waypoint waypointSize = waypointList.get(waypointList.size() - 1);
        Waypoint endWaypoint = new Waypoint();
        endWaypoint.setWayPointLongitude(waypointSize.getWayPointLongitude());
        endWaypoint.setWayPointLatitude(waypointSize.getWayPointLatitude());
        endWaypoint.setWayPointAltitude(startStopAlt);
        endWaypoint.setWayPointSpeed(param.getWaypointSpeed());

        newWaypointList.add(endWaypoint);

        return newWaypointList;
    }


}
