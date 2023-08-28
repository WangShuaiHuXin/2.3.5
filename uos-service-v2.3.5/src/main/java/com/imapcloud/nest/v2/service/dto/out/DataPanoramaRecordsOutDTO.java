package com.imapcloud.nest.v2.service.dto.out;

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
public class DataPanoramaRecordsOutDTO implements Serializable {

    @Data
    public static class RecordsOutDTO implements Serializable {
        /**
         * 任务名
         */
        private String taskName;

        /**
         * 任务ID
         */
        private String taskId;

        /**
         * 标签ID
         */
        private String tagId;

        /**
         * 标签名
         */
        private String tagName;

        /**
         * 任务时间
         */
        private LocalDateTime startTime;

        /**
         * 基站ID
         */
        private String baseNestId;

        /**
         * 基站名称
         */
        private String baseNestName;

        /**
         * 架次记录ID
         */
        private String missionRecordsId;

        /**
         * 全景点总数量
         */
        private Integer pointCount;

        /**
         * 全景点已上传数量
         */
        private Integer pointUploadNum;

        /**
         * 航线类型
         */
        private Integer taskType;
        /**
         * 航线序号
         */
        private Integer missionSeqId;
        /**
         * 变电站类型
         */
        private Integer subType;
        /**
         * 架次号
         */
        private String missionFlyIndex;
    }

    @Data
    public static class RecordsPageOutDTO implements Serializable {
        /**
         * 标签ID
         */
        private String tagId;

        /**
         * 标签名
         */
        private String tagName;

        /**
         * 单位
         */
        private String orgCode;

        /**
         * 任务名
         */
        private String missionName;

        /**
         * 架次飞行次数
         */
        private String flyIndex;

        /**
         * 任务时间
         */
        private LocalDateTime startTime;

        /**
         * 基站ID
         */
        private String baseNestId;

        /**
         * 基站名称
         */
        private String baseNestName;

        /**
         * 架次记录ID
         */
        private String missionRecordsId;

    }



    @Data
    public static class PicOutDTO implements Serializable {
        /**
         * 照片id
         */
        private String missionPhotoId;

        /**
         * 照片名
         */
        private String missionPhotoName;

        /**
         * 缩略图
         */
        private String thumbnailUrl;

        /**
         * 照片原图
         */
        private String photoUrl;

        /**
         * 航点ID
         */
        private String airPointId;

        /**
         * 航点序号
         */
        private Integer airPointIndex;

        /**
         * 全景名
         */
        private String pointName;

        /**
         * 序号
         */
        private Integer waypointsIndex;

    }

    @Data
    public static class AirPointOutDTO implements Serializable {
        /**
         * 航点id
         */
        private String airPointId;

        /**
         * 航点名
         */
        private String panoName;

        /**
         * 航点序号
         */
        private String airPointIndex;

        /**
         * 经度
         */
        private BigDecimal longtitude;

        /**
         * 纬度
         */
        private BigDecimal latitude;

        /**
         * 高度
         */
        private BigDecimal altitude;
    }

    @Data
    public static class TaskOutDTO implements Serializable {
        /**
         * taskId
         */
        private String id;
        /**
         * 任务名称
         */
        private String name;

        /**
         * 任务类型Id
         */
        private Integer type;

        private String baseNestId;

        /**
         * 单位编码
         */
        private String orgCode;

        private String tagId;

    }


}
