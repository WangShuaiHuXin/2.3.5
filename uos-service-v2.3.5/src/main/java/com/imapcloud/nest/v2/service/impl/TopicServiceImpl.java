package com.imapcloud.nest.v2.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.bean.TrustedAccessTracerHolder;
import com.geoai.common.core.util.BizIdUtils;
import com.geoai.common.web.exception.BizParameterException;
import com.geoai.common.web.rest.Result;
import com.geoai.common.web.util.MessageUtils;
import com.google.common.collect.Lists;
import com.imapcloud.nest.enums.RoleIdenValueEnum;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisAlgoProblemTypeRefEntity;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.common.exception.BusinessException;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisTopicIndustryEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisTopicLevelEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisTopicProblemEntity;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisAlgoProblemTypeRefMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisTopicIndustryMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisTopicLevelMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisTopicProblemMapper;
import com.imapcloud.nest.v2.manager.dataobj.out.SimpleDictItemInfoOutDO;
import com.imapcloud.nest.v2.manager.feign.BaseServiceClient;
import com.imapcloud.nest.v2.service.TopicService;
import com.imapcloud.nest.v2.service.dto.in.TopicInDTO;
import com.imapcloud.nest.v2.service.dto.out.AIProblemTypeMappingOutDTO;
import com.imapcloud.nest.v2.service.dto.out.TopicOutDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 专题
 *
 * @author boluo
 * @date 2022-07-14
 */
@Service
public class TopicServiceImpl implements TopicService {

    @Resource
    private DataAnalysisTopicLevelMapper dataAnalysisTopicLevelMapper;

    @Resource
    private DataAnalysisTopicIndustryMapper dataAnalysisTopicIndustryMapper;

    @Resource
    private DataAnalysisTopicProblemMapper dataAnalysisTopicProblemMapper;

    @Resource
    private DataAnalysisAlgoProblemTypeRefMapper dataAnalysisAlgoProblemTypeRefMapper;

    @Resource
    private BaseServiceClient baseServiceClient;

