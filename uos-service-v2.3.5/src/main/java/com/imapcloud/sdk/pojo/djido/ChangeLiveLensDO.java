package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

@Data
public class ChangeLiveLensDO {
    /**
     * 直播视频流的ID
     */
    private String videoId;

    /**
     * 直播视频流镜头类型
     */
    private String videoType;

    public enum VideoTypeEnum {
        /**
         * 默认
         */
        NORMAL,

        /**
         * 广角
         */
        WIDE,

        /**
         * 变焦
         */
        ZOOM,

        /**
         * 红外
         */
        IR;

        public static String getVideoType(Integer ordinal) {
            Optional<VideoTypeEnum> first = Arrays.stream(VideoTypeEnum.values()).filter(e -> e.ordinal() == ordinal).findFirst();
            return first.orElse(NORMAL).name().toLowerCase(Locale.ROOT);
        }
    }
}
