package com.imapcloud.nest.pojo.dto;

import lombok.Data;

@Data
public class GimbalAutoFollowStateDTO {
    private Boolean enable = false;
    private String state = "";
    private Location location = new Location();
    private Integer autoFollowMode = 0;

    @Data
    public static class Location{
        private Float startX = 0.0F;
        private Float startY = 0.0F;
        private Float endX = 0.0F;
        private Float endY = 0.0F;
    }
}
