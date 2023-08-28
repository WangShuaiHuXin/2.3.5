package com.imapcloud.nest.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.pojo.dto.reqDto.SysUnitReqDto;
import com.imapcloud.nest.service.SysUnitService;
import com.imapcloud.nest.utils.RestRes;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgNodeOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgThemeInfoOutDO;
import com.imapcloud.nest.v2.manager.rest.UosOrgManager;
import com.imapcloud.nest.v2.service.dto.UnitEntityDTO;
import com.imapcloud.nest.v2.service.dto.UnitEntityTreeDTO;
import com.imapcloud.nest.v2.web.vo.req.OrgCodeRespVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 单位信息表 前端控制器
 * </p>
 *
 * @author wmin
 * @since 2020-07-02
 */
@Slf4j
@RestController
@RequestMapping("/sysUnit")
public class SysUnitController {

    @Resource
    private SysUnitService sysUnitService;

    @Resource
    private UosOrgManager uosOrgManager;

//    /*添加单位*/
//    @PostMapping("/addSysUnit")
//    public RestRes addRegion(@RequestBody @Valid SysUnitDto sysUnitDto) {
//        String name = sysUnitDto.getName();
//        if (StringUtils.isBlank(name)) {
//            return RestRes.errorParam();
//        }
//        SysUnitEntity sysUnitEntity = sysUnitService.getOne(new QueryWrapper<SysUnitEntity>().eq("name", name).eq("deleted", false));
//        if (sysUnitEntity != null) {
//            return RestRes.err("当前已有同名单位!");
//        }
//
//        String accountId = TrustedAccessTracerHolder.get().getAccountId();
//
//        SysUnitEntity newSysUnitEntity = new SysUnitEntity();
//        BeanUtils.copyProperties(sysUnitDto, newSysUnitEntity);
//        newSysUnitEntity.setCreateTime(LocalDateTime.now());
//        //暂时直接写死在代码里面，配置文件先不操作。每个环境还不确定
//        newSysUnitEntity.setTitle(geoaiUosProperties.getSystemTitle());
//        // 填充中科云图默认icon路径
//        newSysUnitEntity.setIconUrl(geoaiUosProperties.getSystemIcon());
//        // 填充中科云图默认favicon路径
//        newSysUnitEntity.setFaviconUrl(geoaiUosProperties.getSystemFavicon());
//        newSysUnitEntity.setCreatorId(accountId);
//        newSysUnitEntity.setModifyTime(LocalDateTime.now());
//        boolean saveUnit = sysUnitService.save(newSysUnitEntity);
//        mapManageService.saveUnitAddMap(Long.valueOf(newSysUnitEntity.getId()));
//        SysTagEntity sysTagEntity = new SysTagEntity();
//        sysTagEntity.setName(name);
//        sysTagEntity.setUnitId(newSysUnitEntity.getId());
//        sysTagEntity.setCreateTime(LocalDateTime.now());
//        sysTagEntity.setModifyTime(LocalDateTime.now());
//        sysTagEntity.setTagType(1);
//        sysTagEntity.setSeq(0);
//        boolean saveUnitTag = sysTagService.save(sysTagEntity);
//
//        // 创建单位默认角色及单位角色关系
//        OrgRoleAddInDTO orgRoleAddInDTO = new OrgRoleAddInDTO();
//        orgRoleAddInDTO.setUnitId(newSysUnitEntity.getId());
//        orgRoleAddInDTO.setAccountId(accountId);
//        orgRoleService.orgRoleAdd(orgRoleAddInDTO);
//        return saveUnit && saveUnitTag ? RestRes.ok("单位添加成功!") : RestRes.err("单位添加失败!");
//    }
//
//    /*更改单位*/
//    @PostMapping("/updateSysUnit")
//    public RestRes updateRegion(@RequestBody @Valid SysUnitDto sysUnitDto) {
//        String name = sysUnitDto.getName();
//        Integer id = sysUnitDto.getId();
//        Double latitude = sysUnitDto.getLatitude();
//        Double longitude = sysUnitDto.getLongitude();
//        if (id == null) {
//            return RestRes.errorParam();
//        }
//        if (StringUtils.isBlank(name)) {
//            return RestRes.err("单位名称不能为空!");
//        }
//
//        SysUnitEntity sysUnitEntity = sysUnitService.getById(id);
//        if (sysUnitEntity == null) {
//            return RestRes.errorParam();
//        }
//
//        SysUnitEntity sysUnitEntityByName = sysUnitService.getOne(new QueryWrapper<SysUnitEntity>().eq("name", name).eq("deleted", false));
//        if (sysUnitEntityByName != null && !id.equals(sysUnitEntityByName.getId())) {
//            return RestRes.err("当前已有同名单位!");
//        }
//
//        //修改单位的默认标签
//        if (!sysUnitEntity.getName().equals(name)) {
//            sysTagService.lambdaUpdate().set(SysTagEntity::getName, name).eq(SysTagEntity::getName, sysUnitEntity.getName()).update();
//        }
//
//        sysUnitEntity.setModifyTime(LocalDateTime.now());
//        sysUnitEntity.setDescription(sysUnitDto.getDescription());
//        sysUnitEntity.setName(name);
//        sysUnitEntity.setLatitude(latitude);
//        sysUnitEntity.setLongitude(longitude);
//        boolean b = sysUnitService.updateById(sysUnitEntity);
//        return b ? RestRes.ok("单位修改成功!") : RestRes.err("单位修改失败!");
//    }

