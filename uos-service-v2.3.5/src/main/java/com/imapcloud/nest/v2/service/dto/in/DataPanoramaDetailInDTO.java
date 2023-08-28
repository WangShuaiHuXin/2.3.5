package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaDetailReqVO.java
 * @Description DataPanoramaDetailReqVO
 * @createTime 2022年09月16日 11:27:00
 */
@Data
public class DataPanoramaDetailInDTO {

    @Data
    public static class DetailUploadInDTO {
        /**
         * 全景点主键
         */
        private String pointId;

        /**
         * 任务架次记录id
         */
        private String missionRecordsId;

        /**
         * 照片采集时间
         */
        private LocalDateTime acquisitionTime;

    }

    @Data
    public static class AddDetailInDTO {
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
    public static class QueryPageInDTO extends PageInfo {
        /**
         * 全景明细主键
         */
        private String detailId;

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
         * 开始时间
         */
        private LocalDate startTime;

        /**
         * 结束时间
         */
        private LocalDate endTime;

    }

    @Data
    public static class QueryInDTO {
        /**
         * 全景明细主键
         */
        private String detailId;

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
         * 照片采集时间
         */
        private LocalDateTime acquisitionTime;



    }

    @Data
    @Accessors(chain = true)
    public static class QueryOneInDTO {
        /**
         * 全景明细主键
         */
        private String detailId;

        /**
         * 全景点主键
         */
        private String pointId;

    }
}
