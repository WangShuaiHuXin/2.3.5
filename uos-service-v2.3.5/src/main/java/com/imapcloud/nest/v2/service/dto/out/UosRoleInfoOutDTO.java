package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色信息
 * @author boluo
 * @date 2022-05-18
 */
@Data
public class UosRoleInfoOutDTO implements Serializable {

    private String roleId;

    private String roleName;

    private Integer roleType;

    private String appType;

    private Integer orgRoleType;

    private String orgCode;

    private String orgName;

}
