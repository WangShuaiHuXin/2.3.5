package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

public class CpsMissionOutDTO {

    @Data
    public static class CpsMissionAlternateInfoOutDTO {
        private Double latitude;
        private Double longitude;
        private Double altitude;
        private Boolean enable;
    }
}
