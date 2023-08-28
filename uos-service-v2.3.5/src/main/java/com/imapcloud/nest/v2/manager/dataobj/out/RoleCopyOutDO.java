package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.util.Map;

/**
 * 角色复制
 *
 * @author boluo
 * @date 2022-05-20
 */
@Data
public class RoleCopyOutDO {

    /**
     * 角色模板与新角色的id对应关系
     * key:templateRoleId
     * value:newRoleId
     */
    private Map<Long, Long> roleIdMap;
}
