package com.imapcloud.nest.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class BatteryUseNumsDto {
    private String batteryNum;
    private Integer charges;
}
