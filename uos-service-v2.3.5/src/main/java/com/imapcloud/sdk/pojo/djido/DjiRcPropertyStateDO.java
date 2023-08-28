package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

import java.util.List;

/**
 * 遥控器属性，变更推送
 */
@Data
public class DjiRcPropertyStateDO {


    /**
     * 上报直播能力
     */
    private LiveCapacity liveCapacity;


    /**
     * 固件版本
     */
    private String firmwareVersion;



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
        private List<Device> deviceList;
    }

    @Data
    public static class Device {
        private Integer availableVideoNumber;
        private Integer coexistVideoNumberMax;
        private String sn;
        private List<Camera> cameraList;
    }

    @Data
    public static class Camera{
        private Integer availableVideoNumber;
        private Integer coexistVideoNumberMax;
        private String cameraIndex;
        private List<Video> videoList;

    }

    @Data
    public static class Video{
        private String videoIndex;
        private String videoType;
    }
}

