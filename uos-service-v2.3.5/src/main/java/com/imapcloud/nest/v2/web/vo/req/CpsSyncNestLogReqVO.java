package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * CPS同步基站日志信息
 * @author Vastfy
 * @date 2023/2/23 14:58
 * @since 2.3.0
 */
@ApiModel("CPS同步基站日志信息")
@Data
public class CpsSyncNestLogReqVO implements Serializable {

    @ApiModelProperty(value = "基站UUID", position = 1, required = true)
    @NotNull(message = "基站UUID不能为空")
    private String nestUuid;

    @ApiModelProperty(value = "基站位置编号，基站为G503机型时，该字段必填", position = 2, required = true)
    private Integer which;

    @ApiModelProperty(value = "基站日志压缩包实际名称", position = 3, required = true)
    @NotNull(message = "基站日志名称不能为空")
    private String logFilename;

    @ApiModelProperty(value = "基站日志最终存储地址", position = 4, required = true)
    @NotNull(message = "基站日志存储地址不能为空")
    private String storageUrl;

    @ApiModelProperty(value = "基站日志模块", position = 5, required = true)
    @NotNull(message = "基站日志模块不能为空")
    private String module;

}
