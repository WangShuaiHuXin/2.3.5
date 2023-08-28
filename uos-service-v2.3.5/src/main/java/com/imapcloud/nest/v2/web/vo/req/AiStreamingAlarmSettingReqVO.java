package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * AI视频流告警设置请求信息
 * @author Vastfy
 * @date 2022/12/22 17:38
 * @since 2.1.7
 */
@ApiModel("AI视频流告警设置请求信息")
@Data
public class AiStreamingAlarmSettingReqVO implements Serializable {

    @ApiModelProperty(value = "基站ID", position = 1, required = true, example = "9527777")
    @NotNull(message = "基站ID不能为空")
    private String nestId;

    @ApiModelProperty(value = "平台编号【基站为G503时，此参数必填。默认为0】", position = 2, example = "0")
    private int which;

    @ApiModelProperty(value = "是否开启告警【默认为false】", position = 4, example = "true")
    private boolean enableAlarm;

}
