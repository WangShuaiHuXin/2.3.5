package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 基站简要信息
 * @author Vastfy
 * @date 2022/08/12 13:52
 * @since 2.0.0
 */
@ApiModel("基站简要信息")
@Data
public class NestSimpleRespVO implements Serializable {

    @ApiModelProperty(value = "基站ID", position = 1, required = true, example = "111")
    private String nestId;

    @ApiModelProperty(value = "基站编号", position = 1, required = true, example = "G900-9")
    private String nestNumber;

    @ApiModelProperty(value = "基站唯一ID", position = 2, required = true, example = "fb16f2eb-f7da-4bd9-b2a6-fd4de1156268")
    private String nestUuid;

    @ApiModelProperty(value = "基站名称", position = 3, required = true, example = "广州G900基站9号")
    private String name;

}
