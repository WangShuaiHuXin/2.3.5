package com.imapcloud.nest.v2.service.impl;

import com.geoai.common.core.bean.ITrustedAccessTracer;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.geoai.common.web.util.ResultUtils;
import com.imapcloud.nest.utils.GuavaCacheUtil;
import com.imapcloud.nest.utils.GuavaKey;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.manager.dataobj.in.AccountModificationInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.AccountQueryInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.PasswordResetInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.SignUpInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.AccountOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.AccountRoleInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.RoleInfoOutDO;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import com.imapcloud.nest.v2.manager.feign.RoleServiceClient;
import com.imapcloud.nest.v2.manager.sql.BaseNestManager;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.NestAccountService;
import com.imapcloud.nest.v2.service.NestOrgRefService;
import com.imapcloud.nest.v2.service.UosAccountService;
import com.imapcloud.nest.v2.service.converter.AccountConverter;
import com.imapcloud.nest.v2.service.dto.in.*;
import com.imapcloud.nest.v2.service.dto.out.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * UOS账号业务接口实现
 *
 * @author Vastfy
 * @date 2022/5/23 17:40
 * @since 2.0.0
 */
@Slf4j
@Service
public class UosAccountServiceImpl implements UosAccountService {

    @Resource
    private AccountServiceClient accountServiceClient;

    @Resource
    private RoleServiceClient roleServiceClient;

    @Resource
    private AccountConverter accountConverter;

    @Resource
    private NestAccountService nestAccountService;

    @Resource
    private NestOrgRefService nestOrgRefService;

    @Resource
    private BaseNestService baseNestService;

    @Resource
    private BaseNestManager baseNestManager;

    @Override
    public Boolean changeInformation(AccountInfoModificationInDTO info) {
        Result<Boolean> result = accountServiceClient.changeInformation(TrustedAccessTracerHolder.get().getAccountId(), accountConverter.convert(info));
        if (result.isOk()) {
            return result.getData();
        }
        throw new BizException(result.getMsg());
    }

    @Override
    public Boolean changePassword(PasswordModificationInDTO info) {
        Result<Boolean> result = accountServiceClient.changePassword(TrustedAccessTracerHolder.get().getAccountId(), accountConverter.convert(info));
        if (result.isOk()) {
            return result.getData();
        }
        throw new BizException(result.getMsg());
    }

    @Override
    public Boolean resetPassword(String accountId, String newPwd) {
        PasswordResetInDO body = new PasswordResetInDO();
        body.setNewPwd(newPwd);
        Result<Boolean> result = accountServiceClient.resetAccountPassword(accountId, body);
        if (result.isOk()) {
            return result.getData();
        }
        throw new BizException(result.getMsg());
    }

    @Override
    public boolean checkAccountHasOrgDefaultRole(String accountId) {
        String visitorId = TrustedAccessTracerHolder.get().getAccountId();
        Result<Boolean> result = accountServiceClient.hasOrgDefaultRole(visitorId);
        if (result.isOk()) {
            return Optional.ofNullable(result.getData())
                    .orElse(Boolean.FALSE);
        }
        return false;
    }

    public AccountInfoOutDTO accountInfo(String accountId) {

        Result<AccountOutDO> accountInfo = accountServiceClient.getAccountInfo(accountId);
        AccountOutDO accountOutDO = ResultUtils.getData(accountInfo);

        AccountInfoOutDTO accountInfoOutDTO = new AccountInfoOutDTO();
        accountInfoOutDTO.setId(accountOutDO.getAccountId());
        accountInfoOutDTO.setAccount(accountOutDO.getAccount());
        accountInfoOutDTO.setName(accountOutDO.getName());
        accountInfoOutDTO.setMobile(accountOutDO.getMobile());
        accountInfoOutDTO.setEmail(accountOutDO.getEmail());
        accountInfoOutDTO.setStatus(accountOutDO.getStatus());
        return accountInfoOutDTO;
    }

