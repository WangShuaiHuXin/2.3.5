package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 单位角色关系
 */
@Data
public class OrgRoleOutDTO implements Serializable {

    private String orgCode;

    private String roleId;

    private String roleName;

    /**
     * 单位角色类型
     */
    private int type;

    private String appType;
    /**
     * 单位角色类型【1：默认（不能修改其权限，允许上级管理员可修改；0：其他】
     */
    private Integer orgRoleType;
}
