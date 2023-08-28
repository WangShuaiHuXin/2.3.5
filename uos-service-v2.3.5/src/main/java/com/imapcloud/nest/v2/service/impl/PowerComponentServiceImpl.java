package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.geoai.common.core.bean.PageResultInfo;
import cn.hutool.core.collection.CollectionUtil;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.web.util.MessageUtils;
import com.geoai.common.web.util.ResultUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.imapcloud.nest.enums.RoleIdenValueEnum;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.dao.entity.PowerEquipmentLegerInfoEntity;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerComponentInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerComponentRuleInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerComponentRuleInfraredInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.*;
import com.imapcloud.nest.v2.manager.feign.AccountServiceClient;
import com.imapcloud.nest.v2.manager.sql.*;
import com.imapcloud.nest.v2.service.PowerComponentService;
import com.imapcloud.nest.v2.service.dto.in.PowerComponentInDTO;
import com.imapcloud.nest.v2.service.dto.out.PowerComponentOutDTO;
import com.imapcloud.nest.v2.service.dto.out.ComponentOptionListOutDTO;
import com.imapcloud.sdk.utils.StringUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 部件库
 *
 * @author boluo
 * @date 2022-11-25
 */
@Service
public class PowerComponentServiceImpl implements PowerComponentService {

    @Resource
    private PowerComponentInfoManager powerComponentInfoManager;

    @Resource
    private PowerComponentRuleInfoManager powerComponentRuleInfoManager;

    @Resource
    private AccountServiceClient accountServiceClient;

    @Resource
    private PowerWaypointLedgerInfoManager powerWaypointLedgerInfoManager;

    @Resource
    private PowerEquipmentLegerInfoManager powerEquipmentLegerInfoManager;

    @Resource
    private PowerComponentRuleInfraredInfoManager powerComponentRuleInfraredInfoManager;

