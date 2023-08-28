package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;
import lombok.ToString;

/**
 * 媒体设备dto
 *
 * @author boluo
 * @date 2022-08-24
 */
@ToString
public class MediaDeviceInDTO {

    private MediaDeviceInDTO() {}

    @Data
    public static class DeviceInDTO {

        private String accountId;

        private String nestId;

        /**
         * 巢内拉流url
         */
        private String innerPullUrl;

        /**
         * 巢内mac
         */
        private String innerMac;

        /**
         * 巢外拉流url
         */
        private String outerPullUrl;

        /**
         * 巢外mac
         */
        private String outerMac;

        /**
         * 巢外推流地址
         */
        private String outerPushUrl;
    }

    @Data
    public static class SetDeviceInDTO {

        private String accountId;

        private String nestId;

        private String pushUrl;

        private String accessKey;

        private String accessSecret;

        private String deviceDomain;

        private Boolean videoEnable;

        /**
         * 巢内 true  巢外 false
         */
        private Boolean nestInner;
    }
}
