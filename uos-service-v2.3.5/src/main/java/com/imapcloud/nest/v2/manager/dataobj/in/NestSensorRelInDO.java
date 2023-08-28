package com.imapcloud.nest.v2.manager.dataobj.in;

import com.imapcloud.nest.v2.manager.dataobj.BaseInDO;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * 传感器
 *
 * @author boluo
 * @date 2022-08-25
 */
@ToString
public class NestSensorRelInDO {

    private NestSensorRelInDO() {}

    @Data
    public static class NestSensorRelEntityInDO {
        /**
         * 传感器id
         */
        private Integer sensorId;

        /**
         * 创造者id
         */
        private String creatorId;

        /**
         * 基站ID
         */
        private String baseNestId;
    }
}
