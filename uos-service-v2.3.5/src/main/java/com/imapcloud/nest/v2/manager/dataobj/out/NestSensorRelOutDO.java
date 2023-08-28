package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;
import lombok.ToString;

/**
 * 传感器
 *
 * @author boluo
 * @date 2022-08-25
 */
@ToString
public class NestSensorRelOutDO {

    private NestSensorRelOutDO() {}

    @Data
    public static class NestSensorRelEntityOutDO {
        /**
         * 传感器id
         */
        private Integer sensorId;

        /**
         * 创造者id
         */
        private Long creatorId;

        /**
         * 基站ID
         */
        private String baseNestId;
    }
}
