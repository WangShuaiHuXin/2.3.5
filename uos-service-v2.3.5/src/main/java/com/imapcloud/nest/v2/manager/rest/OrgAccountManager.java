package com.imapcloud.nest.v2.manager.rest;

import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.manager.dataobj.out.AccountOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgAccountOutDO;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UOS单位-账号数据管理器
 * @author Vastfy
 * @date 2022/8/23 17:21
 * @since 2.0.0
 */
@Component
public class OrgAccountManager {

    @Resource
    private AccountServiceClient accountServiceClient;

    public List<OrgAccountOutDO> listOrgAccountRefInfos(List<String> orgCodes){
        if(!CollectionUtils.isEmpty(orgCodes)){
            Result<List<AccountOutDO>> result = accountServiceClient.fetchAccountsByOrgCodes(orgCodes);
            if(result.isOk() && !CollectionUtils.isEmpty(result.getData())){
                return result.getData()
                        .stream()
                        .map(r -> {
                            OrgAccountOutDO oaRef = new OrgAccountOutDO();
                            oaRef.setAccountId(r.getAccountId());
                            oaRef.setOrgCode(r.getOrgCode());
                            return oaRef;
                        })
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

}
