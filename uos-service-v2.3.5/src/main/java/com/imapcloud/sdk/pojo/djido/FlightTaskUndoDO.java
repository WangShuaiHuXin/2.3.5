package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

import java.util.List;

@Data
public class FlightTaskUndoDO {
    /**
     * 任务ID
     */
    private List<String> flightIdList;
}
