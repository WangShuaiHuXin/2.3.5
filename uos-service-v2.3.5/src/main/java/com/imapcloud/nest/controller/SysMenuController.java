package com.imapcloud.nest.controller;


import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.service.OrgRoleService;
import com.imapcloud.nest.v2.service.ResourceService;
import com.imapcloud.nest.v2.service.dto.out.ResourceAccountPageOutDTO;
import com.imapcloud.nest.v2.web.vo.resp.ResourcePageInfoVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统菜单表 前端控制器
 * </p>
 *
 * @author kings
 * @since 2020-11-20
 */
@RestController
@RequestMapping("/sysMenu")
public class SysMenuController {

    @Resource
    private ResourceService resourceService;

    @Resource
    private OrgRoleService orgRoleService;

    private List<ResourcePageInfoVo.Info> getChild(ResourceAccountPageOutDTO.PageInfo pageInfo, List<ResourceAccountPageOutDTO.PageInfo> pageInfoList) {

        return pageInfoList.stream()
                .filter(bean -> bean.getParentPageResourceId().equals(pageInfo.getPageResourceId()))
                .map(bean -> {
                    ResourcePageInfoVo.Info info = new ResourcePageInfoVo.Info();
                    info.setId(String.valueOf(bean.getPageResourceId()));
                    info.setPageResourceType(bean.getPageResourceType());
                    info.setUrl(bean.getPageResourceKey());
                    info.setPageResourceKey(bean.getPageResourceKey());
                    // 如果key为空取后台设置的名称，key不为空取国际化名称
                    String key = info.getPageResourceKey();
                    if (StringUtils.isEmpty(key)) {
                        info.setName(bean.getPageResourceName());
                    } else {
                        info.setName(MessageUtils.getMessage(key));
                    }
                    info.setIdenValue(bean.getIdenValue());
                    info.setSeq(bean.getSeq());
                    info.setChildrenList(getChild(bean, pageInfoList));
                    return info;
                }).sorted(Comparator.comparing(ResourcePageInfoVo.Info::getSeq)).collect(Collectors.toList());
    }

    /*获取菜单信息*/
    // 改不动，谁改动处理问题，谁负责
    @ApiOperation("UOS前台菜单权限详情")
    @GetMapping("/listSysMenuBy")
    public RestRes listSysMenuBy() {

        // 查询用户的权限列表resourceService
        ResourceAccountPageOutDTO resourceAccountPageOutDTO = resourceService.accountPageList(TrustedAccessTracerHolder.get().getAccountId());
//        ResourceAccountPageOutDTO resourceAccountPageOutDTO = resourceService.getAccountPageResources(TrustedAccessTracerHolder.get().getAccountId());
        List<ResourcePageInfoVo.Info> infoList = resourceAccountPageOutDTO.getPageInfoList().stream()
                .filter(bean -> !StringUtils.hasText(bean.getParentPageResourceId()) || Objects.equals(bean.getParentPageResourceId(), "0"))
                .map(bean -> {
                    ResourcePageInfoVo.Info info = new ResourcePageInfoVo.Info();
                    info.setId(String.valueOf(bean.getPageResourceId()));
                    info.setPageResourceType(bean.getPageResourceType());
                    info.setUrl(bean.getPageResourceKey());
                    info.setPageResourceKey(bean.getPageResourceKey());
                    // 如果key为空取后台设置的名称，key不为空取国际化名称
                    String key = info.getPageResourceKey();
                    if (StringUtils.isEmpty(key)) {
                        info.setName(bean.getPageResourceName());
                    } else {
                        info.setName(MessageUtils.getMessage(key));
                    }
                    info.setIdenValue(bean.getIdenValue());
                    info.setSeq(bean.getSeq());
                    info.setChildrenList(getChild(bean, resourceAccountPageOutDTO.getPageInfoList()));
                    return info;
                }).sorted(Comparator.comparing(ResourcePageInfoVo.Info::getSeq)).collect(Collectors.toList());
        /*在返回的时候，要加上机巢数*/
        Map<String, Object> map = new HashMap<>(2);
        map.put("SysMenuEntityIPage", infoList);
        return RestRes.ok(map);
    }

//    private List<RolePageInfoRespVO> getRolePageInfoRespVOList(OrgRolePageOutDTO.Info bean, List<OrgRolePageOutDTO.Info> infoList) {
//        return infoList.stream()
//                .filter(info -> bean.getPageResourceId().equals(info.getParentPageResourceId()))
//                .map(info -> {
//                    RolePageInfoRespVO rolePageInfoRespVO = new RolePageInfoRespVO();
//                    rolePageInfoRespVO.setId(String.valueOf(info.getPageResourceId()));
//                    rolePageInfoRespVO.setName(info.getPageResourceName());
//                    rolePageInfoRespVO.setSeq(info.getSeq());
//                    rolePageInfoRespVO.setActive(info.isActive());
//                    rolePageInfoRespVO.setChildrenList(getRolePageInfoRespVOList(info, infoList));
//                    return rolePageInfoRespVO;
//                }).sorted(Comparator.comparing(RolePageInfoRespVO::getSeq)).collect(Collectors.toList());
//    }

//    /*获取菜单信息*/
    // 接口废弃
//    @GetMapping("/listSysMenusByRoleId")
//    public RestRes listSysMenus(Long roleId) {
//
//        // 查询当前用户拥有的角色
//        OrgRolePageInDTO orgRolePageInDTO = new OrgRolePageInDTO();
//        orgRolePageInDTO.setAccountId(TrustedAccessTracerHolder.get().getAccountId());
//        orgRolePageInDTO.setRoleId(roleId);
//
//        OrgRolePageOutDTO orgRolePageOutDTO = orgRoleService.rolePageInfoList(orgRolePageInDTO);
//
//        List<RolePageInfoRespVO> rolePageInfoRespVOList = orgRolePageOutDTO.getInfoList().stream()
//                .filter(info -> info.getParentPageResourceId() == null || info.getParentPageResourceId() == 0)
//                .map(info -> {
//                    RolePageInfoRespVO rolePageInfoRespVO = new RolePageInfoRespVO();
//                    rolePageInfoRespVO.setId(String.valueOf(info.getPageResourceId()));
//                    rolePageInfoRespVO.setName(info.getPageResourceName());
//                    rolePageInfoRespVO.setSeq(info.getSeq());
//                    rolePageInfoRespVO.setActive(info.isActive());
//                    rolePageInfoRespVO.setChildrenList(getRolePageInfoRespVOList(info, orgRolePageOutDTO.getInfoList()));
//                    return rolePageInfoRespVO;
//                }).sorted(Comparator.comparing(RolePageInfoRespVO::getSeq)).collect(Collectors.toList());
//
//        Map<String, Object> map = new HashMap<>(2);
//        map.put("sysMenuEntities", rolePageInfoRespVOList);
//        return RestRes.ok(map);
//    }
}

