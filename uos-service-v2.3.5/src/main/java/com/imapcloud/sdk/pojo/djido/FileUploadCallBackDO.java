package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FileUploadCallBackDO {

    private FileUpload file;

    @Data
    public static class FileUpload{

        private String objectKey;

        private String path ;

        private String name;

        private Ext ext;

        private Metadata metadata;


    }


    @Data
    public static class Ext{
        private String flightId;

        private String droneModelKey;

        private String payloadModelKey;

        private Boolean isOriginal;
    }

    @Data
    public static class Metadata{
        private ShootPosition shootPosition;

        private String gimbalYawDegree;

        private BigDecimal absoluteAltitude;

        private BigDecimal relativeAltitude;

        private String createTime;
    }

    @Data
    public static class ShootPosition{
        private BigDecimal lat;

        private BigDecimal lng;
    }
}
