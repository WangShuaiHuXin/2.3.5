package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaPointOutDTO.java
 * @Description DataPanoramaPointOutDTO
 * @createTime 2022年09月16日 11:23:00
 */
@Data
public class DataPanoramaPointOutDTO implements Serializable {

    @Data
    public static class QueryLessOutDTO implements Serializable {

        /**
         * 全景点主键
         */
        private String pointId;

        /**
         * 全景点名
         */
        private String pointName;

        /**
         * 全景点高度
         */
        private BigDecimal pointHeight;

        /**
         * 全景点经度
         */
        private BigDecimal pointLongitude;

        /**
         * 全景点纬度
         */
        private BigDecimal pointLatitude;

        /**
         * 全景点地址信息
         */
        private String address;

        /**
         * 全景明细数据路径-记录目录位置
         */
        private String detailUrl;

        /**
         * 单位编码
         */
        private String orgCode;

    }

    @Data
    public static class QueryOutDTO implements Serializable {

        /**
         * 全景点主键
         */
        private String pointId;

        /**
         * 全景点名
         */
        private String pointName;

        /**
         * 全景点高度
         */
        private BigDecimal pointHeight;

        /**
         * 全景点经度
         */
        private BigDecimal pointLongitude;

        /**
         * 全景点纬度
         */
        private BigDecimal pointLatitude;

        /**
         * 全景点类型-0自动创建、1手工创建
         */
        private Integer pointType;

        /**
         * 全景点地址信息
         */
        private String address;

        /**
         * 标签id
         */
        private String tagId;

        /**
         * 任务id
         */
        private String taskId;

        /**
         * 航线id
         */
        private String airLineId;

        /**
         * 航点id
         */
        private String airPointId;

        /**
         * 单位编码
         */
        private String orgCode;

        /**
         * 基站id
         */
        private String baseNestId;

        /**
         * 全景明细数据路径-记录目录位置
         */
        private String detailUrl;

    }

    @Data
    public static class QueryPageOutDTO implements Serializable {

        /**
         * 全景点主键
         */
        private String pointId;

        /**
         * 全景点名
         */
        private String pointName;

        /**
         * 全景点高度
         */
        private BigDecimal pointHeight;

        /**
         * 全景点经度
         */
        private BigDecimal pointLongitude;

        /**
         * 全景点纬度
         */
        private BigDecimal pointLatitude;

        /**
         * 全景点类型-0自动创建、1手工创建
         */
        private Integer pointType;

        /**
         * 全景点地址信息
         */
        private String address;

        /**
         * 标签id
         */
        private String tagId;

        /**
         * 任务id
         */
        private String taskId;

        /**
         * 全景明细数据路径-记录目录位置
         */
        private String detailUrl;

        /**
         * 照片采集时间
         */
        private LocalDateTime acquisitionTime;

        /**
         * 航线id
         */
        private String airLineId;

        /**
         * 航点id
         */
        private String airPointId;

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
