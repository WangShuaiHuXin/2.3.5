package com.imapcloud.nest.media;

import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 自建流媒体地址生成方案
 */
public class NmsStream {

    /**************************研发环境*************************/
    private static String TX_PUSH_HOST = "push.live.geoai.com";
    private static String TX_PULL_HOST = "live.geoai.com";
    private static String TX_HTTP_PORT = "443";

    /**************************政务环境*************************/
    private static String HW_PUSH_HOST = "121.37.220.243";
    private static String HW_PULL_HOST = "dz.live.geolive.cn";
    private static String HW_HTTP_PORT = "8443";

    private static String PUSH_PORT = "11935";
    private static String PULL_PORT = "1935";
    private static String LIVE_APP = "live";
    private static String CAMERA_APP = "camera";
    private static String RTMP = "rtmp";
    private static String HTTP = "https";
    private static String KEY = "zkyt@2021";

    /**
     * 获取推流鉴权
     *
     * @param key
     * @param app
     * @param streamName
     * @param txTime
     * @return
     */
    private static String getPushSign(String key, String app, String streamName, long txTime) {
        String input = "/" + app + "/" + streamName + "-" + txTime + "-" + key;
        String txSecret = DigestUtils.md5DigestAsHex(input.getBytes());
        return txSecret == null ? "" :
                "sign=" + txTime + "-" + txSecret;
    }

    /**
     * 获取无人机地址，默认180天
     *
     * @param platform
     * @param streamName
     * @param days
     * @return
     */
    public static Map<String, Object> getLiveDefault(Integer platform, String streamName, Integer days) {
        Map<String, Object> result = new LinkedHashMap<>();
        String pushRtmp = getPushRtmp(platform, LIVE_APP, streamName, days);
        String pullRtmp = getPullRtmp(platform, LIVE_APP, streamName);
        String pullHttp = getPullHttp(platform, LIVE_APP, streamName);
        result.put("无人机pushRtmp", pushRtmp);
        result.put("无人机pullRtmp", pullRtmp);
        result.put("无人机pullHttp", pullHttp);
        return result;
    }

    /**
     * 获取巢内监控地址，默认180天
     *
     * @param platform
     * @param streamName
     * @param days
     * @return
     */
    public static Map<String, Object> getINsideCameraDefault(Integer platform, String streamName, Integer days) {
        streamName = "in" + streamName;
        Map<String, Object> result = new LinkedHashMap<>();
        String pushRtmp = getPushRtmp(platform, CAMERA_APP, streamName, days);
        String pullRtmp = getPullRtmp(platform, CAMERA_APP, streamName);
        String pullHttp = getPullHttp(platform, CAMERA_APP, streamName);
        result.put("巢内Rtmp推流地址", pushRtmp);
        result.put("巢内Rtmp拉流地址", pullRtmp);
        result.put("巢内Http拉流地址", pullHttp);
        return result;
    }

    /**
     * 获取巢外监控地址，默认180天
     *
     * @param platform
     * @param streamName
     * @param days
     * @return
     */
    public static Map<String, Object> getOutsideCameraDefault(Integer platform, String streamName, Integer days) {
        streamName = "out" + streamName;
        Map<String, Object> result = new LinkedHashMap<>();
        String pushRtmp = getPushRtmp(platform, CAMERA_APP, streamName, days);
        String pullRtmp = getPullRtmp(platform, CAMERA_APP, streamName);
        String pullHttp = getPullHttp(platform, CAMERA_APP, streamName);
        result.put("巢外Rtmp推流地址", pushRtmp);
        result.put("巢外Rtmp拉流地址", pullRtmp);
        result.put("巢外Http拉流地址", pullHttp);
        return result;
    }

    /**
     * 获取时间戳（毫秒）
     *
     * @param days
     * @return
     */
    private static Long getTimeMilli(Integer days) {
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(days == null ? 180 : days);
        Long txTime = localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        return txTime;
    }

    /**
     * 获取推流地址
     *
     * @param platform
     * @param app
     * @param streamName
     * @param days
     * @return
     */
    private static String getPushRtmp(Integer platform, String app, String streamName, Integer days) {
        String pushSign = getPushSign(KEY, app, streamName, getTimeMilli(days));
        return RTMP + "://" + (platform == 1 ? TX_PUSH_HOST : HW_PUSH_HOST + ":" + PUSH_PORT) + "/" + app + "/" + streamName + "?" + pushSign;
    }

