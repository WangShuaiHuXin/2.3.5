package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.core.util.BeanCopyUtils;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.enums.NestOpsTypeEnum;
import com.imapcloud.nest.v2.dao.entity.NestAccountEntity;
import com.imapcloud.nest.v2.dao.mapper.NestAccountMapper;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import com.imapcloud.nest.v2.manager.sql.BaseNestManager;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.NestAccountService;
import com.imapcloud.nest.v2.service.NestOrgRefService;
import com.imapcloud.nest.v2.service.dto.out.AccountNestInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.NestAccountInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.NestAccountOutDTO;
import com.imapcloud.nest.v2.service.dto.out.NestSimpleOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * 基站账户服务
 *
 * @author boluo
 * @date 2022-05-23
 */
@Slf4j
@Service
public class NestAccountServiceImpl implements NestAccountService {

    @Resource
    private NestAccountMapper nestAccountMapper;

    @Resource
    private AccountServiceClient accountServiceClient;

    @Resource
    private NestOrgRefService nestOrgRefService;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private BaseNestManager baseNestManager;

    @Override
    public NestAccountInfoOutDTO nestAccountInfo(String accountId) {
        NestAccountInfoOutDTO nestAccountInfoOutDTO = new NestAccountInfoOutDTO();
        List<NestAccountInfoOutDTO.Info> infoList = Lists.newLinkedList();
        nestAccountInfoOutDTO.setInfoList(infoList);
        LambdaQueryWrapper<NestAccountEntity> condition = Wrappers.lambdaQuery(NestAccountEntity.class)
                .eq(NestAccountEntity::getAccountId, accountId);
        List<NestAccountEntity> nestAccountEntityList = nestAccountMapper.selectList(condition);
        if (CollUtil.isEmpty(nestAccountEntityList)) {
            return nestAccountInfoOutDTO;
        }
        for (NestAccountEntity nestAccountEntity : nestAccountEntityList) {
            NestAccountInfoOutDTO.Info info = new NestAccountInfoOutDTO.Info();
            info.setNestId(nestAccountEntity.getBaseNestId());
            info.setAccountId(nestAccountEntity.getAccountId());
            info.setNestControlStatus(NestOpsTypeEnum.OPERABLE.matchEquals(nestAccountEntity.getNestControlStatus()));
            infoList.add(info);
        }
        return nestAccountInfoOutDTO;
    }

