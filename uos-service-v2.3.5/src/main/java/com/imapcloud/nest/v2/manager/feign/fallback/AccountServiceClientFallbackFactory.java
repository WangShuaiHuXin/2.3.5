package com.imapcloud.nest.v2.manager.feign.fallback;

import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.manager.dataobj.in.*;
import com.imapcloud.nest.v2.manager.dataobj.out.AccountOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PageResourceSimpleOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.RoleInfoOutDO;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 认证鉴权服务降级处理接口
 *
 * @author Vastfy
 * @date 2022/5/20 10:03
 * @since 2.0.0
 */
@Component
public class AccountServiceClientFallbackFactory extends AbstractFeignFallbackFactory<AccountServiceClient> {

    @Override
    protected AccountServiceClient doFallbackHandle(String errMessage) {
        return new AccountServiceClientFallback(errMessage);
    }

    @Slf4j
    static  class AccountServiceClientFallback implements AccountServiceClient {

        private final String errorMsg;

        AccountServiceClientFallback(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        @Override
        public Result<AccountOutDO> getAccountInfo(String accountId) {
            log.error("查询账号[{}]信息失败，原因：{}", accountId, errorMsg);
            return Result.error(errorMsg);
        }

        @Override
        public Result<AccountOutDO> fetchAccountInfo(String username) {
            log.error("查询账号[用户名：{}]信息失败，原因：{}", username, errorMsg);
            return Result.error(errorMsg);
        }

        @Override
        public Result<PageResultInfo<AccountOutDO>> queryAccountInfos(AccountQueryInDO queryCon) {
            log.error("分页查询账号信息失败，原因：{}", errorMsg);
            return Result.error(errorMsg);
        }

        @Override
        public Result<AccountOutDO> signUp(SignUpInDO body) {
            log.error("新建账号失败，原因：{}, 请求参数 ==> {}", errorMsg, body);
            return Result.error(errorMsg);
        }

        @Override
        public Result<Void> changeAccount(String accountId, AccountModificationInDO body) {
            log.error("修改账号失败，原因：{}, 请求参数 ==> {}@{}", errorMsg, accountId, body);
            return Result.error(errorMsg);
        }

        @Override
        public Result<Boolean> changeInformation(String accountId, AccountInfoModificationInDO body) {
            log.error("修改账号基本信息失败，原因：{}，请求参数 => {}", errorMsg, body);
            return Result.error(errorMsg);
        }

        @Override
        public Result<Boolean> changePassword(String accountId, PasswordModificationInDO body) {
            log.error("修改账号密码失败，原因：{}，请求参数 => {}", errorMsg, body);
            return Result.error(errorMsg);
        }

        @Override
        public Result<Boolean> resetAccountPassword(String accountId, PasswordResetInDO newPwd) {
            log.error("账号重置密码失败，原因：{}，请求参数 => {}", errorMsg, accountId);
            return Result.error(errorMsg);
        }

        @Override
        public Result<List<RoleInfoOutDO>> queryAccountRoleInfos(String accountId) {
            log.error("获取账号关联角色列表失败，原因：{}，请求参数 => {}", errorMsg, accountId);
            return Result.error(errorMsg);
        }

        @Override
        public Result<Boolean> deleteAccount(String accountId) {
            log.error("删除账号失败，原因：{}，请求参数 => {}", errorMsg, accountId);
            return Result.error(errorMsg);
        }

        @Override
        public Result<List<AccountOutDO>> fetchAccountsWithByRoleIds(List<String> roleIds) {
            log.error("批量根据角色ID获取账号失败，原因：{}，请求参数 => {}", errorMsg, roleIds);
            return Result.error(errorMsg);
        }

        @Override
        public Result<List<AccountOutDO>> fetchAccountsByOrgCodes(List<String> orgCodes) {
            log.error("批量根据单位编码获取账号失败，原因：{}，请求参数 => {}", errorMsg, orgCodes);
            return Result.error(errorMsg);
        }

        @Override
        public Result<Boolean> settingAccountState(String accountId, String status) {
            log.error("账号[{}]设置状态失败，原因：{}，请求参数 => {}", accountId, errorMsg, status);
            return Result.error(errorMsg);
        }

        @Override
        public Result<Boolean> hasOrgDefaultRole(String accountId) {
            log.error("获取账号[{}]单位默认角色失败，原因：{}", accountId, errorMsg);
            return Result.error(errorMsg);
        }

        @Override
        public Result<OrgSimpleOutDO> getAccountOrgInfo(String accountId) {
            log.error("获取账号[{}]单位失败，原因：{}", accountId, errorMsg);
            return Result.error(errorMsg);
        }

        @Override
        public Result<List<PageResourceSimpleOutDO>> listAccountPageResources(String accountId, ApplicationQueryDO condition) {
            log.error("获取账号[{}]前台页面资源失败，原因：{}", accountId, errorMsg);
            return Result.error(errorMsg);
        }

        @Override
        public Result<List<AccountOutDO>> listAccountInfos(List<String> accountIds) {
            log.error("获取账号[{}]信息失败，原因：{}", null, errorMsg);
            return Result.error(errorMsg);
        }

    }

}
