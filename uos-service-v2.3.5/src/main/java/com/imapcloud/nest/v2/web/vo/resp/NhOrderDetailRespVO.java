package com.imapcloud.nest.v2.web.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.checkerframework.checker.formatter.qual.Format;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel("查询工单详情返回值")
public class NhOrderDetailRespVO {
    @ApiModelProperty(value = "经度", position = 4, required = false, example = "")
    private Double longitude;
    @ApiModelProperty(value = "纬度", position = 5, required = false, example = "")
    private Double latitude;
    @ApiModelProperty(value = "标题", name = "title", example = "南海任务")
    private String title;
    @ApiModelProperty(value = "单位编码", name = "orgCode", example = "000")
    private String orgCode;
    @ApiModelProperty(value = "单位名称", name = "orgName", example = "中科云图")
    private String orgName;
    @ApiModelProperty(value = "巡检开始时间", name = "inspectionBeginTime", example = "2022-01-12")
    private String inspectionBeginTime;
    @ApiModelProperty(value = "巡检结束时间", name = "inspectionEndTime", example = "2022-01-12")
    private String inspectionEndTime;
    @ApiModelProperty(value = "工单Id", name = "orderId")
    private String orderId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createdTime;
    @ApiModelProperty(value = "工单状态 字典：GEOAI_WORD_ORDER_STATUS", name = "orderStatus")
    private int orderStatus;
    @ApiModelProperty(value = "优先程度 字典：GEOAI_PRIORITY_DEGREE ", name = "degree")
    private int degree;
    @ApiModelProperty(value = "编码 GO0000001", name = "gdCode")
    private String gdCode;
    @ApiModelProperty(value = "工单描述", name = "desc")
    private String desc;
    @ApiModelProperty(value = "工单类型 字典：GEOAI_WORD_ORDER_TYPE ", name = "orderType")
    private int orderType;
    private List<Process> process;

    private String creatorName;

    private String verificationMethod;
    private String frequency ;
    @Data
    public static class Process {
        private String desc;
        private String nodeId;
        private String userName;
        private Boolean flag;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        private LocalDateTime operationTime;
        private String  remark;
        private String mobile;
    }
}
