package com.imapcloud.nest.v2.manager.feign;

import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.web.rest.IResult;
import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.manager.dataobj.in.*;
import com.imapcloud.nest.v2.manager.dataobj.out.AccountOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PageResourceSimpleOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.RoleInfoOutDO;
import com.imapcloud.nest.v2.manager.feign.config.TokenRelayConfiguration;
import com.imapcloud.nest.v2.manager.feign.fallback.AccountServiceClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 认证鉴权服务客户端接口
 * @author Vastfy
 * @date 2022/5/20 10:02
 * @since 2.0.0
 */
@RequestMapping("accounts")
@FeignClient(contextId = "account-service-client", name = "geoai-auth-service",
        configuration = TokenRelayConfiguration.class, fallbackFactory = AccountServiceClientFallbackFactory.class)
public interface AccountServiceClient {


    @GetMapping("{accountId}")
    Result<AccountOutDO> getAccountInfo(@PathVariable("accountId") String accountId);

    @GetMapping("details")
    Result<AccountOutDO> fetchAccountInfo(@RequestParam("username") String username);

    @GetMapping
    Result<PageResultInfo<AccountOutDO>> queryAccountInfos(@SpringQueryMap AccountQueryInDO queryCon);

    @PostMapping("register")
    Result<AccountOutDO> signUp(@RequestBody SignUpInDO body);

    @PutMapping("{accountId}")
    Result<Void> changeAccount(@PathVariable String accountId,
                               @RequestBody AccountModificationInDO body);

    @PatchMapping("{accountId}/infos")
    Result<Boolean> changeInformation(@PathVariable("accountId") String accountId,
                                      @RequestBody AccountInfoModificationInDO body);

    @PatchMapping("{accountId}/pwd")
    Result<Boolean> changePassword(@PathVariable("accountId") String accountId,
                                   @RequestBody PasswordModificationInDO body);

    @RequestMapping(value = "{accountId}/pwd/reset", method = RequestMethod.PATCH)
    Result<Boolean> resetAccountPassword(@PathVariable("accountId") String accountId,
                                         @RequestBody PasswordResetInDO body);

    @DeleteMapping("{accountId}")
    Result<Boolean> deleteAccount(@PathVariable("accountId") String accountId);

    @GetMapping("{accountId}/roles")
    Result<List<RoleInfoOutDO>> queryAccountRoleInfos(@PathVariable("accountId") String accountId);

    /**
     * 批量根据角色ID获取账号信息
     */
    @PostMapping("list/roles")
    Result<List<AccountOutDO>> fetchAccountsWithByRoleIds(@RequestBody List<String> roleIds);

    /**
     * 批量根据单位编码获取账号信息
     */
    @PostMapping("list/org")
    Result<List<AccountOutDO>> fetchAccountsByOrgCodes(@RequestBody List<String> orgCodes);

    @PatchMapping("{accountId}/status/{status:on|off|locked}")
    Result<Boolean> settingAccountState(@PathVariable("accountId") String accountId, @PathVariable("status") String status);

    @PostMapping("{accountId}/has/org/privilege")
    Result<Boolean> hasOrgDefaultRole(@PathVariable("accountId") String accountId);

    @GetMapping("{accountId}/org")
    Result<OrgSimpleOutDO> getAccountOrgInfo(@PathVariable("accountId") String accountId);

    @PostMapping("{accountId}/pages/resources")
    Result<List<PageResourceSimpleOutDO>> listAccountPageResources(@PathVariable("accountId") String accountId,
                                                                   @RequestBody ApplicationQueryDO condition);

    @PostMapping("/list/account/infos")
    Result<List<AccountOutDO>> listAccountInfos(@RequestBody List<String> accountIds);

}