    @PostMapping("/getSysUnitTitle")
    public RestRes getSysUnitTitle(@RequestBody OrgCodeRespVO body) {
        String orgCode = Optional.ofNullable(body)
                .map(OrgCodeRespVO::getOrgCode)
                .orElseGet(() -> TrustedAccessTracerHolder.get().getOrgCode());
        Optional<OrgThemeInfoOutDO> optional = uosOrgManager.getUosForegroundThemeInfos(orgCode);
        UnitEntityDTO sysUnitEnt = optional.map(r -> {
            UnitEntityDTO ue = new UnitEntityDTO();
            ue.setTitle(r.getSystemTitle());
            ue.setIconUrl(r.getIconUrl());
            ue.setFaviconUrl(r.getFaviconUrl());
            ue.setLoginSetting(r.getLoginSetting());
            return ue;
        }).orElse(null);
        Map<String, Object> map = new HashMap<>(1);
        map.put("sysUnitEntity", sysUnitEnt);
        return sysUnitEnt == null ? RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_EXIST_UNIT.getContent())) : RestRes.ok(map);
    }
    /*获取单位信息*/
    @PostMapping("/getSysUnit")
    public RestRes getSysUnit(@RequestBody OrgCodeRespVO body) {
        String orgCode = Optional.ofNullable(body)
                .map(OrgCodeRespVO::getOrgCode)
                .orElseGet(() -> TrustedAccessTracerHolder.get().getOrgCode());
        UnitEntityDTO sysUnitEnt = uosOrgManager.getOrgInfo(orgCode)
                .map(r -> {
                    UnitEntityDTO ue = new UnitEntityDTO();
                    ue.setName(r.getOrgName());
                    ue.setId(r.getOrgCode());
                    ue.setLongitude(r.getLongitude());
                    ue.setLatitude(r.getLatitude());
                    return ue;
                })
                .orElse(null);
        Map<String, Object> map = new HashMap<>(2);
        map.put("sysUnitEntity", sysUnitEnt);
        return sysUnitEnt == null ? RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_NOT_EXIST_UNIT.getContent())) : RestRes.ok(map);
    }


    /*获取单位信息*/
    @PostMapping("/listSysUnitBy")
    public RestRes listSysUnitBy(@RequestBody SysUnitReqDto sysRoleReqDto) {
        Integer pageNo = sysRoleReqDto.getCurrentPageNo();
        Integer pageSize = sysRoleReqDto.getCurrentPageSize();
        String name = sysRoleReqDto.getName();
        IPage<UnitEntityDTO> sysUnitEntityIPage = sysUnitService.listSysUnitByPages(pageNo, pageSize, name);
        if (sysUnitEntityIPage == null) {
            return RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_FIND_UNITCONTENT.getContent()));
        }

        /*在返回的时候，要加上机巢数*/
        Map<String, Object> map = new HashMap<>(2);
        map.put("sysUnitEntityIPage", sysUnitEntityIPage);
        return CollectionUtils.isEmpty(sysUnitEntityIPage.getRecords()) ? RestRes.err(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_FAIL_FIND_UNITCONTENT.getContent())) : RestRes.ok(map);
    }

