package com.imapcloud.nest.v2.manager.sql.Impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.imapcloud.nest.mapper.SysTagMapper;
import com.imapcloud.nest.mapper.SysTaskTagMapper;
import com.imapcloud.nest.mapper.TaskMapper;
import com.imapcloud.nest.model.SysTagEntity;
import com.imapcloud.nest.model.SysTaskTagEntity;
import com.imapcloud.nest.model.TaskEntity;
import com.imapcloud.nest.v2.manager.dataobj.out.TaskOutDO;
import com.imapcloud.nest.v2.manager.sql.TaskManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 航线任务
 *
 * @author boluo
 * @date 2022-11-30
 */
@Component
public class TaskManagerImpl implements TaskManager {

    @Resource
    private TaskMapper taskMapper;

    @Resource
    private SysTagMapper sysTagMapper;

    @Resource
    private SysTaskTagMapper sysTaskTagMapper;

    private TaskOutDO toTaskOutDO(TaskEntity taskEntity, Map<Integer, Long> taskTagMap, Map<Long, SysTagEntity> tagInfoMap) {

        TaskOutDO taskOutDO = new TaskOutDO();
        taskOutDO.setTaskId(taskEntity.getId().longValue());
        taskOutDO.setName(taskEntity.getName());
        taskOutDO.setDescription(taskEntity.getDescription());
        taskOutDO.setType(taskEntity.getType());
        taskOutDO.setBaseNestId(taskEntity.getBaseNestId());
        taskOutDO.setOrgCode(taskEntity.getOrgCode());
        taskOutDO.setTagId(0L);
        taskOutDO.setTagName("");
        Long tagId = taskTagMap.get(taskEntity.getId());
        if (tagId != null) {
            taskOutDO.setTagId(tagId);
            SysTagEntity sysTagEntity = tagInfoMap.get(tagId);
            if (sysTagEntity != null) {
                taskOutDO.setTagName(sysTagEntity.getName() == null ? "" : sysTagEntity.getName());
            }
        }

        return taskOutDO;
    }

    @Override
    public List<TaskOutDO> selectByTaskIdList(Collection<Long> taskIdList) {
        if (CollUtil.isEmpty(taskIdList)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<TaskEntity> queryWrapper = Wrappers.lambdaQuery(TaskEntity.class)
                .in(TaskEntity::getId, taskIdList)
                .eq(TaskEntity::getDeleted, false);
        List<TaskEntity> taskEntityList = taskMapper.selectList(queryWrapper);
        if (CollUtil.isEmpty(taskEntityList)) {
            return Collections.emptyList();
        }
        // 查询tag
        LambdaQueryWrapper<SysTaskTagEntity> eq = Wrappers.lambdaQuery(SysTaskTagEntity.class)
                .in(SysTaskTagEntity::getTaskId, taskIdList).eq(SysTaskTagEntity::getDeleted, false);
        List<SysTaskTagEntity> sysTaskTagEntityList = sysTaskTagMapper.selectList(eq);

        Map<Integer, Long> taskTagMap = Maps.newHashMap();
        Map<Long, SysTagEntity> tagInfoMap = Maps.newHashMap();
        List<Integer> tagIdList = Lists.newLinkedList();
        for (SysTaskTagEntity sysTaskTagEntity : sysTaskTagEntityList) {
            taskTagMap.put(sysTaskTagEntity.getTaskId(), sysTaskTagEntity.getTagId().longValue());
            tagIdList.add(sysTaskTagEntity.getTagId());
        }
        if (CollUtil.isNotEmpty(tagIdList)) {
            LambdaQueryWrapper<SysTagEntity> sysTagEntityLambdaQueryWrapper = Wrappers.lambdaQuery(SysTagEntity.class)
                    .in(SysTagEntity::getId, tagIdList);
            List<SysTagEntity> sysTagEntityList = sysTagMapper.selectList(sysTagEntityLambdaQueryWrapper);
            tagInfoMap.putAll(sysTagEntityList.stream().collect(Collectors.toMap(SysTagEntity::getId, bean -> bean, (key1, key2) -> key1)));
        }

        return taskEntityList.stream().map(bean -> this.toTaskOutDO(bean, taskTagMap, tagInfoMap)).collect(Collectors.toList());
    }
}
