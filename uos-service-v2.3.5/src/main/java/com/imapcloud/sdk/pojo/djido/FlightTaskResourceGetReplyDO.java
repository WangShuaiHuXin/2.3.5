package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

@Data
public class FlightTaskResourceGetReplyDO {
    private Integer result;
    private Output output;

    @Data
    public static class Output {
        private File file;
    }

    @Data
    public static class File {

        /**
         * 文件签名
         */
        private String fingerprint;
        /**
         * 文件URL
         */
        private String url;
    }
}
