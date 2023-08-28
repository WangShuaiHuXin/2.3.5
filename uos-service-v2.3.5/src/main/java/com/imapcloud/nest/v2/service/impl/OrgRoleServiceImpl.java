package com.imapcloud.nest.v2.service.impl;

import com.imapcloud.nest.v2.manager.dataobj.out.OrgRoleOutDO;
import com.imapcloud.nest.v2.manager.rest.OrgRoleManager;
import com.imapcloud.nest.v2.service.OrgRoleService;
import com.imapcloud.nest.v2.service.dto.in.OrgRoleInfoInDTO;
import com.imapcloud.nest.v2.service.dto.out.OrgRoleInfoOutDTO;
import com.imapcloud.nest.v2.service.dto.out.OrgRoleOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * org角色服务impl
 *
 * @author boluo
 * @date 2022-05-20
 */
@Slf4j
@Service
public class OrgRoleServiceImpl implements OrgRoleService {

    @Resource
    private OrgRoleManager orgRoleManager;

    @Override
    public OrgRoleInfoOutDTO orgRoleInfo(OrgRoleInfoInDTO orgRoleInfoInDTO) {
        OrgRoleInfoOutDTO orgRoleInfoOutDTO = new OrgRoleInfoOutDTO();
        List<OrgRoleOutDO> orgRoleRefs = orgRoleManager.listOrgRoleRefInfos(orgRoleInfoInDTO.getOrgCode());
        if(!CollectionUtils.isEmpty(orgRoleRefs)){
            List<OrgRoleInfoOutDTO.Info> infoList = new ArrayList<>();
            for (OrgRoleOutDO info : orgRoleRefs) {
                OrgRoleInfoOutDTO.Info bean = new OrgRoleInfoOutDTO.Info();
                bean.setRoleId(info.getRoleId());
                bean.setRoleName(info.getRoleName());
                bean.setRoleType(info.getOrgRoleType());
                infoList.add(bean);
            }
            orgRoleInfoOutDTO.setInfoList(infoList);
        }
        return orgRoleInfoOutDTO;
    }

    /*@Override
    public void orgRoleAdd(OrgRoleAddInDTO orgRoleAddInDTO) {
        log.info("#OrgRoleServiceImpl.orgRoleAdd# orgRoleAddInDTO={}", orgRoleAddInDTO);
        // 查询单位模板
        LambdaQueryWrapper<OrgRoleEntity> orgRoleWrapper = Wrappers.lambdaQuery(OrgRoleEntity.class).eq(OrgRoleEntity::getUnitId, 0);
        List<OrgRoleEntity> orgRoleEntityList = orgRoleMapper.selectList(orgRoleWrapper);
        if (CollUtil.isEmpty(orgRoleEntityList)) {
            return;
        }
        Set<Long> collect = orgRoleEntityList.stream().map(OrgRoleEntity::getRoleId).collect(Collectors.toSet());

        RoleCopyInDO roleCopyInDO = new RoleCopyInDO();
        roleCopyInDO.setTemplateRoleIdList(Lists.newArrayList(collect));
        Result<RoleCopyOutDO> doResult = roleServiceClient.copy(roleCopyInDO);
        RoleCopyOutDO roleCopyOutDO = ResultUtils.getData(doResult);

        List<OrgRoleEntity> addList = Lists.newLinkedList();
        for (OrgRoleEntity entity : orgRoleEntityList) {
            Long newRoleId = roleCopyOutDO.getRoleIdMap().get(entity.getRoleId());
            if (newRoleId == null) {
                continue;
            }
            OrgRoleEntity roleEntity = new OrgRoleEntity();
            roleEntity.setUnitId((long) orgRoleAddInDTO.getUnitId());
            roleEntity.setRoleId(newRoleId);
            roleEntity.setRoleType(entity.getRoleType());
            roleEntity.setCreatorId(orgRoleAddInDTO.getAccountId());
            roleEntity.setModifierId(orgRoleAddInDTO.getAccountId());
            addList.add(roleEntity);
        }
        log.info("#OrgRoleServiceImpl.orgRoleAdd# addList={}", addList);
        int row = orgRoleMapper.batchInsert(addList);
        log.info("#OrgRoleServiceImpl.orgRoleAdd# addList={}, row={}", addList, row);
    }*/

//    @Override
//    public List<OrgRoleOutDTO> listOrgRoleRefs(Collection<String> roleIds, Boolean isDefault) {
//        if(!CollectionUtils.isEmpty(roleIds)){
//            List<OrgRoleOutDO> orgRoleRefs = orgRoleManager.listOrgRoleRefInfos(new ArrayList<>(roleIds), isDefault);
//            if(!CollectionUtils.isEmpty(orgRoleRefs)){
//                return orgRoleRefs.stream()
//                        .map(e -> {
//                            OrgRoleOutDTO roleOutDTO = new OrgRoleOutDTO();
//                            roleOutDTO.setRoleId(e.getRoleId());
//                            roleOutDTO.setOrgCode(e.getOrgCode());
//                            roleOutDTO.setType(e.getOrgRoleType());
//                            return roleOutDTO;
//                        })
//                        .collect(Collectors.toList());
//            }
//        }
//        return Collections.emptyList();
//    }

