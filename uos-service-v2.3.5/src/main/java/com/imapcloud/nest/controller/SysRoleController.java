package com.imapcloud.nest.controller;


import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 权限信息表 前端控制器
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
@RestController
@RequestMapping("/sysRole")
public class SysRoleController {

//    @Resource
//    private OrgRoleService orgRoleService;
//
//    @Resource
//    private RoleService roleService;
//
//    private void trimName(SysRoleDto sysRoleDto) {
//        String name = sysRoleDto.getName();
//        sysRoleDto.setName(name.trim());
//    }
//
//    /*添加角色*/
    // 接口废弃
//    @PostMapping("/addSysRole")
//    public RestRes addSysRole(@RequestBody @Valid SysRoleDto sysRoleDto) {
//        trimName(sysRoleDto);
//        String name = sysRoleDto.getName();
//        // 保存角色及单位角色关系
//        RoleAddInDTO roleAddInDTO = new RoleAddInDTO();
//        roleAddInDTO.setOrgCode(sysRoleDto.getUnitId());
//        roleAddInDTO.setRoleName(name);
//        roleAddInDTO.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
//        roleService.roleAdd(roleAddInDTO);
//        return  RestRes.ok("角色添加成功!");
//    }

    /*更改角色*/
    // 接口废弃
//    @PostMapping("/updateSysRole")
//    public RestRes updateRegion(@RequestBody @Valid SysRoleDto sysRoleDto) {
//        trimName(sysRoleDto);
//        String name = sysRoleDto.getName();
//        String id = sysRoleDto.getId();
//        if (!StringUtils.hasText(id)) {
//            return RestRes.errorParam();
//        }
//        // 修改角色名称
//        RoleUpdateInDTO roleUpdateInDTO = new RoleUpdateInDTO();
//        roleUpdateInDTO.setRoleId(id);
//        roleUpdateInDTO.setRoleName(name);
//        RoleUpdateOutDTO roleUpdateOutDTO = roleService.roleUpdate(roleUpdateInDTO);
//        return roleUpdateOutDTO.isSuccess() ? RestRes.ok("角色修改成功!") : RestRes.err("角色修改失败!");
//    }

//    @GetMapping("/listSysRoleByUnit")
    // 接口废弃
//    public RestRes listSysRoleBy(String unitId) {
//
//        OrgRoleInfoInDTO orgRoleInfoInDTO = new OrgRoleInfoInDTO();
//        orgRoleInfoInDTO.setOrgCode(unitId);
//
//        OrgRoleInfoOutDTO orgRoleInfoOutDTO = orgRoleService.orgRoleInfo(orgRoleInfoInDTO);
//
//        String accountId = TrustedAccessTracerHolder.get().getAccountId();
//        // 查询登陆人的角色
//        List<RoleInfoOutDTO> dtoList = roleService.getRoleInfo(accountId);
//        List<Long> roleIdList = Lists.newLinkedList();
//        boolean isAdmin = false;
//        for (RoleInfoOutDTO roleInfoOutDTO : dtoList) {
//            roleIdList.add(roleInfoOutDTO.getRoleId());
//            if (OrgRoleTypeEnum.DEFAULT.getType() == roleInfoOutDTO.getRoleType()) {
//                isAdmin = true;
//            }
//        }
//
//        // 查询登录用户所在单位
//        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
//        boolean oneUnit = Objects.equals(orgCode, unitId);
//
//        List<RoleInfoRespVO> roleInfoRespVOList = Lists.newLinkedList();
//        for (OrgRoleInfoOutDTO.Info info : orgRoleInfoOutDTO.getInfoList()) {
//
//            if (!isAdmin && oneUnit && !roleIdList.contains(info.getRoleId())) {
//                // 不是管理员，并且是同一家单位，取自己有的角色
//                continue;
//            }
//
//            RoleInfoRespVO roleInfoRespVO = new RoleInfoRespVO();
//            roleInfoRespVO.setId(String.valueOf(info.getRoleId()));
//            roleInfoRespVO.setName(info.getRoleName());
//            roleInfoRespVO.setRoleType(info.getRoleType());
//            if (oneUnit) {
//                roleInfoRespVO.setEditStatus(info.getRoleType() != 1);
//            } else {
//                roleInfoRespVO.setEditStatus(true);
//            }
//            roleInfoRespVOList.add(roleInfoRespVO);
//        }
//
//        Map<String, Object> map = new HashMap<>(2);
//        map.put("sysRoleEntitys", roleInfoRespVOList);
//        return RestRes.ok(map);
//    }

//    /*删除角色，要考虑当前绑定的关系的问题*/
//    @GetMapping("/deleteSysRole")
    // 接口废弃
//    public RestRes deleteRegion(Long id) {
//        if (id == null) {
//            return RestRes.errorParam();
//        }
//        // 查询角色是否绑定账户
//        RoleAccountInfoInDTO roleAccountInfoInDTO = new RoleAccountInfoInDTO();
//        roleAccountInfoInDTO.setRoleId(id);
//        RoleAccountInfoOutDTO roleAccountInfoOutDTO = roleService.roleAccountList(roleAccountInfoInDTO);
//        if (CollUtil.isNotEmpty(roleAccountInfoOutDTO.getInfoList())) {
//            return RestRes.err("该单位或其子单位下存在绑定的用户");
//        }
//        // 删除角色
//        RoleDeleteInDTO roleDeleteInDTO = new RoleDeleteInDTO();
//        roleDeleteInDTO.setRoleId(id);
//        roleDeleteInDTO.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
//        RoleDeleteOutDTO roleDeleteOutDTO = roleService.roleDelete(roleDeleteInDTO);
//        return roleDeleteOutDTO.isSuccess() ? RestRes.ok() : RestRes.errorParam();
//    }
}

