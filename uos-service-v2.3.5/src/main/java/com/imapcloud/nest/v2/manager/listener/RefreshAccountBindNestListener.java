package com.imapcloud.nest.v2.manager.listener;

import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.manager.dataobj.out.AccountOutDO;
import com.imapcloud.nest.v2.manager.event.NestUnitChangedEvent;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import com.imapcloud.nest.v2.service.NestAccountService;
import com.imapcloud.nest.v2.service.OrgAccountService;
import com.imapcloud.nest.v2.service.OrgRoleService;
import com.imapcloud.nest.v2.service.dto.out.OrgRoleOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 刷新账号绑定基站事件
 * @author Vastfy
 * @date 2022/07/14 13:30
 * @since 1.9.7
 */
@Slf4j
@Component
public class RefreshAccountBindNestListener implements ApplicationListener<NestUnitChangedEvent> {

    @Resource
    private OrgRoleService orgRoleService;

    @Resource
    private AccountServiceClient accountServiceClient;

    @Resource
    private NestAccountService nestAccountService;

    @Resource
    private OrgAccountService orgAccountService;

    @Override
    public void onApplicationEvent(@NonNull NestUnitChangedEvent nestUnitChangedEvent) {
        Object source = nestUnitChangedEvent.getSource();
        if(source instanceof NestUnitChangedEvent.NestChangedInfo){
            NestUnitChangedEvent.NestChangedInfo nestChangedInfo = (NestUnitChangedEvent.NestChangedInfo) source;
            List<LinkedList<String>> increasedUnitChains = nestChangedInfo.getIncreasedUnitChains();
            // 新增的单位链，拥有单位默认管理员角色的账号都需要新增与基站的关联关系
            if(!CollectionUtils.isEmpty(increasedUnitChains)){
                Set<String> incrUnitIds = new HashSet<>();
                for (LinkedList<String> increasedUnitChain : increasedUnitChains) {
                    incrUnitIds.addAll(increasedUnitChain);
                }
                log.info("#RefreshAccountBindNestListener.onApplicationEvent# incrUnitIds={}", incrUnitIds);
                Set<String> roleIds = fetchUnitDefaultRoleIds(incrUnitIds);
                log.info("#RefreshAccountBindNestListener.onApplicationEvent# roleIds={}", roleIds);
                if(!CollectionUtils.isEmpty(roleIds)){
                    List<String> accountIds = fetchAccountIdsWithAnyRole(roleIds);
                    nestAccountService.bindNest2Accounts(nestChangedInfo.getNestId(), accountIds, true);
                }
            }
            // 移除的单位链，移除单位下所有用户与该基站的关联关系（存在单位冲突则忽略）
            List<LinkedList<String>> decreasedUnitChains = nestChangedInfo.getDecreasedUnitChains();
            if(!CollectionUtils.isEmpty(decreasedUnitChains)){
                Set<String> expectDecrUnitIds = new HashSet<>();
                for (LinkedList<String> decreasedUnitChain : decreasedUnitChains) {
                    expectDecrUnitIds.addAll(decreasedUnitChain);
                }
                List<LinkedList<String>> expectUnitChains = nestChangedInfo.getExpectUnitChains();
                Set<String> actualDecrUnitIds = new HashSet<>();
                for (String decrUnitId : expectDecrUnitIds) {
                    boolean existConflict = false;
                    for (LinkedList<String> expectUnitChain : expectUnitChains) {
                        Optional<String> conflictUnitId = expectUnitChain.stream()
                                .filter(r -> Objects.equals(decrUnitId, r)).findFirst();
                        existConflict = conflictUnitId.isPresent();
                        if(existConflict){
                            break;
                        }
                    }
                    // 不存在冲突
                    if(!existConflict){
                        actualDecrUnitIds.add(decrUnitId);
                    }
                }
                if(!CollectionUtils.isEmpty(actualDecrUnitIds)){
                    // 根据单位ID查询单位下用户ID
                    Set<String> accountIds = orgAccountService.listOrgAccountIds(new ArrayList<>(actualDecrUnitIds), false);
                    nestAccountService.unbindNest2Accounts(nestChangedInfo.getNestId(), accountIds);
                }
            }
        }
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
