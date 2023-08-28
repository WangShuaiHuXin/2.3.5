package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.common.annotation.NestId;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ControlGimbalDto {

    @NestId
    @NotNull
    private String nestId;
    private Float pitchAngle;
    private Float yamAngle;
    /**
     * 俯仰角度
     */
    @NotNull
    private Boolean pitch;
    /**
     * 朝向角度
     */
    @NotNull
    private Boolean yam;
}
