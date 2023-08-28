package com.imapcloud.nest.v2.common.utils;

import com.geoai.common.core.constant.SymbolConstants;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

/**
 * 自建基站流媒体工具类
 * @author Vastfy
 * @date 2022/12/26 10:55
 * @since 2.1.7
 * @deprecated 2.3.2，流媒体相关功能已迁移至media服务，后续版本将会移除该类
 */
@Deprecated
public abstract class NmsStreamUtils {

    private static final String PUSH_HOST = "push.live.geoai.com";
    private static final String PULL_HOST = "live.geoai.com";

    private static final String RTMP = "rtmp";
    private static final String HTTP = "https";
    private static final String KEY = "zkyt@2021";

    /**
     * 获取rtmp协议推流地址
     * @param streamName    流名称
     * @return  推流地址
     */
    public static String getPushRtmp(String app, String streamName, Map<String, String> params) {
        return getPushRtmp(app, streamName, params, null);
    }

    /**
     * 获取rtmp协议推流地址
     * @param streamName    流名称
     * @param days  有效期（单位：天）
     * @return  推流地址
     */
    public static String getPushRtmp(String app, String streamName, Map<String, String> params, Integer days) {
        String pushSign = getPushSign(KEY, app, streamName, getTimeMilli(days));
        return RTMP + "://" + PUSH_HOST + "/" + app + "/" + streamName + "?" + pushSign + SymbolConstants.AND + getQueryParam(params);
    }

    /**
     * 获取rtmp协议拉流地址
     * @param app   APP类型
     * @param streamName    流名称
     * @return  拉流地址
     */
    public static String getPullRtmp(String app, String streamName, Map<String, String> params) {
        return RTMP + "://" + PULL_HOST + "/" + app + "/" + streamName + SymbolConstants.UNKNOWN + getQueryParam(params);
    }

    /**
     * 获取http拉流地址
     * @param app   APP类型
     * @param streamName    流名称
     * @return  拉流地址
     */
    public static String getPullHttp(String app, String streamName, Map<String, String> params) {
        return HTTP + "://" + PULL_HOST + "/" + app + "/" + streamName + ".flv" + SymbolConstants.UNKNOWN + getQueryParam(params);
    }

    /**
     * 获取推流鉴权
     * @param key   秘钥
     * @param app   APP类型
     * @param streamName    流名称
     * @param txTime    时间戳
     * @return  推流鉴权
     */
    private static String getPushSign(String key, String app, String streamName, long txTime) {
        String input = "/" + app + "/" + streamName + "-" + txTime + "-" + key;
        String txSecret = DigestUtils.md5DigestAsHex(input.getBytes());
        return "sign=" + txTime + "-" + txSecret;
    }

    /**
     * 获取时间戳（毫秒）
     */
    private static Long getTimeMilli(Integer days) {
        LocalDateTime localDateTime = LocalDateTime.now().plusDays(days == null ? 180 : days);
        return localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
    }

    private static String getQueryParam(Map<String, String> params){
        if(CollectionUtils.isEmpty(params)){
            return "";
        }
        StringBuilder qp = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            // a=b&
            qp.append(entry.getKey()).append(SymbolConstants.EQUAL).append(entry.getValue()).append(SymbolConstants.AND);
        }
        return qp.deleteCharAt(qp.length() - 1).toString();
    }

    private NmsStreamUtils(){}

}
