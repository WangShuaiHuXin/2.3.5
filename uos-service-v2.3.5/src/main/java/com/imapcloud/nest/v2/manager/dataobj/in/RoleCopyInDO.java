package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.util.List;

/**
 * 角色复制
 *
 * @author boluo
 * @date 2022-05-20
 */
@Data
public class RoleCopyInDO {

    private List<Long> templateRoleIdList;
}
