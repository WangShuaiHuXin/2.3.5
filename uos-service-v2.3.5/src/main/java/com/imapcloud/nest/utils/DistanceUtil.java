package com.imapcloud.nest.utils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.imapcloud.nest.pojo.LocationMercator;
import com.imapcloud.nest.pojo.dto.FlyPoint;
import com.imapcloud.nest.utils.airline.PointCloudWaypoint;
import com.imapcloud.sdk.pojo.entity.Waypoint;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author wmin
 */
public class DistanceUtil {
    private static final double EARTH_RADIUS = 6378137;

    private static final double RC = 6378137.0D;
    private static final double TOLERANCE = 0.5d;
    private static final String regEx = "([0-9]\\d*\\.?\\d*)|((-)?[0-9]\\d*\\.?\\d*)";

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static double getDistance(double lng1, double lat1, double lng2, double lat2) {
        double x = Math.abs(lng2 - lng1)
                * Math.PI
                * RC
                * Math.cos((lat1 + lat2) / 2.0D * Math.PI / 180.0D)
                / 180.0D;
        double y = Math.abs(lat2 - lat1) * Math.PI * RC / 180.0D;

        return Math.hypot(x, y);
    }

    /**
     * 经纬度转墨卡托
     *
     * @param lon
     * @param lat
     * @return
     */
    static LocationMercator LonLatToMercator(double lon, double lat) {
        double toX = lon * 20037508.34D / 180.0D;
        double toY = Math.log(Math
                .tan((90.0D + lat) * 3.141592653589793D / 360.0D)) / 0.0174532925199433D;
        toY = toY * 20037508.34D / 180.0D;
        return new LocationMercator(toX, toY);
    }

    public static Boolean checkWaypointDistance(Waypoint point1, Waypoint point2) {
        Double lng1 = point1.getWayPointLongitude();
        Double lat1 = point1.getWayPointLatitude();
        Double lng2 = point2.getWayPointLongitude();
        Double lat2 = point2.getWayPointLatitude();
        double s = getDistance(lng1, lat1, lng2, lat2);
        Double h1 = point1.getWayPointAltitude();
        Double h2 = point2.getWayPointAltitude();
        double h = Math.abs(h1 - h2);

        if (s > TOLERANCE || h > TOLERANCE) {
            return true;
        }
        return false;
    }

    public static boolean checkPcWaypointDistance(PointCloudWaypoint pc1, PointCloudWaypoint pc2) {
        Double lng1 = pc1.getAircraftLocationLongitude();
        Double lat1 = pc1.getAircraftLocationLatitude();

        Double lng2 = pc2.getAircraftLocationLongitude();
        Double lat2 = pc2.getAircraftLocationLatitude();

        double s = getDistance(lng1, lat1, lng2, lat2);

        Double h1 = pc1.getAircraftLocationAltitude();
        Double h2 = pc2.getAircraftLocationAltitude();
        double h = Math.abs(h1 - h2);

        if (s > TOLERANCE || h > TOLERANCE) {
            return true;
        }
        return false;
    }

    /**
     * 判断两点距离大于0.6m
     *
     * @param point1
     * @param point2
     * @return
     */
    @Deprecated
    public static Boolean checkFlyPointDistance(FlyPoint point1, FlyPoint point2, Double rang) {
        Double lng1 = point1.getAircraftLocationLongitude();
        Double lat1 = point1.getAircraftLocationLatitude();
        Double lng2 = point2.getAircraftLocationLongitude();
        Double lat2 = point2.getAircraftLocationLatitude();
        double s = getDistance(lng1, lat1, lng2, lat2);
        Double h1 = point1.getAircraftLocationAltitude();
        Double h2 = point2.getAircraftLocationAltitude();
        double h = Math.abs(h1 - h2);

        if (s > rang || h > rang) {
            return true;
        }
        return false;
    }

    /**
     * 检查墨卡托距离是否大于指定范围
     *
     * @param point1
     * @param point2
     * @param rang
     * @return
     */
    public static Boolean checkMercatorDistance(FlyPoint point1, FlyPoint point2, Double rang) {
        double distance = getMercatorDistanceViaFlyPoint(point1, point2);
        if (distance > rang) {
            return true;
        }
        return false;
    }


