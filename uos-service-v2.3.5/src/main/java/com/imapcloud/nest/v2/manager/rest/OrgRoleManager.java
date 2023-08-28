package com.imapcloud.nest.v2.manager.rest;

import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.common.enums.OrgRoleTypeEnum;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgRoleOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.RoleBasicOutDO;
import com.imapcloud.nest.v2.manager.feign.RoleServiceClient;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * UOS单位-角色数据管理器
 * @author Vastfy
 * @date 2022/8/23 17:21
 * @since 2.0.0
 */
@Component
public class OrgRoleManager {

    @Resource
    private RoleServiceClient roleServiceClient;

    public List<OrgRoleOutDO> listOrgRoleRefInfos(String orgCode){
        if(StringUtils.hasText(orgCode)){
            Result<List<RoleBasicOutDO>> result = roleServiceClient.listOrgRoles(Collections.singletonList(orgCode), UosOrgManager.UOS_SYSTEM_TYPE);
            if(result.isOk() && !CollectionUtils.isEmpty(result.getData())){
                return result.getData()
                        .stream()
                        .map(r-> {
                            OrgRoleOutDO or = new OrgRoleOutDO();
                            or.setRoleId(r.getRoleId());
                            or.setOrgCode(or.getOrgCode());
                            or.setOrgRoleType(r.getOrgRoleType());
                            return or;
                        }).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    public List<OrgRoleOutDO> getOrgRoleRefInfos(List<String> orgCodes, Boolean isDefault){
        if(!CollectionUtils.isEmpty(orgCodes)){
            Result<List<RoleBasicOutDO>> result = roleServiceClient.listOrgRoles(orgCodes, UosOrgManager.UOS_SYSTEM_TYPE);
            if(result.isOk() && !CollectionUtils.isEmpty(result.getData())){
                List<OrgRoleOutDO> results = result.getData()
                        .stream()
                        .map(r-> {
                            OrgRoleOutDO or = new OrgRoleOutDO();
                            or.setRoleId(r.getRoleId());
                            or.setOrgCode(or.getOrgCode());
                            or.setOrgRoleType(r.getOrgRoleType());
                            or.setRoleName(r.getRoleName());
                            or.setAppType(r.getAppType());
                            return or;
                        })
                        .collect(Collectors.toList());
                if(isDefault){
                    results = results.stream()
                            .filter(r -> OrgRoleTypeEnum.DEFAULT.matchEquals(r.getOrgRoleType()))
                            .collect(Collectors.toList());
                }
                return results;
            }
        }
        return Collections.emptyList();
    }

}
