package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.out.AccountNestInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.NestAccountInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.NestAccountOutDTO;
import com.imapcloud.nest.v2.service.dto.out.NestSimpleOutDTO;

import java.util.Collection;
import java.util.List;

/**
 * 基站账户服务
 *
 * @author boluo
 * @date 2022-05-23
 */
public interface NestAccountService {
    /**
     * 基站账户信息
     *
     * @param accountId 窝在dto账户信息
     * @return {@link NestAccountInfoOutDTO}
     */
    NestAccountInfoOutDTO nestAccountInfo(String accountId);

    /**
     * 根据账号ID列表获取基站和账号信息
     *
     * @param accountIds 账号ID
     * @return {@link NestAccountInfoOutDTO}
     */
    List<NestAccountOutDTO> listNestAccountInfos(List<String> accountIds);

    /**
     * 根据账号ID列表获取基站信息
     * @param accountId 账号ID
     * @param filterOrgCode 单位编码
     * @return 账号可见基站列表（如果单位编码不为空，则会求交集返回）
     */
    List<AccountNestInfoOutDTO> listAccountVisibleNestInfos(String accountId, String filterOrgCode);

    /**
     * 更新用户绑定的基站（覆盖），该接口会对账号的单位角色进行检查，如果是单位管理员，则忽略给定的基站
     * @param accountId 账号ID
     * @param nestIds   基站列表
     * @param canOps    是否可操作
     */
    void updateAccountBoundNests(String accountId, List<String> nestIds, boolean canOps);

    /**
     * 用户绑定基站（覆盖）
     * @param accountId 账号ID
     * @param nestIds   基站列表
     * @param canOps    是否可操作
     * @return 绑定成功数量
     */
    int grantNests2Account(String accountId, Collection<String> nestIds, boolean canOps);

    /**
     * 用户绑定基站（仅新增）
     * @param nestId     基站ID
     * @param accountIds 账号ID列表
     * @param canOps     是否可操作
     * @return 绑定成功数量
     */
    int bindNest2Accounts(String nestId, List<String> accountIds, boolean canOps);

    /**
     * 用户解绑基站
     *
     * @param nestId     基站ID
     * @param accountIds 账号ID列表
     * @return 解绑数量
     */
    int unbindNest2Accounts(String nestId, Collection<String> accountIds);

    /**
     * 删除基站和用户关系
     *
     * @param nestId 基站ID
     */
    void deleteByNestId(Integer nestId);

    /**
     * 基站用户
     *
     * @param nestId 巢id
     * @return {@link List}<{@link Long}>
     */
    List<String> getAccountIdByNest(String nestId);

    /**
     * 查询基站uuid通过用户id
     */
    List<String> listNestUuidsByAccountId(Long accountId);

    List<String> listNestIdByAccountId(String accountId);

}
