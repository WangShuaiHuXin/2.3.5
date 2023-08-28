package com.imapcloud.nest.v2.manager.feign;

import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.manager.dataobj.in.ResourcePageListInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.RolePageEditInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.ResourceAccountPageOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.ResourcePageListOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.RolePageEditOutDO;
import com.imapcloud.nest.v2.manager.feign.config.TokenRelayConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 资源服务
 *
 * @author boluo
 * @date 2022-05-23
 */
@Deprecated
@FeignClient(contextId = "resource-service-client", name = "geoai-auth-service", configuration = TokenRelayConfiguration.class)
@RequestMapping("resource")
public interface ResourceServiceClient {

    /**
     * 账户的页面资源列表
     *
     * @return {@link Result}<{@link ResourceAccountPageOutDO}>
     */
    @PostMapping("accountPageList")
    Result<ResourceAccountPageOutDO> accountPageList();

    /**
     * 账户的前台页面资源列表
     *
     * @return {@link Result}<{@link ResourceAccountPageOutDO}>
     */
    @PostMapping("accountFrontPageList")
    Result<ResourceAccountPageOutDO> accountFrontPageList();

    /**
     * 角色的页面资源
     *
     * @param resourcePageListInDO 资源页面列表中
     * @return {@link Result}<{@link ResourcePageListOutDO}>
     */
    @PostMapping("pageList")
    Result<ResourcePageListOutDO> pageList(@RequestBody ResourcePageListInDO resourcePageListInDO);

    /**
     * 编辑角色页面资源
     *
     * @param rolePageEditInDO 页面编辑在做角色
     * @return {@link Result}<{@link RolePageEditOutDO}>
     */
    @PostMapping("editRolePage")
    Result<RolePageEditOutDO> editRolePage(@RequestBody RolePageEditInDO rolePageEditInDO);
}
