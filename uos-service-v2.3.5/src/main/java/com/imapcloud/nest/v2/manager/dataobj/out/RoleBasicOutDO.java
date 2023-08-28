package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色信息
 * @author Vastfy
 * @date 2022/08/26 15:06
 * @since 2.0.0
 */
@Data
public class RoleBasicOutDO implements Serializable {

    private String roleId;

    private String roleName;

    private String appType;

    private Integer orgRoleType;

}
