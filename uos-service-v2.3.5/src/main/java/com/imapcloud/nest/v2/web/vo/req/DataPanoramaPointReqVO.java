package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
public class DataPanoramaPointReqVO{

    @Data
    public static class AddPointReqVO {
        /**
         * 全景点名
         */
        @ApiModelProperty(value = "全景点名", position = 1, example = "")
        @NotNull(message = "全景点名称不能为空!")
        private String pointName;

        /**
         * 全景点高度
         */
        @ApiModelProperty(value = "全景点高度", position = 1, example = "")
        @NotNull(message = "全景点高度不能为空!")
        private BigDecimal pointHeight;

        /**
         * 全景点经度
         */
        @ApiModelProperty(value = "全景点经度", position = 1, example = "")
        @NotNull(message = "全景点经度不能为空!")
        private BigDecimal pointLongitude;

        /**
         * 全景点纬度
         */
        @ApiModelProperty(value = "全景点纬度", position = 1, example = "")
        @NotNull(message = "全景点纬度不能为空!")
        private BigDecimal pointLatitude;

        /**
         * 全景点类型-0自动创建、1手工创建
         */
        @ApiModelProperty(value = "全景点类型-0自动创建、1手工创建", position = 1, example = "0")
        @NotNull(message = "全景点类型不能为空!")
        private Integer pointType;

        /**
         * 全景点地址信息
         */
        @ApiModelProperty(value = "全景点地址信息", position = 1, example = "")
        @NotNull(message = "全景点地址信息不能为空!")
        private String address;

        /**
         * 标签id
         */
        @ApiModelProperty(value = "标签id", position = 1, example = "")
        @NotEmpty(message = "标签ID不能为空!")
        private String tagId;

        /**
         * 任务id
         */
        @ApiModelProperty(value = "任务id", position = 1, example = "")
        private String taskId;

//        /**
//         * 航线id
//         */
//        @ApiModelProperty(value = "航线id", position = 1, example = "")
//        private String airLineId;

        /**
         * 航点id
         */
        @ApiModelProperty(value = "航点id", position = 1, example = "")
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
    }

    @Data
    public static class UpdatePointReqVO {
        /**
         * 全景点ID
         */
        @ApiModelProperty(value = "全景点ID", position = 1, example = "")
        @NotNull(message = "全景点ID不能为空！")
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
         * 标签id
         */
        @ApiModelProperty(value = "标签id", position = 1, example = "")
        private String tagId;

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


    }

    @Data
    public static class QueryReqVO {
        /**
         * 全景点名
         */
        @ApiModelProperty(value = "全景点名", position = 1, example = "")
        private String pointName;

        /**
         * 标签id
         */
        @ApiModelProperty(value = "标签id", position = 1, example = "")
        private String tagId;

        /**
         * 航线id
         */
        @ApiModelProperty(value = "航线id", position = 1, example = "")
        private String airLineId;

        /**
         * 航点id
         */
        @ApiModelProperty(value = "航点id", position = 1, example = "")
        private String airPointId;

        /**
         * 架次记录ID
         */
        @ApiModelProperty(value = "架次记录ID", position = 1, example = "传了该参数，返回的全景URL为当前架次记录下时间倒序第一条")
        private String missionRecordsId;

        /**
         * 全景点类型-0自动创建、1手工创建
         */
        @ApiModelProperty(value = "全景点类型-0自动创建、1手工创建", position = 1, example = "0")
        private Integer pointType;

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

        /**
         * 组织编码
         */
        @ApiModelProperty(value = "组织编码", position = 1, example = "")
        private String orgCode;




    }

    @Data
    public static class QueryPageReqVO extends PageInfo {
        /**
         * 全景点名
         */
        @ApiModelProperty(value = "全景点名", position = 1, example = "")
        private String pointName;

        /**
         * 标签id
         */
        @ApiModelProperty(value = "标签id", position = 1, example = "")
        private String tagId;

        /**
         * 航线id
         */
        @ApiModelProperty(value = "航线id", position = 1, example = "")
        private String airLineId;

        /**
         * 航点id
         */
        @ApiModelProperty(value = "航点id", position = 1, example = "")
        private String airPointId;

        /**
         * 全景点类型-0自动创建、1手工创建
         */
        @ApiModelProperty(value = "全景点类型-0自动创建、1手工创建", position = 1, example = "0")
        private Integer pointType;

        /**
         * 架次ID
         */
        @ApiModelProperty(value = "架次ID", position = 1, example = "传了该参数，返回的全景URL为当前架次下时间倒序第一条")
        private String missionId;

        /**
         * 架次记录ID
         */
        @ApiModelProperty(value = "架次记录ID", position = 1, example = "传了该参数，返回的全景URL为当前架次记录下时间倒序第一条")
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

        /**
         * 组织编码
         */
        @ApiModelProperty(value = "组织编码", position = 1, example = "")
        private String orgCode;

    }

    @Data
    public static class QueryOneReqVO {
        /**
         * 全景点主键
         */
        @ApiModelProperty(value = "全景点主键", position = 1, example = "")
        private String pointId;

        /**
         * 架次记录ID
         */
        @ApiModelProperty(value = "架次记录ID", position = 1, example = "传了该参数，返回的全景URL为当前架次记录下时间倒序第一条")
        private String missionRecordsId;

    }



}
