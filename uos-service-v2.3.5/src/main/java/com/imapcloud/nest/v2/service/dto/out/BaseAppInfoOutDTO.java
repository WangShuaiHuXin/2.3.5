package com.imapcloud.nest.v2.service.dto.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BaseAppInfoOutDTO {
    /**
     * 终端ID
     */
    private String appId;

    /**
     * 终端名字
     */
    private String name;

    private String orgCode;

    /**
     * 终端设备id
     */
    private String deviceId;

    /**
     * 查看监控的状态，0为不展示，默认为1展示
     */
    private Integer showStatus;

}
