package com.imapcloud.nest.v2.service.impl;

import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.web.rest.Result;
import com.imapcloud.nest.v2.manager.feign.RoleServiceClient;
import com.imapcloud.nest.v2.service.UosRoleService;
import com.imapcloud.nest.v2.service.dto.in.UosRoleQueryDTO;
import com.imapcloud.nest.v2.service.dto.out.UosPageResourceNodeOutDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRoleCreationInDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRoleInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRoleModificationInDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 角色服务impl
 *
 * @author boluo
 * @date 2022-05-20
 */
@Service
public class UosRoleServiceImpl implements UosRoleService {

    @Resource
    private RoleServiceClient roleServiceClient;

    @Override
    public PageResultInfo<UosRoleInfoOutDTO> queryRoleInfos(UosRoleQueryDTO condition) {
        Result<PageResultInfo<UosRoleInfoOutDTO>> result = roleServiceClient.queryRoleInfos(condition);
        if(result.isOk()){
            return result.getData();
        }
        return PageResultInfo.empty();
    }

    @Override
    public Optional<UosRoleInfoOutDTO> getRoleDetails(String roleId) {
        Result<UosRoleInfoOutDTO> result = roleServiceClient.getRoleDetails(roleId);
        if(result.isOk()){
            return Optional.ofNullable(result.getData());
        }
        return Optional.empty();
    }

    @Override
    public String createRole(UosRoleCreationInDTO data) {
        Result<String> result = roleServiceClient.createRole(data);
        if(result.isOk()){
            return result.getData();
        }
        throw new BizException(result.getMsg());
    }

    @Override
    public void updateRole(String roleId, UosRoleModificationInDTO data) {
        Result<Void> result = roleServiceClient.updateRole(roleId, data);
        if(result.isOk()){
            return;
        }
        throw new BizException(result.getMsg());
    }

    @Override
    public void deleteRole(String roleId) {
        Result<Void> result = roleServiceClient.deleteRole(roleId);
        if(result.isOk()){
            return;
        }
        throw new BizException(result.getMsg());
    }

    @Override
    public UosPageResourceNodeOutDTO getPageResourceTree(String appType) {
        Result<UosPageResourceNodeOutDTO> result = roleServiceClient.getPageResourceTree(appType);
        if(result.isOk()){
            return result.getData();
        }
        return null;
    }

    @Override
    public List<String> getRolePageResourceIdsByRoleId(String roleId) {
        Result<List<String>> result = roleServiceClient.listRolePageResourceInfos(roleId);
        if(result.isOk()){
            return result.getData();
        }
        return Collections.emptyList();
    }

    @Override
    public void updateRolePageResources(String roleId, List<String> pageResourceIds) {
        Result<Void> result = roleServiceClient.updateRoleBoundPageResources(roleId, pageResourceIds);
        if(result.isOk()){
            return;
        }
        throw new BizException(result.getMsg());
    }

}
