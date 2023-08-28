package com.imapcloud.sdk.pojo.djido;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
public class DjiAirportBindStatusInDO {
    private Device[] devices;
    @Data
    public static class Device{
        private String sn;

    }

}

