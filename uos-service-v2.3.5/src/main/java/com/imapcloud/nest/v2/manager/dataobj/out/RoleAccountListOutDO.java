package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.util.List;

/**
 * 角色绑定的账户列表
 *
 * @author boluo
 * @date 2022-05-22
 */
@Data
public class RoleAccountListOutDO {

    private List<AccountInfo> accountInfoList;

    @Data
    public static class AccountInfo {
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