    @Override
    public PageResultInfo<AccountInfoOutDTO> queryAccountInfo(AccountInfoInDTO accountInfoInDTO) {
        AccountQueryInDO condition = new AccountQueryInDO();
        condition.setUsername(accountInfoInDTO.getAccount());
        condition.setMobile(accountInfoInDTO.getMobile());
        condition.setAccountStatus(accountInfoInDTO.getStatus());
        condition.setPageNo(accountInfoInDTO.getPageNo());
        condition.setPageSize(accountInfoInDTO.getPageSize());
        condition.setOrgCode(accountInfoInDTO.getUnitId());
        Result<PageResultInfo<AccountOutDO>> result = accountServiceClient.queryAccountInfos(condition);
        if (result.isOk()) {
            PageResultInfo<AccountOutDO> pageData = result.getData();
            // 数据转换，批量获取单位信息
            Collection<AccountOutDO> records = pageData.getRecords();
            if (!CollectionUtils.isEmpty(records)) {
                // 获取角色列表
                Map<String, AccountRoleInfoOutDO> accountRoleInfoMap = getAccountRoleInfoMap(records);
                // 获取基站列表
                Map<String, List<NestSimpleOutDTO>> accountNestInfoMap = getAccountNestInfoMap(records);
                return pageData.map(e -> {
                    AccountInfoOutDTO convert = accountConverter.convert(e);
                    convert.setUnitId(e.getOrgCode());
                    convert.setUnitName(e.getOrgName());
                    if (!CollectionUtils.isEmpty(accountRoleInfoMap) && accountRoleInfoMap.containsKey(e.getAccountId())) {
                        List<RoleInfoOutDO> roleInfos = accountRoleInfoMap.get(e.getAccountId()).getRoleInfos();
                        if (!CollectionUtils.isEmpty(roleInfos)) {
                            List<String> roleInfoOutDTOS = roleInfos.stream()
                                    .map(RoleInfoOutDO::getRoleName)
                                    .collect(toList());
                            convert.setRoleNameList(roleInfoOutDTOS);
                        }
                    }
                    if (!CollectionUtils.isEmpty(accountNestInfoMap) && accountNestInfoMap.containsKey(e.getAccountId())) {
                        List<NestSimpleOutDTO> nestEntities = accountNestInfoMap.get(e.getAccountId());
                        if (!CollectionUtils.isEmpty(nestEntities)) {
                            List<String> roleInfoOutDTOS = nestEntities.stream()
                                    .map(NestSimpleOutDTO::getName)
                                    .collect(toList());
                            convert.setNestNameList(roleInfoOutDTOS);
                        }
                    }
                    return convert;
                });
            }
        }
        return PageResultInfo.empty();
    }

    private Map<String, List<NestSimpleOutDTO>> getAccountNestInfoMap(Collection<AccountOutDO> records) {
        List<String> accountIds = records.stream()
                .map(AccountOutDO::getAccountId)
                .collect(toList());
        List<NestAccountOutDTO> nestAccountRefs = nestAccountService.listNestAccountInfos(accountIds);
        if (!CollectionUtils.isEmpty(nestAccountRefs)) {
            Set<String> nestIds = nestAccountRefs.stream().map(NestAccountOutDTO::getNestId).collect(Collectors.toSet());
            // 查询基站信息
            List<NestSimpleOutDTO> nestInfos = baseNestService.listNestInfos(nestIds);
            if (!CollectionUtils.isEmpty(nestInfos)) {
                Map<String, NestSimpleOutDTO> nestEntityMap = nestInfos.stream().collect(toMap(NestSimpleOutDTO::getId, e -> e));
                Map<String, List<NestSimpleOutDTO>> accountNestMap = new HashMap<>(accountIds.size());
                for (NestAccountOutDTO nestAccountRef : nestAccountRefs) {
                    String accountId = nestAccountRef.getAccountId();
                    if (!accountNestMap.containsKey(accountId)) {
                        accountNestMap.put(accountId, new ArrayList<>());
                    }
                    if (nestEntityMap.containsKey(nestAccountRef.getNestId())) {
                        accountNestMap.get(accountId).add(nestEntityMap.get(nestAccountRef.getNestId()));
                    }
                }
                return accountNestMap;
            }
        }
        return Collections.emptyMap();
    }

    private Map<String, AccountRoleInfoOutDO> getAccountRoleInfoMap(Collection<AccountOutDO> records) {
        List<String> accIds = records.stream()
                .map(AccountOutDO::getAccountId)
                .collect(toList());
        Result<List<AccountRoleInfoOutDO>> listResult = roleServiceClient.listAccountRoleInfos(accIds);
        if (listResult.isOk()) {
            List<AccountRoleInfoOutDO> data = listResult.getData();
            if (!CollectionUtils.isEmpty(data)) {
                Map<String, AccountRoleInfoOutDO> map = data.stream().collect(toMap(AccountRoleInfoOutDO::getAccountId, e -> e));
                for (Map.Entry<String, AccountRoleInfoOutDO> entry : map.entrySet()) {
                    List<RoleInfoOutDO> roleInfos = entry.getValue().getRoleInfos();
                    if(!CollectionUtils.isEmpty(roleInfos)){
                        // 过滤前台角色
                        List<RoleInfoOutDO> collect = roleInfos.stream()
                                .filter(r -> Objects.equals(r.getAppType(), "geoai-uos-foreground"))
                                .collect(toList());
                        entry.getValue().setRoleInfos(collect);
                    }
                }
                return map;
            }
        }
        return Collections.emptyMap();
    }

