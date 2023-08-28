package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 标注地址信息
 * @author Vastfy
 * @date 2023/02/21 09:52
 * @since 2.2.3
 */
@ApiModel("标注地址信息")
@Data
public class MarkAddrInfoReqVO implements Serializable {

    @NotNull(message = "markId 不能为空")
    private String markId;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String addr;

    @ApiModelProperty(value = "地址图片路径", position = 4, required = true)
    private String addrPicPath;

}
