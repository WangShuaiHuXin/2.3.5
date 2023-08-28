package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

@Data
public class LiveSetDetailDO {
    /**
     * 直播码流标识符
     * 格式为 #{uav_sn}/#{camera_id}/#{video_index};飞机SN号/负载及挂载位置枚举值/负载lens编号
     */
    private String videoId;

    /**
     * 直播视频帧率
     */
    private Integer fps;

    /**
     * 直播视频码率
     */
    private Integer bps;

    /**
     * 直播视频分辨率
     */
    private String dpi;
}