    /**
     * 欧几里德公式
     */
    @Deprecated
    public static double euclidComputeDistance(Waypoint p1, Waypoint p2) {
        Double lat = (p1.getWayPointLatitude() - p2.getWayPointLatitude()) * (p1.getWayPointLatitude() - p2.getWayPointLatitude());
        Double lng = (p1.getWayPointLongitude() - p2.getWayPointLongitude()) * (p1.getWayPointLongitude() - p2.getWayPointLongitude());
        Double alt = (p1.getWayPointAltitude() - p2.getWayPointAltitude()) * (p1.getWayPointAltitude() - p2.getWayPointAltitude());
        return Math.sqrt(lat + lng + alt);
    }

    /**
     * 欧几里德
     *
     * @param p1
     * @param p2
     * @return
     */
    @Deprecated
    public static double getFlyPointDistance(FlyPoint p1, FlyPoint p2) {
        Double lat = (p1.getAircraftLocationLatitude() - p2.getAircraftLocationLatitude()) * (p1.getAircraftLocationLatitude() - p2.getAircraftLocationLatitude());
        Double lng = (p1.getAircraftLocationLongitude() - p2.getAircraftLocationLongitude()) * (p1.getAircraftLocationLongitude() - p2.getAircraftLocationLongitude());
        Double alt = (p1.getAircraftLocationAltitude() - p2.getAircraftLocationAltitude()) * (p1.getAircraftLocationAltitude() - p2.getAircraftLocationAltitude());
        return Math.sqrt(lat + lng + alt);
    }

    /**
     * 计算航飞坐标计算墨卡托投影距离
     *
     * @param p1
     * @param p2
     * @return
     */
    public static double getMercatorDistanceViaFlyPoint(FlyPoint p1, FlyPoint p2) {
        LocationMercator m1 = LonLatToMercator(p1.getAircraftLocationLongitude(), p1.getAircraftLocationLatitude());
        LocationMercator m2 = LonLatToMercator(p2.getAircraftLocationLongitude(), p2.getAircraftLocationLatitude());
        Double xy2 = Math.pow((m1.getX() - m2.getX()), 2) + Math.pow((m1.getY() - m2.getY()), 2);
        Double alt = Math.pow((p1.getAircraftLocationAltitude() - p2.getAircraftLocationAltitude()), 2);
        return Math.sqrt(xy2 + alt);
    }

    /**
     * 通过经纬度计算墨卡托投影距离
     *
     * @param longitude1
     * @param latitude1
     * @param longitude2
     * @param latitude2
     * @return
     */
    public static double getMercatorDistanceViaLonLat(double longitude1, double latitude1, double longitude2, double latitude2) {
        // 纬度
//        double lat1 = Math.toRadians(latitude1);
//        double lat2 = Math.toRadians(latitude2);
//        // 经度
//        double lng1 = Math.toRadians(longitude1);
//        double lng2 = Math.toRadians(longitude2);
//        // 纬度之差
//        double a = lat1 - lat2;
//        // 经度之差
//        double b = lng1 - lng2;
//        // 计算两点距离的公式
//        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
//                Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(b / 2), 2)));
//        // 弧长乘地球半径, 返回单位: 米
//        s = s * EARTH_RADIUS;
//        return s;
        LocationMercator m1 = LonLatToMercator(longitude1, latitude1);
        LocationMercator m2 = LonLatToMercator(longitude2, latitude2);
        Double xy2 = Math.pow((m1.getX() - m2.getX()), 2) + Math.pow((m1.getY() - m2.getY()), 2);
        return Math.sqrt(xy2);
    }

    public static Double pointToDouble(String point) {
        Double du = Double.parseDouble(point.substring(0, point.indexOf("°")).trim());
        Double fen = Double.parseDouble(point.substring(point.indexOf("°") + 1, point.indexOf("'")).trim());
        Double miao = Double.parseDouble(point.substring(point.indexOf("'") + 1, point.indexOf("\"")).trim());
        Double duStr = du + fen / 60 + miao / 60 / 60;
        return duStr;
    }

