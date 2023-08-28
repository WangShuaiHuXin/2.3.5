package com.imapcloud.nest.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Data
public class G600NestBatteryInfoDto {

    /**
     * 可用电池数量
     */
    private Integer available = -1;

    /**
     * 正在使用的，或者是下一块的电池编号，（已经转换成数组编号）
     */
    private Integer readyUseBatteryIndex = -1;

    /**
     * 具体电池信息
     */
    private List<NestBatteryInfoDto> nestBatteryInfoDtoList;
}