    @Override
    public List<NestAccountOutDTO> listNestAccountInfos(List<String> accountIds) {
        if (!CollectionUtils.isEmpty(accountIds)) {
            Set<Long> accIds = accountIds.stream()
                    .map(Long::valueOf)
                    .collect(Collectors.toSet());
            LambdaQueryWrapper<NestAccountEntity> condition = Wrappers.lambdaQuery(NestAccountEntity.class)
                    .in(NestAccountEntity::getAccountId, accIds);
            List<NestAccountEntity> nestAccountEntities = nestAccountMapper.selectList(condition);
            if (!CollectionUtils.isEmpty(nestAccountEntities)) {
                return nestAccountEntities.stream()
                        .map(e -> {
                            NestAccountOutDTO r = new NestAccountOutDTO();
                            r.setAccountId(e.getAccountId());
                            r.setNestId(e.getBaseNestId());
                            r.setNestControlStatus(e.getNestControlStatus());
                            return r;
                        })
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<AccountNestInfoOutDTO> listAccountVisibleNestInfos(String accountId, String filterOrgCode) {
        if(StringUtils.hasText(accountId)){
            LambdaQueryWrapper<NestAccountEntity> condition = Wrappers.lambdaQuery(NestAccountEntity.class)
                    .eq(NestAccountEntity::getAccountId, accountId);
            List<NestAccountEntity> entities = nestAccountMapper.selectList(condition);
            Set<String> nestIds = entities.stream().map(NestAccountEntity::getBaseNestId)
                    .filter(StringUtils::hasText)
                    .collect(Collectors.toSet());
            if(StringUtils.hasText(filterOrgCode)){
                Set<String> filterNestIds = nestOrgRefService.listOrgVisibleNestIds(filterOrgCode);
//                if(!CollectionUtils.isEmpty(filterNestIds)){
                    nestIds.retainAll(filterNestIds);
//                }
            }
            if(!CollectionUtils.isEmpty(nestIds)){
                int nestControlStatus = entities.get(0).getNestControlStatus();
                List<NestSimpleOutDTO> result = baseNestService.listNestInfos(nestIds);
                return result.stream()
                        .map(r -> {
                            AccountNestInfoOutDTO accountNestInfoOutDTO = new AccountNestInfoOutDTO();
                            BeanCopyUtils.copyProperties(r, accountNestInfoOutDTO, true);
                            accountNestInfoOutDTO.setGrantControl(nestControlStatus > 0);
                            return accountNestInfoOutDTO;
                        }).collect(toList());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public void updateAccountBoundNests(String accountId, List<String> nestIds, boolean canOps) {
        if(StringUtils.hasText(accountId)){
            Result<Boolean> result = accountServiceClient.hasOrgDefaultRole(accountId);
            if(result.isOk() && Boolean.FALSE.equals(result.getData())){
                // 获取账号所属单位
                Result<OrgSimpleOutDO> result1 = accountServiceClient.getAccountOrgInfo(accountId);
                if(!result1.isOk() || Objects.isNull(result1.getData())){
                    throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_FIND_UNITINFO.getContent()));
                }
                OrgSimpleOutDO org = result1.getData();
                Set<String> orgVisibleNestIds = nestOrgRefService.listOrgVisibleNestIds(org.getOrgCode());
                orgVisibleNestIds.retainAll(nestIds);
                // FIXME: 事务不生效
                grantNests2Account(accountId, orgVisibleNestIds, canOps);
                // 清理redis缓存
                baseNestManager.clearNestListRedisCache();
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int grantNests2Account(String accountId, Collection<String> nestIds, boolean canOps) {
        if (Objects.nonNull(accountId)) {
            LambdaQueryWrapper<NestAccountEntity> condition = Wrappers.lambdaQuery(NestAccountEntity.class).eq(NestAccountEntity::getAccountId, accountId);
            int delete = nestAccountMapper.delete(condition);
            if (log.isDebugEnabled()) {
                log.debug("账号[{}]移除[{}]个关联基站", accountId, delete);
            }
            if (!CollectionUtils.isEmpty(nestIds)) {
                NestOpsTypeEnum nestOpsTypeEnum = canOps ? NestOpsTypeEnum.OPERABLE : NestOpsTypeEnum.VISIBLE;
                String visitorId = TrustedAccessTracerHolder.get().getAccountId();
                List<NestAccountEntity> entities = nestIds.stream()
                        .map(e -> {
                            NestAccountEntity entity = new NestAccountEntity();
                            entity.setAccountId(accountId);
                            entity.setBaseNestId(e);
                            entity.setCreatorId(visitorId);
                            entity.setModifierId(visitorId);
                            entity.setNestControlStatus(nestOpsTypeEnum.getType());
                            return entity;
                        }).collect(Collectors.toList());
                int affect = nestAccountMapper.insertBatch(entities);
                if (log.isDebugEnabled()) {
                    log.debug("账号[{}]新增[{}]个关联基站", accountId, affect);
                }
                return affect;
            }
        }
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int bindNest2Accounts(String nestId, List<String> accountIds, boolean canOps) {
        if (Objects.nonNull(nestId) && !CollectionUtils.isEmpty(accountIds)) {
            // 先查询，过滤已存在的记录
            LambdaQueryWrapper<NestAccountEntity> con = Wrappers.lambdaQuery(NestAccountEntity.class)
                    .eq(NestAccountEntity::getBaseNestId, nestId)
                    .in(NestAccountEntity::getAccountId, accountIds);
            List<NestAccountEntity> nestAccountEntities = nestAccountMapper.selectList(con);
            if(!CollectionUtils.isEmpty(nestAccountEntities)){
                Set<String> dbAccountIds = nestAccountEntities.stream().map(NestAccountEntity::getAccountId).collect(Collectors.toSet());
                accountIds = accountIds.stream().filter(r -> !dbAccountIds.contains(r)).collect(Collectors.toList());
            }
            if(!CollectionUtils.isEmpty(accountIds)){
                NestOpsTypeEnum nestOpsTypeEnum = canOps ? NestOpsTypeEnum.OPERABLE : NestOpsTypeEnum.VISIBLE;
                String visitorId = TrustedAccessTracerHolder.get().getAccountId();
                List<NestAccountEntity> entities = accountIds.stream()
                        .map(e -> {
                            NestAccountEntity entity = new NestAccountEntity();
                            entity.setAccountId(e);
                            entity.setBaseNestId(nestId);
                            entity.setCreatorId(visitorId);
                            entity.setModifierId(visitorId);
                            entity.setNestControlStatus(nestOpsTypeEnum.getType());
                            return entity;
                        }).collect(Collectors.toList());
                int affect = nestAccountMapper.insertBatch(entities);
                if (log.isDebugEnabled()) {
                    log.debug("基站[{}]新增[{}]个关联账号", nestId, affect);
                }
                return affect;
            }
        }
        return 0;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public int unbindNest2Accounts(String nestId, Collection<String> accountIds) {
        if(Objects.nonNull(nestId) && !CollectionUtils.isEmpty(accountIds)){
            LambdaQueryWrapper<NestAccountEntity> condition = Wrappers.lambdaQuery(NestAccountEntity.class)
                    .eq(NestAccountEntity::getBaseNestId, nestId)
                    .in(NestAccountEntity::getAccountId, accountIds);
            nestAccountMapper.delete(condition);
            if(log.isDebugEnabled()){
                log.debug("基站[{}]解绑账号关系 ==> {}", nestId, accountIds);
            }
        }
        return 0;
    }


    @Override
    public void deleteByNestId(Integer nestId) {
        if (Objects.nonNull(nestId)) {
            LambdaQueryWrapper<NestAccountEntity> condition = Wrappers.lambdaQuery(NestAccountEntity.class)
                    .eq(NestAccountEntity::getBaseNestId, nestId.longValue());
            nestAccountMapper.delete(condition);
        }
    }

    @Override
    public List<String> getAccountIdByNest(String nestId) {

        LambdaQueryWrapper<NestAccountEntity> nestAccountWrapper = Wrappers.lambdaQuery(NestAccountEntity.class)
                .eq(NestAccountEntity::getBaseNestId, nestId);
        List<NestAccountEntity> nestAccountEntityList = nestAccountMapper.selectList(nestAccountWrapper);
        if (CollUtil.isEmpty(nestAccountEntityList)) {
            return Collections.emptyList();
        }
        return nestAccountEntityList.stream().map(NestAccountEntity::getAccountId).collect(toList());
    }

    @Override
    public List<String> listNestUuidsByAccountId(Long accountId) {
        if (Objects.nonNull(accountId)) {
            return nestAccountMapper.selectNestUuidByAccountId(accountId);
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> listNestIdByAccountId(String accountId) {
        if (Objects.nonNull(accountId)) {
            LambdaQueryWrapper<NestAccountEntity> wrapper = Wrappers.lambdaQuery(NestAccountEntity.class)
                    .eq(NestAccountEntity::getAccountId, accountId)
                    .select(NestAccountEntity::getBaseNestId);
            List<NestAccountEntity> nestAccountEntities = nestAccountMapper.selectList(wrapper);
            if(CollectionUtil.isNotEmpty(nestAccountEntities)) {
                List<String> nestIdList = nestAccountEntities.stream().map(NestAccountEntity::getBaseNestId).distinct().collect(toList());
                return nestIdList;
            }
        }
        return Collections.emptyList();
    }

}
