package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 红外测温
 *
 * @author boluo
 * @date 2022-12-28
 */
@ToString
public class PowerInfraredReqVO {

    private PowerInfraredReqVO() {}

    @Data
    public static class PictureReqVO {
        @NotEmpty(message = "{geoai_common_Illegal_request_parameters}")
        private String detailId;

        @NotNull(message = "{geoai_common_Illegal_request_parameters}")
        private BigDecimal siteX1;

        @NotNull(message = "{geoai_common_Illegal_request_parameters}")
        private BigDecimal siteX2;

        @NotNull(message = "{geoai_common_Illegal_request_parameters}")
        private BigDecimal siteY1;

        @NotNull(message = "{geoai_common_Illegal_request_parameters}")
        private BigDecimal siteY2;
    }

    @Data
    public static class SaveReqVO {

        @NotEmpty(message = "{geoai_common_Illegal_request_parameters}")
        private String detailId;

        @NotNull(message = "{geoai_common_Illegal_request_parameters}")
        private BigDecimal siteX1;

        @NotNull(message = "{geoai_common_Illegal_request_parameters}")
        private BigDecimal siteX2;

        @NotNull(message = "{geoai_common_Illegal_request_parameters}")
        private BigDecimal siteY1;

        @NotNull(message = "{geoai_common_Illegal_request_parameters}")
        private BigDecimal siteY2;

        /**
         * 最高温
         */
        @NotNull(message = "{geoai_common_Illegal_request_parameters}")
        private BigDecimal maxTemperature;

        /**
         * 最低温
         */
        @NotNull(message = "{geoai_common_Illegal_request_parameters}")
        private BigDecimal minTemperature;

        /**
         * 平均温度
         */
        @NotNull(message = "{geoai_common_Illegal_request_parameters}")
        private BigDecimal avgTemperature;

        /**
         * 最高温坐标x
         */
        @NotNull(message = "{geoai_common_Illegal_request_parameters}")
        private BigDecimal maxSiteX;

        /**
         * 最高温坐标y
         */
        @NotNull(message = "{geoai_common_Illegal_request_parameters}")
        private BigDecimal maxSiteY;

        /**
         * 最低温坐标x
         */
        @NotNull(message = "{geoai_common_Illegal_request_parameters}")
        private BigDecimal minSiteX;

        /**
         * 最低温坐标y
         */
        @NotNull(message = "{geoai_common_Illegal_request_parameters}")
        private BigDecimal minSiteY;
    }
}
