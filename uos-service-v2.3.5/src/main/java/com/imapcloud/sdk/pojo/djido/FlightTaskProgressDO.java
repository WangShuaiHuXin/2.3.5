package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

@Data
public class FlightTaskProgressDO {


    private Output output;

    /**
     * 0 -> 正常
     * 非0 -> 错误
     */
    private Integer result;


    @Data
    public static class Ext {
        /**
         * 当前执行到的航点数
         */
        private Integer currentWaypointIndex;
        /**
         * 本次航线任务执行产生的媒体文件数量
         */
        private Integer mediaCount;
        /**
         * 航迹id
         */
        private String trackId;

        /**
         * 执行id -新版本
         */
        private String flightId;

    }

    @Data
    public static class Progress {
        private Integer currentStep;
        private Integer percent;
    }

    @Data
    public static class Output {
        private Ext ext;
        private Progress progress;
        private String status;
        /**
         * 执行任务id - 旧版本
         */
        private String flightId;
    }

    public enum CurrentStepEnum {
        DOWNLOAD_KMZ(1, "下载kmz航线任务"),
        UPLOAD_KMZ(2, "KMZ上传中"),
        EXECUTE_TASK(3, "航线执行");
        private Integer value;
        private String express;

        CurrentStepEnum(Integer value, String express) {
            this.value = value;
            this.express = express;
        }

        public Integer getValue() {
            return value;
        }

        public String getExpress() {
            return express;
        }
    }
}
