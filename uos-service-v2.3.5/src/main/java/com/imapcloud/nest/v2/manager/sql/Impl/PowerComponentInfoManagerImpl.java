package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.imapcloud.nest.enums.RoleIdenValueEnum;
import com.imapcloud.nest.v2.dao.entity.PowerComponentInfoEntity;
import com.imapcloud.nest.v2.dao.mapper.PowerComponentInfoMapper;
import com.imapcloud.nest.v2.dao.po.in.PowerComponentInfoInPO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerComponentInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerComponentInfoOutDO;
import com.imapcloud.nest.v2.manager.sql.PowerComponentInfoManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 电力部件库信息表
 *
 * @author boluo
 * @date 2022-11-24
 */
@Component
public class PowerComponentInfoManagerImpl implements PowerComponentInfoManager {

    @Resource
    private PowerComponentInfoMapper powerComponentInfoMapper;

    @Override
    public List<PowerComponentInfoOutDO> selectByComponentIdCollection(Collection<String> componentIdCollection) {

        if (CollUtil.isEmpty(componentIdCollection)) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<PowerComponentInfoEntity> queryWrapper = Wrappers.lambdaQuery(PowerComponentInfoEntity.class)
                .in(PowerComponentInfoEntity::getComponentId, componentIdCollection)
                .eq(PowerComponentInfoEntity::getDeleted, false);
        List<PowerComponentInfoEntity> powerComponentInfoEntityList = powerComponentInfoMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(powerComponentInfoEntityList)) {
            return Collections.emptyList();
        }
        List<PowerComponentInfoOutDO> powerComponentInfoOutDOList = Lists.newLinkedList();
        for (PowerComponentInfoEntity powerComponentInfoEntity : powerComponentInfoEntityList) {

            powerComponentInfoOutDOList.add(toPowerComponentInfoOutDO(powerComponentInfoEntity));
        }
        return powerComponentInfoOutDOList;
    }

    private PowerComponentInfoOutDO toPowerComponentInfoOutDO(PowerComponentInfoEntity powerComponentInfoEntity) {
        PowerComponentInfoOutDO powerComponentInfoOutDO = new PowerComponentInfoOutDO();
        powerComponentInfoOutDO.setComponentId(powerComponentInfoEntity.getComponentId());
        powerComponentInfoOutDO.setComponentName(powerComponentInfoEntity.getComponentName());
        powerComponentInfoOutDO.setComponentPicture(powerComponentInfoEntity.getComponentPicture());
        powerComponentInfoOutDO.setComponentPictureName(powerComponentInfoEntity.getComponentPictureName());
        powerComponentInfoOutDO.setOrgCode(powerComponentInfoEntity.getOrgCode());
        powerComponentInfoOutDO.setEquipmentType(powerComponentInfoEntity.getEquipmentType());
        powerComponentInfoOutDO.setDescription(powerComponentInfoEntity.getDescription());
        powerComponentInfoOutDO.setRoleIdenValueEnumList(RoleIdenValueEnum.bitNumToEnum(powerComponentInfoEntity.getAnalysisType()));
        powerComponentInfoOutDO.setCreatedTime(powerComponentInfoEntity.getCreatedTime());
        powerComponentInfoOutDO.setModifiedTime(powerComponentInfoEntity.getModifiedTime());
        powerComponentInfoOutDO.setModifierId(powerComponentInfoEntity.getModifierId());
        return powerComponentInfoOutDO;
    }

    private PowerComponentInfoEntity toPowerComponentInfoEntity(PowerComponentInfoInDO powerComponentInfoInDO) {
        PowerComponentInfoEntity powerComponentInfoEntity = new PowerComponentInfoEntity();
        powerComponentInfoEntity.setComponentId(powerComponentInfoInDO.getComponentId());
        powerComponentInfoEntity.setComponentName(powerComponentInfoInDO.getComponentName());
        powerComponentInfoEntity.setComponentPicture(StringUtils.isBlank(powerComponentInfoInDO.getComponentPicture()) ? "" : powerComponentInfoInDO.getComponentPicture());
        powerComponentInfoEntity.setComponentPictureName(StringUtils.isBlank(powerComponentInfoInDO.getComponentPictureName()) ? "" : powerComponentInfoInDO.getComponentPictureName());
        powerComponentInfoEntity.setEquipmentType(powerComponentInfoInDO.getEquipmentType());
        powerComponentInfoEntity.setDescription(StringUtils.isBlank(powerComponentInfoInDO.getDescription()) ? "" : powerComponentInfoInDO.getDescription());
        int analysis = 0;
        for (RoleIdenValueEnum roleIdenValueEnum : powerComponentInfoInDO.getRoleIdenValueEnumList()) {
            analysis += roleIdenValueEnum.getBitNum();
        }
        powerComponentInfoEntity.setOrgCode(powerComponentInfoInDO.getOrgCode());
        powerComponentInfoEntity.setAnalysisType(analysis);
        return powerComponentInfoEntity;
    }

    @Override
    public int update(PowerComponentInfoInDO powerComponentInfoInDO) {

        PowerComponentInfoEntity powerComponentInfoEntity = toPowerComponentInfoEntity(powerComponentInfoInDO);
        powerComponentInfoInDO.setUpdateAccount(powerComponentInfoEntity);
        return powerComponentInfoMapper.updateByComponentId(powerComponentInfoEntity);
    }

    @Override
    public int save(PowerComponentInfoInDO powerComponentInfoInDO) {

        PowerComponentInfoEntity powerComponentInfoEntity = toPowerComponentInfoEntity(powerComponentInfoInDO);
        powerComponentInfoInDO.setInsertAccount(powerComponentInfoEntity);
        return powerComponentInfoMapper.insertOne(powerComponentInfoEntity);
    }

    @Override
    public int deleteByComponentId(String componentId, String accountId) {
        if (StringUtils.isAnyBlank(componentId, accountId)) {
            return 0;
        }
        return powerComponentInfoMapper.deleteByComponentId(componentId, accountId);
    }

    @Override
    public long listSum(PowerComponentInfoInDO.ListInfoInDO listInfoInDO) {

        return powerComponentInfoMapper.listSum(toListInfoInPO(listInfoInDO));
    }

    private PowerComponentInfoInPO.ListInPO toListInfoInPO(PowerComponentInfoInDO.ListInfoInDO listInfoInDO) {
        PowerComponentInfoInPO.ListInPO listInfoInPO = new PowerComponentInfoInPO.ListInPO();
        listInfoInPO.setOrgCode(listInfoInDO.getOrgCode());
        listInfoInPO.setEquipmentType(listInfoInDO.getEquipmentType());
        listInfoInPO.setComponentName(listInfoInDO.getComponentName());
        listInfoInPO.setStart(listInfoInDO.getStart());
        listInfoInPO.setEnd(listInfoInDO.getEnd());
        listInfoInPO.setPageNo(listInfoInDO.getPageNo());
        listInfoInPO.setPageSize(listInfoInDO.getPageSize());
        listInfoInPO.setLimit(listInfoInDO.toLimit());
        listInfoInPO.setOffset(listInfoInDO.toOffset());

        if (CollUtil.isNotEmpty(listInfoInDO.getRoleIdenValueEnumList())) {
            int analysis = 0;
            for (RoleIdenValueEnum roleIdenValueEnum : listInfoInDO.getRoleIdenValueEnumList()) {
                analysis += roleIdenValueEnum.getBitNum();
            }
            listInfoInPO.setAnalysisType(analysis);
        }

        return listInfoInPO;
    }

    @Override
    public List<PowerComponentInfoOutDO> listPage(PowerComponentInfoInDO.ListInfoInDO listInfoInDO) {

        PowerComponentInfoInPO.ListInPO listInPO = toListInfoInPO(listInfoInDO);
        List<PowerComponentInfoEntity> powerComponentInfoEntityList = powerComponentInfoMapper.listPage(listInPO);
        if (CollUtil.isEmpty(powerComponentInfoEntityList)) {
            return Collections.emptyList();
        }

        List<PowerComponentInfoOutDO> powerComponentInfoOutDOList = Lists.newLinkedList();
        for (PowerComponentInfoEntity powerComponentInfoEntity : powerComponentInfoEntityList) {
            powerComponentInfoOutDOList.add(toPowerComponentInfoOutDO(powerComponentInfoEntity));
        }
        return powerComponentInfoOutDOList;
    }

    @Override
    public List<PowerComponentInfoOutDO> queryListByOrg(String orgCode) {
        LambdaQueryWrapper<PowerComponentInfoEntity> queryWrapper = Wrappers.lambdaQuery(PowerComponentInfoEntity.class)
                .eq(PowerComponentInfoEntity::getDeleted, false)
                .eq(PowerComponentInfoEntity::getOrgCode, orgCode);
        List<PowerComponentInfoEntity> powerComponentInfoEntityList = powerComponentInfoMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(powerComponentInfoEntityList)) {
            return Collections.emptyList();
        }
        List<PowerComponentInfoOutDO> powerComponentInfoOutDOList = Lists.newLinkedList();
        for (PowerComponentInfoEntity powerComponentInfoEntity : powerComponentInfoEntityList) {
            PowerComponentInfoOutDO powerComponentInfoOutDO = new PowerComponentInfoOutDO();
            powerComponentInfoOutDO.setComponentId(powerComponentInfoEntity.getComponentId());
            powerComponentInfoOutDO.setComponentName(powerComponentInfoEntity.getComponentName());
            powerComponentInfoOutDO.setComponentPicture(powerComponentInfoEntity.getComponentPicture());
            powerComponentInfoOutDO.setComponentPictureName(powerComponentInfoEntity.getComponentPictureName());
            powerComponentInfoOutDO.setOrgCode(powerComponentInfoEntity.getOrgCode());
            powerComponentInfoOutDO.setEquipmentType(powerComponentInfoEntity.getEquipmentType());
            powerComponentInfoOutDO.setDescription(powerComponentInfoEntity.getDescription());
            powerComponentInfoOutDO.setRoleIdenValueEnumList(RoleIdenValueEnum.bitNumToEnum(powerComponentInfoEntity.getAnalysisType()));
            powerComponentInfoOutDOList.add(powerComponentInfoOutDO);
        }
        return powerComponentInfoOutDOList;
    }

    @Override
    public List<PowerComponentInfoOutDO> queryListByOrgAKeyWord(String orgCode, String keyWord, String equipmentType) {
        LambdaQueryWrapper<PowerComponentInfoEntity> queryWrapper = Wrappers.lambdaQuery(PowerComponentInfoEntity.class)
                .eq(PowerComponentInfoEntity::getDeleted, false)
                .like(StringUtils.isNotEmpty(keyWord), PowerComponentInfoEntity::getComponentName, keyWord)
                .eq(StringUtils.isNotEmpty(equipmentType), PowerComponentInfoEntity::getEquipmentType, equipmentType)
                .eq(PowerComponentInfoEntity::getOrgCode, orgCode)
                .orderByAsc(PowerComponentInfoEntity::getId);
        List<PowerComponentInfoEntity> powerComponentInfoEntityList = powerComponentInfoMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(powerComponentInfoEntityList)) {
            return Collections.emptyList();
        }
        List<PowerComponentInfoOutDO> powerComponentInfoOutDOList = Lists.newLinkedList();
        for (PowerComponentInfoEntity powerComponentInfoEntity : powerComponentInfoEntityList) {
            PowerComponentInfoOutDO powerComponentInfoOutDO = new PowerComponentInfoOutDO();
            powerComponentInfoOutDO.setComponentId(powerComponentInfoEntity.getComponentId());
            powerComponentInfoOutDO.setComponentName(powerComponentInfoEntity.getComponentName());
            powerComponentInfoOutDO.setComponentPicture(powerComponentInfoEntity.getComponentPicture());
            powerComponentInfoOutDO.setComponentPictureName(powerComponentInfoEntity.getComponentPictureName());
            powerComponentInfoOutDO.setOrgCode(powerComponentInfoEntity.getOrgCode());
            powerComponentInfoOutDO.setEquipmentType(powerComponentInfoEntity.getEquipmentType());
            powerComponentInfoOutDO.setDescription(powerComponentInfoEntity.getDescription());
            powerComponentInfoOutDO.setRoleIdenValueEnumList(RoleIdenValueEnum.bitNumToEnum(powerComponentInfoEntity.getAnalysisType()));
            powerComponentInfoOutDOList.add(powerComponentInfoOutDO);
        }
        return powerComponentInfoOutDOList;
    }
}
