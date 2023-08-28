package com.imapcloud.nest.pojo.dto;

import lombok.Data;

/**
 * 电池状态
 *
 * @author wmin
 */
@Data
public class NestBatteryInfoDto {
    /**
     * 电池序号
     */
    private Integer index;
    /**
     * 00: 初始状态
     * 01: 正在充电
     * 02: 电池已充满
     * 03: 电池正在使用
     */
    private String state;
    /**
     * 电池伏数
     */
    private Double voltage;
    /**
     * 电量百分比
     */
    private Integer percentage;

    /**
     * 电池使用次数
     */
    private Integer chargeCount;

}
