package com.imapcloud.nest.v2.web.vo.req;

import com.imapcloud.nest.common.annotation.LimitVal;
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
public class StreamReqVO {
    @ApiModelProperty(value = "基站ID", position = 1, required = true, example = "10000")
    private String nestId;
    @ApiModelProperty(value = "经度", position = 2, required = true, example = "10000")
    private Integer mode;
    @ApiModelProperty(value = "机位标识，0：G503外其他基站， 1、2、3：标识G503的三个机位", position = 2, example = "1", required = true)
    @LimitVal(values = {"0","1","2","3"}, message = "which值不在范围内")
    private Integer uavWhich;
}
