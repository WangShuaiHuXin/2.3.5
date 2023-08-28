package com.imapcloud.sdk.pojo.djido;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DjiAirportOrganizationBindInDO {
    private BindDevice[] bindDevices;

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class BindDevice{

        private String sn;

        private String deviceBindingCode;

        private String organizationId;

        private String deviceCallsign;

        private String deviceModelKey;

    }

}

