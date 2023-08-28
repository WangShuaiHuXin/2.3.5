package com.imapcloud.nest.v2.service.impl;

import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgAccountOutDO;
import com.imapcloud.nest.v2.manager.rest.OrgAccountManager;
import com.imapcloud.nest.v2.service.OrgAccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * org impl账户服务
 *
 * @author boluo
 * @date 2022-05-23
 */
@Slf4j
@Service
public class OrgAccountServiceImpl implements OrgAccountService {

    @Resource
    private OrgAccountManager orgAccountManager;

    @Override
    public Set<String> listOrgAccountIds(List<String> orgCodes, boolean filterPerms) {
        if(!CollectionUtils.isEmpty(orgCodes)){
            // 异步线程可能为空
            String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
            // 过滤权限：只能查看当前单位以及下级单位数据
            if(filterPerms && StringUtils.hasText(orgCode)){
                orgCodes = orgCodes.stream()
                        .filter(r -> r.startsWith(orgCode))
                        .distinct()
                        .collect(Collectors.toList());
            }
            List<OrgAccountOutDO> oaRefs = orgAccountManager.listOrgAccountRefInfos(orgCodes);
            if(!CollectionUtils.isEmpty(oaRefs)){
                return oaRefs.stream().map(OrgAccountOutDO::getAccountId).collect(Collectors.toSet());
            }
        }
        return Collections.emptySet();
    }

    @Override
    public List<OrgAccountOutDO> listOrgAccountRefs(List<String> orgCodes) {
        return orgAccountManager.listOrgAccountRefInfos(orgCodes);
    }

}
