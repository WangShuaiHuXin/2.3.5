package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.util.List;

/**
 * 基站用户信息
 *
 * @author boluo
 * @date 2022-05-23
 */
@Data
public class NestAccountInfoOutDTO {

    private List<Info> infoList;

    @Data
    public static class Info {
        /**
         * 基站自增id
         */
        private String nestId;

        /**
         * 账号id
         */
        private String accountId;

        /**
         * 用户操控基站状态 0：不可控 1：可控
         */
        private boolean nestControlStatus;
    }
}
