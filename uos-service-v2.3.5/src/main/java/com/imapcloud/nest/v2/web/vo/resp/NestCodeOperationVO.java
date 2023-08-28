package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ApiModel("基站信息（含固件版本信息）")
public class NestCodeOperationVO implements Serializable {
    private static final long serialVersionUID = -1774653306534520669L;

    @ApiModelProperty(value = "账户名称", position = 1, required = true, example = "某某")
    private String username;
    @ApiModelProperty(value = "操作指令", position = 2, required = true, example = "一键返航")
    private String nestCodeName;
    @ApiModelProperty(value = "操作时间", position = 3, required = true, example = "2022-09-23 13:42:00")
    private String operationTime;
}
