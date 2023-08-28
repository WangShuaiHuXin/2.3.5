package com.imapcloud.nest.v2.manager.listener;

import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.service.SysUnitService;
import com.imapcloud.nest.v2.manager.dataobj.in.NestUnitOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.AccountOutDO;
import com.imapcloud.nest.v2.manager.event.NestCreatedEvent;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import com.imapcloud.nest.v2.service.NestAccountService;
import com.imapcloud.nest.v2.service.OrgRoleService;
import com.imapcloud.nest.v2.service.dto.out.OrgRoleOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 账号绑定基站事件
 *
 * @author Vastfy
 * @date 2022/6/6 19:30
 * @since 2.0.0
 */
@Deprecated
@Slf4j
//@Component
public class AccountBindNestListener implements ApplicationListener<NestCreatedEvent> {

    @Resource
    private OrgRoleService orgRoleService;

    @Resource
    private AccountServiceClient accountServiceClient;

    @Resource
    private NestAccountService nestAccountService;

    @Resource
    private SysUnitService sysUnitService;

    @Override
    public void onApplicationEvent(@NonNull NestCreatedEvent nestCreatedEvent) {
        Object source = nestCreatedEvent.getSource();
        if(source instanceof NestUnitOutDO){
            NestUnitOutDO nestUnitRef = (NestUnitOutDO) source;
            // 获取基站关联的单位ID
            List<String> unitIds = nestUnitRef.getUnitIds();
            // 单位和祖级单位且拥有默认角色的账号新增基站-账号关联关系
            if(!CollectionUtils.isEmpty(unitIds)){
                Set<String> superiorUnitIds = new HashSet<>();
                for (String unitId : unitIds) {
                    // 获取所有上级单位(含本级)
                    superiorUnitIds.addAll(getSuperiorUnitIds(unitId));
                }
                // 获取各单位的默认角色
                Set<String> unitDefaultRoleIds = fetchUnitDefaultRoleIds(superiorUnitIds);
                // 获取拥有指定角色的账号
                List<String> accountIds = fetchAccountIdsWithAnyRole(unitDefaultRoleIds);
                // 插入账号-基站关联关系
                nestAccountService.bindNest2Accounts(nestUnitRef.getNestId(), accountIds, true);
            }
        }
    }

    private List<String> getSuperiorUnitIds(String unitId){
        List<String> superiorUnitIds = new ArrayList<>();
        superiorUnitIds.add(unitId);
        // 获取所有上级单位
        List<String> sUnitIds = sysUnitService.getSuperiorOrgCodes(unitId);
        if(!CollectionUtils.isEmpty(sUnitIds)){
            superiorUnitIds.addAll(sUnitIds);
        }
        log.info("查询到单位ID【{}】的上级单位有：{}", unitId, superiorUnitIds);
        return superiorUnitIds;
    }

    private Set<String> fetchUnitDefaultRoleIds(Set<String> superiorUnitIds) {
        log.info("获取到所有上级单位（含本级）ID为：{}", superiorUnitIds);
        List<OrgRoleOutDTO> orgRoleRefs = orgRoleService.getOrgRoleRefs(superiorUnitIds, true);
        if(!CollectionUtils.isEmpty(orgRoleRefs)){
            return orgRoleRefs.stream()
                    .map(OrgRoleOutDTO::getRoleId)
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    private List<String> fetchAccountIdsWithAnyRole(Set<String> unitDefaultRoleIds) {
        log.info("获取到所有默认角色ID为：{}", unitDefaultRoleIds);
        Result<List<AccountOutDO>> result = accountServiceClient.fetchAccountsWithByRoleIds(new ArrayList<>(unitDefaultRoleIds));
        if(result.isOk()){
            List<AccountOutDO> data = result.getData();
            if(!CollectionUtils.isEmpty(data)){
                return data.stream()
                        .map(AccountOutDO::getAccountId)
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

}
