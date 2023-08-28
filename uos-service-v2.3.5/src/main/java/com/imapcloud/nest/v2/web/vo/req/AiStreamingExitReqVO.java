package com.imapcloud.nest.v2.web.vo.req;

import com.imapcloud.nest.common.annotation.NestId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * AI视频流退出请求信息
 * @author Vastfy
 * @date 2022/12/22 17:38
 * @since 2.1.7
 */
@ApiModel("AI视频流退出请求信息")
@Data
public class AiStreamingExitReqVO implements Serializable {

    @ApiModelProperty(value = "基站ID", position = 1, required = true, example = "9527777")
    @NotNull(message = "基站ID不能为空")
    @NestId
    private String nestId;

    @ApiModelProperty(value = "平台编号【基站型号为G503时，此参数必填。默认为0】", position = 2, example = "0")
    private int which;

}
