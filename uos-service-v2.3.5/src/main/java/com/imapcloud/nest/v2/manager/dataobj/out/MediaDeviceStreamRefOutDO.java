package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

/**
 * 设备与流关系
 *
 * @author boluo
 * @date 2022-08-25
 */
@Data
public class MediaDeviceStreamRefOutDO {

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 流ID
     */
    private String streamId;
}
