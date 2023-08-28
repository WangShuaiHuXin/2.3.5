package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class IntelligentShutdownDTO {
    @NotNull
    private String nestId;
    /**
     * true-开启
     * false-关闭
     */
    private Boolean enable;

    /**
     * 电量值（百分比）
     */
    @Min(5)
    @Max(40)
    private Integer threshold;
}
