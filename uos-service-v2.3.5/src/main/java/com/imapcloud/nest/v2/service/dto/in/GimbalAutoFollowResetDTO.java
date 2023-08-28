package com.imapcloud.nest.v2.service.dto.in;

import com.imapcloud.nest.common.annotation.NestId;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GimbalAutoFollowResetDTO {
    private String nestId;
    private Float pitchAngle;
    private Float yamAngle;
    private Float ratio;
}