    public static String getNumFromString(String str) {
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        if (m.find()) {
            return m.group();
        }
        return null;
    }

    /**
     * 从三维点列表获取最近的点
     *
     * @param source
     * @param flyPointList
     * @return
     */
    public static FlyPoint getClosestPoint(FlyPoint source, List<FlyPoint> flyPointList, Double rang) {
        Integer size = flyPointList.size();
        if (size == 0) {
            return null;
        }
        //只有一个点，直接比较距离
        if (size == 1) {
            //超过0.6米为无效点
            if (checkMercatorDistance(source, flyPointList.get(0), rang)) {
                return null;
            }
            return flyPointList.get(0);
        }
        List<FlyPoint> result = flyPointList.stream().sorted(new Comparator<FlyPoint>() {
            @Override
            public int compare(FlyPoint o1, FlyPoint o2) {
                Double d1 = getMercatorDistanceViaFlyPoint(source, o1);
                Double d2 = getMercatorDistanceViaFlyPoint(source, o2);
                return d1.compareTo(d2);
            }
        }).collect(Collectors.toList());
        //超过0.6米为无效点
        if (checkMercatorDistance(source, result.get(0), rang)) {
            return null;
        }
        return result.get(0);
    }

    /**
     * 从文件获取经纬度坐标信息
     *
     * @param filePath 文件路径
     * @return
     */
    public static FlyPoint getGisFromPic(InputStream inputStream) {
        FlyPoint source = new FlyPoint();
        //从图片获取三维信息
        Metadata metadata;
        try {
            metadata = ImageMetadataReader.readMetadata(inputStream);
            for (Directory directory : metadata.getDirectories()) {
                if (directory.getName().equals("GPS")) {
                    for (Tag tag : directory.getTags()) {
                        //标签名
                        String tagName = tag.getTagName();
                        //标签信息
                        String desc = tag.getDescription();
                        switch (tagName) {
                            case "GPS Latitude":
                                source.setAircraftLocationLatitude(DistanceUtil.pointToDouble(desc));
                                break;
                            case "GPS Longitude":
                                source.setAircraftLocationLongitude(DistanceUtil.pointToDouble(desc));
                                break;
                            case "GPS Altitude":
                                if (desc.length() > 6 && desc.contains("metres")) {
                                    desc = desc.substring(0, desc.indexOf("metres")).trim();
                                    System.out.println("test: " + desc);
                                }
                                source.setAircraftLocationAltitude(Double.parseDouble(getNumFromString(desc)));
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                }
                //获取图片拍摄时间
                if (directory.getName().equals("Exif IFD0")) {
                    for (Tag tag : directory.getTags()) {
                        //标签名
                        String tagName = tag.getTagName();
                        //标签信息
                        String desc = tag.getDescription();
                        switch (tagName) {
                            case "Date/Time":
                                source.setTakeTime(desc);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            return source;
        } catch (IOException | ImageProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 根据经纬度，计算两点间的距离
     *
     * @param longitude1 第一个点的经度
     * @param latitude1  第一个点的纬度
     * @param longitude2 第二个点的经度
     * @param latitude2  第二个点的纬度
     * @return 返回距离 单位米
     */
    public static BigDecimal getDistanceEarth(double longitude1, double latitude1, double longitude2, double latitude2) {
        // 纬度
        double lat1 = Math.toRadians(latitude1);
        double lat2 = Math.toRadians(latitude2);
        // 经度
        double lng1 = Math.toRadians(longitude1);
        double lng2 = Math.toRadians(longitude2);
        // 纬度之差
        double a = lat1 - lat2;
        // 经度之差
        double b = lng1 - lng2;
        // 计算两点距离的公式
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(b / 2), 2)));
        // 弧长乘地球半径, 返回单位: 千米
        s =  s * EARTH_RADIUS;
        return new BigDecimal(s);
    }
}