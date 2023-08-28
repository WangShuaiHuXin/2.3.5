package com.imapcloud.nest.utils.airline;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.imapcloud.nest.enums.WaypointTypeEnum;
import com.imapcloud.nest.utils.DistanceUtil;
import com.imapcloud.nest.utils.airline.enums.PointCloudWaypointTypeEnum;
import com.imapcloud.sdk.pojo.entity.Waypoint;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;

/**
 * @author wmin
 */
public class ComputePcWaypointUtil {
    private final static int DEFAULT_SPEED = 4;

    /**
     * json转换成java类
     *
     * @param routeJsonArrayStr
     * @return
     */
    public static List<PointCloudWaypoint> jsonArray2OpcList(String routeJsonArrayStr) {
        if (routeJsonArrayStr == null) {
            return Collections.emptyList();
        }
        try {
            return JSONArray.parseArray(routeJsonArrayStr, PointCloudWaypoint.class);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public static List<PointCloudWaypoint> jsonArray2OpcList(JSONArray jsonArray) {
        return jsonArray == null ? Collections.emptyList() : jsonArray2OpcList(jsonArray.toJSONString());
    }

    /**
     * 计算航线长度，航点数量，拍照动作
     *
     * @param pcList
     * @return
     */
    public static Res compute(List<PointCloudWaypoint> pcList) {
        if (CollectionUtil.isNotEmpty(pcList)) {
            double routeLength = 0.0;
            int photoActionCount = 0;
            int tooCloseCount = 0; //距离过近点
            double routeMaxAlt = Double.MIN_VALUE;
            for (int i = 0; i < pcList.size() - 1; i++) {
                PointCloudWaypoint pc1 = pcList.get(i);
                PointCloudWaypoint pc2 = pcList.get(i + 1);
                if (routeMaxAlt < pc1.getAircraftLocationAltitude()) {
                    routeMaxAlt = pc1.getAircraftLocationAltitude();
                }

                if (!DistanceUtil.checkPcWaypointDistance(pc1, pc2)) {
                    tooCloseCount += 1;
                }

                if (PointCloudWaypointTypeEnum.PHOTO_POINT.getValue() == pc1.getWaypointType()) {
                    photoActionCount += 1;
                }
                routeLength += DistanceUtil.getMercatorDistanceViaLonLat(pc1.getAircraftLocationLongitude(), pc1.getAircraftLocationLatitude(), pc2.getAircraftLocationLongitude(), pc2.getAircraftLocationLatitude());
            }
            if (routeMaxAlt < pcList.get(pcList.size() - 1).getAircraftLocationAltitude()) {
                routeMaxAlt = pcList.get(pcList.size() - 1).getAircraftLocationAltitude();
            }
            Res res = new Res().setRouteLength(routeLength)
                    .setPhotoActionCount(photoActionCount)
                    .setPointCount(pcList.size() - tooCloseCount)
                    .setRouteMaxAlt(routeMaxAlt);

            return res;
        }
        return new Res();
    }

    public static List<PointCloudWaypoint> handleByNameAndSpeed(List<PointCloudWaypoint> pcwList) {
        if (CollectionUtil.isNotEmpty(pcwList)) {
            for (PointCloudWaypoint pcw : pcwList) {
                if (pcw.getSpeed() == null || pcw.getSpeed() <= 0) {
                    pcw.setSpeed(DEFAULT_SPEED);
                }
                if (pcw.getWaypointType() == WaypointTypeEnum.PHOTO_POINT.getValue()) {
                    List<PointCloudWaypoint.PhotoPosition> photoPositionList = pcw.getPhotoPositionList();
                    if (CollectionUtil.isNotEmpty(photoPositionList)) {
                        PointCloudWaypoint.PhotoPosition photoPosition = photoPositionList.get(0);
                        String saveName = photoPosition.getSaveName();
                        if (saveName != null) {
                            String[] split = saveName.split("-");
                            if (split.length == 4) {
                                String byname = split[1] + "_" + split[2] + "_" + split[0] + "_" + split[3];
                                pcw.setByname(byname);
                            }
                        }
                    }
                }
            }
            return pcwList;
        }
        return Collections.emptyList();
    }

    public static List<Waypoint> pcRoute2nestRoute(List<PointCloudWaypoint> pcwList) {

        return Collections.emptyList();
    }

    @Data
    @Accessors(chain = true)
    public static class NestCoordinate {
        private Double lat = 0.0;
        private Double lng = 0.0;
        private Double alt = 0.0;
    }


    @Data
    @Accessors(chain = true)
    public static class Res {
        /**
         * 航点数量
         */
        private Integer pointCount = 0;
        /**
         * 拍照动作数
         */
        private Integer photoActionCount = 0;
        /**
         * 航线总长度
         */
        private Double routeLength = 0.0;

        /**
         * 航线最高点高度
         */
        private Double routeMaxAlt = 0.0;
    }
}