    @Override
    public List<TopicOutDTO.LevelInfoOut> levelList(String topicKey) {

        List<TopicOutDTO.LevelInfoOut> result = Lists.newLinkedList();

        LambdaQueryWrapper<DataAnalysisTopicLevelEntity> wrapper = Wrappers.lambdaQuery(DataAnalysisTopicLevelEntity.class)
                .eq(DataAnalysisTopicLevelEntity::getTopicKey, topicKey);
        List<DataAnalysisTopicLevelEntity> dataAnalysisTopicLevelEntityList = dataAnalysisTopicLevelMapper.selectList(wrapper);

        if (CollUtil.isEmpty(dataAnalysisTopicLevelEntityList)) {
            return result;
        }

        for (DataAnalysisTopicLevelEntity dataAnalysisTopicLevelEntity : dataAnalysisTopicLevelEntityList) {
            TopicOutDTO.LevelInfoOut levelInfo = new TopicOutDTO.LevelInfoOut();
            levelInfo.setTopicLevelId(dataAnalysisTopicLevelEntity.getTopicLevelId());
            levelInfo.setTopicLevelName(MessageUtils.getMessage(dataAnalysisTopicLevelEntity.getTopicLevelName()));
            levelInfo.setTopicLevelCode(dataAnalysisTopicLevelEntity.getTopicLevelCode());
            result.add(levelInfo);
        }

        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void editIndustry(TopicInDTO.EditIndustryIn insertIndustryIn) {
        // 清理单位已有的专题行业
        LambdaQueryWrapper<DataAnalysisTopicIndustryEntity> wrapper = Wrappers.lambdaQuery(DataAnalysisTopicIndustryEntity.class)
                .eq(DataAnalysisTopicIndustryEntity::getOrgCode, insertIndustryIn.getOrgId())
                .eq(DataAnalysisTopicIndustryEntity::getTopicKey, insertIndustryIn.getTopicKey());
        dataAnalysisTopicIndustryMapper.delete(wrapper);
        // 插入最新的单位-行业
        if(!CollectionUtils.isEmpty(insertIndustryIn.getIndustryTypes())){
            List<DataAnalysisTopicIndustryEntity> entities = new ArrayList<>(insertIndustryIn.getIndustryTypes().size());
            for (int i = 0; i < insertIndustryIn.getIndustryTypes().size(); i++) {
                Integer industryType = insertIndustryIn.getIndustryTypes().get(i);
                DataAnalysisTopicIndustryEntity entity = new DataAnalysisTopicIndustryEntity();
                entity.setOrgCode(insertIndustryIn.getOrgId());
                entity.setTopicKey(insertIndustryIn.getTopicKey());
                entity.setCreatorId(insertIndustryIn.getAccountId());
                entity.setModifierId(insertIndustryIn.getAccountId());
                entity.setIndustryType(industryType);
                entity.setSeq(i + 1);
                entities.add(entity);
            }
            dataAnalysisTopicIndustryMapper.batchInsert(entities);
        }
    }

    @Override
    public Map<String, String> getIndustryMappings(){
        Result<List<SimpleDictItemInfoOutDO>> result = baseServiceClient.listDictItemInfos("GEOAI_INDUSTRY_TYPE");
        if(result.isOk()){
            return result.getData()
                    .stream()
                    .collect(Collectors.toMap(SimpleDictItemInfoOutDO::getValue, SimpleDictItemInfoOutDO::getName));
        }
        return Collections.emptyMap();
    }

    @Override
    public List<TopicOutDTO.IndustryListOut> industryList(String orgId, String topicKey) {
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();
        // 查询已有的专题行业
        LambdaQueryWrapper<DataAnalysisTopicIndustryEntity> wrapper = Wrappers.lambdaQuery(DataAnalysisTopicIndustryEntity.class)
                .eq(DataAnalysisTopicIndustryEntity::getTopicKey, topicKey)
                .likeRight(DataAnalysisTopicIndustryEntity::getOrgCode, orgCode);
        if(StringUtils.hasText(orgId)){
            wrapper.eq(DataAnalysisTopicIndustryEntity::getOrgCode, orgId);
        }
        List<DataAnalysisTopicIndustryEntity> entities = dataAnalysisTopicIndustryMapper.selectList(wrapper);

        // 查询行业字典数据
        Map<String, String> industryMappings = getIndustryMappings();
        return entities.stream()
                .map(DataAnalysisTopicIndustryEntity::getIndustryType)
                .distinct()
                .map(r -> {
                    TopicOutDTO.IndustryListOut industryListOut = new TopicOutDTO.IndustryListOut();
                    industryListOut.setIndustryType(r);
                    if(!CollectionUtils.isEmpty(industryMappings)){
                        String industryName = industryMappings.get(r.toString());
                        industryListOut.setTopicIndustryName(industryName);
                    }
                    return industryListOut;
                })
                .collect(Collectors.toList());
    }

    private List<DataAnalysisTopicProblemEntity> getOrgIndustryProblemList(String orgId, String topicKey, int industryType){
        String orgCode = TrustedAccessTracerHolder.get().getOrgCode();

        // 查询指定行业下的问题
        LambdaQueryWrapper<DataAnalysisTopicProblemEntity> wrapper = Wrappers.lambdaQuery(DataAnalysisTopicProblemEntity.class)
                .eq(DataAnalysisTopicProblemEntity::getTopicKey, topicKey)
                .eq(DataAnalysisTopicProblemEntity::getIndustryType, industryType)
                .eq(DataAnalysisTopicProblemEntity::getSource, 0)
                .likeRight(DataAnalysisTopicProblemEntity::getOrgCode, orgCode);
        if(StringUtils.hasText(orgId)){
            wrapper.eq(DataAnalysisTopicProblemEntity::getOrgCode, orgId);
        }
        // 系统默认的问题类型
        wrapper.or(w -> w.eq(DataAnalysisTopicProblemEntity::getSource, -1).eq(DataAnalysisTopicProblemEntity::getIndustryType, industryType));
        return dataAnalysisTopicProblemMapper.selectList(wrapper);
    }

    private Map<Long, DataAnalysisTopicProblemEntity> getDbProblemEntityMap(List<DataAnalysisTopicProblemEntity> entities){
        if(!CollectionUtils.isEmpty(entities)){
            return entities.stream()
                    .collect(Collectors.toMap(DataAnalysisTopicProblemEntity::getTopicProblemId, r -> r));
        }
        return Collections.emptyMap();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void editIndustryProblem(TopicInDTO.EditIndustryProblemIn editIndustryProblemIn) {

        // 查询操作人所属单位下的指定行业的问题：系统默认和自定义
        List<DataAnalysisTopicProblemEntity> dataAnalysisTopicProblemEntityList = getOrgIndustryProblemList(editIndustryProblemIn.getOrgId(), editIndustryProblemIn.getTopicKey(), editIndustryProblemIn.getIndustryType());

        Map<Long, DataAnalysisTopicProblemEntity> map = getDbProblemEntityMap(dataAnalysisTopicProblemEntityList);
        List<TopicInDTO.IndustryProblemIn> candidates = editIndustryProblemIn.getIndustryProblemInList();
        // 1. 过滤非系统默认的问题类型
        if(!CollectionUtils.isEmpty(map)){
            candidates = editIndustryProblemIn.getIndustryProblemInList()
                    .stream()
                    .filter(r -> {
                        if (Objects.isNull(r.getTopicProblemId())) {
                            return true;
                        }
                        return map.containsKey(r.getTopicProblemId()) && !Objects.equals(map.get(r.getTopicProblemId()).getSource(), -1);
                    })
                    .collect(Collectors.toList());
        }
        Set<String> duplicates = new HashSet<>();
        List<DataAnalysisTopicProblemEntity> addList = new ArrayList<>();
        Map<Long, DataAnalysisTopicProblemEntity> updateMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        if(!CollectionUtils.isEmpty(candidates)){
            for (int i = 0; i < candidates.size(); i++) {
                TopicInDTO.IndustryProblemIn industryProblemIn = candidates.get(i);
                // 保证提交的数据内名称不重复
                if(!duplicates.add(industryProblemIn.getTopicProblemName())){
                    throw new BizParameterException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_INDUSTRY_ISSUE_NAMES_ARE_NOT_ALLOWED_TO_REPEAT.getContent()));
                }
                // 新增的问题类型
                if(Objects.isNull(industryProblemIn.getTopicProblemId()) || !map.containsKey(industryProblemIn.getTopicProblemId())){
                    DataAnalysisTopicProblemEntity entity = new DataAnalysisTopicProblemEntity();
                    entity.setTopicProblemId(BizIdUtils.snowflakeId());
                    entity.setTopicProblemName(industryProblemIn.getTopicProblemName());
                    entity.setIndustryType(editIndustryProblemIn.getIndustryType());
                    entity.setOrgCode(editIndustryProblemIn.getOrgId());
                    entity.setTopicKey(editIndustryProblemIn.getTopicKey());
                    // 默认为单位自定义类型
                    entity.setSource(0);
                    entity.setSeq(i + 1);
                    entity.setCreatorId(editIndustryProblemIn.getAccountId());
                    entity.setModifierId(editIndustryProblemIn.getAccountId());
                    entity.setCreatedTime(now);
                    entity.setModifiedTime(now);
                    addList.add(entity);
                }
                // 更新的问题类型
                else{
                    DataAnalysisTopicProblemEntity entity = map.get(industryProblemIn.getTopicProblemId());
                    entity.setSeq(i + 1);
                    entity.setModifierId(editIndustryProblemIn.getAccountId());
                    entity.setTopicProblemName(industryProblemIn.getTopicProblemName());
                    updateMap.put(industryProblemIn.getTopicProblemId(), entity);
                }
            }
        }

        // 处理需要删除的行业问题
        for (Map.Entry<Long, DataAnalysisTopicProblemEntity> entry : map.entrySet()) {
            DataAnalysisTopicProblemEntity entity = entry.getValue();
            // 忽略系统默认的问题类型和非当前提交行业的问题类型
            if(Objects.equals(entity.getSource(), -1)
                    || !Objects.equals(entity.getIndustryType(), editIndustryProblemIn.getIndustryType())){
                continue;
            }
            // 删除的行业问题
            if (!updateMap.containsKey(entity.getTopicProblemId())) {
                entity.setModifierId(editIndustryProblemIn.getAccountId());
                entity.setDeleted(true);
                entity.setSeq(0);
                updateMap.put(entity.getTopicProblemId(), entity);
            }
        }

        // 持久化
        if (!CollectionUtils.isEmpty(addList)) {
            dataAnalysisTopicProblemMapper.batchInsert(addList);
        }
        if (!CollectionUtils.isEmpty(dataAnalysisTopicProblemEntityList)) {
            dataAnalysisTopicProblemMapper.batchUpdate(dataAnalysisTopicProblemEntityList);
        }
    }