    @Override
    public boolean createNewUser(AccountCreationInDTO accountCreationInDTO) {
        // 调用auth服务创建账号
        SignUpInDO signUpInDO = new SignUpInDO();
        signUpInDO.setUsername(accountCreationInDTO.getAccount());
        signUpInDO.setPassword(accountCreationInDTO.getPassword());
        signUpInDO.setMobile(accountCreationInDTO.getMobile());
        signUpInDO.setOrgCode(accountCreationInDTO.getUnitId());
        signUpInDO.setRoleIds(accountCreationInDTO.getRoleIds());
        signUpInDO.setRealName(accountCreationInDTO.getRealName());
        signUpInDO.setSourceType("geoai-uos-foreground");
        Result<AccountOutDO> result = accountServiceClient.signUp(signUpInDO);
        AccountOutDO accountOutDO = ResultUtils.getData(result);
        // 绑定基站和账号关系
        bindNestToAccount(accountOutDO.getAccountId(), accountCreationInDTO);
        return true;
    }

    @Override
    public boolean updateUosUser(AccountModificationInDTO accountCreationInDTO) {
        // 调用auth服务更新账号
        AccountModificationInDO body = new AccountModificationInDO();
        body.setRealName(accountCreationInDTO.getRealName());
        body.setMobile(accountCreationInDTO.getMobile());
        body.setOrgCode(accountCreationInDTO.getUnitId());
        body.setIncRoleIds(accountCreationInDTO.getIncRoleIds());
        body.setDecRoleIds(accountCreationInDTO.getDecRoleIds());
        Result<Void> result = accountServiceClient.changeAccount(accountCreationInDTO.getId(), body);
        ResultUtils.getData(result);
        // 更新账号的关联基站信息
        bindNestToAccount2(accountCreationInDTO.getId(), accountCreationInDTO);
        // 清理redis缓存
        baseNestManager.clearNestListRedisCache();
        return true;
    }

