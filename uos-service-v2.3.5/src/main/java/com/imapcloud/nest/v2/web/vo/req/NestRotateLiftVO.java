package com.imapcloud.nest.v2.web.vo.req;

import com.imapcloud.nest.common.annotation.NestId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel("G503平台旋转条件")
@Data
public class NestRotateLiftVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "基站id", position = 1, example = "1", required = true)
    @NotNull(message = "基站id不能为null")
    @NestId
    private String nestId;

    /**
     * 00：平台重置
     * 11：1号平台朝上
     * 10：1号平台朝下
     * 21：2号平台朝上
     * 20：2号平台朝下
     * 31：3号平台朝上
     * 30：3号平台朝下
     */
    @ApiModelProperty(value = "旋转动作。00：平台重置，11：1号平台朝上，21：2号平台朝上，20：2号平台朝下，31：3号平台朝上，30：3号平台朝下", position = 1, example = "1", required = true)
    @NotNull(message = "action不能为null")
    private String action;
}