    @Override
    public void saveOrUpdate(PowerComponentInDTO.SaveOrUpdateInDTO saveOrUpdateInDTO) {

        PowerComponentInfoInDO powerComponentInfoInDO = new PowerComponentInfoInDO();

        powerComponentInfoInDO.setComponentName(saveOrUpdateInDTO.getComponentName());
        powerComponentInfoInDO.setComponentPicture(saveOrUpdateInDTO.getComponentPicture());
        powerComponentInfoInDO.setComponentPictureName(saveOrUpdateInDTO.getComponentPictureName());
        powerComponentInfoInDO.setEquipmentType(saveOrUpdateInDTO.getEquipmentType());
        powerComponentInfoInDO.setDescription(saveOrUpdateInDTO.getDescription());
        powerComponentInfoInDO.setRoleIdenValueEnumList(RoleIdenValueEnum.idenValueToEnum(saveOrUpdateInDTO.getAnalysisTypeList()));
        powerComponentInfoInDO.setAccountId(saveOrUpdateInDTO.getAccountId());

        if (StringUtils.isNotBlank(saveOrUpdateInDTO.getComponentId())) {

            // 更新
            List<PowerComponentInfoOutDO> powerComponentInfoOutDOList =
                    powerComponentInfoManager.selectByComponentIdCollection(Lists.newArrayList(saveOrUpdateInDTO.getComponentId()));
            if (CollUtil.isEmpty(powerComponentInfoOutDOList)) {
                throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWER_COMPONENT_BUSINESS_001.getContent()));
            }
            powerComponentInfoInDO.setComponentId(saveOrUpdateInDTO.getComponentId());
            powerComponentInfoManager.update(powerComponentInfoInDO);
        } else {
            powerComponentInfoInDO.setComponentId(BizIdUtils.snowflakeIdStr());
            powerComponentInfoInDO.setOrgCode(saveOrUpdateInDTO.getOrgCode());
            powerComponentInfoManager.save(powerComponentInfoInDO);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void componentRuleEdit(PowerComponentInDTO.ComponentRuleInDTO componentRuleInDTO) {

        String componentId = componentRuleInDTO.getComponentId();
        String accountId = componentRuleInDTO.getAccountId();
        // 查询部件库
        List<PowerComponentInfoOutDO> powerComponentInfoOutDOList =
                powerComponentInfoManager.selectByComponentIdCollection(Lists.newArrayList(componentId));
        if (CollUtil.isEmpty(powerComponentInfoOutDOList)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWER_COMPONENT_BUSINESS_001.getContent()));
        }
        List<PowerComponentRuleInfoInDO> addInfoList = Lists.newLinkedList();
        Map<String, Integer> seqMap = Maps.newHashMap();
        Map<String, PowerComponentInDTO.RuleInDTO> inDtoMap = Maps.newHashMap();
        for (int i = 0; i < componentRuleInDTO.getRuleInDTOList().size(); i++) {

            PowerComponentInDTO.RuleInDTO ruleInDTO = componentRuleInDTO.getRuleInDTOList().get(i);
            if (StringUtils.isBlank(ruleInDTO.getComponentRuleId())) {
                PowerComponentRuleInfoInDO powerComponentRuleInfoInDO = new PowerComponentRuleInfoInDO();
                powerComponentRuleInfoInDO.setComponentRuleId(BizIdUtils.snowflakeIdStr());
                powerComponentRuleInfoInDO.setComponentId(componentId);
                powerComponentRuleInfoInDO.setComponentRuleName(ruleInDTO.getComponentRuleName());
                powerComponentRuleInfoInDO.setAlarmStatus(ruleInDTO.getAlarmStatus());
                powerComponentRuleInfoInDO.setAlarmMin(ruleInDTO.getAlarmMin());
                powerComponentRuleInfoInDO.setAlarmMax(ruleInDTO.getAlarmMax());
                powerComponentRuleInfoInDO.setAccountId(accountId);
                powerComponentRuleInfoInDO.setSeq(i + 1);
                addInfoList.add(powerComponentRuleInfoInDO);
            } else {
                seqMap.put(ruleInDTO.getComponentRuleId(), i + 1);
                inDtoMap.put(ruleInDTO.getComponentRuleId(), ruleInDTO);
            }
        }

        List<PowerComponentRuleInfoInDO> updateInfoList = Lists.newLinkedList();
        List<String> deleteList = Lists.newLinkedList();
        // 查询部件库规则
        List<PowerComponentRuleInfoOutDO> powerComponentRuleInfoOutDOList = powerComponentRuleInfoManager.selectByComponentId(componentId);
        for (PowerComponentRuleInfoOutDO powerComponentRuleInfoOutDO : powerComponentRuleInfoOutDOList) {
            Integer seq = seqMap.get(powerComponentRuleInfoOutDO.getComponentRuleId());
            if (seq != null) {
                PowerComponentInDTO.RuleInDTO ruleInDTO = inDtoMap.get(powerComponentRuleInfoOutDO.getComponentRuleId());
                PowerComponentRuleInfoInDO powerComponentRuleInfoInDO = new PowerComponentRuleInfoInDO();
                powerComponentRuleInfoInDO.setComponentRuleId(powerComponentRuleInfoOutDO.getComponentRuleId());
                powerComponentRuleInfoInDO.setComponentRuleName(ruleInDTO.getComponentRuleName());
                powerComponentRuleInfoInDO.setAlarmStatus(ruleInDTO.getAlarmStatus());
                powerComponentRuleInfoInDO.setAlarmMin(ruleInDTO.getAlarmMin());
                powerComponentRuleInfoInDO.setAlarmMax(ruleInDTO.getAlarmMax());
                powerComponentRuleInfoInDO.setAccountId(accountId);
                powerComponentRuleInfoInDO.setSeq(seq);
                updateInfoList.add(powerComponentRuleInfoInDO);
            } else {
                deleteList.add(powerComponentRuleInfoOutDO.getComponentRuleId());
            }
        }
        powerComponentRuleInfoManager.batchInsert(addInfoList);
        powerComponentRuleInfoManager.batchUpdate(updateInfoList);
        powerComponentRuleInfoManager.deleteByComponentRuleIdList(deleteList, accountId);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void componentDeleteByComponentId(String componentId, String accountId) {

        // 部件库已被关联不能删除
        List<PowerWaypointLedgerInfoOutDO> powerWaypointLedgerInfoOutDOList = powerWaypointLedgerInfoManager.selectByComponentIdList(Lists.newArrayList(componentId));
        if (CollUtil.isNotEmpty(powerWaypointLedgerInfoOutDOList)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWER_COMPONENT_BUSINESS_002.getContent()));
        }
        powerComponentInfoManager.deleteByComponentId(componentId, accountId);
        powerComponentRuleInfoManager.deleteByComponentId(componentId, accountId);
        powerComponentRuleInfraredInfoManager.deleteByComponentId(componentId, accountId);
    }

    @Override
    public PowerComponentOutDTO.PowerComponentInfoOutDTO componentDetail(String componentId) {

        List<PowerComponentInfoOutDO> powerComponentInfoOutDOList = powerComponentInfoManager.selectByComponentIdCollection(Lists.newArrayList(componentId));
        if (CollUtil.isEmpty(powerComponentInfoOutDOList)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWER_COMPONENT_BUSINESS_001.getContent()));
        }
        // 部件库规则
        List<PowerComponentRuleInfoOutDO> powerComponentRuleInfoOutDOList = powerComponentRuleInfoManager.selectByComponentId(componentId);
        // 红外测温规则
        List<PowerComponentRuleInfraredInfoOutDO> powerComponentRuleInfraredInfoOutDOList = powerComponentRuleInfraredInfoManager.selectListByComponentIdCollection(Lists.newArrayList(componentId));

        PowerComponentInfoOutDO powerComponentInfoOutDO = powerComponentInfoOutDOList.get(0);
        PowerComponentOutDTO.PowerComponentInfoOutDTO powerComponentInfoOutDTO = new PowerComponentOutDTO.PowerComponentInfoOutDTO();
        toPowerComponentInfoOutDTO(powerComponentInfoOutDTO, powerComponentInfoOutDO);
        powerComponentInfoOutDTO.setRuleInfoList(toPowerComponentRuleInfoOutDTO(powerComponentRuleInfoOutDOList));
        powerComponentInfoOutDTO.setInfraredRuleInfoList(toInfraredRuleInfo(powerComponentRuleInfraredInfoOutDOList));
        return powerComponentInfoOutDTO;
    }

    private List<PowerComponentOutDTO.PowerComponentRuleInfoOutDTO> toPowerComponentRuleInfoOutDTO(List<PowerComponentRuleInfoOutDO> powerComponentRuleInfoOutDOList) {

        if (CollUtil.isEmpty(powerComponentRuleInfoOutDOList)) {
            return Collections.emptyList();
        }
        powerComponentRuleInfoOutDOList.sort(Comparator.comparing(PowerComponentRuleInfoOutDO::getSeq));
        List<PowerComponentOutDTO.PowerComponentRuleInfoOutDTO> powerComponentRuleInfoOutDTOList = Lists.newLinkedList();
        for (PowerComponentRuleInfoOutDO powerComponentRuleInfoOutDO : powerComponentRuleInfoOutDOList) {
            PowerComponentOutDTO.PowerComponentRuleInfoOutDTO powerComponentRuleInfoOutDTO = new PowerComponentOutDTO.PowerComponentRuleInfoOutDTO();
            BeanUtils.copyProperties(powerComponentRuleInfoOutDO, powerComponentRuleInfoOutDTO);
            powerComponentRuleInfoOutDTOList.add(powerComponentRuleInfoOutDTO);
        }
        return powerComponentRuleInfoOutDTOList;
    }

    private void toPowerComponentInfoOutDTO(PowerComponentOutDTO.PowerComponentInfoOutDTO powerComponentInfoOutDTO, PowerComponentInfoOutDO powerComponentInfoOutDO) {
        BeanUtils.copyProperties(powerComponentInfoOutDO, powerComponentInfoOutDTO);
        powerComponentInfoOutDTO.setAnalysisTypeList(powerComponentInfoOutDO.getRoleIdenValueEnumList().stream()
                .map(RoleIdenValueEnum::getIdenValue).collect(Collectors.toList()));
    }

    @Override
    public PageResultInfo<PowerComponentOutDTO.PowerComponentInfoOutDTO> componentList(PowerComponentInDTO.ComponentListInDTO componentListInDTO) {

        PowerComponentInfoInDO.ListInfoInDO listInfoInDO = new PowerComponentInfoInDO.ListInfoInDO();
        BeanUtils.copyProperties(componentListInDTO, listInfoInDO);
        if (componentListInDTO.getAnalysisType() != null) {
            listInfoInDO.setRoleIdenValueEnumList(RoleIdenValueEnum.idenValueToEnum(Lists.newArrayList(componentListInDTO.getAnalysisType())));
        }

        long listSum = powerComponentInfoManager.listSum(listInfoInDO);
        if (listSum == 0) {
            return PageResultInfo.of(listSum, Collections.emptyList());
        }
        List<PowerComponentInfoOutDO> powerComponentInfoOutDOList = powerComponentInfoManager.listPage(listInfoInDO);

        if (CollUtil.isEmpty(powerComponentInfoOutDOList)) {
            return PageResultInfo.of(listSum, Collections.emptyList());
        }
        // 查询规则
        List<String> componentIdList = powerComponentInfoOutDOList.stream().map(PowerComponentInfoOutDO::getComponentId).collect(Collectors.toList());
        List<PowerComponentRuleInfoOutDO> powerComponentRuleInfoOutDOList = powerComponentRuleInfoManager.selectByComponentIdList(componentIdList);
        Map<String, List<PowerComponentRuleInfoOutDO>> collectMap = powerComponentRuleInfoOutDOList.stream().collect(Collectors.groupingBy(PowerComponentRuleInfoOutDO::getComponentId));
        // 查询红外规则
        List<PowerComponentRuleInfraredInfoOutDO> powerComponentRuleInfraredInfoOutDOList = powerComponentRuleInfraredInfoManager.selectListByComponentIdCollection(componentIdList);
        Map<String, List<PowerComponentRuleInfraredInfoOutDO>> infraredMap = powerComponentRuleInfraredInfoOutDOList.stream().collect(Collectors.groupingBy(PowerComponentRuleInfraredInfoOutDO::getComponentId));
        // 查询操作人
        List<String> accountIdList = powerComponentInfoOutDOList.stream().map(PowerComponentInfoOutDO::getModifierId).collect(Collectors.toList());
        List<AccountOutDO> accountOutDOList = ResultUtils.getData(accountServiceClient.listAccountInfos(accountIdList));
        Map<String, String> accountMap = accountOutDOList.stream()
                .collect(Collectors.toMap(AccountOutDO::getAccountId, AccountOutDO::getName, (key1, key2) -> key1));

        List<PowerComponentOutDTO.PowerComponentInfoOutDTO> outDTOList = Lists.newLinkedList();
        for (PowerComponentInfoOutDO powerComponentInfoOutDO : powerComponentInfoOutDOList) {

            PowerComponentOutDTO.PowerComponentInfoOutDTO powerComponentInfoOutDTO = new PowerComponentOutDTO.PowerComponentInfoOutDTO();
            toPowerComponentInfoOutDTO(powerComponentInfoOutDTO, powerComponentInfoOutDO);
            powerComponentInfoOutDTO.setRuleInfoList(toPowerComponentRuleInfoOutDTO(collectMap.get(powerComponentInfoOutDO.getComponentId())));

            powerComponentInfoOutDTO.setInfraredRuleInfoList(toInfraredRuleInfo(infraredMap.get(powerComponentInfoOutDO.getComponentId())));
            String accountName = accountMap.get(powerComponentInfoOutDO.getModifierId());
            powerComponentInfoOutDTO.setOperator(accountName == null ? powerComponentInfoOutDO.getModifierId() : accountName);
            outDTOList.add(powerComponentInfoOutDTO);
        }
        return PageResultInfo.of(listSum, outDTOList);
    }

    private List<PowerComponentOutDTO.InfraredRuleInfoOutDTO> toInfraredRuleInfo(List<PowerComponentRuleInfraredInfoOutDO> infraredInfoOutDOList) {

        if (CollUtil.isEmpty(infraredInfoOutDOList)) {
            return Collections.emptyList();
        }
        List<PowerComponentOutDTO.InfraredRuleInfoOutDTO> infraredRuleInfoList = Lists.newLinkedList();
        infraredInfoOutDOList.sort(Comparator.comparing(PowerComponentRuleInfraredInfoOutDO::getSeq));
        for (PowerComponentRuleInfraredInfoOutDO powerComponentRuleInfraredInfoOutDO : infraredInfoOutDOList) {
            PowerComponentOutDTO.InfraredRuleInfoOutDTO infraredRuleInfo = new PowerComponentOutDTO.InfraredRuleInfoOutDTO();
            BeanUtils.copyProperties(powerComponentRuleInfraredInfoOutDO, infraredRuleInfo);
            infraredRuleInfoList.add(infraredRuleInfo);
        }
        return infraredRuleInfoList;
    }

    @Override
    public List<ComponentOptionListOutDTO> componentOptionList(String orgCode, String keyWord, String waypointId) {
        //查询对应航点信息
        List<ComponentOptionListOutDTO> result = new ArrayList<>();
        PowerWaypointLedgerInfoOutDO powerWaypointLedgerInfoOutDOS = powerWaypointLedgerInfoManager.selectByWaypointStationId(waypointId);
        if (ObjectUtils.isEmpty(powerWaypointLedgerInfoOutDOS)) {
            throw new BusinessException("查询航点台账设备失败,请稍后重试");
        }
        //获取航点是否已关联设备
        List<PowerEquipmentLegerInfoOutDO> powerEquipmentLegerInfoOutDOList = powerEquipmentLegerInfoManager.queryEquipmentByIdCollection(Lists.newArrayList(powerWaypointLedgerInfoOutDOS.getEquipmentId()));

        if (CollUtil.isEmpty(powerEquipmentLegerInfoOutDOList)) {
            return result;
        }
        String equipmentRelPmsId = powerEquipmentLegerInfoOutDOList.get(0).getPmsId();
        //查询对应设备信息
        PowerEquipmentLegerInfoEntity powerEquipmentLegerInfoEntity = powerEquipmentLegerInfoManager.queryEquipmentByPmsAndOrg(equipmentRelPmsId, orgCode);
        if (ObjectUtils.isEmpty(powerWaypointLedgerInfoOutDOS)) {
            throw new BusinessException("查询航点台账关联的设备失败，请关联后重试");
        }
        //查询对应类型
        String equipmentType = powerEquipmentLegerInfoEntity.getEquipmentType();
        if (StringUtils.isEmpty(equipmentType)) {
            throw new BusinessException("当前航点台账关联设备类型为空，请完善后重试");
        }

        List<PowerComponentInfoOutDO> powerComponentInfoOutDOS = powerComponentInfoManager.queryListByOrgAKeyWord(orgCode, keyWord, equipmentType);
        if (CollectionUtil.isNotEmpty(powerComponentInfoOutDOS)) {
            result = powerComponentInfoOutDOS.stream().map(e -> {
                ComponentOptionListOutDTO dto = new ComponentOptionListOutDTO();
                dto.setComponentId(e.getComponentId());
                dto.setComponentName(e.getComponentName());
                return dto;
            }).collect(Collectors.toList());
            return result;
        }
        return null;
    }

    @Override
    public Map<String, List<Integer>> selectComponentRuleByWaypointIdList(List<String> waypointIdList, String orgCode) {

        List<PowerWaypointLedgerInfoOutDO> powerWaypointLedgerInfoOutDOList = powerWaypointLedgerInfoManager.selectByWaypointIdList(waypointIdList, orgCode);
        if (CollUtil.isEmpty(powerWaypointLedgerInfoOutDOList)) {
            return Collections.emptyMap();
        }

        Set<String> componentIdSet = powerWaypointLedgerInfoOutDOList.stream().map(PowerWaypointLedgerInfoOutDO::getComponentId)
                .filter(StringUtils::isNotBlank).collect(Collectors.toSet());

        Map<String, List<PowerWaypointLedgerInfoOutDO>> stringListMap = powerWaypointLedgerInfoOutDOList.stream()
                .filter(bean -> StringUtils.isNotBlank(bean.getComponentId()))
                .collect(Collectors.groupingBy(PowerWaypointLedgerInfoOutDO::getComponentId));

        List<PowerComponentInfoOutDO> powerComponentInfoOutDOList = powerComponentInfoManager.selectByComponentIdCollection(componentIdSet);
        Map<String, List<Integer>> listMap = powerComponentInfoOutDOList.stream().collect(Collectors.toMap(PowerComponentInfoOutDO::getComponentId
                , bean -> bean.getRoleIdenValueEnumList().stream().map(RoleIdenValueEnum::getIdenValue).collect(Collectors.toList())
                , (key1, key2) -> key1));
        Map<String, List<Integer>> resultMap = Maps.newHashMap();
        stringListMap.forEach((key, value) -> {
            List<Integer> integerList = listMap.get(key);
            if (CollUtil.isEmpty(integerList)) {
                return;
            }
            for (PowerWaypointLedgerInfoOutDO powerWaypointLedgerInfoOutDO : value) {
                resultMap.put(powerWaypointLedgerInfoOutDO.getWaypointId(), Lists.newArrayList(integerList));
            }
        });
        return resultMap;
    }

    @Override
    public void componentInfraredRuleEdit(PowerComponentInDTO.ComponentRuleInfraredInDTO componentRuleInfraredInDTO) {
        String componentId = componentRuleInfraredInDTO.getComponentId();
        String accountId = componentRuleInfraredInDTO.getAccountId();
        // 查询部件库
        List<PowerComponentInfoOutDO> powerComponentInfoOutDOList =
                powerComponentInfoManager.selectByComponentIdCollection(Lists.newArrayList(componentId));
        if (CollUtil.isEmpty(powerComponentInfoOutDOList)) {
            throw new BusinessException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_POWER_COMPONENT_BUSINESS_001.getContent()));
        }
        List<PowerComponentRuleInfraredInfoInDO> addInfoList = Lists.newLinkedList();
        Map<String, Integer> seqMap = Maps.newHashMap();
        Map<String, PowerComponentInDTO.InfraredRuleInDTO> inDtoMap = Maps.newHashMap();
        for (int i = 0; i < componentRuleInfraredInDTO.getInfraredRuleInDTOList().size(); i++) {

            PowerComponentInDTO.InfraredRuleInDTO infraredRuleInDTO = componentRuleInfraredInDTO.getInfraredRuleInDTOList().get(i);
            if (StringUtils.isBlank(infraredRuleInDTO.getComponentRuleId())) {
                PowerComponentRuleInfraredInfoInDO powerComponentRuleInfoInDO = new PowerComponentRuleInfraredInfoInDO();
                powerComponentRuleInfoInDO.setComponentRuleId(BizIdUtils.snowflakeIdStr());
                powerComponentRuleInfoInDO.setComponentId(componentId);
                powerComponentRuleInfoInDO.setDeviceState(infraredRuleInDTO.getDeviceState());
                powerComponentRuleInfoInDO.setInfraredRuleState(infraredRuleInDTO.getInfraredRuleState());
                powerComponentRuleInfoInDO.setThreshold(infraredRuleInDTO.getThreshold());
                powerComponentRuleInfoInDO.setAccountId(accountId);
                powerComponentRuleInfoInDO.setSeq(i + 1);
                addInfoList.add(powerComponentRuleInfoInDO);
            } else {
                seqMap.put(infraredRuleInDTO.getComponentRuleId(), i + 1);
                inDtoMap.put(infraredRuleInDTO.getComponentRuleId(), infraredRuleInDTO);
            }
        }

        List<PowerComponentRuleInfraredInfoInDO> updateInfoList = Lists.newLinkedList();
        List<String> deleteList = Lists.newLinkedList();
        // 查询部件库规则
        List<PowerComponentRuleInfraredInfoOutDO> powerComponentRuleInfraredInfoOutDOList =
                powerComponentRuleInfraredInfoManager.selectListByComponentIdCollection(Lists.newArrayList(componentId));
        for (PowerComponentRuleInfraredInfoOutDO powerComponentRuleInfoOutDO : powerComponentRuleInfraredInfoOutDOList) {
            Integer seq = seqMap.get(powerComponentRuleInfoOutDO.getComponentRuleId());
            if (seq != null) {
                PowerComponentInDTO.InfraredRuleInDTO infraredRuleInDTO = inDtoMap.get(powerComponentRuleInfoOutDO.getComponentRuleId());
                PowerComponentRuleInfraredInfoInDO powerComponentRuleInfoInDO = new PowerComponentRuleInfraredInfoInDO();
                powerComponentRuleInfoInDO.setComponentRuleId(powerComponentRuleInfoOutDO.getComponentRuleId());
                powerComponentRuleInfoInDO.setDeviceState(infraredRuleInDTO.getDeviceState());
                powerComponentRuleInfoInDO.setInfraredRuleState(infraredRuleInDTO.getInfraredRuleState());
                powerComponentRuleInfoInDO.setThreshold(infraredRuleInDTO.getThreshold());
                powerComponentRuleInfoInDO.setAccountId(accountId);
                powerComponentRuleInfoInDO.setSeq(seq);
                updateInfoList.add(powerComponentRuleInfoInDO);
            } else {
                deleteList.add(powerComponentRuleInfoOutDO.getComponentRuleId());
            }
        }
        powerComponentRuleInfraredInfoManager.batchInsert(addInfoList);
        powerComponentRuleInfraredInfoManager.batchUpdate(updateInfoList);
        powerComponentRuleInfraredInfoManager.deleteByComponentRuleIdList(deleteList, accountId);
    }
}
