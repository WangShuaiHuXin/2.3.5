package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;

@Data
@ApiModel("首页-根据设备查询巡检报告入参")
@Validated
public class PowerHomeInspectionQueryByReqVO implements Serializable {

    @ApiModelProperty("设备id")
    private String equipmentId;
    @ApiModelProperty("开始时间")
    private String beginTime;
    @ApiModelProperty("结束时间")
    private String endTime;
    @ApiModelProperty("单位编码")
    private String orgCode;
}
