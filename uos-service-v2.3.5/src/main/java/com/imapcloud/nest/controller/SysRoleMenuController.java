package com.imapcloud.nest.controller;


import cn.hutool.core.collection.CollUtil;
import com.imapcloud.nest.pojo.dto.SysRoleFunctionDto;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.service.OrgRoleService;
import com.imapcloud.nest.v2.service.ResourceService;
import com.imapcloud.nest.v2.service.dto.in.OrgRoleInfoInDTO;
import com.imapcloud.nest.v2.service.dto.in.ResourceRolePageEditInDTO;
import com.imapcloud.nest.v2.service.dto.out.OrgRoleInfoOutDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户菜单对应表 前端控制器
 * </p>
 *
 * @author kings
 * @since 2020-11-24
 */
@RestController
@RequestMapping("/sysRoleMenu")
public class SysRoleMenuController {

//    @Resource
//    private ResourceService resourceService;
//
//    @Resource
//    private OrgRoleService orgRoleService;

//    /*添加区域*/
    // 接口废弃
//    @PostMapping("/addSysRoleMenu")
//    public RestRes addSysRoleFunction(@RequestBody SysRoleFunctionDto sysRoleFunctionDto) {
//        String roleId = sysRoleFunctionDto.getRoleId();
//        if (roleId == null) {
//            return RestRes.err("请选择用户角色!");
//        }
//        List<Long> menuIds = sysRoleFunctionDto.getMenuIds();
//        if (menuIds == null || menuIds.size() <= 0) {
//            return RestRes.err("请选择功能!");
//        }
//        // 查询登录用户所在单位
//        String accountId = TrustedAccessTracerHolder.get().getAccountId();
//        Long accountUnitId = orgRoleService.getUnitIdByAccountId(accountId);
//        if (accountUnitId == null) {
//            return RestRes.errorParam();
//        }
//        // 查询角色所在单位
//        Long unitIdByRoleId = orgRoleService.getUnitIdByRoleId(roleId);
//        if (unitIdByRoleId == null) {
//            return RestRes.errorParam();
//        }
//        // 如果登录人所在单位与被修改的角色是同一个单位，单位管理员不能修改
//        if (accountUnitId.equals(unitIdByRoleId)) {
//
//            OrgRoleInfoInDTO orgRoleInfoInDTO = new OrgRoleInfoInDTO();
//            orgRoleInfoInDTO.setUnitId(unitIdByRoleId.intValue());
//            OrgRoleInfoOutDTO orgRoleInfoOutDTO = orgRoleService.orgRoleInfo(orgRoleInfoInDTO);
//            if (CollUtil.isEmpty(orgRoleInfoOutDTO.getInfoList())) {
//                return RestRes.errorParam();
//            }
//            Map<String, OrgRoleInfoOutDTO.Info> longInfoMap = orgRoleInfoOutDTO.getInfoList().stream()
//                    .collect(Collectors.toMap(OrgRoleInfoOutDTO.Info::getRoleId, bean -> bean, (key1, key2) -> key1));
//            OrgRoleInfoOutDTO.Info info = longInfoMap.get(roleId);
//            if (info == null) {
//                return RestRes.err("角色不存在");
//            }
//            if (info.admin()) {
//                return RestRes.err("你不能编辑该角色");
//            }
//        }
//        // 保存权限
//        ResourceRolePageEditInDTO resourceRolePageEditInDTO = new ResourceRolePageEditInDTO();
//        resourceRolePageEditInDTO.setRoleId(roleId);
//        resourceRolePageEditInDTO.setPageResourceIdList(menuIds);
//        boolean pageEdit = resourceService.rolePageEdit(resourceRolePageEditInDTO);
//        return pageEdit ? RestRes.ok("保存角色菜单列表成功!") : RestRes.err("保存角色菜单列表失败!");
//    }
}

