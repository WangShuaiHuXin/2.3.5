package com.imapcloud.nest.v2.manager.dataobj.out;

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
public class NestRtkOutDO {

    private NestRtkOutDO() {}

    @Data
    public static class NestRtkEntityOutDO {
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

        public boolean rtkEnable() {
            return this.enable != null && this.enable == 0;
        }
    }
}
