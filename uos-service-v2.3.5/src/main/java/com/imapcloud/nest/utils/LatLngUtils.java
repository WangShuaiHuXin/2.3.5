package com.imapcloud.nest.utils;

import com.alibaba.fastjson.JSONObject;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.common.exception.NestException;
import com.imapcloud.nest.v2.common.enums.MessageEnum;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * 经纬度计算集合
 *
 * @author wmin
 */
public class LatLngUtils {
    static double Rc = 6378137.0D;
    static double Rj = 6356725.0D;


    public static double getDistance(double aLatitude, double aLongitude,
                                     double bLatitude, double bLongitude) {
        double x = (bLongitude - aLongitude)
                * 3.141592653589793D
                * Rc
                * Math.cos((aLatitude + bLatitude) / 2.0D * 3.141592653589793D / 180.0D)
                / 180.0D;

        double y = (bLatitude - aLatitude) * 3.141592653589793D * Rc / 180.0D;
        double distance = Math.hypot(x, y);
        return distance;
    }

    /**
     * 获取与正北方向夹角（顺时针）*A到B方向
     *
     * @param bLatitude
     * @param bLongitude
     * @param aLatitude
     * @param aLongitude
     * @return 返回0-360度
     */
    public static double getABAngle(double bLatitude, double bLongitude,
                                    double aLatitude, double aLongitude) {
        double dx = (getRadLongitude(bLongitude) - getRadLongitude(aLongitude))
                * getEd(aLatitude);
        double dy = (getRadLatitude(bLatitude) - getRadLatitude(aLatitude))
                * getEc(aLatitude);

        double angle = Math.atan(Math.abs(dx / dy)) * 180.0D / 3.141592653589793D;

        double dLo = bLongitude - aLongitude;
        double dLa = bLatitude - aLatitude;
        if ((dLo > 0.0D) && (dLa <= 0.0D)) {
            angle = 90.0D - angle + 90.0D;
        } else if ((dLo <= 0.0D) && (dLa < 0.0D)) {
            angle += 180.0D;
        } else if ((dLo < 0.0D) && (dLa >= 0.0D)) {
            angle = 90.0D - angle + 270.0D;
        }
        return angle;
    }

    private static double getRadLongitude(double longitude) {
        return longitude * 3.141592653589793D / 180.0D;
    }

    private static double getRadLatitude(double latitude) {
        return latitude * 3.141592653589793D / 180.0D;
    }

    private static double getEc(double latitude) {
        return Rj + (Rc - Rj) * (90.0D - latitude) / 90.0D;
    }

    private static double getEd(double latitude) {
        return getEc(latitude) * Math.cos(getRadLatitude(latitude));
    }


    /**
     * 百度地图-根据经纬度获取详细地址
     */
    public static String getBDAddress(Double lat, Double lng) {
        // 百度地图开发者密钥
        String BD_SERVICE_API = "ho2OQlrvsaRUByr46Xs99K0nihX2w9fm";

        StringBuilder path = new StringBuilder()
                .append("http://api.map.baidu.com/reverse_geocoding/v3/?ak=")
                .append(BD_SERVICE_API).append("&output=json&coordtype=wgs84ll")
                .append("&location=").append(lat).append(",").append(lng);

        HttpURLConnection con = null;
        try {
            URL url = new URL(path.toString());
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.104 Safari/537.36");
            con.setRequestProperty("Referer", "http://api.map.baidu.com/geocoder/v2/");
            String b = "0", c = "{", d = "status";
            String responseBody = new Scanner(con.getInputStream(), "utf-8").useDelimiter("\\A").next();
            System.out.println("getGDAddress: res=" + responseBody);

            JSONObject jsonObject = JSONObject.parseObject(responseBody);
            if (!b.equals(jsonObject.getString(d))) {
                throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_GET_DETAILED_ADDRESS_ACCORDING_TO_LATITUDE_AND_LONGITUDE.getContent()));
            }
            if (!responseBody.startsWith(c)) {
                throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_GET_DETAILED_ADDRESS_ACCORDING_TO_LATITUDE_AND_LONGITUDE.getContent()));
            }
            String address = jsonObject.getJSONObject("result").getString("formatted_address");
            return address;
//            return JSONObject.parseObject(jsonObject.getJSONObject("result").getString("addressComponent"), AddressDTO.class);
        } catch (Exception e) {
            throw new NestException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAILED_TO_GET_DETAILED_ADDRESS_ACCORDING_TO_LATITUDE_AND_LONGITUDE.getContent()));
        } finally {
            if (null != con) {
                con.disconnect();
            }
        }
    }
}
