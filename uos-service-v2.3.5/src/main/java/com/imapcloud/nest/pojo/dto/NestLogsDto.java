package com.imapcloud.nest.pojo.dto;


import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class NestLogsDto {

    @NotNull
    private String nestId;

    @Min(1)
    @NotNull
    private Integer currentPage;

    @Max(100)
    @Min(1)
    @NotNull
    private Integer pageSize;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;


    /**
     * uavWhich
     */
    private Integer uavWhich;
}
