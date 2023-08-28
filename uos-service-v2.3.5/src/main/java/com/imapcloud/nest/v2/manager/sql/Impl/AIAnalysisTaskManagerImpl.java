package com.imapcloud.nest.v2.manager.sql.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.exception.BizException;
import com.imapcloud.nest.v2.common.enums.AIAnalysisTaskStateEnum;
import com.imapcloud.nest.v2.dao.entity.AIAnalysisPhotoEntity;
import com.imapcloud.nest.v2.dao.entity.AIAnalysisTaskEntity;
import com.imapcloud.nest.v2.dao.mapper.AIAnalysisPhotoMapper;
import com.imapcloud.nest.v2.dao.mapper.AIAnalysisTaskMapper;
import com.imapcloud.nest.v2.manager.sql.AIAnalysisTaskManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * AI分析任务数据管理器
 *
 * @author Vastfy
 * @date 2022/11/2 16:20
 * @since 2.1.4
 */
@Slf4j
@Component
public class AIAnalysisTaskManagerImpl implements AIAnalysisTaskManager {

    @Resource
    private AIAnalysisTaskMapper aiAnalysisTaskMapper;

    @Resource
    private AIAnalysisPhotoMapper aiAnalysisPhotoMapper;

    @Override
    public AIAnalysisTaskEntity findAIAnalysisTaskEntity(String taskId) {
        if(StringUtils.hasText(taskId)){
            LambdaQueryWrapper<AIAnalysisTaskEntity> condition = Wrappers.lambdaQuery(AIAnalysisTaskEntity.class)
                    .eq(AIAnalysisTaskEntity::getTaskId, taskId);
            return aiAnalysisTaskMapper.selectOne(condition);
        }
        return null;
    }

    @Override
    public List<AIAnalysisTaskEntity> fetchAIAnalysisTasks(Collection<String> aiTaskIds) {
        if(!CollectionUtils.isEmpty(aiTaskIds)){
            LambdaQueryWrapper<AIAnalysisTaskEntity> condition = Wrappers.lambdaQuery(AIAnalysisTaskEntity.class)
                    .in(AIAnalysisTaskEntity::getAiTaskId, aiTaskIds);
            return aiAnalysisTaskMapper.selectList(condition);
        }
        return Collections.emptyList();
    }

    @Override
    public List<AIAnalysisTaskEntity> fetchAIAnalysisTasks(Integer... aiTaskStates) {
        LambdaQueryWrapper<AIAnalysisTaskEntity> condition = Wrappers.lambdaQuery(AIAnalysisTaskEntity.class);
        if(Objects.nonNull(aiTaskStates) && aiTaskStates.length > 0){
            condition.in(AIAnalysisTaskEntity::getState, Arrays.stream(aiTaskStates).toArray());
        }
        return aiAnalysisTaskMapper.selectList(condition);
    }

    @Override
    public List<AIAnalysisTaskEntity> fetchIncompleteTasks(String accountId) {
        if(StringUtils.hasText(accountId)){
            LambdaQueryWrapper<AIAnalysisTaskEntity> condition = Wrappers.lambdaQuery(AIAnalysisTaskEntity.class)
                    .eq(AIAnalysisTaskEntity::getCreatorId, accountId)
                    .in(AIAnalysisTaskEntity::getState, AIAnalysisTaskStateEnum.PAUSED.getType(),
                            AIAnalysisTaskStateEnum.EXECUTING.getType(), AIAnalysisTaskStateEnum.QUEUING.getType());
            List<AIAnalysisTaskEntity> results = aiAnalysisTaskMapper.selectList(condition);
            if(!CollectionUtils.isEmpty(results)){
                return results;
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<AIAnalysisTaskEntity> fetchOrgIncompleteTasks(String orgCode) {
        if(StringUtils.hasText(orgCode)){
            LambdaQueryWrapper<AIAnalysisTaskEntity> condition = Wrappers.lambdaQuery(AIAnalysisTaskEntity.class)
                    .eq(AIAnalysisTaskEntity::getOrgCode, orgCode)
                    .in(AIAnalysisTaskEntity::getState, AIAnalysisTaskStateEnum.PAUSED.getType(),
                            AIAnalysisTaskStateEnum.EXECUTING.getType(), AIAnalysisTaskStateEnum.QUEUING.getType());
            return aiAnalysisTaskMapper.selectList(condition);
        }
        return Collections.emptyList();
    }

    @Override
    public boolean insertAnalysisTask(AIAnalysisTaskEntity taskEntity, Collection<? extends AIAnalysisPhotoEntity> photos) {
        int affect = aiAnalysisTaskMapper.insert(taskEntity);
        if(!CollectionUtils.isEmpty(photos)){
            aiAnalysisPhotoMapper.saveBatch(new ArrayList<>(photos));
        }
        return affect > 0;
    }

    @Override
    public boolean updateAIAnalysisTaskEntity(AIAnalysisTaskEntity entity) {
        if(Objects.isNull(entity) || Objects.isNull(entity.getId())){
            throw new BizException("AI分析任务表自增主键为空");
        }
        return aiAnalysisTaskMapper.updateById(entity) > 0;
    }

}
