package com.imapcloud.nest.v2.service;

import com.geoai.common.core.bean.PageResultInfo;
import com.imapcloud.nest.common.core.PageInfo;
import com.imapcloud.nest.v2.service.dto.in.UosRoleQueryDTO;
import com.imapcloud.nest.v2.service.dto.out.UosPageResourceNodeOutDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRoleCreationInDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRoleInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.UosRoleModificationInDTO;

import java.util.List;
import java.util.Optional;

/**
 * 角色服务
 *
 * @author boluo
 * @date 2022-05-20
 */
public interface UosRoleService {

    PageResultInfo<UosRoleInfoOutDTO> queryRoleInfos(UosRoleQueryDTO condition);

    Optional<UosRoleInfoOutDTO> getRoleDetails(String roleId);

    String createRole(UosRoleCreationInDTO data);

    void updateRole(String roleId, UosRoleModificationInDTO data);

    void deleteRole(String roleId);

    UosPageResourceNodeOutDTO getPageResourceTree(String appType);

    List<String> getRolePageResourceIdsByRoleId(String roleId);

    void updateRolePageResources(String roleId, List<String> pageResourceIds);

}
