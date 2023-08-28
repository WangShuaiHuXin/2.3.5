package com.imapcloud.nest.v2.manager.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.imapcloud.nest.common.listener.AbstractEventListener;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisMarkEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisMarkMergeEntity;
import com.imapcloud.nest.v2.dao.entity.DataAnalysisResultEntity;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisMarkMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisMarkMergeMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisResultGroupMapper;
import com.imapcloud.nest.v2.dao.mapper.DataAnalysisResultMapper;
import com.imapcloud.nest.v2.manager.event.MarkDelResultEvent;
import com.imapcloud.nest.v2.service.DataAnalysisResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName MarkDelResultListener.java
 * @Description MarkDelResultListener
 * @createTime 2022年03月23日 09:12:00
 */
@Slf4j
@Service
public class MarkDelResultListener extends AbstractEventListener<MarkDelResultEvent> {

    @Resource
    private DataAnalysisMarkMapper dataAnalysisMarkMapper;

    @Resource
    private DataAnalysisResultService dataAnalysisResultService;

    @Resource
    private DataAnalysisMarkMergeMapper dataAnalysisMarkMergeMapper;

    @Resource
    private DataAnalysisResultGroupMapper dataAnalysisResultGroupMapper;

    @Resource
    private DataAnalysisResultMapper dataAnalysisResultMapper;

    /**
     * 消息监听-处理
     *
     * @param markDelResultEvent 消息事件
     */
    @Override
    @Async("dataAnalysisExecutor")
    @EventListener
    public void eventListener(MarkDelResultEvent markDelResultEvent) {
        log.info("【MarkDelResultEvent】-【MarkDelResultListener】事件:{}", markDelResultEvent.toString());

        List<Long> details = markDelResultEvent.getSource();
        if (CollectionUtil.isNotEmpty(details)) {
            List<DataAnalysisMarkEntity> dataAnalysisMarkEntities = this.dataAnalysisMarkMapper.queryDeleteDataByDetailId(details);
            List<Long> markIds = dataAnalysisMarkEntities.stream().map(DataAnalysisMarkEntity::getMarkId).collect(Collectors.toList());
            //为 true 则为撤回图片
            Boolean flag = markDelResultEvent.getFlag();
            this.dataAnalysisResultService.deleteByMarkIdList(markIds,flag);
        }
    }

}
