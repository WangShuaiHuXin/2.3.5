package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class QueryMqttLogFromMongoDto {
    private String nestUuid;
    private String traceId;
    private String nodeId;
    @NotNull
    private Long startTime;
    @NotNull
    private Long endTime;
    @NotNull
    private Integer currentPage;
    @NotNull
    private Integer pageSize;
}
