package com.imapcloud.nest.v2.manager.dataobj.in;

import com.imapcloud.nest.v2.manager.dataobj.BaseInDO;
import lombok.Data;
import lombok.ToString;

/**
 * 媒体设备
 *
 * @author boluo
 * @date 2022-08-25
 */
@ToString
public class MediaDeviceInDO {

    private MediaDeviceInDO() {}

    public static int deviceType(Boolean deviceType) {
        return Boolean.TRUE.equals(deviceType) ? 1 : 0;
    }

    @Data
    public static class MediaDeviceEntityInDO extends BaseInDO {
        /**
         * 设备ID
         */
        private String deviceId;

        /**
         * 设备名称
         */
        private String deviceName;

        /**
         * 设备mac
         */
        private String deviceMac;

        /**
         * 设备地址
         */
        private String deviceDomain;

        /**
         * 设备品牌
         */
        private String deviceBrand;

        /**
         * 设备类型 0->摄像头，1->无人机，2->其他
         */
        private Integer deviceType;

        /**
         * 接入账号
         */
        private String accessKey;

        /**
         * 接入密钥
         */
        private String accessSecret;

        /**
         * 是否推流
         */
        private Integer videoEnable;
    }
}
