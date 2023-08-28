package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 红外测温
 *
 * @author boluo
 * @date 2022-12-29
 */
@ToString
public class PowerInfraredRespVO {

    private PowerInfraredRespVO() {}

    @Data
    public static class PictureRespVO {

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
