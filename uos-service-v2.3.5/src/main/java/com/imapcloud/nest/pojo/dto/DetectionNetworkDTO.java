package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.common.annotation.NestId;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class DetectionNetworkDTO {

    @NestId
    @NotNull
    private String nestId;
    private Integer pingCount;
    private Integer pingSize;
    private Boolean speed;
}
