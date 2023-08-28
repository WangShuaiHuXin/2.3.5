package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.util.List;

/**
 * 单位的角色信息
 *
 * @author boluo
 * @date 2022-05-20
 */
@Data
public class OrgRoleInfoOutDTO {

    /**
     * 信息列表
     */
    private List<Info> infoList;

    @Data
    public static class Info {

        /**
         * 角色ID
         */
        private String roleId;

        /**
         * 角色名称
         */
        private String roleName;

        /**
         * 角色类型【1：默认（不能修改其权限，允许上级管理员可修改；0：其他】
         */
        private Integer roleType;

        public boolean admin() {
            return roleType == 1;
        }
    }
}
