package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
public class DjiPilotPropertyOsdDO {

    private Long lastTimestamp;

    /**
     * 视频直播
     */
    private List<LiveStatus> liveStatus;


    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 剩余电量百分比
     */
    private Integer capacityPercent;

    /**
     * 图传质量
     */
    private Integer transmissionSignalQuality;

    /**
     * 图传质量
     */
    private WirelessLinkState wirelessLinkState;

    @Data
    public static class WirelessLinkState {
        /**
         * 上行信号质量
         */
        private Integer upwardQuality;
        /**
         * 下行信号质量
         */
        private Integer downwardQuality;
        /**
         * 频段
         */
        private Float frequencyBand;
    }

    @Data
    public static class LiveStatus {
        private Integer liveTime;
        private Integer liveTrendline;
        private String videoId;
        private Integer videoQuality;
    }

    public boolean isConnect() {
        return Objects.nonNull(lastTimestamp) && System.currentTimeMillis() - lastTimestamp < 30000;
    }

}

