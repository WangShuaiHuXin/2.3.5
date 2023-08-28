package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

@Data
public class LiveSetDetailReplyDO {

    private Integer result;

    private Output output;

    @Data
    public static class Output {
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

}
