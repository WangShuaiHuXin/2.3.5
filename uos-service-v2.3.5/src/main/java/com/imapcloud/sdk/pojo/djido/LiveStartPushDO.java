package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

@Data
public class LiveStartPushDO {
    private Integer urlType;
    private String url;
    private String videoId;
    private Integer videoQuality;

    public enum UrlTypeEnum {
        AGORA(0, "声网"),
        RTMP(1, "RTMP"),
        RTSP(2, "RTSP"),
        GB28281(3, "GB28281"),
        UNKNOWN(-1, "UNKNOWN");
        private int value;
        private String express;

        UrlTypeEnum(int value, String express) {
            this.value = value;
            this.express = express;
        }

        public static UrlTypeEnum getInstance(int value) {
            for (UrlTypeEnum e : UrlTypeEnum.values()) {
                if (e.value == value) {
                    return e;
                }
            }
            return UNKNOWN;
        }
    }

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
