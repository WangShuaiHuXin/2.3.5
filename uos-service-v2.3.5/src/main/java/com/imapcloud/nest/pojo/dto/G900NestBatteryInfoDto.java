package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class G900NestBatteryInfoDto {
    /**
     * 可用电池数量
     */
    private Integer available = -1;

    /**
     * 正在使用的，或者是下一块的电池编号，（已经转换成数组编号）
     */
    private Integer readyUseBatteryIndex = -1;

    /**
     * 无人机电池百分比
     */
    private Integer aircraftBatteryChargeInPercent = 0;

    private List<M300BatteryInfoDTO> m300BatteryInfoDTOList;

}
