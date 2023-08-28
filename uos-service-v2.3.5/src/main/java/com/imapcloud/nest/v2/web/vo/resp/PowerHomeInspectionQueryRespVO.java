package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("首页-根据设备查询巡检报告返回")
public class PowerHomeInspectionQueryRespVO implements Serializable {

    @ApiModelProperty("巡视点位")
    private String inspectionPoints;
    @ApiModelProperty("分析类型   字典 GEOAI_POWER_DISCERN_TYPE")
    private String analysisType;
    @ApiModelProperty("分析结论 字典 GEOAI_DIAL_DEVICE_STATE")
    private String analysisConclusion;
    @ApiModelProperty("分析结果")
    private String analysisResult;
    @ApiModelProperty("告警原因")
    private String alarmReason;
    private String photographyTime;
    private String url;
}
