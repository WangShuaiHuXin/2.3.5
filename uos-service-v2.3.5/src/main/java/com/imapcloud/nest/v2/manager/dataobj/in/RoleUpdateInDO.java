package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

/**
 * 角色更新
 *
 * @author boluo
 * @date 2022-05-22
 */
@Data
public class RoleUpdateInDO {

    private Long roleId;

    private String roleName;
}
