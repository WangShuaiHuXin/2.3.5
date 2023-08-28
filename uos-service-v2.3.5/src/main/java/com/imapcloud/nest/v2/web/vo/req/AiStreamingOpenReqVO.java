package com.imapcloud.nest.v2.web.vo.req;

import com.imapcloud.nest.common.annotation.NestId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * AI视频流开启请求信息
 * @author Vastfy
 * @date 2022/12/22 17:38
 * @since 2.1.7
 */
@ApiModel("AI视频流开启请求信息")
@Data
public class AiStreamingOpenReqVO implements Serializable {

    @ApiModelProperty(value = "基站ID", position = 1, required = true, example = "9527777")
    @NotNull(message = "基站ID不能为空")
    @NestId
    private String nestId;

    @ApiModelProperty(value = "平台编号【默认为0】", position = 2, example = "0")
    private int which;

    @ApiModelProperty(value = "飞行架次记录ID", position = 3, required = true, example = "9527")
    @NotNull(message = "飞行架次记录ID不能为空")
    private String missionRecordId;

    @ApiModelProperty(value = "视频流AI识别功能ID", position = 4, required = true, example = "9527666")
    @NotNull(message = "识别功能ID不能为空不能为空")
    private String functionId;

    @ApiModelProperty(value = "是否开启告警【默认为false】", position = 5, example = "true")
    private boolean enableAlarm;

}
