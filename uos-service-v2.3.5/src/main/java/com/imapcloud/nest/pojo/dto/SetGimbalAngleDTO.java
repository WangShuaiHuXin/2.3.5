package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.common.annotation.NestId;
import lombok.Data;

@Data
public class SetGimbalAngleDTO {

    @NestId
    private String nestId;
    private Integer angle;
}
