package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

@Data
public class LiveSetQualityDO {
    private String videoId;
    private Integer videoQuality;

    public enum VideoQualityEnum{
        SELF_ADAPTION(0, "自适应"),
        FLUENCY(1, "流畅"),
        STANDARD_DEFINITION(2, "标清"),
        HIGH_DEFINITION(3, "高清"),
        SUPER_DEFINITION(4, "超清"),
        UNKNOWN(-1, "UNKNOWN");

        private int value;
        private String express;

        VideoQualityEnum(int value, String express) {
            this.value = value;
            this.express = express;
        }

        public static VideoQualityEnum getInstance(int value) {
            for (VideoQualityEnum e : VideoQualityEnum.values()) {
                if (e.value == value) {
                    return e;
                }
            }
            return UNKNOWN;
        }
    }
}
