package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

import java.util.List;

@Data
public class DjiDockLiveCapacityStateDO {

    /**
     * 上报直播能力
     */
    private LiveCapacity liveCapacity;

    @Data
    public static class LiveCapacity {

        /**
         * 可用于直播的视频流总数
         */
        private Integer availableVideoNumber;

        /**
         * 可同时进行直播的最大视频流总数
         */
        private Integer coexistVideoNumberMax;

        /**
         * 设备直播能力列表
         */
        private List<DjiDockLiveCapacityStateDO.Device> deviceList;
    }

    @Data
    public static class Device {
        private Integer availableVideoNumber;
        private Integer coexistVideoNumberMax;
        private String sn;
        private List<DjiDockLiveCapacityStateDO.Camera> cameraList;
    }

    @Data
    public static class Camera {
        private Integer availableVideoNumber;
        private Integer coexistVideoNumberMax;
        private String cameraIndex;
        private List<DjiDockLiveCapacityStateDO.Video> videoList;

    }

    @Data
    public static class Video {
        private String videoIndex;
        private String videoType;
    }
}
