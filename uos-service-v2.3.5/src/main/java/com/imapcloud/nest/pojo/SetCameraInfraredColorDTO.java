package com.imapcloud.nest.pojo;

import com.imapcloud.nest.common.annotation.NestId;
import lombok.Data;

@Data
public class SetCameraInfraredColorDTO {

    @NestId
    private String nestId;
    private String color;
}
