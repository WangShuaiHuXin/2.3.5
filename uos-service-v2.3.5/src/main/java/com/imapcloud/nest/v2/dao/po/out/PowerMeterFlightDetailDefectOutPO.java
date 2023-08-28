package com.imapcloud.nest.v2.dao.po.out;

import lombok.Data;
import lombok.ToString;

/**
 * 缺陷识别
 *
 * @author boluo
 * @date 2023-03-08
 */
@ToString
public final class PowerMeterFlightDetailDefectOutPO {

    @Data
    public static class StatisticsOutPO {

        private int deviceState;

        private Long num;
    }
}
