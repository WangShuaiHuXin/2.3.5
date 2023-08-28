package com.imapcloud.nest.v2.manager.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Maps;
import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisMarkEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisMarkMergeEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisResultEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisResultGroupEntity;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisMarkMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisMarkMergeMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisResultGroupMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisResultMapper;
import com.imapcloud.nest.v2.manager.event.MarkUndoMergeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * auth chenjiahong
 * data  2022/10/12
 */
@Slf4j
@Service
public class MarkUndoMergeEventListener extends AbstractEventListener<MarkUndoMergeEvent> {

    @Resource
    private DataAnalysisMarkMapper dataAnalysisMarkMapper;

    @Resource
    private DataAnalysisMarkMergeMapper dataAnalysisMarkMergeMapper;

    @Resource
    private DataAnalysisResultGroupMapper dataAnalysisResultGroupMapper;

    @Resource
    private DataAnalysisResultMapper dataAnalysisResultMapper;
    @Override
    @Async("dataAnalysisExecutor")
    @EventListener
    public void eventListener(MarkUndoMergeEvent markUndoMergeEvent) {
        log.info("【MarkUndoMergeEvent】-【MarkUndoMergeEventListener】事件:{}", markUndoMergeEvent.toString());

        List<Long> mergeDetailIds = markUndoMergeEvent.getSource();
        //核实对应结果的markId是否存在merge关系，存在则为存在合并关系
        if (CollectionUtil.isNotEmpty(mergeDetailIds)) {
            List<DataAnalysisMarkEntity> dataAnalysisMarkEntities = dataAnalysisMarkMapper.queryDeleteDataByDetailId(mergeDetailIds);
            //获取图片的所有标注ID
            List<Long> markIds = dataAnalysisMarkEntities.stream().map(DataAnalysisMarkEntity::getMarkId).collect(Collectors.toList());
            //检查所有标注ID是否存在merge关系
            List<DataAnalysisMarkMergeEntity> dataAnalysisMarkMergeEntities = dataAnalysisMarkMergeMapper.queryDataByMarkId(markIds);
            //存在关系不允许删除分组的数据
            if (CollectionUtil.isNotEmpty(dataAnalysisMarkMergeEntities)) {
                List<String> groupId = dataAnalysisMarkMergeEntities.stream().map(DataAnalysisMarkMergeEntity::getResultGroupId).collect(Collectors.toList());
                //查找对应分组是否存在除了撤回核实的标注以外的关联标注  存在则不允许删除分组，不存在则可以删除分组
                LambdaQueryWrapper<DataAnalysisResultEntity> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(DataAnalysisResultEntity::getDeleted, "0");
                wrapper.notIn(DataAnalysisResultEntity::getMarkId, markIds);
                wrapper.in(DataAnalysisResultEntity::getResultGroupId, groupId);
                List<DataAnalysisResultEntity> dataAnalysisResultEntities = dataAnalysisResultMapper.selectList(wrapper);
                if (CollectionUtil.isEmpty(dataAnalysisResultEntities)) {
                    //删除对应的分组
                    List<String> delIds = dataAnalysisResultEntities.stream().map(DataAnalysisResultEntity::getResultGroupId).collect(Collectors.toList());
                    dataAnalysisResultGroupMapper.deleteGroupByIds(delIds);
                }
            }
        }
    }
}
