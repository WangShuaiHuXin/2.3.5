package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AirPushStreamDTO {
    @NotNull
    private String nestId;
    @NotNull
    private String pushUrl;
}
