package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色新建信息
 * @author Vastfy
 * @date 2022/08/12 14:12
 * @since 2.0.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class RoleCreationInDO extends RoleModificationInDO {

    private String appType;

    private Integer roleType;

}
