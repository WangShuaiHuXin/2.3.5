package com.imapcloud.nest.utils.airline;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.imapcloud.sdk.pojo.constant.ActionTypeEnum;
import com.imapcloud.sdk.pojo.constant.HeadingModeEnum;
import com.imapcloud.sdk.pojo.entity.Waypoint;
import com.imapcloud.sdk.pojo.entity.WaypointAction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by wmin on 2020/11/20 11:50
 *
 * @author wmin
 */
public class WaypointUtil {

    /**
     * 航点列表转换成机巢航线类型json格式
     *
     * @param list
     * @return
     */
    public static String waypointListToJsonStr(List<Waypoint> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            return JSON.toJSONString(transWaypointAction(list));
        }
        return "";
    }

    /**
     * 把动作转换成航点动作
     *
     * @param list
     * @return
     */
    public static List<Waypoint> transWaypointAction(List<Waypoint> list) {
        if (CollectionUtil.isNotEmpty(list)) {
            for (Waypoint wp : list) {
                StringBuilder wayPointAction = new StringBuilder();
                StringBuilder wayPointActionParam = new StringBuilder();
                List<WaypointAction> waypointActionList = wp.getWaypointActionList();
                if (CollectionUtil.isNotEmpty(waypointActionList)) {
                    for (WaypointAction wa : waypointActionList) {
                        wayPointAction.append(",").append(wa.getActionType().getValue());
                        wayPointActionParam.append(",").append(wa.getActionParam());
                    }
                    if (!"".equals(wayPointAction.toString())) {
                        wayPointAction = new StringBuilder(wayPointAction.substring(1));
                    }
                    if (!"".equals(wayPointActionParam.toString())) {
                        wayPointActionParam = new StringBuilder(wayPointActionParam.substring(1));
                    }
                }

                wp.setWayPointAction(wayPointAction.toString());
                wp.setWayPointActionParam(wayPointActionParam.toString());
                wp.setWaypointActionList(null);
            }
            return list;
        }
        return Collections.emptyList();
    }

    /**
     * 安全策略转换效率策略
     *
     * @param waypointList
     * @return
     */
    public static List<Waypoint> safety2efficiency(List<Waypoint> waypointList) {
        //把机头旋转动作去掉
        for (Waypoint wp : waypointList) {
            String wayPointAction = wp.getWayPointAction();
            String wayPointActionParam = wp.getWayPointActionParam();
            if (StrUtil.isNotEmpty(wayPointAction)) {
                String[] actionArray = wayPointAction.split(",");
                String[] paramArray = wayPointActionParam.split(",");
                for (int i = 0; i < actionArray.length; i++) {
                    if (HeadingModeEnum.USING_WAYPOINT_HEADING.getValue().toString().equals(actionArray[i])) {
                        wp.setHeading(Double.valueOf(paramArray[i]));
                        actionArray[i] = null;
                        paramArray[i] = null;
                    }
                }
                String actionStr = Arrays.stream(actionArray).filter(StrUtil::isNotEmpty).collect(Collectors.joining(","));
                String paramStr = Arrays.stream(paramArray).filter(StrUtil::isNotEmpty).collect(Collectors.joining(","));
                wp.setWayPointAction(actionStr);
                wp.setWayPointActionParam(paramStr);
            }
        }
        return waypointList;
    }

}
