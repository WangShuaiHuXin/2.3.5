package com.imapcloud.nest.pojo.dto;

import lombok.Data;

@Data
public class BaseNestInfoDto {
    /**
     * 遥控器电量
     */
    private Integer rcPercentage = 0;
    private Integer rcCharging = 0;
    /**
     * cps剩余空间
     */
    private Double cpsMemoryUseRate = 0.0;
    /**
     * 无人机剩余空间
     */
    private Double airSdMemoryUseRate = 0.0;

    /**
     * 0->没有维保
     * 1->维保中
     */
    private Integer maintenanceState = 0;
}
