package com.imapcloud.nest.v2.manager.sql.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.geoai.common.core.exception.BizException;
import com.imapcloud.nest.v2.common.enums.AIAnalysisPhotoStateEnum;
import com.imapcloud.nest.v2.dao.entity.AIAnalysisPhotoEntity;
import com.imapcloud.nest.v2.dao.mapper.AIAnalysisPhotoMapper;
import com.imapcloud.nest.v2.manager.sql.AIAnalysisPhotoManager;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * AI分析图片数据管理器业务接口实现
 *
 * @author Vastfy
 * @date 2022/11/3 16:41
 * @since 2.1.4
 */
@Component
public class AIAnalysisPhotoManagerImpl implements AIAnalysisPhotoManager {

    @Resource
    private AIAnalysisPhotoMapper aiAnalysisPhotoMapper;

    @Override
    public AIAnalysisPhotoEntity findAIAnalysisPhotoEntity(String recordId) {
        if(StringUtils.hasText(recordId)){
            LambdaQueryWrapper<AIAnalysisPhotoEntity> wrapper = Wrappers.lambdaQuery(AIAnalysisPhotoEntity.class)
                    .eq(AIAnalysisPhotoEntity::getRecordId, recordId);
            return aiAnalysisPhotoMapper.selectOne(wrapper);
        }
        return null;
    }

    @Override
    public List<AIAnalysisPhotoEntity> getAnalysisPhotos(String taskId) {
        if(StringUtils.hasText(taskId)){
            LambdaQueryWrapper<AIAnalysisPhotoEntity> wrapper = Wrappers.lambdaQuery(AIAnalysisPhotoEntity.class)
                    .eq(AIAnalysisPhotoEntity::getTaskId, taskId);
            return aiAnalysisPhotoMapper.selectList(wrapper);
        }
        return Collections.emptyList();
    }

    @Override
    public List<AIAnalysisPhotoEntity> fetchAnalysisPhotos(Collection<Long> centerDetailIds, Integer... states) {
        if(!CollectionUtils.isEmpty(centerDetailIds)){
            LambdaQueryWrapper<AIAnalysisPhotoEntity> wrapper = Wrappers.lambdaQuery(AIAnalysisPhotoEntity.class)
                    .in(AIAnalysisPhotoEntity::getCenterDetailId, centerDetailIds);
            if(Objects.nonNull(states) && states.length > 0){
                wrapper.in(AIAnalysisPhotoEntity::getState, states);
            }
            return aiAnalysisPhotoMapper.selectList(wrapper);
        }
        return Collections.emptyList();
    }

    @Override
    public List<AIAnalysisPhotoEntity> fetchAnalysisPhotos(Collection<String> recordIds) {
        return fetchAnalysisPhotoEntities(recordIds);
    }

    @Override
    public List<AIAnalysisPhotoEntity> fetchIncompleteAnalysisPhotos(Collection<String> recordIds) {
        return fetchAnalysisPhotoEntities(recordIds, AIAnalysisPhotoStateEnum.QUEUING.getType(),
                AIAnalysisPhotoStateEnum.EXECUTING.getType(), AIAnalysisPhotoStateEnum.PAUSED.getType());
    }

    @Override
    public int getIncompleteAnalysisPhotoCounts(String taskId) {
        if(StringUtils.hasText(taskId)){
            LambdaQueryWrapper<AIAnalysisPhotoEntity> wrapper = Wrappers.lambdaQuery(AIAnalysisPhotoEntity.class)
                    .eq(AIAnalysisPhotoEntity::getTaskId, taskId)
                    .in(AIAnalysisPhotoEntity::getState, AIAnalysisPhotoStateEnum.QUEUING.getType(),
                            AIAnalysisPhotoStateEnum.EXECUTING.getType(), AIAnalysisPhotoStateEnum.PAUSED.getType());
            return aiAnalysisPhotoMapper.selectCount(wrapper);
        }
        return 0;
    }

    @Override
    public boolean updateAIAnalysisPhotoEntity(AIAnalysisPhotoEntity updateInfo) {
        if(Objects.isNull(updateInfo) || Objects.isNull(updateInfo.getId())){
            throw new BizException("AI分析图片更新记录表自增ID为空");
        }
        LambdaQueryWrapper<AIAnalysisPhotoEntity> wrapper = Wrappers.lambdaQuery(AIAnalysisPhotoEntity.class)
                .eq(AIAnalysisPhotoEntity::getId, updateInfo.getId())
                .eq(AIAnalysisPhotoEntity::getVersion, updateInfo.getVersion() - 1);
        return aiAnalysisPhotoMapper.update(updateInfo, wrapper) > 0;
    }

    @Override
    public void updateAutoAIAnalysisPhoto(Integer expectState, String aiTaskId, String taskId, List<Integer> states) {
        if(!CollectionUtils.isEmpty(states) && StringUtils.hasText(taskId)){
            AIAnalysisPhotoEntity photoEntity = new AIAnalysisPhotoEntity();
            photoEntity.setState(expectState);
            if(StringUtils.hasText(aiTaskId)){
                photoEntity.setAiTaskId(aiTaskId);
            }
            LambdaQueryWrapper<AIAnalysisPhotoEntity> con = Wrappers.lambdaQuery(AIAnalysisPhotoEntity.class)
                    .eq(AIAnalysisPhotoEntity::getTaskId, taskId)
                    .in(AIAnalysisPhotoEntity::getState, states);
            aiAnalysisPhotoMapper.update(photoEntity, con);
        }
    }

    @Override
    public void updateAIAnalysisPhotoState(Integer expectState, String taskId, List<Integer> states) {
        if(!CollectionUtils.isEmpty(states) && StringUtils.hasText(taskId)){
            AIAnalysisPhotoEntity photoEntity = new AIAnalysisPhotoEntity();
            photoEntity.setState(expectState);
            LambdaQueryWrapper<AIAnalysisPhotoEntity> con = Wrappers.lambdaQuery(AIAnalysisPhotoEntity.class)
                    .eq(AIAnalysisPhotoEntity::getTaskId, taskId)
                    .in(AIAnalysisPhotoEntity::getState, states);
            aiAnalysisPhotoMapper.update(photoEntity, con);
        }
    }

    private List<AIAnalysisPhotoEntity> fetchAnalysisPhotoEntities(Collection<String> recordIds, Integer... states){
        if(!CollectionUtils.isEmpty(recordIds)){
            LambdaQueryWrapper<AIAnalysisPhotoEntity> wrapper = Wrappers.lambdaQuery(AIAnalysisPhotoEntity.class)
                    .in(AIAnalysisPhotoEntity::getRecordId, recordIds);
            if(Objects.nonNull(states) && states.length > 0){
                wrapper.in(AIAnalysisPhotoEntity::getState, states);
            }
            return aiAnalysisPhotoMapper.selectList(wrapper);
        }
        return Collections.emptyList();
    }

}