//    /*获取单位列表*/
    // 接口废弃
//    @ApiOperation("单位列表查询")
//    @PostMapping("/listSysUnits")
//    public RestRes listSysUnits(@RequestBody SysUnitReqDto sysRoleReqDto) {
//        Integer pageNo = sysRoleReqDto.getCurrentPageNo();
//        Integer pageSize = sysRoleReqDto.getCurrentPageSize();
//        String name = sysRoleReqDto.getName();
//        List<SysUnitEntity> sysUnitEntities = sysUnitService.lambdaQuery().eq(SysUnitEntity::getDeleted, false).list();
//        if (CollectionUtils.isEmpty(sysUnitEntities)) {
//            return RestRes.err("当前找不到相关单位内容!");
//        }
//        Map<Integer, Integer> appCountMap = sysAppMapper.listAppCountByUnit().stream().collect(Collectors.toMap(SysAppEntity::getUnitId, SysAppEntity::getAppCount));
//        Map<Integer, Integer> nestCountMap = nestMapper.listNestCountByUnit().stream().collect(Collectors.toMap(NestDto::getUnitId, NestDto::getNestCount));
//        if (sysUnitEntities != null && !CollectionUtil.isEmpty(sysUnitEntities)) {
//            sysUnitEntities.forEach(it -> {
//                if (appCountMap.get(it.getId()) != null) {
//                    it.setAppCount(appCountMap.get(it.getId()));
//                }
//                if (nestCountMap.get(it.getId()) != null) {
//                    it.setNestCount(nestCountMap.get(it.getId()));
//                }
//            });
//        }
//
//        IPage<SysUnitEntity> sysUnitEntityIPage = sysUnitService.selectUnitPage(pageNo, pageSize, name);
//        List<SysUnitEntity> records = sysUnitEntityIPage.getRecords();
//        //过滤出存在父级单位的情况,并去除子单位
////        List<Integer> parentIds = records.stream().filter(e -> e.getParentId() != null).map(SysUnitEntity::getParentId).collect(toList());
////        records = records.stream().filter(e -> e.getParentId() == null || !parentIds.contains(e.getParentId())).collect(toList());
//        List<Integer> ids = records.stream().map(SysUnitEntity::getId).collect(toList());
//        records = records.stream().filter(re -> !ids.contains(re.getParentId())).collect(toList());
//        records.forEach(
//                e -> {
//                    e.setChildList(getChildrens(e, sysUnitEntities));
//                    if (appCountMap.get(e.getId()) != null) {
//                        e.setAppCount(appCountMap.get(e.getId()));
//                    }
//                    if (nestCountMap.get(e.getId()) != null) {
//                        e.setNestCount(nestCountMap.get(e.getId()));
//                    }
//                }
//        );
//        /*在返回的时候，要加上机巢数*/
//        Map<String, Object> map = new HashMap<>(2);
//        sysUnitEntityIPage.setRecords(records);
//        map.put("sysUnitEntityIPage", sysUnitEntityIPage);
//        return RestRes.ok(map);
//    }


    /*获取单位列表*/
    @ApiOperation("获取单位树")
    @PostMapping("/listUnits")
    public RestRes listUnits() {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        /*在返回的时候，要加上机巢数*/
        Map<String, Object> map = new HashMap<>(1);
        UnitEntityTreeDTO unitEntities = uosOrgManager.getOrgTree(orgCode)
                .map(this::buildOrgTree)
                .orElse(null);
        map.put("unitEntities", Collections.singletonList(unitEntities));
        return RestRes.ok(map);
    }

    private UnitEntityTreeDTO buildOrgTree(OrgNodeOutDO data) {
        if(Objects.nonNull(data)){
            UnitEntityTreeDTO uet = new UnitEntityTreeDTO();
            uet.setId(data.getOrgCode());
            uet.setName(data.getOrgName());
            if(!CollectionUtils.isEmpty(data.getChildren())){
                List<UnitEntityTreeDTO> childList = new ArrayList<>(data.getChildren().size());
                for (OrgNodeOutDO child : data.getChildren()) {
                    UnitEntityTreeDTO dto = buildOrgTree(child);
                    if(Objects.nonNull(dto)){
                        childList.add(dto);
                    }
                }
                uet.setChildList(childList);
            }
            return uet;
        }
        return null;
    }

