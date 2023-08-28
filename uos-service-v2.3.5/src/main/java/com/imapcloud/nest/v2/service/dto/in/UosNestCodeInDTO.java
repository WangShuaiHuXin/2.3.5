package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

@Data
public class UosNestCodeInDTO {

    @Data
    public static class NestProcessControlInDTO{

        private String nestId;
        
        private Integer which;

    }

    @Data
    public static class NestRoutineControlInDTO{
        
        private String nestId;
        
        private Integer which;

    }

    @Data
    public static class NestRotateLiftInDTO{

        private String nestId;
        
        private String action;

    }

    @Data
    public static class NestSquareControlInDTO{

        private String nestId;
        
        private Integer which;

        /**
         * 0：一体化归中
         * 1：x轴归中
         * 2：y轴归中
         */
        private Integer square;

    }

    @Data
    public static class NestDetectionNetworkInDTO{

        private String nestId;
        
        private Integer which;

        private Integer pingCount;

        private Integer pingSize;

        private Boolean speed;

    }

    @Data
    public static class NestBackLandPointInDTO{

        private String nestId;
        
        private Integer which;
        
        private Integer enable;

    }

    @Data
    public static class NestPowerControlInDTO{

        private String nestId;
        
        private Integer which;

    }

    @Data
    public static class NestSysControlInDTO{

        private String nestId;
        
        private Integer which;

    }





}
