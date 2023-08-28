package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.OrgRoleInfoInDTO;
import com.imapcloud.nest.v2.service.dto.out.OrgRoleInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.OrgRoleOutDTO;

import java.util.Collection;
import java.util.List;

/**
 * org角色服务
 *
 * @author boluo
 * @date 2022-05-19
 */
public interface OrgRoleService {
    /**
     * 单位角色信息
     *
     * @param orgRoleInfoInDTO org dto角色信息
     * @return {@link OrgRoleInfoOutDTO}
     */
    OrgRoleInfoOutDTO orgRoleInfo(OrgRoleInfoInDTO orgRoleInfoInDTO);
//
//    /**
//     * 创建角色以及保存单位角色关系
//     *
//     * @param orgRoleAddInDTO org角色加入dto
//     */
//    void orgRoleAdd(OrgRoleAddInDTO orgRoleAddInDTO);

//    /**
//     * 角色页面资源
//     *
//     * @param orgRolePageInDTO org在dto角色页面
//     * @return {@link OrgRolePageOutDTO}
//     */
//    OrgRolePageOutDTO rolePageInfoList(OrgRolePageInDTO orgRolePageInDTO);

//    /**
//     * 根据角色ID列表查询单位-角色关系
//     * @param roleIds   角色ID
//     * @param isDefault 是否单位默认管理员
//     * @return  单位-角色关系
//     */
//    List<OrgRoleOutDTO> listOrgRoleRefs(Collection<String> roleIds, Boolean isDefault);

    /**
     * 根据单位ID列表查询单位-角色关系
     * @param unitIds   单位ID
     * @param isDefault 是否单位默认管理员
     * @return  单位-角色关系
     */
    List<OrgRoleOutDTO> getOrgRoleRefs(Collection<String> unitIds, Boolean isDefault);

}
