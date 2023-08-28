package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

@Data
public class UosUavCodeInDTO {

    @Data
    public static class UavOneKeyTakeOffInDTO{

        private String nestId;

        private Integer which;

        private Float height;

        private Boolean confirm;

    }


    @Data
    public static class CommonControlInDTO{

        private String nestId;

        private Integer which;

        private Integer enable;

    }

    @Data
    public static class UavControlInDTO{

        private String nestId;

        private Integer which;

    }

    @Data
    public static class UavMissionControlInDTO{

        private String nestId;

        private Integer which;

        private Boolean breakPoint;

    }

    @Data
    public static class UavCalibrationCompassInDTO{

        private String nestId;

        private Integer which;

        private Integer enable;

    }

    @Data
    public static class UavG900LiveSourceInDTO{

        private String nestId;

        private Integer source;

        private Integer which;

    }

    @Data
    public static class UavVsInfraredInfoInDTO{

        private String nestId;

        private Integer enable;

        private Integer which;

    }

    @Data
    public static class UavChargeSwitchInDTO{

        private String nestId;

        private Integer which;

        private Integer enable;

    }

    @Data
    public static class UavSwitchInDTO{

        private String nestId;

        private Integer which;

        private Integer enable;

    }

    @Data
    public static class UavLandControlInDTO{

        private String nestId;

        private Integer which;

    }

    @Data
    public static class NestSysControlInDTO{

        private String nestId;

        private Integer which;
    }

    @Data
    public static class SetUavMaxFlyAltInDTO{

        private String nestId;

        private Integer which;

        private Integer maxFlyAlt;

    }

    @Data
    public static class SetRthAltInDTO{

        private String nestId;

        private Integer which;

        private Integer rthAlt;

    }

    @Data
    public static class SetUavFlyLongestRadiusInDTO{

        private String nestId;

        private Integer which;

        private Integer radius;

    }

    @Data
    public static class SetUavBehaviorInDTO{

        private String nestId;

        private Integer which;

        private String behavior;

    }

    @Data
    public static class IntellectShutdownInDTO{

        private String nestId;

        private Integer which;

        /**
         * true-开启
         * false-关闭
         */
        private Boolean enable;

        /**
         * 电量值（百分比）
         */
        private Integer threshold;

    }

    @Data
    public static class IntellectGoHomeInDTO{

        private String nestId;

        private Integer which;

        private Integer enable;

    }

    @Data
    public static class SetRtkTypeInDTO{

        private String nestId;

        private Integer which;

        private Integer type;

    }

    @Data
    public static class SetRtkInfoInDTO{

        private String nestId;

        private Integer which;

        /**
         * IP地址或域名
         */
        private String ip;

        /**
         * 端口
         */
        private Integer port;

        /**
         * 源节点
         */
        private String mountPoint;

        /**
         * 账号
         */
        private String userName;

        /**
         * 密码
         */
        private String password;

    }


    @Data
    public static class SetRtkExpireTimeInDTO{
        private String nestId;

        private Integer which;

        private String expireTime;
    }

    @Data
    public static class UavDjiLoginControlInDTO{

        private String nestId;

        private Integer which;

        private String userName;

        private String password;
    }

    @Data
    public static class UavCameraControlInDTO{

        private String nestId;

        private Integer which;

        private Integer model;
    }


    @Data
    public static class UavGimbalControlInDTO{

        private String nestId;

        private Float pitchAngle;

        private Float yamAngle;
        /**
         * 俯仰角度
         */
        private Boolean pitch;
        /**
         * 朝向角度
         */
        private Boolean yam;

        private Integer which;

    }


    @Data
    public static class UavZoomControlInDTO{

        private Integer which;

        private String nestId;

        private Float ratio;
    }

    @Data
    public static class UavSwitchControlInDTO{

        private String nestId;

        private Integer which;

        private Integer enable;
    }

    @Data
    public static class UavCameraParamControlInDTO{

        private String nestId;

        private Integer which;

        private Integer mold;

    }


    @Data
    public static class SetCameraInfraredColorInDTO {

        private String nestId;

        private String colorKey;

        private Integer which;
    }

    @Data
    public static class InfraredTestTempeInDTO {

        private String nestId;

        private String measureMode;

        private Double startX;

        private Double startY;

        private Double endX;

        private Double endY;

        private Double textX;

        private Double textY;

        private Integer which;
    }

}
