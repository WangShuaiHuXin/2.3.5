package com.imapcloud.sdk.pojo.djido;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DjiAirportBindStatusDO {
    private BindStatus[] bindStatus;

    private String environment;
    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class BindStatus{
        private String sn;
        private Boolean isDeviceBindOrganization;

        private String organizationId;
        private String organizationName;

        private String deviceCallsign;
    }

}

