package com.imapcloud.nest.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AirLinkInfoDTO {
    /**
     * 遥控器链路信号
     */
    private Integer wirelessLinkSignal = 0;
    /**
     * 4G链路信号
     */
    private Integer lteLinkSignal = 0;
    /**
     * 飞机通讯链路模式
     */
    private String airLinkMode = "UNKNOWN";
    /**
     * 遥控器链路是否连接
     */
    private Boolean wirelessLinkConnected = false;
    /**
     * 4G链路是否连接
     */
    private Boolean lteLinkConnected = false;
}