    @Override
    public List<TopicOutDTO.IndustryProblemListOut> industryProblemList(String orgId, String topicKey, long topicIndustryId) {
        throw new UnsupportedOperationException("接口已过时，请使用新接口");
    }

    @Override
    public List<TopicOutDTO.IndustryProblemListOut> industryProblemList(String orgId, String topicKey, int industryType) {
        List<DataAnalysisTopicProblemEntity> dataAnalysisTopicProblemEntityList = getOrgIndustryProblemList(orgId, topicKey, industryType);
        // 返回结果
        List<TopicOutDTO.IndustryProblemListOut> industryProblemListOutList = Lists.newLinkedList();
        dataAnalysisTopicProblemEntityList.stream()
                .sorted((o1, o2) -> {
                    // 优先以数据类型排序
                    if(o1.getSource() - o2.getSource() == 0){
                        return o1.getSeq() - o2.getSeq();
                    }
                    return o1.getSource() - o2.getSource();
                })
                .forEach(dataAnalysisTopicProblemEntity -> {
                    TopicOutDTO.IndustryProblemListOut industryProblemListOut = new TopicOutDTO.IndustryProblemListOut();
                    industryProblemListOut.setTopicProblemId(dataAnalysisTopicProblemEntity.getTopicProblemId());
                    industryProblemListOut.setTopicProblemName(dataAnalysisTopicProblemEntity.getTopicProblemName());
                    industryProblemListOut.setSource(dataAnalysisTopicProblemEntity.getSource());
                    industryProblemListOutList.add(industryProblemListOut);
                });
        return industryProblemListOutList;
    }

