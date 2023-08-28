package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

/**
 * 媒体流信息
 *
 * @author boluo
 * @date 2022-08-26
 */
@Data
public class MediaStreamOutDO {

    /**
     * 流ID
     */
    private String streamId;

    /**
     * 推流地址
     */
    private String streamPushUrl;

    /**
     * 拉流地址
     */
    private String streamPullUrl;

    /**
     * 协议类型 RTMP/RTSP/HTTP/SRT/RTC/GB28181
     */
    private String protocol;
}
