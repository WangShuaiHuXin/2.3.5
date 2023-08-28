package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.util.List;

/**
 * 角色权限修改
 *
 * @author boluo
 * @date 2022-05-24
 */
@Data
public class RolePageEditInDO {

    private String roleId;

    private List<String> pageResourceIdList;
}
