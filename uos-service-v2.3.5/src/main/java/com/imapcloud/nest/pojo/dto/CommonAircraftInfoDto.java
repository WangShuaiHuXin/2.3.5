package com.imapcloud.nest.pojo.dto;

import com.imapcloud.sdk.pojo.constant.NetworkChannelMsgEnum;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CommonAircraftInfoDto {
    private String cameraMode;
    private Integer photoStoring;
    private Integer recording;
    /**
     * 桨叶是否在转动，1-转动，0-不转动
     */
    private Integer areMotorsOn = 0;

    /**
     * 是否推流，0 - 未推流，1 - 推流
     */
    private Integer liveStreaming = 0;

    /**
     * 推流模式
     */
    private Integer liveMode = 0;

    /**
     * rtk是否可用
     */
    private Integer rtkEnable = -1;

    /**
     * rtk是否准备就绪
     */
    private Integer rtkReady = -1;

    /**
     * RTK网络通道信息
     */
    private NetworkChannelMsgEnum rtkNetworkChannelMsg;

    private Double cameraZoomRatio = 1.0;


}
