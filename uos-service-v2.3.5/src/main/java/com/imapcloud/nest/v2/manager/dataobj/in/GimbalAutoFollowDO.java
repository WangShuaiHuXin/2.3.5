package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

@Data
public class GimbalAutoFollowDO {
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