    @Override
    public Long getDefaultTopicLevelId() {
        List<TopicOutDTO.LevelInfoOut> levelInfoOuts = this.levelList(RoleIdenValueEnum.ANALYSIS_STATISTICS_ZH.getRoleKey());
        for (TopicOutDTO.LevelInfoOut levelInfoOut : levelInfoOuts) {
            if(Objects.equals("ordinary", levelInfoOut.getTopicLevelCode())){
                return levelInfoOut.getTopicLevelId();
            }
        }
        return null;
    }

    @Override
    public List<AIProblemTypeMappingOutDTO> fetchProblemInfos(Collection<String> aiProblemTypeIds) {
        // 根据 UDA 算法侧的问题类型ID获取UOS的问题类型映射
        if(CollectionUtils.isEmpty(aiProblemTypeIds)){
            return Collections.emptyList();
        }
        LambdaQueryWrapper<DataAnalysisAlgoProblemTypeRefEntity> condition = Wrappers.lambdaQuery(DataAnalysisAlgoProblemTypeRefEntity.class)
                .in(DataAnalysisAlgoProblemTypeRefEntity::getTypeRelationId, aiProblemTypeIds);
        List<DataAnalysisAlgoProblemTypeRefEntity> refs = dataAnalysisAlgoProblemTypeRefMapper.selectList(condition);
        if(CollectionUtils.isEmpty(refs)){
            return Collections.emptyList();
        }

        Set<Long> uosProblemTypeIds = refs.stream()
                .map(DataAnalysisAlgoProblemTypeRefEntity::getTypeId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        LambdaQueryWrapper<DataAnalysisTopicProblemEntity> condition2 = Wrappers.lambdaQuery(DataAnalysisTopicProblemEntity.class)
                .in(DataAnalysisTopicProblemEntity::getTopicProblemId, uosProblemTypeIds);
        List<DataAnalysisTopicProblemEntity> problemEntities = dataAnalysisTopicProblemMapper.selectList(condition2);
        if(CollectionUtils.isEmpty(problemEntities)){
            return Collections.emptyList();
        }
        Map<Long, DataAnalysisTopicProblemEntity> problemEntityMap = problemEntities.stream()
                .collect(Collectors.toMap(DataAnalysisTopicProblemEntity::getTopicProblemId, r -> r));

        return refs.stream()
                .filter(r -> problemEntityMap.containsKey(r.getTypeId()))
                .map(r -> {
                    AIProblemTypeMappingOutDTO mapping = new AIProblemTypeMappingOutDTO();
                    mapping.setAiProblemTypeId(r.getTypeRelationId());
                    mapping.setProblemTypeId(r.getTypeId());
                    mapping.setProblemTypeName(problemEntityMap.get(r.getTypeId()).getTopicProblemName());
                    mapping.setIndustryType(problemEntityMap.get(r.getTypeId()).getIndustryType());
                    return mapping;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TopicOutDTO.IndustryProblemListOut> selectIndustryProblemList(Collection<Long> topicProblemIdCollection) {

        if (CollUtil.isEmpty(topicProblemIdCollection)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<DataAnalysisTopicProblemEntity> queryWrapper = Wrappers
                .lambdaQuery(DataAnalysisTopicProblemEntity.class)
                .in(DataAnalysisTopicProblemEntity::getTopicProblemId, topicProblemIdCollection)
                .eq(DataAnalysisTopicProblemEntity::getDeleted, false);
        List<DataAnalysisTopicProblemEntity> entityList = dataAnalysisTopicProblemMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(entityList)) {
            return Collections.emptyList();
        }
        return entityList.stream().map(this::toIndustryProblemListOut).collect(Collectors.toList());
    }

    private TopicOutDTO.IndustryProblemListOut toIndustryProblemListOut(DataAnalysisTopicProblemEntity entity) {
        TopicOutDTO.IndustryProblemListOut out = new TopicOutDTO.IndustryProblemListOut();
        out.setTopicProblemId(entity.getTopicProblemId());
        out.setTopicProblemName(entity.getTopicProblemName());
        out.setSource(entity.getSource());
        out.setIndustryType(entity.getIndustryType());
        return out;
    }
}
