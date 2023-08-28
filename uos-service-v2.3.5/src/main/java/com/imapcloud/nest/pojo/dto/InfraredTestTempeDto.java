package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.common.annotation.LimitVal;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InfraredTestTempeDto {

    @NotNull
    private String nestId;

    @NotNull
    private String measureMode;

    @NotNull
    private Double startX;

    @NotNull
    private Double startY;

    private Double endX;

    private Double endY;

    private Double textX;

    private Double textY;
}
