package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;

import java.io.Serializable;

@Data
public class CpsMissionRespVO implements Serializable {

    @Data
    public static class CpsMissionAlternateLandingInfoRespVO {
        private Double latitude;
        private Double longitude;
        private Double altitude;
        private Boolean enable;
    }

    @Data
    public static class CpsMissionAlternateAltitudeInfoRespVO {
        private Double altitude;
    }
}
