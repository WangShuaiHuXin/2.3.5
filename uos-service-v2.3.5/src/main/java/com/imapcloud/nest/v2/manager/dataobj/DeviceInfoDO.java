package com.imapcloud.nest.v2.manager.dataobj;

import lombok.Data;

/**
 * @Classname DeviceInfoDO
 * @Description 设备信息
 * @Date 2023/4/7 9:30
 * @Author Carnival
 */
@Data
public class DeviceInfoDO {

    private String deviceCode;

    private String channelCode;

    private String serverId;
}
