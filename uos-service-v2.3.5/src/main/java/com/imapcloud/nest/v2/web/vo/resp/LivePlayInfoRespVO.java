package com.imapcloud.nest.v2.web.vo.resp;

import com.imapcloud.nest.v2.manager.dataobj.out.PullStreamUrlOutDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 设备点播地址信息
 * @author Vastfy
 * @date 2023/03/30 15:12
 * @since 2.3.2
 */
@ApiModel("视频点播信息")
@Data
public class LivePlayInfoRespVO implements Serializable {

    @ApiModelProperty(value = "推流协议", position = 1, required = true, example = "rtp")
    private String app;

    @ApiModelProperty(value = "推流名称", position = 2, required = true, example = "rtp")
    private String stream;

    @ApiModelProperty(value = "媒体服务ID", position = 3, required = true, example = "XXX")
    private String mediaServerId;

    @ApiModelProperty(value = "RTMP协议拉流地址", position = 4, required = true, example = "rtmp://139.9.93.41:1936/rtp/063337C1")
    private String rtmp;

    @ApiModelProperty(value = "RTSP协议拉流地址", position = 5, required = true, example = "rtsp://139.9.93.41:554/rtp/063337C1")
    private String rtsp;

    @ApiModelProperty(value = "HTTP协议拉流地址列表", position = 6, required = true, example = "[]")
    private PullStreamUrlOutDO http;

    @ApiModelProperty(value = "HTTPS协议拉流地址列表", position = 7, required = true, example = "[]")
    private PullStreamUrlOutDO https;

    @ApiModelProperty(value = "WS协议拉流地址列表", position = 8, required = true, example = "[]")
    private PullStreamUrlOutDO ws;

    @ApiModelProperty(value = "WSS协议拉流地址列表", position = 9, required = true, example = "[]")
    private PullStreamUrlOutDO wss;

}
