package com.imapcloud.nest.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.exception.BizException;
import com.geoai.common.web.exception.BizParameterException;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.mapper.ElectronicFenceMapper;
import com.imapcloud.nest.model.ElectronicFenceEntity;
import com.imapcloud.nest.service.SysUnitService;
import com.imapcloud.nest.v2.common.enums.ElecfenceDisplayStateEnum;
import com.imapcloud.nest.v2.common.enums.ElecfenceTypeEnum;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.enums.SharedElecfenceStatusEnum;
import com.imapcloud.nest.v2.dao.entity.ElecfenceShareBlacklistEntity;
import com.imapcloud.nest.v2.dao.mapper.ElecfenceShareBlacklistMapper;
import com.imapcloud.nest.v2.service.UosElecfenceService;
import com.imapcloud.nest.v2.service.dto.in.ElecfenceCreationInDTO;
import com.imapcloud.nest.v2.service.dto.in.ElecfenceModificationInDTO;
import com.imapcloud.nest.v2.service.dto.out.ElecfenceInfoOutDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * UOS电子围栏业务接口实现
 *
 * @author Vastfy
 * @date 2022/9/26 16:32
 * @since 2.1.0
 */
@Slf4j
@Service
public class UosElecfenceServiceImpl implements UosElecfenceService {

    @Resource
    private ElectronicFenceMapper electronicFenceMapper;

    @Resource
    private ElecfenceShareBlacklistMapper elecfenceShareBlacklistMapper;

    @Resource
    private SysUnitService sysUnitService;

