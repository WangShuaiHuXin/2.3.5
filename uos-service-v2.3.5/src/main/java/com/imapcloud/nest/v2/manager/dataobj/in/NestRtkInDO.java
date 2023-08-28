package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;

/**
 * rtk
 *
 * @author boluo
 * @date 2022-08-25
 */
@ToString
public class NestRtkInDO {

    private NestRtkInDO() {}

    @Data
    public static class NestRtkEntityInDO {
        /**
         * 是否开启（0-开启；1-关闭）
         */
        private Integer enable;

        /**
         * 到期时间
         */
        private LocalDate expireTime;

        /**
         * 基站ID
         */
        private String baseNestId;
    }
}
