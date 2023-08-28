package com.imapcloud.nest.v2.dao.po.in;

import lombok.Data;
import lombok.NoArgsConstructor;

public class CpsMissionInPO {

    @Data
    public static class CpsMissionLandingInPO {
        private String nestId;
        private Double latitude;
        private Double longitude;
        private Double altitude;
        private Boolean enable;
    }
}
