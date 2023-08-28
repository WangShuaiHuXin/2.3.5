package com.imapcloud.sdk.pojo.djido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class FlightTaskPrepareDO {
    /**
     * 任务ID
     */
    private String flightId;
    /**
     * 开始执行时间
     */
    private Long executeTime;
    /**
     * 任务类型
     */
    private Integer taskType;
    /**
     * 航线类型
     */
    private Integer waylineType;

    /**
     * 航线文件对象
     */
    private File file;

    /**
     * 返航高度
     * min:20
     * max:500
     */
    private Integer rthAltitude;

    /**
     * 失控动作
     */
    private Integer outOfControlAction;

    @Data
    public static class File {

        /**
         * 文件URL
         */
        private String url;

        /**
         * 文件内容MD5签名
         */
        private String fingerprint;
    }

    public enum TaskTypeEnum {
        IMMEDIATE_TASK(0, "立即任务"),
        TIMED_TASK(1, "定时任务");;
        private int value;
        private String express;

        TaskTypeEnum(int value, String express) {
            this.value = value;
            this.express = express;
        }

        public int getValue() {
            return value;
        }

        public String getExpress() {
            return express;
        }
    }

    public enum WaylineTypeEnum {
        COMMON_WAYPOINT(0, "普通航点");
        private int value;
        private String express;

        WaylineTypeEnum(int value, String express) {
            this.value = value;
            this.express = express;
        }

        public int getValue() {
            return value;
        }

        public String getExpress() {
            return express;
        }
    }

    public enum OutOfControlActionEnum {
        GO_HOME(0, "返航"),
        HOVER(1, "悬停"),
        LAND(2, "降落"),
        UNKNOWN(-1, "未知");
        private int value;
        private String express;

        OutOfControlActionEnum(int value, String express) {
            this.value = value;
            this.express = express;
        }

        public int getValue() {
            return value;
        }

        public static OutOfControlActionEnum getInstance(int value) {
            for (OutOfControlActionEnum e : OutOfControlActionEnum.values()) {
                if (e.value == value) {
                    return e;
                }
            }
            return UNKNOWN;
        }

        public String getExpress() {
            return express;
        }
    }
}