    @Override
    public List<OrgRoleOutDTO> getOrgRoleRefs(Collection<String> unitIds, Boolean isDefault) {
        if(!CollectionUtils.isEmpty(unitIds)){
            List<OrgRoleOutDO> orgRoleRefs = orgRoleManager.getOrgRoleRefInfos(new ArrayList<>(unitIds), isDefault);
            if(!CollectionUtils.isEmpty(orgRoleRefs)){
                return orgRoleRefs.stream()
                        .map(e -> {
                            OrgRoleOutDTO roleOutDTO = new OrgRoleOutDTO();
                            roleOutDTO.setRoleId(e.getRoleId());
                            roleOutDTO.setOrgCode(e.getOrgCode());
                            roleOutDTO.setType(e.getOrgRoleType());
                            roleOutDTO.setRoleName(e.getRoleName());
                            roleOutDTO.setAppType(e.getAppType());
                            roleOutDTO.setOrgRoleType(e.getOrgRoleType());
                            return roleOutDTO;
                        })
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

//    @Override
//    public OrgRolePageOutDTO rolePageInfoList(OrgRolePageInDTO orgRolePageInDTO) {
//        OrgRolePageOutDTO orgRolePageOutDTO = new OrgRolePageOutDTO();
//        List<OrgRolePageOutDTO.Info> infoList = Lists.newLinkedList();
//        orgRolePageOutDTO.setInfoList(infoList);
//
//        String accountId = orgRolePageInDTO.getAccountId();
//        Long roleId = orgRolePageInDTO.getRoleId();
//        // 查询当前登陆人的单位
//        Long unitId = getUnitIdByAccountId(accountId);
//        if (unitId == null) {
//            return orgRolePageOutDTO;
//        }
//
//        // 查询角色的所在的单位
//        LambdaQueryWrapper<OrgRoleEntity> orgRoleIdWrapper = Wrappers.lambdaQuery(OrgRoleEntity.class)
//                .eq(OrgRoleEntity::getRoleId, roleId);
//        List<OrgRoleEntity> entityList = orgRoleMapper.selectList(orgRoleIdWrapper);
//        if (CollUtil.isEmpty(entityList)) {
//            return orgRolePageOutDTO;
//        }
//        long roleUnitId = entityList.get(0).getUnitId();
//        boolean roleAdmin = entityList.get(0).getRoleType() == OrgRoleTypeEnum.DEFAULT.getType();
//
//        // 查询单位绑定的角色
//        LambdaQueryWrapper<OrgRoleEntity> orgRoleWrapper = Wrappers.lambdaQuery(OrgRoleEntity.class)
//                .in(OrgRoleEntity::getUnitId, Lists.newArrayList(unitId, roleUnitId));
//        List<OrgRoleEntity> orgRoleEntityList = orgRoleMapper.selectList(orgRoleWrapper);
//        if (CollUtil.isEmpty(orgRoleEntityList)) {
//            return orgRolePageOutDTO;
//        }
//        // 获取模板ID
//        Map<Long, List<OrgRoleEntity>> unitIdBeanMap = orgRoleEntityList.stream().collect(Collectors.groupingBy(OrgRoleEntity::getUnitId));
//        long templateRoleId = getTemplateRoleId(unitIdBeanMap, unitId, roleId, roleUnitId);
//        // 查看角色权限
//        ResourcePageListInDO resourcePageListInDO = new ResourcePageListInDO();
//        resourcePageListInDO.setRoleIdList(Lists.newArrayList(roleId, templateRoleId));
//
//        Result<ResourcePageListOutDO> result = resourceServiceClient.pageList(resourcePageListInDO);
//        ResourcePageListOutDO resourcePageListOutDO = ResultUtils.getData(result);
//
//        Map<Long, List<ResourcePageListOutDO.PageInfo>> longListMap = resourcePageListOutDO.getRolePageInfoList().stream()
//                .collect(Collectors.toMap(ResourcePageListOutDO.RolePageInfo::getRoleId,
//                        ResourcePageListOutDO.RolePageInfo::getPageInfoList, (key1, key2) -> key1));
//
//        List<ResourcePageListOutDO.PageInfo> pageInfoList = longListMap.get(roleId);
//        List<ResourcePageListOutDO.PageInfo> templatePageInfoList = longListMap.get(templateRoleId);
//        List<Long> collect;
//        if (CollUtil.isNotEmpty(pageInfoList)) {
//            collect = pageInfoList.stream().map(ResourcePageListOutDO.PageInfo::getPageResourceId).collect(Collectors.toList());
//        } else {
//            collect = Collections.emptyList();
//        }
//
//        // 角色页面权限
//        List<Long> rolePageResourceIdList = nacosConfigurationService.getRolePageResourceIdList();
//        for (ResourcePageListOutDO.PageInfo pageInfo : templatePageInfoList) {
//            if (!roleAdmin && rolePageResourceIdList.contains(pageInfo.getPageResourceId())) {
//                continue;
//            }
//            OrgRolePageOutDTO.Info info = new OrgRolePageOutDTO.Info();
//            info.setPageResourceId(pageInfo.getPageResourceId());
//            info.setPageResourceName(pageInfo.getPageResourceName());
//            info.setPageResourceType(pageInfo.getPageResourceType());
//            info.setPageResourceKey(pageInfo.getPageResourceKey());
//            info.setSeq(pageInfo.getSeq());
//            info.setParentPageResourceId(pageInfo.getParentPageResourceId());
//            info.setActive(collect.contains(pageInfo.getPageResourceId()));
//            infoList.add(info);
//        }
//        return orgRolePageOutDTO;
//    }

}