//    private List<SysUnitEntity> getChildrens(SysUnitEntity sysUnitEntity, List<SysUnitEntity> all) {
//        List<SysUnitEntity> children = all.stream().filter(e -> Objects.equals(e.getParentId(), sysUnitEntity.getId())).map(
//                (e) -> {
//                    e.setChildList(getChildrens(e, all));
//                    return e;
//                }
//        ).sorted(Comparator.comparing(SysUnitEntity::getName)).collect(Collectors.toList());
//        return children;
//    }

//    /*批量删除单位*/
//    @PostMapping("/batchDeleteUnits")
//    public RestRes batchDeleteUnits(@RequestBody SysUnitDto sysUnitDto) {
//        List<Integer> ids = sysUnitDto.getUnitIds();
//        if (CollectionUtils.isEmpty(ids)) {
//            return RestRes.errorParam();
//        }
//        //是否要判断都存在
//        /*todo 1.删除要看是否有些是绑定了机巢的，不可以直接删除。 2.要做软删除。做删除的时候，要看对眼的数据是否有联系，或者是否会改变*/
//        QueryWrapper<SysUnitEntity> queryWrapper = new QueryWrapper<>();
//        queryWrapper.in("id", ids);
//
//        SysUnitEntity updateSysUnitEntity = new SysUnitEntity();
//        updateSysUnitEntity.setDeleted(true);
//
//        //todo  这里要考虑删掉现有的绑定了单位数据的  其他的问题
//
//        return sysUnitService.update(updateSysUnitEntity, queryWrapper) ? RestRes.ok() : RestRes.errorParam();
//    }
//
//    /*更改系统主题*/
//    @PostMapping("/updateSysUnitTopic")
//    public RestRes updateSysUnitTopic(SysUnitTopicReqDto sysUnitTopicReqDto) {
//        String filePath = sysUnitTopicReqDto.getFilePath();
//        String faviconFilePath = sysUnitTopicReqDto.getFaviconFilePath();
//        Integer id = sysUnitTopicReqDto.getId();
//        String title = sysUnitTopicReqDto.getTitle();
//        Integer theme = sysUnitTopicReqDto.getTheme();
//        if (id == null || StringUtils.isBlank(title) || theme == null) {
//            return RestRes.errorParam();
//        }
//        if (StringUtils.isBlank(title)) {
//            return RestRes.err("单位标题不能为空!");
//        }
//
//        SysUnitEntity sysUnitEntity = sysUnitService.getById(id);
//        if (sysUnitEntity == null) {
//            return RestRes.errorParam();
//        }
//        sysUnitEntity.setIconUrl(filePath);
//        sysUnitEntity.setFaviconUrl(faviconFilePath);
//        sysUnitEntity.setModifyTime(LocalDateTime.now());
//        sysUnitEntity.setTitle(title);
//        sysUnitEntity.setTheme(theme);
//
//        return sysUnitService.updateById(sysUnitEntity) ? RestRes.ok("单位主题修改成功!") : RestRes.err("单位主题修改失败!");
//    }

}

