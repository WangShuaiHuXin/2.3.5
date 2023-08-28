package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.v2.service.dto.in.*;
import com.imapcloud.nest.v2.service.dto.out.AccountDetailOutDTO;
import com.imapcloud.nest.v2.service.dto.out.AccountInfoOutDTO;

/**
 * UOS账号业务接口实现
 * @author Vastfy
 * @date 2022/5/23 9:54
 * @since 2.0.0
 */
public interface UosAccountService {

    /**
     * 修改账号基本信息
     * @param info    待修改信息
     * @return  是否修改成功
     */
    Boolean changeInformation(AccountInfoModificationInDTO info);

    /**
     * 修改密码
     * @param info  密码修改信息
     * @return  是否修改成功
     */
    Boolean changePassword(PasswordModificationInDTO info);

    /**
     * 重置账号密码
     * @param accountId 账号ID
     * @return  是否重置成功
     */
    Boolean resetPassword(String accountId, String newPwd);

    /**
     * 是否单位默认角色
     * @param accountId 账号ID
     * @return 结果
     */
    boolean checkAccountHasOrgDefaultRole(String accountId);

     /* 账户信息
     *
     * @param accountId 帐户id
     * @return {@link AccountInfoOutDTO}
     */
    AccountInfoOutDTO accountInfo(String accountId);

    /**
     * 账号检索接口
     * @param accountInfoInDTO  账号ID
     * @return  分页结果
     */
    PageResultInfo<AccountInfoOutDTO> queryAccountInfo(AccountInfoInDTO accountInfoInDTO);

    /**
     * 创建新用户
     * @return 创建成功=true
     */
    boolean createNewUser(AccountCreationInDTO accountCreationInDTO);

    /**
     * 获取账号详情信息
     * @param accountId 账号ID
     * @return  账号详情
     */
    AccountDetailOutDTO getAccountDetails(String accountId);

    /**
     * 获取账号详情信息
     * @param username 账号名
     * @return  账号详情
     */
    AccountDetailOutDTO getAccountDetailsByUsername(String username);

    /**
     * 更新用户信息
     * @param accountCreationInDTO 用户信息
     * @return  true：更新成功
     */
    boolean updateUosUser(AccountModificationInDTO accountCreationInDTO);

    /**
     * 设置账号状态
     * @param accountId 账号ID
     * @param status    状态
     * @return  是否设置成功
     */
    boolean settingAccountStatus(String accountId, String status);

    /**
     * 删除账号
     * @param accountId 账号ID
     * @return  是否删除成功
     */
    boolean deleteAccount(String accountId);

    AccountDetailOutDTO getAccountDetailsByUsernameInGuavaCache(String username);

    /**
     * 检测账号是否需要授权基站（如果不需要，则会刷新单位下所有基站）
     * @param accountId 账号ID
     * @return  TRUE：需要
     */
    Boolean checkAccountNeedGranted(String accountId);

}
