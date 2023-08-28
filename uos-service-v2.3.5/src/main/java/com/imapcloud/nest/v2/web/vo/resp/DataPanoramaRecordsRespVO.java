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
public class DataPanoramaRecordsRespVO implements Serializable {

    @Data
    public static class RecordsRespVO implements Serializable {
        /**
         * 架次名
         */
        @ApiModelProperty(value = "架次名", position = 1, example = "")
        private String missionName;

        /**
         * 标签ID
         */
        @ApiModelProperty(value = "标签ID", position = 1, example = "")
        private String tagId;

        /**
         * 标签名
         */
        @ApiModelProperty(value = "标签名", position = 1, example = "")
        private String tagName;

        /**
         * 任务时间
         */
        @ApiModelProperty(value = "任务时间", position = 1, example = "")
        private LocalDateTime startTime;

        /**
         * 基站ID
         */
        @ApiModelProperty(value = "基站ID", position = 1, example = "")
        private String baseNestId;

        /**
         * 基站名称
         */
        @ApiModelProperty(value = "基站名称", position = 1, example = "")
        private String baseNestName;

        /**
         * 架次记录ID
         */
        @ApiModelProperty(value = "架次记录ID", position = 1, example = "")
        private String missionRecordsId;

        /**
         * 架次飞行次数
         */
        @ApiModelProperty(value = "架次飞行次数", position = 1, example = "")
        private String missionFlyIndex;

        /**
         * 单位
         */
        @ApiModelProperty(value = "单位", position = 1, example = "")
        private String orgCode;
    }

    @Data
    public static class PicRespVO implements Serializable {
        /**
         * 照片id
         */
        @ApiModelProperty(value = "照片id", position = 1, example = "")
        private String missionPhotoId;

        /**
         * 照片名
         */
        @ApiModelProperty(value = "照片名", position = 1, example = "")
        private String missionPhotoName;

        /**
         * 缩略图
         */
        @ApiModelProperty(value = "缩略图", position = 1, example = "")
        private String thumbnailUrl;

        /**
         * 照片原图
         */
        @ApiModelProperty(value = "照片原图", position = 1, example = "")
        private String photoUrl;

        /**
         * 镜头类型
         */
        @ApiModelProperty(value = "镜头类型", position = 1, example = "0-普通可见光、1-广角、2-变焦、3-红外")
        private String lenType;

        /**
         * 航点ID
         */
        @ApiModelProperty(value = "航点ID", position = 1, example = "")
        private String airPointId;

        /**
         * 航点序号
         */
        @ApiModelProperty(value = "航点序号", position = 1, example = "0")
        private Integer airPointIndex;

        /**
         * 全景名
         */
        @ApiModelProperty(value = "全景名", position = 1, example = "")
        private String pointName;

        /**
         * 序号
         */
        @ApiModelProperty(value = "序号", position = 1, example = "0")
        private Integer waypointsIndex;

    }

    @Data
    public static class AirPointRespVO implements Serializable {
        /**
         * 航点id
         */
        @ApiModelProperty(value = "航点id", position = 1, example = "")
        private String airPointId;

        /**
         * 航点名
         */
        @ApiModelProperty(value = "航点名", position = 1, example = "")
        private String panoName;

        /**
         * 航点序号
         */
        @ApiModelProperty(value = "航点序号", position = 1, example = "")
        private String airPointIndex;

        /**
         * 经度
         */
        @ApiModelProperty(value = "经度", position = 1, example = "")
        private BigDecimal longtitude;

        /**
         * 纬度
         */
        @ApiModelProperty(value = "纬度", position = 1, example = "")
        private BigDecimal latitude;

        /**
         * 高度
         */
        @ApiModelProperty(value = "高度", position = 1, example = "")
        private BigDecimal altitude;
    }

    @Data
    public static class TaskRespVO implements Serializable {
        /**
         * taskId
         */
        @ApiModelProperty(value = "任务Id", position = 1, example = "")
        private String taskId;
        /**
         * 任务名称
         */
        @ApiModelProperty(value = "任务名称", position = 1, example = "")
        private String name;

        /**
         * 任务类型Id
         */
        @ApiModelProperty(value = "任务类型Id", position = 1, example = "0")
        private Integer type;

        @ApiModelProperty(value = "基站Id", position = 1, example = "")
        private String baseNestId;

        /**
         * 单位编码
         */
        @ApiModelProperty(value = "单位编码", position = 1, example = "")
        private String orgCode;

        @ApiModelProperty(value = "标签ID", position = 1, example = "")
        private String tagId;


    }



}
