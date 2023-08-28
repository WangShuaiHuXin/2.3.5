package com.imapcloud.nest.v2.web.vo.req;

import com.imapcloud.nest.common.annotation.NestId;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GimbalAutoFollowResetVO {

    @NestId
    @NotNull
    private String nestId;
    @NotNull
    private Float pitchAngle;
    @NotNull
    private Float yamAngle;
    @NotNull
    private Float ratio;
}
