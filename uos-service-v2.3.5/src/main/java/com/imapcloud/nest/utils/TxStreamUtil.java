package com.imapcloud.nest.utils;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.iotvideoindustry.v20201201.IotvideoindustryClient;
import com.tencentcloudapi.iotvideoindustry.v20201201.models.DescribeDeviceStreamsRequest;
import com.tencentcloudapi.iotvideoindustry.v20201201.models.DescribeDeviceStreamsResponse;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author daolin
 * 腾讯云鉴权工具类
 * @deprecated 2.3.2，功能已迁移至media服务，将在后续版本移除该类
 */
@Deprecated
public class TxStreamUtil {

    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final String PUSH_DOMAIN = "105642.livepush.myqcloud.com";
    private static final String PULL_DOMAIN = "live.iflyer360.com";
    private static final String APP = "live";
    private static final String KEY = "c21dd1c81e94edcd330c39932c508cd8";
    private static final String RTMP = "rtmp://";
    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";
    private static final String SEPARATE = "/";
    private static final String FLV = ".flv";
    private static final String OUT = "out";
    private static final String IN = "in";
    private static final String SECRETID = "AKIDiCQN4UdVCdnfGbgV3LM6YkkTv60dz4tI";
    private static final String SECRETKEY = "91dy2oJYWDcmfGaeSzFExPZT7UbQzHI1";


    public static String getGbFlv(String deviceId) {
//        try {
//            // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
//            // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
//            Credential cred = new Credential(SECRETID, SECRETKEY);
//            // 实例化一个http选项，可选的，没有特殊需求可以跳过
//            HttpProfile httpProfile = new HttpProfile();
//            httpProfile.setEndpoint("iotvideoindustry.tencentcloudapi.com");
//            // 实例化一个client选项，可选的，没有特殊需求可以跳过
//            ClientProfile clientProfile = new ClientProfile();
//            clientProfile.setHttpProfile(httpProfile);
//            // 实例化要请求产品的client对象,clientProfile是可选的
//            IotvideoindustryClient client = new IotvideoindustryClient(cred, "ap-guangzhou", clientProfile);
//            // 实例化一个请求对象,每个接口都会对应一个request对象
//            DescribeDeviceStreamsRequest req = new DescribeDeviceStreamsRequest();
//            req.setDeviceId(deviceId);
//            //设置过期时间
//            LocalDateTime localDateTime = LocalDateTime.now().plusDays(30);
//            Long txTime = localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
//            req.setExpireTime(txTime / 1000);
//            DescribeDeviceStreamsResponse resp = client.DescribeDeviceStreams(req);
//            return resp.getData().getFlvAddr();
//        } catch (TencentCloudSDKException e) {
//            e.printStackTrace();
//        }
        return "";
        // 输出json格式的字符串回包
    }

    public static Map<String, Object> getDefault(String streamName) {
        String pushRtmp = getPushRtmp(PUSH_DOMAIN, APP, KEY, streamName, 360L);
        String pullRtmp = RTMP +
                PULL_DOMAIN +
                SEPARATE +
                APP +
                SEPARATE +
                streamName;
        String pullHttp = HTTPS +
                PULL_DOMAIN +
                SEPARATE +
                APP +
                SEPARATE +
                streamName +
                FLV;
        Map<String, Object> result = new HashMap(8);
        Map<String, Object> out = getOutDefault(streamName);
        result.put("pushRtmp", pushRtmp);
        result.put("pullRtmp", pullRtmp);
        result.put("pullHttp", pullHttp);
        result.put("pushOutRtmp", out.get("pushOutRtmp"));
        result.put("pullOutRtmp", out.get("pullOutRtmp"));
        result.put("pullOutHttp", out.get("pullOutHttp"));
        return result;
    }

    public static Map<String, Object> getOutDefault(String streamName) {
        String streamOut = OUT + streamName;
        String pushOutRtmp = getPushRtmp(PUSH_DOMAIN, APP, KEY, streamOut, 360L);
        String pullOutRtmp = RTMP +
                PULL_DOMAIN +
                SEPARATE +
                APP +
                SEPARATE +
                streamOut;
        String pullOutHttp = HTTPS +
                PULL_DOMAIN +
                SEPARATE +
                APP +
                SEPARATE +
                streamOut +
                FLV;
        Map<String, Object> result = new HashMap(2);
        result.put("pushOutRtmp", pushOutRtmp);
        result.put("pullOutRtmp", pullOutRtmp);
        result.put("pullOutHttp", pullOutHttp);
        return result;
    }

    public static Map<String, Object> getInDefault(String streamName) {
        String streamIn = IN + streamName;
        String pushInRtmp = getPushRtmp(PUSH_DOMAIN, APP, KEY, streamIn, 360L);
        String pullInRtmp = RTMP +
                PULL_DOMAIN +
                SEPARATE +
                APP +
                SEPARATE +
                streamIn;
        String pullInHttp = HTTPS +
                PULL_DOMAIN +
                SEPARATE +
                APP +
                SEPARATE +
                streamIn +
                FLV;
        Map<String, Object> result = new HashMap(4);
        result.put("pushInRtmp", pushInRtmp);
        result.put("pullInRtmp", pullInRtmp);
        result.put("pullInHttp", pullInHttp);
        return result;
    }

    public static Map<String, Object> getAppDefault(String streamName) {
        String pushRtmp = getPushRtmp(PUSH_DOMAIN, APP, KEY, streamName, 360L);
        String pullRtmp = RTMP +
                PULL_DOMAIN +
                SEPARATE +
                APP +
                SEPARATE +
                streamName;
        String pullHttp = HTTPS +
                PULL_DOMAIN +
                SEPARATE +
                APP +
                SEPARATE +
                streamName +
                FLV;
        Map<String, Object> result = new HashMap(2);
        result.put("pushRtmp", pushRtmp);
        result.put("pullRtmp", pullRtmp);
        result.put("pullHttp", pullHttp);
        return result;
    }

    /**
     * 获取腾讯云直播推流地址
     *
     * @param domain     推流域名，tx.xx.xx
     * @param app        live
     * @param key        腾讯云推流主key
     * @param streamName 流名称
     * @param days       过期时间，天
     * @return
     */
    public static String getPushRtmp(String domain, String app, String key, String streamName, Long days) {
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(days);
        Long txTime = localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        String txSecret = getSafeUrl(key, streamName, txTime/1000);
        return RTMP +
                domain +
                SEPARATE +
                app +
                SEPARATE +
                streamName +
                "?"+
                txSecret;
    }

    /*
     * KEY+ streamName + txTime
     */
    private static String getSafeUrl(String key, String streamName, long txTime) {
        String input = key +
                streamName +
                Long.toHexString(txTime).toUpperCase();
        String txSecret = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            txSecret = byteArrayToHexString(
                    messageDigest.digest(input.getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return txSecret == null ? "" :
                "txSecret="
                        + txSecret
                        + "&"
                        + "txTime="
                        + Long.toHexString(txTime).toUpperCase();
    }

    private static String byteArrayToHexString(byte[] data) {
        char[] out = new char[data.length << 1];
        for (int i = 0, j = 0; i < data.length; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return new String(out);
    }

}
