package com.imapcloud.nest.v2.service.dto.in;

import com.imapcloud.nest.common.annotation.LimitVal;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName GuidanceFlightReqVO.java
 * @Description GuidanceFlightReqVO
 * @createTime 2022年08月16日 14:34:00
 */
@Data
@Accessors(chain = true)
public class GimbalInDTO {

    private String nestId;

    private BigDecimal pitchAngle;

    private BigDecimal yamAngle;
    /**
     * 俯仰角度
     */
    private Boolean pitch;
    /**
     * 朝向角度
     */
    private Boolean yam;
    /**
     * 无人机标识
     */
    private Integer which;


}
