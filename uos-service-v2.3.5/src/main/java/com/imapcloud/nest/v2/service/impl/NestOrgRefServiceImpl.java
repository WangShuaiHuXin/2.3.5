package com.imapcloud.nest.v2.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.imapcloud.nest.v2.dao.entity.BaseNestOrgRefEntity;
import com.imapcloud.nest.v2.dao.mapper.BaseNestOrgRefMapper;
import com.imapcloud.nest.v2.manager.dataobj.out.OrgSimpleOutDO;
import com.imapcloud.nest.v2.manager.rest.UosOrgManager;
import com.imapcloud.nest.v2.service.BaseNestService;
import com.imapcloud.nest.v2.service.NestOrgRefService;
import com.imapcloud.nest.v2.service.dto.out.NestOrgRefOutDTO;
import com.imapcloud.nest.v2.service.dto.out.NestSimpleOutDTO;
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
 * 基站-单位关系业务接口实现
 *
 * @author Vastfy
 * @date 2022/8/25 15:21
 * @since 2.0.0
 */
@Slf4j
@Service
public class NestOrgRefServiceImpl implements NestOrgRefService {

    @Resource
    private BaseNestOrgRefMapper baseNestOrgRefMapper;

    @Resource
    private UosOrgManager uosOrgManager;

    @Resource
    private BaseNestService baseNestService;

    @Override
    public List<NestOrgRefOutDTO> listNestOrgRefs(Collection<String> nestIds, boolean setOrgName) {
        if(CollectionUtils.isEmpty(nestIds)){
            return Collections.emptyList();
        }
        LambdaQueryWrapper<BaseNestOrgRefEntity> condition = Wrappers.lambdaQuery(BaseNestOrgRefEntity.class)
                .in(BaseNestOrgRefEntity::getNestId, nestIds);
        List<BaseNestOrgRefEntity> entities = baseNestOrgRefMapper.selectList(condition);
        if(CollectionUtils.isEmpty(entities)){
            return Collections.emptyList();
        }
        Map<String, String> orgInfoMappings = getOrgInfoMappings(setOrgName, entities);
        return entities.stream()
                .map(r -> {
                    NestOrgRefOutDTO noRef = new NestOrgRefOutDTO();
                    noRef.setNestId(r.getNestId());
                    noRef.setOrgCode(r.getOrgCode());
                    String orgName = orgInfoMappings.get(r.getOrgCode());
                    if(!StringUtils.isEmpty(orgName)) {
                        noRef.setOrgName(orgInfoMappings.get(r.getOrgCode()));
                    }else{
                        noRef.setOrgName("");
                    }
                    return noRef;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<NestOrgRefOutDTO> fetchNestOrgRefs(Collection<String> orgCodes) {
        if(CollectionUtils.isEmpty(orgCodes)){
            return Collections.emptyList();
        }
        LambdaQueryWrapper<BaseNestOrgRefEntity> condition = Wrappers.lambdaQuery(BaseNestOrgRefEntity.class)
                .in(BaseNestOrgRefEntity::getOrgCode, orgCodes);
        List<BaseNestOrgRefEntity> entities = baseNestOrgRefMapper.selectList(condition);
        if(CollectionUtils.isEmpty(entities)){
            return Collections.emptyList();
        }
        return entities.stream()
                .map(r -> {
                    NestOrgRefOutDTO noRef = new NestOrgRefOutDTO();
                    noRef.setNestId(r.getNestId());
                    noRef.setOrgCode(r.getOrgCode());
                    return noRef;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> listOrgVisibleNestIds(String orgCode) {
        if(!StringUtils.hasText(orgCode)){
            return Collections.emptySet();
        }
        LambdaQueryWrapper<BaseNestOrgRefEntity> condition = Wrappers.lambdaQuery(BaseNestOrgRefEntity.class)
                .likeRight(BaseNestOrgRefEntity::getOrgCode, orgCode);
        List<BaseNestOrgRefEntity> entities = baseNestOrgRefMapper.selectList(condition);
        if(CollectionUtils.isEmpty(entities)){
            return Collections.emptySet();
        }
        return entities.stream()
                .map(BaseNestOrgRefEntity::getNestId)
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
    }

    @Override
    public List<Object> listOrgNestInfos(String orgCode) {
        Map<String, Object> result = new HashMap<>(1);
        Optional<OrgSimpleOutDO> orgInfo = uosOrgManager.getOrgInfo(orgCode);
        orgInfo.ifPresent(r -> {
            result.put("id", r.getOrgCode());
            result.put("name", r.getOrgName());
        });
        // 查询所有子孙单位
        Set<String> visibleNestIds = listOrgVisibleNestIds(orgCode);
        List<NestSimpleOutDTO> nestSimpleOutDTOS = baseNestService.listNestInfos(visibleNestIds);
        result.put("nestEntityList", nestSimpleOutDTOS);
        return Collections.singletonList(result);
    }

    @Override
    public void deleteNestOrgRefs(String nestId) {
        if(StringUtils.hasText(nestId)){
            LambdaQueryWrapper<BaseNestOrgRefEntity> condition = Wrappers.lambdaQuery(BaseNestOrgRefEntity.class)
                    .eq(BaseNestOrgRefEntity::getNestId, nestId);
            baseNestOrgRefMapper.delete(condition);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateNestOrgRefs(String nestId, List<String> orgCodes) {
        if(Objects.nonNull(nestId)){
            LambdaQueryWrapper<BaseNestOrgRefEntity> condition = Wrappers.lambdaQuery(BaseNestOrgRefEntity.class)
                    .eq(BaseNestOrgRefEntity::getNestId, nestId);
            int delete = baseNestOrgRefMapper.delete(condition);
            if(log.isDebugEnabled()){
                log.debug("移除基站[{}]原有单位[{}]个", nestId, delete);
            }
            if(!CollectionUtils.isEmpty(orgCodes)){
                String accountId = TrustedAccessTracerHolder.get().getAccountId();
                List<BaseNestOrgRefEntity> entities = orgCodes.stream()
                        .map(r -> {
                            BaseNestOrgRefEntity entity = new BaseNestOrgRefEntity();
                            entity.setNestId(nestId);
                            entity.setOrgCode(r);
                            entity.setCreatedTime(LocalDateTime.now());
                            entity.setCreatorId(accountId);
                            entity.setModifierId(accountId);
                            return entity;
                        })
                        .collect(Collectors.toList());
                int saveBatch = baseNestOrgRefMapper.batchInsert(entities);
                if(log.isDebugEnabled() && saveBatch > 0){
                    log.debug("基站[{}]新绑定单位：{}", nestId, orgCodes);
                }
            }
        }
    }

    private Map<String, String> getOrgInfoMappings(boolean setOrgName, List<BaseNestOrgRefEntity> entities) {
        if(setOrgName){
            List<String> orgCodes = entities.stream()
                    .map(BaseNestOrgRefEntity::getOrgCode)
                    .distinct()
                    .collect(Collectors.toList());
            List<OrgSimpleOutDO> orgInfos = uosOrgManager.listOrgInfos(orgCodes);
            if(!CollectionUtils.isEmpty(orgInfos)){
                return orgInfos.stream()
                        .collect(Collectors.toMap(OrgSimpleOutDO::getOrgCode, OrgSimpleOutDO::getOrgName));
            }
        }
        return Collections.emptyMap();
    }

}
