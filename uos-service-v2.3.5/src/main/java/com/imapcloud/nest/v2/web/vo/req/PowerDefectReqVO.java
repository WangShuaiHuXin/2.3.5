package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 缺陷识别
 *
 * @author boluo
 * @date 2023-03-07
 */
@ToString
public final class PowerDefectReqVO {

    private PowerDefectReqVO() {
    }

    @Data
    public static class ListReqVO extends PageInfo {

        private Integer deviceState;

        /**
         * 缺陷识别状态
         */
        private Integer defectState;

        private Integer verificationState;
    }

    @Data
    public static class AddMarkReqVO {

        private String defectMarkId;

        /**
         * 坐标x1
         */
        @NotNull(message = "siteX1 is null")
        private BigDecimal siteX1;

        /**
         * 坐标y1
         */
        @NotNull(message = "siteY1 is null")
        private BigDecimal siteY1;

        /**
         * 坐标x2
         */
        @NotNull(message = "siteX2 is null")
        private BigDecimal siteX2;

        /**
         * 坐标y2
         */
        @NotNull(message = "siteY2 is null")
        private BigDecimal siteY2;

        /**
         * 设备状态【取字典 geoai_dial_device_state 数据项值】
         */
        @NotNull(message = "deviceState is null")
        private Integer deviceState;

        /**
         * 行业类型【取字典 geoai_industry_type 值】
         */
        @NotNull(message = "industryType is null")
        private Integer industryType;

        /**
         * 问题类型
         */
        @NotNull(message = "topicProblemId is null")
        private String topicProblemId;

        /**
         * 问题类型名称，ai识别没有匹配时，显示ai问题名称
         */
        @NotNull(message = "topicProblemName is null")
        private String topicProblemName;

        /**
         * 裁剪宽度
         */
        @NotNull(message = "relY is null")
        private Double relY;

        /**
         * 裁剪高度
         */
        @NotNull(message = "relX is null")
        private Double relX;

        /**
         * 放大缩小比例
         */
        @NotNull(message = "picScale is null")
        private Double picScale;
    }
}
