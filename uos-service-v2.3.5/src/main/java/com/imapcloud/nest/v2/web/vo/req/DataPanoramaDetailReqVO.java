package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
public class DataPanoramaDetailReqVO{

    /**
     * @deprecated 2.2.3，使用新接口{@link PanoramaDataDetailReqVO}替代，将在后续版本删除
     */
    @Deprecated
    @Data
    public static class DetailUploadReqVO {
        /**
         * 全景点主键
         */
        @NotEmpty(message = "全景点主键不能为空!")
        private String pointId;

        /**
         * 任务架次记录id
         */
        private String missionRecordsId;

        /**
         * 照片采集时间
         */
        @NotNull(message = "照片采集时间不能为空!!")
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime acquisitionTime;

    }

    @Data
    public static class QueryPageReqVO extends PageInfo {
        /**
         * 全景明细主键
         */
        @ApiModelProperty(value = "全景明细主键", position = 1, example = "")
        private String detailId;

        /**
         * 全景点主键
         */
        @ApiModelProperty(value = "全景点主键", position = 1, example = "")
        private String pointId;

        /**
         * 架次id
         */
        @ApiModelProperty(value = "架次id", position = 1, example = "")
        private String missionId;

        /**
         * 任务架次记录id
         */
        @ApiModelProperty(value = "任务架次记录id", position = 1, example = "")
        private String missionRecordsId;

        /**
         * 开始时间
         */
        @ApiModelProperty(value = "开始时间", position = 1, example = "")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startTime;

        /**
         * 结束时间
         */
        @ApiModelProperty(value = "结束时间", position = 1, example = "")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endTime;

    }

    @Data
    public static class QueryReqVO {
        /**
         * 全景明细主键
         */
        @ApiModelProperty(value = "全景明细主键", position = 1, example = "")
        private String detailId;

        /**
         * 全景点主键
         */
        @ApiModelProperty(value = "全景点主键", position = 1, example = "")
        private String pointId;

        /**
         * 架次id
         */
        @ApiModelProperty(value = "架次id", position = 1, example = "")
        private String missionId;

        /**
         * 任务架次记录id
         */
        @ApiModelProperty(value = "任务架次记录id", position = 1, example = "")
        private String missionRecordsId;

//        /**
//         * 航线id
//         */
//        @ApiModelProperty(value = "航线id", position = 1, example = "")
//        private String airLineId;
//
//        /**
//         * 航点id
//         */
//        @ApiModelProperty(value = "航点id", position = 1, example = "")
//        private String airPointId;

//        /**
//         * 照片采集时间
//         */
//        @ApiModelProperty(value = "照片采集时间", position = 1, example = "")
//        private LocalDateTime acquisitionTime;

        /**
         * 开始时间
         */
        @ApiModelProperty(value = "开始时间", position = 1, example = "")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate startTime;

        /**
         * 结束时间
         */
        @ApiModelProperty(value = "结束时间", position = 1, example = "")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endTime;

    }

    @Data
    public static class QueryOneReqVO {

        /**
         * 全景明细主键
         */
        @ApiModelProperty(value = "全景明细主键", position = 1, example = "")
        private String detailId;
        /**
         * 全景点主键
         */
        @ApiModelProperty(value = "全景点主键", position = 1, example = "")
        private String pointId;

    }
}
