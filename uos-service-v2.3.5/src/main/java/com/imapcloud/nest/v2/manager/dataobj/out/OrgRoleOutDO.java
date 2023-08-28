package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询org信息
 *
 * @author boluo
 * @date 2022-05-23
 */

@Data
public class OrgRoleOutDO implements Serializable {

    private String orgCode;

    private String roleId;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 单位角色类型【1：默认（不能修改其权限，允许上级管理员可修改；0：其他】
     */
    private Integer orgRoleType;

    private String appType;

}
