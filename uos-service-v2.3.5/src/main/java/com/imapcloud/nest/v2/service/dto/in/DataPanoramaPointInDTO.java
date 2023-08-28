package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaPointReqVO.java
 * @Description DataPanoramaPointReqVO
 * @createTime 2022年09月16日 11:23:00
 */
@ToString
public class DataPanoramaPointInDTO {

    @Data
    public static class AddPointInDTO {
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
    }

    @Data
    public static class UpdatePointInDTO {

        /**
         * 全景点ID
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
         * 标签id
         */
        private String tagId;

//        /**
//         * 航线id
//         */
//        private String airLineId;
//
//        /**
//         * 航点id
//         */
//        private String airPointId;


    }

    @Data
    public static class QueryInDTO {
        /**
         * 全景点名
         */
        private String pointName;

        /**
         * 标签id
         */
        private String tagId;

        /**
         * 航线id
         */
        private String airLineId;

        /**
         * 航点id
         */
        private String airPointId;

        /**
         * 架次ID
         */
        private String missionId;

        /**
         * 架次记录ID
         */
        private String missionRecordsId;

        /**
         * 全景点类型
         */
        private Integer pointType;

        /**
         * 开始时间
         */
        private LocalDate startTime;

        /**
         * 结束时间
         */
        private LocalDate endTime;

        /**
         * 组织编码
         */
        private String orgCode;


    }

    @Data
    public static class QueryPageInDTO extends PageInfo {
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
         * 标签id
         */
        private String tagId;

        /**
         * 航线id
         */
        private String airLineId;

        /**
         * 航点id
         */
        private String airPointId;

        /**
         * 开始时间
         */
        private LocalDate startTime;

        /**
         * 结束时间
         */
        private LocalDate endTime;

        /**
         * 架次ID
         */
        private String missionId;

        /**
         * 架次记录ID
         */
        private String missionRecordsId;

        /**
         * 组织编码
         */
        private String orgCode;


    }

    @Data
    @Accessors(chain = true)
    public static class QueryOneInDTO {
        /**
         * 全景点主键
         */
        private String pointId;
        /**
         * 架次ID
         */
        private String missionId;
        /**
         * 架次记录ID
         */
        private String missionRecordsId;

    }



}
