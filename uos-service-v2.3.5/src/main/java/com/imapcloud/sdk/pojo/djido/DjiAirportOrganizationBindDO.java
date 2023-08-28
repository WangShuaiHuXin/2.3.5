package com.imapcloud.sdk.pojo.djido;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DjiAirportOrganizationBindDO {
    private ErrInfos[] errInfos;

    private String environment;

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class ErrInfos{

        private String sn;

        private Integer errCode;

    }

}

