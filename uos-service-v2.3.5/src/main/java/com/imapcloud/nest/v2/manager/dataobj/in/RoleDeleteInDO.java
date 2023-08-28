package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.util.List;

/**
 * 删除角色
 *
 * @author boluo
 * @date 2022-05-22
 */
@Data
public class RoleDeleteInDO {

    private List<Long> roleIdList;
}