    /**
     * 获取拉流地址
     *
     * @param platform
     * @param app
     * @param streamName
     * @return
     */
    private static String getPullRtmp(Integer platform, String app, String streamName) {
        return RTMP + "://" + (platform == 1 ? TX_PULL_HOST : HW_PULL_HOST + ":" + PULL_PORT) + "/" + app + "/" + streamName;
    }

    /**
     * 获取http拉流地址
     *
     * @param platform
     * @param app
     * @param streamName
     * @return
     */
    private static String getPullHttp(Integer platform, String app, String streamName) {
        return HTTP + "://" + (platform == 1 ? TX_PULL_HOST : HW_PULL_HOST) + (platform == 1 ? "" : ":" + HW_HTTP_PORT) + "/" + app + "/" + streamName + ".flv";
    }

    /**
     * 输出流地址
     *
     * @param name
     * @param platform
     * @param streamName
     * @param days
     * @param mode
     */
    private static void poDrone(String name, Integer platform, String streamName, Integer days, Integer mode) {
        Map result;
        switch (mode) {
            case 1:
                result = getLiveDefault(platform, streamName, days);
                break;
            case 2:
                result = getOutsideCameraDefault(platform, streamName, days);
                break;
            case 3:
                result = getINsideCameraDefault(platform, streamName, days);
                break;
            case 4:
                result = getInAndOutCameraDefault(platform, streamName, days);
                break;
            default:
                result = getAllDefault(platform, streamName, days);
                break;
        }
        System.out.println("名称：" + name + "(" + days + "天)");
        for (Object o : result.keySet()) {
            System.out.println(o + ":" + result.get(o));
        }
    }

    private static Map getInAndOutCameraDefault(Integer platform, String streamName, Integer days) {
        Map result = new LinkedHashMap();
        Map out = getOutsideCameraDefault(platform, streamName, days);
        for (Object o : out.keySet()) {
            result.put(o, out.get(o));
        }
        Map in = getINsideCameraDefault(platform, streamName, days);
        for (Object o : in.keySet()) {
            result.put(o, in.get(o));
        }
        return result;
    }

    /**
     * 获取全部地址
     *
     * @param platform
     * @param streamName
     * @param days
     * @return
     */
    private static Map getAllDefault(Integer platform, String streamName, Integer days) {
        Map result = new LinkedHashMap();
        Map live = getLiveDefault(platform, streamName, 180);
        for (Object o : live.keySet()) {
            result.put(o, live.get(o));
        }
        Map out = getOutsideCameraDefault(platform, streamName, 180);
        for (Object o : out.keySet()) {
            result.put(o, out.get(o));
        }
        Map in = getINsideCameraDefault(platform, streamName, 180);
        for (Object o : in.keySet()) {
            result.put(o, in.get(o));
        }
        return result;
    }



    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("步骤一，请输入名称: ");
        String name = scanner.nextLine();
        System.out.println("步骤二，请输入机巢uuid(默认截取第一个”-“之前): ");
        String uuid = scanner.nextLine();
        //机巢uuid默认截取第一个‘-’前半段，无则取原值;
        //一巢三机等特殊机型，需单独生成每个地址，此模板不适用
        String streamName = uuid.contains("-") ? uuid.substring(0, uuid.indexOf("-")) : uuid;
        System.out.println("步骤三，请输入平台：1-研发，2-政务: ");
        Integer platform = scanner.nextInt();
        System.out.println("步骤四，请选择模式:1-无人机,2-巢外,3-巢内,4-内+外,5-全部");
        Integer mode = scanner.nextInt();
        System.out.println("步骤五，请输入流地址有效天数(建议60-180天,默认180):");
        scanner.nextLine();//接收上一个enter
        String days = scanner.nextLine();
        if (days.equals("")) {
            poDrone(name, platform, streamName, 180, mode);
        } else {
            poDrone(name, platform, streamName, Integer.parseInt(days), mode);
        }
        scanner.close();

    }
}