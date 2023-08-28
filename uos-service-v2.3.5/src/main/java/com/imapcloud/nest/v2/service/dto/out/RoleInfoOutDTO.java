package com.imapcloud.nest.v2.service.dto.out;

import com.imapcloud.nest.v2.common.enums.OrgRoleTypeEnum;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色信息
 * @author boluo
 * @date 2022-05-18
 */
@Data
public class RoleInfoOutDTO implements Serializable {

    private Long roleId;

    private String roleName;

    /**
     * 角色类型【0：普通管理员；1：默认管理员】
     * @see OrgRoleTypeEnum
     */
    private Integer roleType;
}
