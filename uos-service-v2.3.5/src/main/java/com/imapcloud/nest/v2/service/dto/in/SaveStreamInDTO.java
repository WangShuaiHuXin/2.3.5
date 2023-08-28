package com.imapcloud.nest.v2.service.dto.in;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaveStreamInDTO {
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
