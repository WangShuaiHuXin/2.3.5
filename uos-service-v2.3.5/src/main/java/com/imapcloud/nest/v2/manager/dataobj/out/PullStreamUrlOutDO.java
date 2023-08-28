package com.imapcloud.nest.v2.manager.dataobj.out;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 拉流地址
 * @author Vastfy
 * @date 2023/3/30 17:33
 * @since 1.0.0
 */
@Data
public class PullStreamUrlOutDO implements Serializable {

    @ApiModelProperty(value = "flv格式", position = 2, required = true, example = "ws://139.9.93.41:80/rtp/063337C1.live.flv")
    private String flv;

    @ApiModelProperty(value = "fmp4格式", position = 3, required = true, example = "ws://139.9.93.41:80/rtp/063337C1.live.mp4")
    private String fmp4;

    @ApiModelProperty(value = "hls格式", position = 3, required = true, example = "ws://139.9.93.41:80/rtp/063337C1.live.hls")
    private String hls;

    @ApiModelProperty(value = "ts格式", position = 3, required = true, example = "ws://139.9.93.41:80/rtp/063337C1.live.ts")
    private String ts;

}
