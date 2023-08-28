package com.imapcloud.nest.v2.dao.po.out;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceStateStatisticPO implements Serializable {
    private String deviceState;
    private Integer counts;
}
