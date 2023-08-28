package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 红外测温
 *
 * @author boluo
 * @date 2022-12-28
 */
@ToString
public class PowerInfraredInDTO {

    private PowerInfraredInDTO() {}

    @Data
    public static class PictureInDTO {

        private String detailId;

        private BigDecimal siteX1;

        private BigDecimal siteX2;

        private BigDecimal siteY1;

        private BigDecimal siteY2;

        private String pictureUrl;
    }

    @Data
    public static class SaveInDTO {

        private String accountId;

        private String detailId;

        private BigDecimal siteX1;

        private BigDecimal siteX2;

        private BigDecimal siteY1;

        private BigDecimal siteY2;

        private BigDecimal maxTemperature;

        private BigDecimal minTemperature;

        private BigDecimal avgTemperature;

        private BigDecimal maxSiteX;

        private BigDecimal maxSiteY;

        private BigDecimal minSiteX;

        private BigDecimal minSiteY;
    }
}
