package com.imapcloud.nest.v2.manager.feign;

import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.web.rest.IResult;
import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.manager.dataobj.out.AccountRoleInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.RoleBasicOutDO;
import com.imapcloud.nest.v2.manager.feign.config.TokenRelayConfiguration;
import com.imapcloud.nest.v2.service.dto.in.UosRoleQueryDTO;
import com.imapcloud.nest.v2.service.dto.out.UosPageResourceNodeOutDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRoleCreationInDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRoleInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRoleModificationInDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限系统 role模块
 *
 * @author boluo
 * @date 2022-05-20
 */
@FeignClient(contextId = "role-service-client", name = "geoai-auth-service",
        configuration = TokenRelayConfiguration.class)
public interface RoleServiceClient {

    @PostMapping("roles/defaults/exists")
    Result<Boolean> existsDefaultRole(@RequestBody List<String> roleIds);

    @PostMapping("roles/list/accounts")
    Result<List<AccountRoleInfoOutDO>> listAccountRoleInfos(@RequestBody List<String> accountIds);

    @PostMapping("org/list/roles")
    Result<List<RoleBasicOutDO>> listOrgRoles(@RequestBody List<String> orgCodes,
                                              @RequestParam("appType") String appType);

    @GetMapping("roles")
    Result<PageResultInfo<UosRoleInfoOutDTO>> queryRoleInfos(@SpringQueryMap UosRoleQueryDTO condition);

    @GetMapping("roles/{roleId}")
    Result<UosRoleInfoOutDTO> getRoleDetails(@PathVariable("roleId") String roleId);

    @PostMapping("roles")
    Result<String> createRole(@RequestBody UosRoleCreationInDTO body);

    @PutMapping("roles/{roleId}")
    Result<Void> updateRole(@PathVariable("roleId") String roleId, @RequestBody UosRoleModificationInDTO body);

    @DeleteMapping("roles/{roleId}")
    Result<Void> deleteRole(@PathVariable("roleId") String roleId);

    @GetMapping("roles/{roleId}/perms")
    Result<List<String>> listRolePageResourceInfos(@PathVariable("roleId") String roleId);

    @PutMapping("roles/{roleId}/perms")
    Result<Void> updateRoleBoundPageResources(@PathVariable("roleId") String roleId,
                                              @RequestBody List<String> pageResourceIds);

    @GetMapping("pages/resources/tree")
    Result<UosPageResourceNodeOutDTO> getPageResourceTree(@RequestParam String appType);

}
