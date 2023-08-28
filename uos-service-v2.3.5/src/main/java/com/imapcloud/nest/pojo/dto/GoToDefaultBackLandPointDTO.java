package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.common.annotation.NestId;
import lombok.Data;

@Data
public class GoToDefaultBackLandPointDTO {

    @NestId
    private String nestId;
    private Double alt;
}
