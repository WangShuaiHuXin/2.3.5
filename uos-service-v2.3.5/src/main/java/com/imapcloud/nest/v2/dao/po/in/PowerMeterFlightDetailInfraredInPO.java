package com.imapcloud.nest.v2.dao.po.in;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 红外测温
 *
 * @author boluo
 * @date 2023-03-10
 */
@ToString
public class PowerMeterFlightDetailInfraredInPO {

    private PowerMeterFlightDetailInfraredInPO() {}

    @Data
    public static class InfraredStateInPO {

        private String paramDetailId;

        private int temperatureState;

        private Integer taskState;
    }
}
