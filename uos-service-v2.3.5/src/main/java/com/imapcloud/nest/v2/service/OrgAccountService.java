package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.manager.dataobj.out.OrgAccountOutDO;

import java.util.List;
import java.util.Set;

/**
 * org账户服务
 *
 * @author boluo
 * @date 2022-05-23
 */
public interface OrgAccountService {

    /**
     * 根据单位ID批量获取单位挂载的账号ID列表
     * 会自动过滤当前登录人的数据权限
     * @param orgCodes    单位编码列表
     * @param filterPerms    过滤权限
     * @return  用户ID列表
     */
    Set<String> listOrgAccountIds(List<String> orgCodes, boolean filterPerms);

    List<OrgAccountOutDO> listOrgAccountRefs(List<String> orgCodes);

}
