package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.util.List;

/**
 * 角色绑定的账户信息
 *
 * @author boluo
 * @date 2022-05-22
 */
@Data
public class RoleAccountInfoOutDTO {
    private List<Info> infoList;

    @Data
    public static class Info {
        /**
         * 账号ID
         */
        private Long accountId;

        /**
         * 角色ID
         */
        private Long roleId;
    }
}