    private void bindNestToAccount2(String accountId, AccountModificationInDTO data) {
        // 角色列表含[单位默认管理员] ==> 新增默认最大角色所属单位及子孙单位挂载的基站关系
        boolean canOps = data.getIsOperation();
        List<String> inchargeNestIds = data.getInchargeNestList();
        // 1. 过滤角色类型为【默认管理员】的角色-单位关联关系
        Result<Boolean> result = accountServiceClient.hasOrgDefaultRole(accountId);
        if (!result.isOk()) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DETECTION_UNIT_ROLE_ABNORMAL.getContent()));
        }
        if (Boolean.TRUE.equals(result.getData())) {
            // 获取单位(及子孙单位)关联的基站列表
            String orgCode = data.getUnitId();
            Set<String> nestIds = nestOrgRefService.listOrgVisibleNestIds(orgCode);
            inchargeNestIds = !CollectionUtils.isEmpty(nestIds) ? new ArrayList<>(nestIds) : Collections.emptyList();
            // 拥有单位默认管理员角色，对基站可控
            canOps = true;
        }
        nestAccountService.grantNests2Account(accountId, inchargeNestIds, canOps);
    }

    private void bindNestToAccount(String accountId, AccountCreationInDTO accountCreationInDTO) {
        // 角色列表含[单位默认管理员] ==> 新增默认最大角色所属单位及子孙单位挂载的基站关系
        boolean canOps = accountCreationInDTO.getIsOperation();
        List<String> inchargeNestIds = accountCreationInDTO.getInchargeNestList();
        // 1. 过滤角色类型为【默认管理员】的角色-单位关联关系
        Result<Boolean> result = roleServiceClient.existsDefaultRole(accountCreationInDTO.getRoleIds());
        if (!result.isOk()) {
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_DETECTION_UNIT_ROLE_ABNORMAL.getContent()));
        }
        if (Boolean.TRUE.equals(result.getData())) {
            // 获取单位(及子孙单位)关联的基站列表
            String orgCode = accountCreationInDTO.getUnitId();
            Set<String> nestIds = nestOrgRefService.listOrgVisibleNestIds(orgCode);
            inchargeNestIds = !CollectionUtils.isEmpty(nestIds) ? new ArrayList<>(nestIds) : Collections.emptyList();
            // 拥有单位默认管理员角色，对基站可控
            canOps = true;
        }
        nestAccountService.grantNests2Account(accountId, inchargeNestIds, canOps);
    }

    @Override
    public boolean settingAccountStatus(String accountId, String status) {
        Result<Boolean> result = accountServiceClient.settingAccountState(accountId, status);
        return ResultUtils.getData(result);
    }

    @Override
    public boolean deleteAccount(String accountId) {
        Result<Boolean> result = accountServiceClient.deleteAccount(accountId);
        return ResultUtils.getData(result);
    }

    @Override
    public AccountDetailOutDTO getAccountDetailsByUsernameInGuavaCache(String username) {
        String guavaKey = GuavaKey.ACCOUNT_USER_INFO + username;
        AccountDetailOutDTO dto = (AccountDetailOutDTO) GuavaCacheUtil.get(guavaKey);
        if (Objects.isNull(dto)) {
            Result<AccountOutDO> result = accountServiceClient.fetchAccountInfo(username);
            AccountOutDO data = ResultUtils.getData(result);
            dto = accountConverter.convertDetail(data);
            if (Objects.nonNull(dto)) {
                GuavaCacheUtil.set(guavaKey,dto);
            }
        }
        return dto;
    }

    @Override
    public AccountDetailOutDTO getAccountDetails(String accountId) {
        // 调用auth服务查询账号基本信息
        ITrustedAccessTracer trustedAccessTracer = TrustedAccessTracerHolder.get();
        Result<AccountOutDO> result = accountServiceClient.getAccountInfo(accountId);
        AccountOutDO data = ResultUtils.getData(result);
        AccountDetailOutDTO detail = accountConverter.convertDetail(data);
        // 查询用户单位信息
        String orgCode = getAccountOrgCode(accountId);
        // 只允许查询本单位及子单位账号
        if(!StringUtils.hasText(orgCode) || !orgCode.startsWith(trustedAccessTracer.getOrgCode())){
            return null;
        }
        detail.setUnitId(orgCode);
        // 查询用户基站信息
        NestAccountInfoOutDTO nestAccountInfoOutDTO = nestAccountService.nestAccountInfo(accountId);
        if (!CollectionUtils.isEmpty(nestAccountInfoOutDTO.getInfoList())) {
            List<String> nestIds = nestAccountInfoOutDTO.getInfoList()
                    .stream()
                    .map(NestAccountInfoOutDTO.Info::getNestId)
                    .collect(toList());
            detail.setNestIdList(nestIds);
            detail.setOperation(nestAccountInfoOutDTO.getInfoList().get(0).isNestControlStatus());
        }
        // 查询用户角色信息
        Result<List<RoleInfoOutDO>> result1 = accountServiceClient.queryAccountRoleInfos(accountId);
        if (result1.isOk()) {
            List<RoleInfoOutDO> data1 = result1.getData();
            if (!CollectionUtils.isEmpty(data1)) {
                List<String> roleIds = data1.stream()
                        // 过滤前台角色
                        .filter(r -> Objects.equals(r.getAppType(), "geoai-uos-foreground"))
                        .map(RoleInfoOutDO::getRoleId)
                        .collect(toList());
                detail.setRoleIdList(roleIds);
            }
        }
        return detail;
    }

    @Override
    public AccountDetailOutDTO getAccountDetailsByUsername(String username) {
        Result<AccountOutDO> result = accountServiceClient.fetchAccountInfo(username);
        AccountOutDO data = ResultUtils.getData(result);
        return accountConverter.convertDetail(data);
    }

    @Override
    public Boolean checkAccountNeedGranted(String accountId) {
        Result<Boolean> result = accountServiceClient.hasOrgDefaultRole(accountId);
        // 无需分配时（即当前账号为单位管理员）需要重新绑定所属单位下可见基站
        if(result.isOk() && Boolean.TRUE.equals(result.getData())){
            String orgCode = getAccountOrgCode(accountId);
            if(StringUtils.hasText(orgCode)){
                // 刷新对于单位的基站
                Set<String> nestIds = nestOrgRefService.listOrgVisibleNestIds(orgCode);
                // TODO 是否需要异步执行？异步时注意用户上下问题
                nestAccountService.grantNests2Account(accountId, new ArrayList<>(nestIds), true);
                // 清理redis缓存
                baseNestManager.clearNestListRedisCache();
            }
        }
        return result.getData();
    }

    private String getAccountOrgCode(String accountId) {
        Result<OrgSimpleOutDO> result = accountServiceClient.getAccountOrgInfo(accountId);
        if(result.isOk() && Objects.nonNull(result.getData())){
            return result.getData().getOrgCode();
        }
        return null;
    }

}
