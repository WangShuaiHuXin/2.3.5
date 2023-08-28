package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaPointRespVO.java
 * @Description DataPanoramaPointRespVO
 * @createTime 2022年09月16日 11:23:00
 */
@Data
public class DataPanoramaPointRespVO implements Serializable {

    @Data
    public static class QueryLessRespVO implements Serializable {

        /**
         * 全景点主键
         */
        @ApiModelProperty(value = "全景点主键", position = 1, example = "")
        private String pointId;

        /**
         * 全景点名
         */
        @ApiModelProperty(value = "全景点名", position = 1, example = "")
        private String pointName;

        /**
         * 全景点高度
         */
        @ApiModelProperty(value = "全景点高度", position = 1, example = "")
        private BigDecimal pointHeight;

        /**
         * 全景点经度
         */
        @ApiModelProperty(value = "全景点经度", position = 1, example = "")
        private BigDecimal pointLongitude;

        /**
         * 全景点纬度
         */
        @ApiModelProperty(value = "全景点纬度", position = 1, example = "")
        private BigDecimal pointLatitude;

        /**
         * 全景点地址信息
         */
        @ApiModelProperty(value = "全景点地址信息", position = 1, example = "")
        private String address;

        /**
         * 全景明细数据路径-记录目录位置
         */
        @ApiModelProperty(value = "全景明细URL", position = 1, example = "")
        private String detailUrl;

        /**
         * 单位编码
         */
        @ApiModelProperty(value = "单位编码", position = 1, example = "")
        private String orgCode;

    }

    @Data
    public static class QueryRespVO implements Serializable {

        /**
         * 全景点主键
         */
        @ApiModelProperty(value = "全景点主键", position = 1, example = "")
        private String pointId;

        /**
         * 全景点名
         */
        @ApiModelProperty(value = "全景点名", position = 1, example = "")
        private String pointName;

        /**
         * 全景点高度
         */
        @ApiModelProperty(value = "全景点高度", position = 1, example = "")
        private BigDecimal pointHeight;

        /**
         * 全景点经度
         */
        @ApiModelProperty(value = "全景点经度", position = 1, example = "")
        private BigDecimal pointLongitude;

        /**
         * 全景点纬度
         */
        @ApiModelProperty(value = "全景点纬度", position = 1, example = "")
        private BigDecimal pointLatitude;

        /**
         * 全景点类型-0自动创建、1手工创建
         */
        @ApiModelProperty(value = "全景点类型", position = 1, example = "0")
        private Integer pointType;

        /**
         * 全景点地址信息
         */
        @ApiModelProperty(value = "全景点地址信息", position = 1, example = "")
        private String address;

        /**
         * 标签id
         */
        @ApiModelProperty(value = "标签id", position = 1, example = "")
        private String tagId;

        /**
         * 任务id
         */
        @ApiModelProperty(value = "任务id", position = 1, example = "")
        private String taskId;

        /**
         * 航线id
         */
        @ApiModelProperty(value = "航线id", position = 1, example = "")
        private String airLineId;

        /**
         * 航点id
         */
        @ApiModelProperty(value = "航线id", position = 1, example = "")
        private String airPointId;

        /**
         * 单位编码
         */
        @ApiModelProperty(value = "单位编码", position = 1, example = "")
        private String orgCode;

        /**
         * 基站id
         */
        @ApiModelProperty(value = "基站id", position = 1, example = "")
        private String baseNestId;

        /**
         * 全景明细数据路径-记录目录位置
         */
        @ApiModelProperty(value = "全景明细URL", position = 1, example = "")
        private String detailUrl;

    }

    @Data
    public static class QueryPageRespVO implements Serializable {

        /**
         * 全景点主键
         */
        @ApiModelProperty(value = "全景点主键", position = 1, example = "")
        private String pointId;

        /**
         * 全景点名
         */
        @ApiModelProperty(value = "全景点名", position = 1, example = "")
        private String pointName;

        /**
         * 全景点高度
         */
        @ApiModelProperty(value = "全景点高度", position = 1, example = "")
        private BigDecimal pointHeight;

        /**
         * 全景点经度
         */
        @ApiModelProperty(value = "全景点经度", position = 1, example = "")
        private BigDecimal pointLongitude;

        /**
         * 全景点纬度
         */
        @ApiModelProperty(value = "全景点纬度", position = 1, example = "")
        private BigDecimal pointLatitude;

        /**
         * 全景点类型-0自动创建、1手工创建
         */
        @ApiModelProperty(value = "全景点类型", position = 1, example = "0")
        private Integer pointType;

        /**
         * 全景点地址信息
         */
        @ApiModelProperty(value = "全景点地址信息", position = 1, example = "")
        private String address;

        /**
         * 标签id
         */
        @ApiModelProperty(value = "标签id", position = 1, example = "")
        private String tagId;

        /**
         * 任务id
         */
        @ApiModelProperty(value = "任务id", position = 1, example = "")
        private String taskId;

        /**
         * 照片采集时间
         */
        @ApiModelProperty(value = "照片采集时间", position = 1, example = "")
        private LocalDateTime acquisitionTime;

//        /**
//         * 航线id
//         */
//        @ApiModelProperty(value = "航线id", position = 1, example = "")
//        private String airLineId;

        /**
         * 航点id
         */
        @ApiModelProperty(value = "航线id", position = 1, example = "")
        private String airPointId;

        /**
         * 全景明细数据路径-记录目录位置
         */
        @ApiModelProperty(value = "全景明细URL", position = 1, example = "")
        private String detailUrl;

//        /**
//         * 单位编码
//         */
//        @ApiModelProperty(value = "单位编码", position = 1, example = "")
//        private String orgCode;
//
//        /**
//         * 基站id
//         */
//        @ApiModelProperty(value = "基站id", position = 1, example = "")
//        private String baseNestId;

    }

}
