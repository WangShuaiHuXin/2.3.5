package com.imapcloud.nest.v2.service.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class CpsMissionInDTO {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CpsMissionLandingInDTO {
        private String nestId;
        private Double latitude;
        private Double longitude;
        private Double altitude;
        private Boolean enable;

    }

    @Data
    public static class CpsMissionLandingStatusInDTO {
        private String nestId;
        private Boolean enable;
    }
}
