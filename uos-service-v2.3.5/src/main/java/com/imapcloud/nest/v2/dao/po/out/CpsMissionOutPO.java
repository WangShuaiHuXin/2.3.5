package com.imapcloud.nest.v2.dao.po.out;

import lombok.Data;

public class CpsMissionOutPO {
    @Data
    public static class CpsMissionPositionOutPO {
        private Double latitude;
        private Double longitude;
    }

    @Data
    public static class CpsMissionAltitudeOutPO {
        private Double altitude;
    }

    @Data
    public static class CpsMissionEnableOutPO {
        public Boolean enable;
    }
}
