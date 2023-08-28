package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色信息
 * @author boluo
 * @date 2022-05-18
 */
@Data
public class RoleInfoOutDO implements Serializable {

    private String roleId;

    private String roleName;

    private String appType;

}
