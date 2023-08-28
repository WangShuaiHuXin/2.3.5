package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 表计数据详情设备状态统计信息
 * @author Vastfy
 * @date 2022/12/04 17:59
 * @since 2.1.5
 */
@Data
public class MeterDeviceStateStatsOutDTO implements Serializable {

    private String deviceState;

    private int total;

}
