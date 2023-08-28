package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJIPilotCommonResultOutDTO.java
 * @Description DJIPilotCommonResultOutDTO
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DJIPilotCommonResultReqVO implements Serializable {

    @ApiModelProperty(value = "无人机sn", position = 1, required = true, example = "")
    @NotBlank(message = "无人机sn不能为空")
    private String uavSn;

    @ApiModelProperty(value = "基站sn", position = 2, required = true, example = "")
    @NotBlank(message = "基站sn不能为空")
    private String nestSn;


}
