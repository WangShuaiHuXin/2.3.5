package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.ResultUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.enums.RoleIdenValueEnum;
import com.imapcloud.nest.v2.manager.dataobj.in.ResourcePageListInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.ResourceAccountPageOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.ResourcePageListOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.RoleInfoOutDO;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import com.imapcloud.nest.v2.manager.feign.ResourceServiceClient;
import com.imapcloud.nest.v2.manager.nacos.NacosConfigurationService;
import com.imapcloud.nest.v2.service.OrgRoleService;
import com.imapcloud.nest.v2.service.ResourceService;
import com.imapcloud.nest.v2.service.dto.in.OrgRoleInfoInDTO;
import com.imapcloud.nest.v2.service.dto.out.OrgRoleInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.ResourceAccountPageOutDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 页面资源服务impl
 *
 * @author boluo
 * @date 2022-05-23
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    @Resource
    private ResourceServiceClient resourceServiceClient;

    @Resource
    private NacosConfigurationService nacosConfigurationService;

    @Resource
    private OrgRoleService orgRoleService;

    @Resource
    private AccountServiceClient accountServiceClient;

    @Override
    public ResourceAccountPageOutDTO accountPageList(String accountId) {

        ResourceAccountPageOutDTO resourceAccountPageOutDTO = new ResourceAccountPageOutDTO();
        List<ResourceAccountPageOutDTO.PageInfo> pageInfoList = Lists.newLinkedList();
        resourceAccountPageOutDTO.setPageInfoList(pageInfoList);
        // 查询用户角色
        Result<List<RoleInfoOutDO>> listResult = accountServiceClient.queryAccountRoleInfos(accountId);
        List<RoleInfoOutDO> roleInfoOutDOList = ResultUtils.getData(listResult);
        if (CollUtil.isEmpty(roleInfoOutDOList)) {
            return resourceAccountPageOutDTO;
        }
//        List<String> longList = roleInfoOutDOList.stream().map(RoleInfoOutDO::getRoleId).collect(Collectors.toList());

        // 查询用户所在单位
        // 单位角色信息
//        OrgRoleInfoInDTO orgRoleInfoInDTO = new OrgRoleInfoInDTO();
//        orgRoleInfoInDTO.setOrgCode(TrustedAccessTracerHolder.get().getOrgCode());
//
//        OrgRoleInfoOutDTO orgRoleInfoOutDTO = orgRoleService.orgRoleInfo(orgRoleInfoInDTO);
//        OrgRoleInfoOutDTO.Info orgRoleDtoInfo = orgRoleInfoOutDTO.getInfoList().stream()
//                .filter(OrgRoleInfoOutDTO.Info::admin)
//                .findFirst().orElse(null);
//        if (orgRoleDtoInfo == null) {
//            return resourceAccountPageOutDTO;
//        }
        // 查询当前用户前台页面资源
        Result<ResourceAccountPageOutDO> result = resourceServiceClient.accountFrontPageList();

        ResourceAccountPageOutDO resourceAccountPageOutDO = ResultUtils.getData(result);

        // 单位管理员权限集合
//        List<String> adminPageResourceIdList = null;
//        boolean admin = false;

        // 用户角色拥有单位管理员权限
//        if (longList.contains(orgRoleDtoInfo.getRoleId())) {
//            admin = true;
//        } else {
            // 查询单位管理员角色信息
//            ResourcePageListInDO resourcePageListInDO = new ResourcePageListInDO();
//            resourcePageListInDO.setRoleIdList(Lists.newArrayList(orgRoleDtoInfo.getRoleId()));

//            Result<ResourcePageListOutDO> doResult = resourceServiceClient.pageList(resourcePageListInDO);
//            ResourcePageListOutDO resourcePageListOutDO = ResultUtils.getData(doResult);
//
//            if (CollUtil.isEmpty(resourcePageListOutDO.getRolePageInfoList())) {
//                return resourceAccountPageOutDTO;
//            }
//            adminPageResourceIdList = resourcePageListOutDO.getRolePageInfoList().get(0).getPageInfoList().stream()
//                    .map(ResourcePageListOutDO.PageInfo::getPageResourceId).collect(Collectors.toList());
//        }

        // 角色相关页面权限
//        List<String> rolePageResourceIdList = nacosConfigurationService.getRolePageResourceIdList();

        for (ResourceAccountPageOutDO.PageInfo bean : resourceAccountPageOutDO.getPageInfoList()) {

//            if (!admin) {
//                if (!adminPageResourceIdList.contains(bean.getPageResourceId())) {
//                    continue;
//                }
//                if (rolePageResourceIdList.contains(bean.getPageResourceId())) {
//                    continue;
//                }
//            }
            ResourceAccountPageOutDTO.PageInfo pageInfo = new ResourceAccountPageOutDTO.PageInfo();
            pageInfo.setPageResourceId(bean.getPageResourceId());
            pageInfo.setPageResourceName(bean.getPageResourceName());
            pageInfo.setPageResourceType(bean.getPageResourceType());
            pageInfo.setPageResourceKey(bean.getPageResourceKey());
            pageInfo.setSeq(bean.getSeq());
            pageInfo.setParentPageResourceId(bean.getParentPageResourceId());
            pageInfo.setIdenValue(RoleIdenValueEnum.getIdenValue(bean.getPageResourceKey()));
            pageInfoList.add(pageInfo);
        }
        return resourceAccountPageOutDTO;
    }

}
