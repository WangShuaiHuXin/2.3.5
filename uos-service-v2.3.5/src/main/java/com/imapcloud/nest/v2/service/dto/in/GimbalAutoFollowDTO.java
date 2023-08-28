package com.imapcloud.nest.v2.service.dto.in;

import com.imapcloud.nest.common.annotation.NestId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class GimbalAutoFollowDTO {
    private String nestId;
    private Float startX;
    private Float startY;
    private Float endX;
    private Float endY;
    private Boolean showFrame;
    private Boolean moveHeading;
    private Integer zoomType;
    private Float zoomRatio;
}
