package com.imapcloud.nest.v2.web.vo.resp;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * AI视频流识别信息
 * @author Vastfy
 * @date 2022/12/22 17:38
 * @since 2.1.7
 */
@ApiModel("AI视频流识别信息")
@Data
public class AiStreamingInfoRespVO implements Serializable {

    @ApiModelProperty(value = "基站ID", position = 1, required = true, example = "9527777")
    private String nestId;

    @ApiModelProperty(value = "基站平台编号", position = 2, required = true, example = "0")
    private Integer which;

    @ApiModelProperty(value = "飞行架次记录ID", position = 3, required = true, example = "9527")
    private String missionRecordId;

    @ApiModelProperty(value = "视频流AI识别功能ID", position = 4, required = true, example = "9527666")
    private String functionId;

    @ApiModelProperty(value = "是否开启告警【默认为false】", position = 5, example = "true")
    private boolean enableAlarm;

    @ApiModelProperty(value = "AI识别流拉流地址【算法侧使用】", position = 6, example = "rtmp://media.geoai.com/live/ai/9527678?missionRecordId=9527")
    private String aiStreamPullUrl;

    @ApiModelProperty(value = "AI识别流推流地址【客户端使用】", position = 7, example = "http://media.geoai.com/live/ai/9527678.flv?missionRecordId=9527")
    private String aiStreamPushUrl;

}
