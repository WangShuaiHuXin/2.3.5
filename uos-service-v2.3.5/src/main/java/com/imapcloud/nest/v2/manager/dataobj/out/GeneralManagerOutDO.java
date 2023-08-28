package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * cps
 *
 * @author boluo
 * @date 2022-08-27
 */
@ToString
public class GeneralManagerOutDO {

    private GeneralManagerOutDO() {}

    @Data
    public static class ComponentSerialNumberOutDO implements Serializable {

        /**
         * 相机版本
         */
        private String cameraVersion;

        /**
         * fc版本
         */
        private String fcVersion;

        /**
         * rc版本
         */
        private String rcVersion;

        /**
         * 电池版本
         */
        private String batteryVersion;
    }

    @Data
    public static class NestCameraInfoOutDO {

        /**
         * 品牌
         */
        private String brand;

        /**
         * 设备类型
         */
        private String deviceType;

        /**
         * ip
         */
        private String ip;

        /**
         * mac
         */
        private String mac;

        /**
         * 序列号
         */
        private String serialNo;

        /**
         * 版本
         */
        private String version;
    }
}
