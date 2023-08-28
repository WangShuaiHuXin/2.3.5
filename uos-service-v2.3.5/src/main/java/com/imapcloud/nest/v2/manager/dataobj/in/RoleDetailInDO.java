package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.util.List;

/**
 * 角色详情
 *
 * @author boluo
 * @date 2022-05-20
 */
@Data
public class RoleDetailInDO {

    private List<Long> roleIdList;
}
