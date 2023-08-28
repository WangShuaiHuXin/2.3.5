package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 照片红外测温
 *
 * @author boluo
 * @date 2022-12-28
 */
@ToString
public class PowerInfraredOutDTO {

    private PowerInfraredOutDTO() {}

    @Data
    public static class PictureOutDTO {

        /**
         * 最高温
         */
        private BigDecimal maxTemperature;

        /**
         * 最低温
         */
        private BigDecimal minTemperature;

        /**
         * 平均温度
         */
        private BigDecimal avgTemperature;

        /**
         * 最高温坐标x
         */
        private BigDecimal maxSiteX;

        /**
         * 最高温坐标y
         */
        private BigDecimal maxSiteY;

        /**
         * 最低温坐标x
         */
        private BigDecimal minSiteX;

        /**
         * 最低温坐标y
         */
        private BigDecimal minSiteY;
    }
}
