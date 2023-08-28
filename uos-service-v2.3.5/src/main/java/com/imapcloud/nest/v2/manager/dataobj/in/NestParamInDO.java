package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;
import lombok.ToString;

/**
 * 在做巢参数
 *
 * @author boluo
 * @date 2022-08-25
 */
@ToString
public class NestParamInDO {

    private NestParamInDO() {}

    @Data
    public static class NestParamEntityInDO {

        /**
         * 基站ID
         */
        private String baseNestId;

        private Long creatorId;
    }

    @Data
    public static class BatteryInDO {

        /**
         * 基站业务ID
         */
        private String nestId;

        /**
         * 告警循环次数
         */
        private Integer alarmCircleNum;

        /**
         * 禁用循环次数
         */
        private Integer forbiddenCircleNum;
    }
}
