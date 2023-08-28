package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaDetailRespVO.java
 * @Description DataPanoramaDetailRespVO
 * @createTime 2022年09月16日 11:27:00
 */
@Data
public class DataPanoramaDetailRespVO implements Serializable {

    @Data
    public static class QueryPageRespVO implements Serializable {

        /**
         * 全景明细主键
         */
        private String detailId;

        /**
         * 全景明细数据路径-记录目录位置
         */
        private String detailUrl;

        /**
         * 全景点主键
         */
        private String pointId;

        /**
         * 架次id
         */
        private String missionId;

        /**
         * 任务架次记录id
         */
        private String missionRecordsId;

        /**
         * 航线id
         */
        private String airLineId;

        /**
         * 航点id
         */
        private String airPointId;

        /**
         * 架次号
         */
        private String missionFlyIndex;

        /**
         * 任务架次开始时间
         */
        private LocalDateTime missionRecordTime;

        /**
         * 照片采集时间
         */
        private LocalDateTime acquisitionTime;

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
    public static class QueryLessRespVO implements Serializable {

        /**
         * 全景明细主键
         */
        private String detailId;

        /**
         * 全景明细数据路径-记录目录位置
         */
        private String detailUrl;

        /**
         * 全景点主键
         */
        private String pointId;

        /**
         * 架次id
         */
        private String missionId;

        /**
         * 任务架次记录id
         */
        private String missionRecordsId;

        /**
         * 航线id
         */
        private String airLineId;

        /**
         * 航点id
         */
        private String airPointId;

        /**
         * 架次号
         */
        private String missionFlyIndex;

        /**
         * 任务架次开始时间
         */
        private LocalDateTime missionRecordTime;

        /**
         * 照片采集时间
         */
        private LocalDateTime acquisitionTime;

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
    public static class QueryRespVO implements Serializable {

        /**
         * 全景明细主键
         */
        private String detailId;

        /**
         * 全景明细数据路径-记录目录位置
         */
        private String detailUrl;

        /**
         * 全景点主键
         */
        private String pointId;

        /**
         * 架次id
         */
        private String missionId;

        /**
         * 任务架次记录id
         */
        private String missionRecordsId;

        /**
         * 航线id
         */
        private String airLineId;

        /**
         * 航点id
         */
        private String airPointId;

        /**
         * 架次号
         */
        private String missionFlyIndex;

        /**
         * 任务架次开始时间
         */
        private LocalDateTime missionRecordTime;

        /**
         * 照片采集时间
         */
        private LocalDateTime acquisitionTime;

        /**
         * 单位编码
         */
        private String orgCode;

        /**
         * 基站id
         */
        private String baseNestId;
    }

}