    @Override
    public List<ElecfenceInfoOutDTO> getExclusiveElecfenceInfos(String orgCode, Integer state, boolean includeSub) {
        if(StringUtils.hasText(orgCode)){
            LambdaQueryWrapper<ElectronicFenceEntity> condition;
            if(includeSub){
                condition = Wrappers.lambdaQuery(ElectronicFenceEntity.class)
                        .likeRight(ElectronicFenceEntity::getOrgCode, orgCode)
                        .eq(ElectronicFenceEntity::getDeleted, false);
            }else{
                condition = Wrappers.lambdaQuery(ElectronicFenceEntity.class)
                        .eq(ElectronicFenceEntity::getOrgCode, orgCode)
                        .eq(ElectronicFenceEntity::getDeleted, false);
            }
            // 过滤指定状态的数据
            if(Objects.nonNull(state)){
                condition.eq(ElectronicFenceEntity::getState, state);
            }
            List<ElectronicFenceEntity> entities = electronicFenceMapper.selectList(condition);
            if(!CollectionUtils.isEmpty(entities)){
                return entities.stream()
                        .map(r -> {
                            ElecfenceInfoOutDTO info = new ElecfenceInfoOutDTO();
                            info.setId(r.getId().toString());
                            info.setName(r.getName());
                            info.setCoordinates(r.getCoordinate());
                            info.setHeight(r.getHeight());
                            info.setShared(r.getShared());
                            info.setState(r.getState());
                            info.setType(r.getType());
                            info.setOrgCode(r.getOrgCode());
                            info.setEffectiveStartTime(r.getEffectiveStartTime());
                            info.setEffectiveEndTime(r.getEffectiveEndTime());
                            info.setNeverExpired(r.getNeverExpired());
                            return info;
                        })
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<ElecfenceInfoOutDTO> getSharedElecfenceInfos(String orgCode) {
        if(StringUtils.hasText(orgCode)){
            List<String> superiorOrgCodes = sysUnitService.getSuperiorOrgCodes(orgCode);
            if(!CollectionUtils.isEmpty(superiorOrgCodes)){
                LambdaQueryWrapper<ElectronicFenceEntity> condition = Wrappers.lambdaQuery(ElectronicFenceEntity.class)
                        .in(ElectronicFenceEntity::getOrgCode, superiorOrgCodes)
                        .eq(ElectronicFenceEntity::getShared, Boolean.TRUE)
                        // 默认查询电子围栏开启状态（1）的数据
                        .eq(ElectronicFenceEntity::getState, ElecfenceDisplayStateEnum.ON.getType())
                        .eq(ElectronicFenceEntity::getDeleted, false);
                List<ElectronicFenceEntity> entities = electronicFenceMapper.selectList(condition);
                Set<Integer> closedSharedElecfenceIds = getClosedSharedElecfenceIds(orgCode);
                return entities.stream()
                        .map(r -> {
                            ElecfenceInfoOutDTO info = new ElecfenceInfoOutDTO();
                            info.setId(r.getId().toString());
                            info.setName(r.getName());
                            info.setCoordinates(r.getCoordinate());
                            info.setHeight(r.getHeight());
                            info.setShared(r.getShared());
                            info.setType(r.getType());
                            info.setOrgCode(r.getOrgCode());
                            info.setEffectiveStartTime(r.getEffectiveStartTime());
                            info.setEffectiveEndTime(r.getEffectiveEndTime());
                            info.setNeverExpired(r.getNeverExpired());
                            // 状态取共享的开启关闭
                            info.setState(closedSharedElecfenceIds.contains(r.getId()) ? SharedElecfenceStatusEnum.OFF.getStatus() : SharedElecfenceStatusEnum.ON.getStatus());
                            return info;
                        })
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<ElecfenceInfoOutDTO> listVisibleElecfenceInfos() {
        // 该接口只查询已开启的数据
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        // 专属数据（只查询开启的数据）：包含子单位的数据
        List<ElecfenceInfoOutDTO> exclusiveElecfenceInfos = getExclusiveElecfenceInfos(orgCode, ElecfenceDisplayStateEnum.ON.getType(), true);
        // 适航区要求只展示本单位数据：过滤适航区且不是用户单位的数据
        exclusiveElecfenceInfos = exclusiveElecfenceInfos.stream()
                .filter(r -> ElecfenceTypeEnum.NO_FLY_ZONE.matchEquals(r.getType())
                        || (ElecfenceTypeEnum.AIRWORTHINESS_ZONE.matchEquals(r.getType()) && Objects.equals(r.getOrgCode(), orgCode)))
                .collect(Collectors.toList());
        // 查询共享数据
        List<ElecfenceInfoOutDTO> sharedElecfenceInfos = this.getSharedElecfenceInfos(orgCode);
        // 合并去重
        int size = exclusiveElecfenceInfos.size() + exclusiveElecfenceInfos.size();
        List<ElecfenceInfoOutDTO> result = new ArrayList<>(size);
        if(!CollectionUtils.isEmpty(exclusiveElecfenceInfos)){
            result.addAll(exclusiveElecfenceInfos);
        }
        if(!CollectionUtils.isEmpty(sharedElecfenceInfos)){
            result.addAll(sharedElecfenceInfos);
        }
        // 过滤掉有效期之外的数据
        LocalDateTime now = LocalDateTime.now();
        return result.stream()
                .filter(r -> Boolean.TRUE.equals(r.getNeverExpired()) || (now.isAfter(r.getEffectiveStartTime()) && now.isBefore(r.getEffectiveEndTime())))
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public String createElecfence(ElecfenceCreationInDTO data) {
        // 只允许上级单位新建子孙单位数据
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        if(!data.getOrgCode().startsWith(orgCode)){
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ONLY_ALLOW_UPPER_LEVEL_UNIT_NEW_ELECTRONIC_FENCE_DATA.getContent()));
        }
        LocalDateTime effectiveStartTime = Objects.nonNull(data.getEffectiveStartTime()) ? data.getEffectiveStartTime() : LocalDateTime.now();
        if(Boolean.FALSE.equals(data.getNeverExpired()) && Objects.isNull(data.getEffectiveEndTime())){
            // TODO 国际化
            throw new BizParameterException("有效期截止不能为空");
        }
        ElectronicFenceEntity entity = new ElectronicFenceEntity();
        entity.setName(data.getName());
        entity.setHeight(data.getHeight());
        entity.setState(data.getState());
        entity.setType(data.getType());
        entity.setShared(data.getShared());
        entity.setCoordinate(data.getCoordinates());
        entity.setEffectiveStartTime(effectiveStartTime);
        entity.setNeverExpired(data.getNeverExpired());
        entity.setEffectiveEndTime(Objects.nonNull(data.getEffectiveEndTime()) ? data.getEffectiveEndTime() : effectiveStartTime.plusYears(100L));
        entity.setOrgCode(data.getOrgCode());
        entity.setCreateTime(LocalDateTime.now());
        electronicFenceMapper.insert(entity);
        return entity.getId().toString();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void modifyElecfence(String elecfenceId, ElecfenceModificationInDTO data) {
        ElectronicFenceEntity dbEntity = checkAndGot(elecfenceId);
        if(Boolean.FALSE.equals(data.getNeverExpired()) && Objects.isNull(data.getEffectiveEndTime())){
            // TODO 国际化
            throw new BizParameterException("有效期截止不能为空");
        }
        if(Objects.nonNull(data.getEffectiveStartTime()) && data.getEffectiveStartTime().isBefore(dbEntity.getEffectiveStartTime())){
            // TODO 国际化
            throw new BizParameterException("有效期开始时间不能向前调整");
        }
        ElectronicFenceEntity entity = new ElectronicFenceEntity();
        entity.setId(dbEntity.getId());
        boolean changed = false;
        if(StringUtils.hasText(data.getName())){
            entity.setName(data.getName());
            changed = true;
        }
        if(Objects.nonNull(data.getHeight())){
            entity.setHeight(data.getHeight());
            changed = true;
        }
        if(Objects.nonNull(data.getState())){
            entity.setState(data.getState());
            changed = true;
        }
        if(Objects.nonNull(data.getShared())){
            entity.setShared(data.getShared());
            changed = true;
        }
        if(StringUtils.hasText(data.getCoordinates())){
            entity.setCoordinate(data.getCoordinates());
            changed = true;
        }
        if(Objects.nonNull(data.getEffectiveStartTime())){
            entity.setEffectiveStartTime(data.getEffectiveStartTime());
            changed = true;
        }
        if(Objects.nonNull(data.getEffectiveEndTime())){
            entity.setEffectiveEndTime(data.getEffectiveEndTime());
            changed = true;
        }
        if(Objects.nonNull(data.getNeverExpired())){
            entity.setNeverExpired(data.getNeverExpired());
            changed = true;
        }
        if(changed){
            entity.setModifyTime(LocalDateTime.now());
            electronicFenceMapper.updateById(entity);
        }
        // 共享开关：开启-->关闭，同步删除所有子孙单位的黑名单
        if(Objects.equals(dbEntity.getShared(), Boolean.TRUE) && Objects.equals(data.getShared(), Boolean.FALSE)){
            LambdaQueryWrapper<ElecfenceShareBlacklistEntity> condition = Wrappers.lambdaQuery(ElecfenceShareBlacklistEntity.class)
                    .likeRight(ElecfenceShareBlacklistEntity::getOrgCode, dbEntity.getOrgCode());
            elecfenceShareBlacklistMapper.delete(condition);
        }
    }

    private ElectronicFenceEntity checkAndGot(String elecfenceId) {
        ElectronicFenceEntity dbEntity = electronicFenceMapper.selectById(Integer.valueOf(elecfenceId));
        if(Objects.isNull(dbEntity)){
           throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_ELECTRONIC_FENCE_INFORMATION_DOES_NOT_EXIST.getContent()));
        }
        return dbEntity;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void deleteElecfence(String elecfenceId) {
        ElectronicFenceEntity dbEntity = checkAndGot(elecfenceId);
        // 当前电子围栏开启了共享，需要删除所有子孙单位的黑名单
        if(Objects.equals(dbEntity.getShared(), Boolean.TRUE)){
            LambdaQueryWrapper<ElecfenceShareBlacklistEntity> condition = Wrappers.lambdaQuery(ElecfenceShareBlacklistEntity.class)
                    .likeRight(ElecfenceShareBlacklistEntity::getOrgCode, dbEntity.getOrgCode());
            elecfenceShareBlacklistMapper.delete(condition);
        }
        LambdaQueryWrapper<ElectronicFenceEntity> condition = Wrappers.lambdaQuery(ElectronicFenceEntity.class)
                .eq(ElectronicFenceEntity::getId, elecfenceId)
                .eq(ElectronicFenceEntity::getDeleted, false);
        electronicFenceMapper.delete(condition);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void modifyOrgElecfenceStatus(String orgCode, String elecfenceId, Integer status) {
        // 删除共享表黑名单（保证数据不重复）
        LambdaQueryWrapper<ElecfenceShareBlacklistEntity> condition = Wrappers.lambdaQuery(ElecfenceShareBlacklistEntity.class)
                .eq(ElecfenceShareBlacklistEntity::getOrgCode, orgCode)
                .eq(ElecfenceShareBlacklistEntity::getElecfenceId, Integer.valueOf(elecfenceId));
        elecfenceShareBlacklistMapper.delete(condition);
        if(SharedElecfenceStatusEnum.OFF.matchEquals(status)){
            // 关闭共享，则新增共享表黑名单
            ElecfenceShareBlacklistEntity entity = new ElecfenceShareBlacklistEntity();
            entity.setOrgCode(orgCode);
            entity.setElecfenceId(Integer.valueOf(elecfenceId));
            elecfenceShareBlacklistMapper.insert(entity);
        }
    }

    private Set<Integer> getClosedSharedElecfenceIds(String orgCode) {
        LambdaQueryWrapper<ElecfenceShareBlacklistEntity> condition2 = Wrappers.lambdaQuery(ElecfenceShareBlacklistEntity.class)
                .eq(ElecfenceShareBlacklistEntity::getOrgCode, orgCode)
                .eq(ElecfenceShareBlacklistEntity::getDeleted, false).select(ElecfenceShareBlacklistEntity::getElecfenceId);
        List<ElecfenceShareBlacklistEntity> orgShareElecfenceEntities = elecfenceShareBlacklistMapper.selectList(condition2);
        if(!CollectionUtils.isEmpty(orgShareElecfenceEntities)){
            return orgShareElecfenceEntities.stream()
                    .map(ElecfenceShareBlacklistEntity::getElecfenceId)
                    .collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }
}
