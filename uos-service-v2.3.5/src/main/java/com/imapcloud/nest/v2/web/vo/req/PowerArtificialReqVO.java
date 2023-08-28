package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("匹配设备")
public class PowerArtificialReqVO implements Serializable {
    @ApiModelProperty("本地航点台账主键")
    private String wayPointStationId;
    @ApiModelProperty("本地设备台账主键")
    private String equipmentId;
    @ApiModelProperty("本地部件库台账主键")
    private String componentId;
}
