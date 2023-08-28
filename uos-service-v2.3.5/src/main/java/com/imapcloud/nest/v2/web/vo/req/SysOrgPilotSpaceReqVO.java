package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class SysOrgPilotSpaceReqVO {

    @ApiModelProperty(value = "所属单位", position = 1, required = false, example = "")
    private String orgCode;

    @ApiModelProperty(value = "工作空间", position = 2, required = false, example = "")
    private String workSpaceId;
}
