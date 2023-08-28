package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 角色修改信息
 * @author Vastfy
 * @date 2022/08/12 14:12
 * @since 2.0.0
 */
@Data
public class RoleModificationInDO implements Serializable {

    private String roleName;

    private String orgCode;

    private Integer orgRoleType;

    private List<String> pageResourceIds;

}
