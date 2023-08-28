package com.imapcloud.nest.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imapcloud.nest.mapper.SysTaskTagMapper;
import com.imapcloud.nest.model.SysTaskTagEntity;
import com.imapcloud.nest.model.TaskEntity;
import com.imapcloud.nest.pojo.dto.MissionRecordsDto;
import com.imapcloud.nest.service.SysTaskTagService;
import com.imapcloud.nest.utils.Query;
import com.imapcloud.nest.utils.redis.RedisKeyConstantList;
import com.imapcloud.nest.utils.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 任务标签关系表 服务实现类
 * </p>
 *
 * @author kings
 * @since 2020-12-03
 */
@Service
public class SysTaskTagServiceImpl extends ServiceImpl<SysTaskTagMapper, SysTaskTagEntity> implements SysTaskTagService {

    @Autowired
    private RedisService redisService;

    @Override
    public List<SysTaskTagEntity> listTaskTagAndName(List<Integer> taskIds) {
        return baseMapper.listTaskTagAndName(taskIds);
    }

    @Override
    public List<SysTaskTagEntity> listAllTaskTagAndName() {
        return baseMapper.listAllTaskTagAndName();
    }

    @Override
    public List<Integer> selectTagIdByAirLineId(Integer airLineId) {
        if (airLineId != null) {
            return baseMapper.selectTagIdByAirLineId(airLineId);
        }
        return Collections.emptyList();
    }

    @Override
    public List<SysTaskTagEntity> getSysTaskTag() {
        return baseMapper.getSysTaskTag();
    }

    @Override
    public IPage<MissionRecordsDto> getMissionRecords(Map<String, Object> params, Integer tagId, Integer dataType, String name) {
        return baseMapper.getMissionRecords(new Query<TaskEntity>().getPage(params), tagId, dataType, name);
    }

    @Override
    public List<Integer> getMissionRecordsIds(List<Long> tagIds, String startTime, String endTime) {
        return baseMapper.getMissionRecordsIds(tagIds, startTime, endTime);

    }

    @Override
    public String getTagNameByTaskIdCache(Integer taskId) {
        String redisKey = RedisKeyConstantList.redisKeyFormat(RedisKeyConstantList.SYS_TASK_TAG, taskId);
        String tagName = (String) redisService.get(redisKey);
        if (tagName == null) {
            tagName = baseMapper.getTagNameByTaskId(taskId);
            if (tagName != null) {
                redisService.set(redisKey, tagName);
            }
        }
        return tagName;
    }
}
