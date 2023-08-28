package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.util.List;

/**
 * 角色详情
 *
 * @author boluo
 * @date 2022-05-20
 */
@Data
public class RoleDetailOutDO {

    /**
     * 信息列表
     */
    private List<Info> infoList;

    @Data
    public static class Info {

        /**
         * 角色ID
         */
        private Long roleId;

        /**
         * 角色名称
         */
        private String roleName;
    }
}
