package com.imapcloud.nest.v2.web;

import cn.hutool.core.collection.CollectionUtil;
import com.geoai.common.core.bean.PageResultInfo;
import com.geoai.common.web.rest.IResult;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.github.xiaoymin.knife4j.annotations.ApiSupport;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.service.OrgRoleService;
import com.imapcloud.nest.v2.service.UosRoleService;
import com.imapcloud.nest.v2.service.dto.out.*;
import com.imapcloud.nest.v2.web.transformer.UosRoleTransformer;
import com.imapcloud.nest.v2.web.vo.req.UosRoleBasicRespVO;
import com.imapcloud.nest.v2.web.vo.req.UosRoleCreationReqVO;
import com.imapcloud.nest.v2.web.vo.req.UosRoleModificationReqVO;
import com.imapcloud.nest.v2.web.vo.req.UosRoleQueryReqVO;
import com.imapcloud.nest.v2.web.vo.resp.UosPageResourceNodeRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * V1版兼容性接口
 *
 * @author Vastfy
 * @date 2022/9/1 15:00
 * @since 2.0.0
 */
@ApiSupport(author = "liujiahua@geoai.com", order = 10)
@Api(value = "UOS-v1兼容API", tags = "UOS-v1兼容API")
@RequestMapping
@RestController
public class V1CompatibilityController {

    @Resource
    private OrgRoleService orgRoleService;

    @Resource
    private UosRoleService uosRoleService;

    @Resource
    private UosRoleTransformer uosRoleTransformer;

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 1)
    @ApiOperation(value = "前台编辑账号-查询单位下角色列表")
    @GetMapping("sysRole/listSysRoleByUnit")
    public RestRes listSysRoleByUnit(@RequestParam String unitId) {
        List<OrgRoleOutDTO> orgRoleRefs = orgRoleService.getOrgRoleRefs(Collections.singletonList(unitId), false);
        List<V1RoleEntityVo> result = null;
        if (!CollectionUtils.isEmpty(orgRoleRefs)) {
            result = orgRoleRefs.stream()
                    // 过滤前台角色
                    .filter(r -> Objects.equals(r.getAppType(), "geoai-uos-foreground"))
                    .map(r -> {
                        V1RoleEntityVo vr = new V1RoleEntityVo();
                        vr.setId(r.getRoleId());
                        vr.setName(r.getRoleName());
                        vr.setLocalDateTime(LocalDateTime.now());
                        vr.setRoleType(r.getOrgRoleType());
                        return vr;
                    })
                    .collect(Collectors.toList());
        }
        Map<String, Object> map = Collections.singletonMap("sysRoleEntitys", result);
        return RestRes.ok(map);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 2)
    @ApiOperation(value = "检索UOS前台角色信息（分页）")
    @GetMapping("roles")
    public IResult<PageResultInfo<UosRoleBasicRespVO>> queryRoleInfos(UosRoleQueryReqVO condition) {
        PageResultInfo<UosRoleBasicRespVO> result = uosRoleService.queryRoleInfos(uosRoleTransformer.transform(condition))
                .map(uosRoleTransformer::transform);
        return Result.ok(result);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 3)
    @ApiOperation(value = "查询UOS前台角色详情")
    @ApiImplicitParam(name = "roleId", value = "角色ID", paramType = "path", required = true)
    @GetMapping("roles/{roleId}")
    public IResult<UosRoleBasicRespVO> getRoleDetails(@PathVariable String roleId) {
        Optional<UosRoleInfoOutDTO> optional = uosRoleService.getRoleDetails(roleId);
        UosRoleBasicRespVO result = optional.map(uosRoleTransformer::transform).orElse(null);
        return Result.ok(result);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 4)
    @ApiOperation(value = "新建UOS前台角色")
    @PostMapping("roles")
    public IResult<String> createRole(@Validated @RequestBody UosRoleCreationReqVO body) {
        UosRoleCreationInDTO data = uosRoleTransformer.transform(body);
        String roleId = uosRoleService.createRole(data);
        return Result.ok(roleId);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 5)
    @ApiOperation(value = "修改UOS前台角色")
    @ApiImplicitParam(name = "roleId", value = "角色ID", paramType = "path", required = true)
    @PutMapping("roles/{roleId}")
    public IResult<Void> updateRole(@PathVariable String roleId,
                                    @Validated @RequestBody UosRoleModificationReqVO body) {
        UosRoleModificationInDTO data = uosRoleTransformer.transform(body);
        uosRoleService.updateRole(roleId, data);
        return Result.ok();
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 6)
    @ApiOperation(value = "删除UOS前台角色")
    @ApiImplicitParam(name = "roleId", value = "角色ID", paramType = "path", required = true)
    @DeleteMapping("roles/{roleId}")
    public IResult<Void> deleteRole(@PathVariable String roleId) {
        uosRoleService.deleteRole(roleId);
        return Result.ok();
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 7)
    @ApiOperation(value = "获取UOS前台页面资源树（权限模板）")
    @GetMapping("pages/resources/tree")
    public IResult<UosPageResourceNodeRespVO> getPageResourceTree(@RequestParam String appType) {
        UosPageResourceNodeOutDTO prNode = uosRoleService.getPageResourceTree(appType);
        //国际化翻译的添加
        UosPageResourceNodeRespVO vos = uosRoleTransformer.transform(prNode);
        List<UosPageResourceNodeRespVO> result = new ArrayList<>();
        List<UosPageResourceNodeRespVO> children = vos.getChildren();
        List<UosPageResourceNodeRespVO> uosPageResourceNodeRespVOS = setVosName(children, result);
        vos.setChildren(uosPageResourceNodeRespVOS);
        return Result.ok(vos);
        //  return Result.ok(uosRoleTransformer.transform(prNode));
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 8)
    @ApiOperation(value = "获取UOS前台角色的页面资源")
    @ApiImplicitParam(name = "roleId", value = "角色ID", paramType = "path", required = true)
    @GetMapping("roles/{roleId}/perms")
    public IResult<List<String>> getRolePageResourceIdsByRoleId(@PathVariable String roleId) {
        List<String> pageResourceIds = uosRoleService.getRolePageResourceIdsByRoleId(roleId);
        return Result.ok(pageResourceIds);
    }

    @ApiOperationSupport(author = "wumiao@geoai.com", order = 9)
    @ApiOperation(value = "更新UOS前台角色的页面资源")
    @ApiImplicitParam(name = "roleId", value = "角色ID", paramType = "path", required = true)
    @PutMapping("roles/{roleId}/perms")
    public IResult<Void> updateRoleBoundPageResources(@PathVariable String roleId,
                                                      @RequestBody List<String> pageResourceIds) {
        // 一般用于单独的角色绑定权限
        uosRoleService.updateRolePageResources(roleId, pageResourceIds);
        return Result.ok();
    }

    @Data
    static class V1RoleEntityVo {
        private String id;
        private String name;
        private Integer roleType;
        private LocalDateTime localDateTime;
    }

    public List<UosPageResourceNodeRespVO> setVosName(List<UosPageResourceNodeRespVO> vo, List<UosPageResourceNodeRespVO> result) {
        vo.forEach(e -> {
            e.setPageResourceName(MessageUtils.getMessage(e.getPageResourceKey()));
            result.add(e);
            if (CollectionUtil.isNotEmpty(e.getChildren())) {
                List<UosPageResourceNodeRespVO> chi = new ArrayList<>();
                setVosName(e.getChildren(), chi);
            }
        });
        return result;
    }
}
