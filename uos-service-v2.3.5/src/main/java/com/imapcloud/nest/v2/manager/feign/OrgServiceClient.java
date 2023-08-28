package com.imapcloud.nest.v2.manager.feign;

import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.manager.dataobj.in.OrgPageQueryDO;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgNodeOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgThemeInfoOutDO;
import com.imapcloud.nest.v2.manager.feign.config.TokenRelayConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 账号-角色关系服务客户端接口
 * @author Vastfy
 * @date 2022/5/20 10:02
 * @since 2.0.0
 */
@RequestMapping("org")
@FeignClient(contextId = "org-service-client", name = "geoai-base-service",
        configuration = TokenRelayConfiguration.class)
public interface OrgServiceClient {

    @GetMapping("tree")
    Result<OrgNodeOutDO> getOrgTree(@RequestParam String orgCode);

    @GetMapping("{orgCode}")
    Result<OrgSimpleOutDO> getOrgDetails(@PathVariable("orgCode") String orgCode);

    @PostMapping("list")
    Result<List<OrgSimpleOutDO>> listOrgDetails(@RequestBody List<String> orgCodes);

    @GetMapping("list/basic")
    Result<List<OrgSimpleOutDO>> listAllOrgSimpleInfos();

    @GetMapping("list/all/basic")
    Result<List<OrgSimpleOutDO>> listAllOrgSimpleInfoWithoutAccount();

    /**
     * 分页查询
     */
    @GetMapping("page")
    Result<PageResultInfo<OrgInfoOutDO>> pageOrgInfo(@SpringQueryMap OrgPageQueryDO condition);

    @GetMapping("{orgCode}/theme")
    Result<List<OrgThemeInfoOutDO>> getOrgThemeInfos(@PathVariable String orgCode,
                                                     @RequestParam(required = false) String appType);

}
