package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName GuidanceFlightReqVO.java
 * @Description GuidanceFlightReqVO
 * @createTime 2022年08月16日 14:34:00
 */
@Data
@ApiModel("推流模式")
public class StreamRespVO {
    @ApiModelProperty(value = "基站ID", position = 1, required = true, example = "10000")
    private String nestId;
    @ApiModelProperty(value = "推流模式", position = 2, required = true, example = "10000")
    private Integer mode;
    @ApiModelProperty(value = "推流模式描述", position = 3, required = true, example = "10000")
    private String modeStr;

}
