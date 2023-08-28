package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ParseEmqxLogDto {
    @NotNull
    private String nestId;
    @NotNull
    private Long startTime;
    @NotNull
    private Long endTime;

    private Boolean all = false;
}
