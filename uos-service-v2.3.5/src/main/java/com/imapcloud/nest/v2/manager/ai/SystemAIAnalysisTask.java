package com.imapcloud.nest.v2.manager.ai;

import com.geoai.common.core.exception.BizException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * 系统AI识别分析任务
 * @author Vastfy
 * @date 2022/11/30 18:13
 * @since 2.1.5
 */
@Slf4j
public class SystemAIAnalysisTask extends AbstractAIAnalysisTask {

    @Getter
    private final String orgCode;

    /**
     * 事件发布器
     */
    private final ApplicationEventPublisher publisher;

    /**
     * 单位运行中AI任务队列数据
     * key: 单位编码
     * value: AI分析任务ID列表
     */
    private static final Map<String, Set<String>> ORG_AI_ANALYSIS_TASKS = new ConcurrentHashMap<>();

    /**
     * 单位-自动AI识别任务待运行队列
     * key: 单位编码
     * value: 待运行AI分析任务ID
     */
    private static final Map<String, Queue<WaitingTaskElement>> ORG_WAITING_TASK_QUEUE_MAP = new ConcurrentHashMap<>();

    /**
     * 添加等待任务
     * @param waitingTaskElement   等待任务元素
     * @return  true：如果添加队列成功
     */
    public static synchronized boolean addWaitingTask(WaitingTaskElement waitingTaskElement){
        if(Objects.isNull(waitingTaskElement)){
            throw new IllegalArgumentException("WaitingTaskElement is null");
        }
        String taskId = waitingTaskElement.getTaskId();
        if(!StringUtils.hasText(taskId)){
            throw new IllegalArgumentException("WaitingTaskElement taskId is null");
        }
        String orgCode = waitingTaskElement.getOrgCode();
        if(!StringUtils.hasText(orgCode)){
            throw new IllegalArgumentException("WaitingTaskElement orgCode is null");
        }
        Queue<WaitingTaskElement> waitingTaskQueue = ORG_WAITING_TASK_QUEUE_MAP.computeIfAbsent(orgCode, key -> new LinkedBlockingQueue<>());
        boolean success = waitingTaskQueue.add(waitingTaskElement);
        if(success){
            if(log.isDebugEnabled()){
                log.debug("单位[{}]添加待运行AI识别任务[{}]，待运行识别任务队列数量：{}", orgCode, taskId, waitingTaskQueue.size());
            }
        }
        return success;
    }

    public static synchronized WaitingTaskElement pollWaitingTask(Predicate<String> filterRule){
        boolean debugEnabled = log.isDebugEnabled();
        for (Map.Entry<String, Queue<WaitingTaskElement>> entry : ORG_WAITING_TASK_QUEUE_MAP.entrySet()) {
            String orgCode = entry.getKey();
            // 单位下运行中队列已满
            if(!filterRule.test(orgCode)){
                continue;
            }
            // 单位下运行中队列未满
            Queue<WaitingTaskElement> waitingTaskQueue = entry.getValue();
            WaitingTaskElement waitingTaskElement = waitingTaskQueue.poll();
            if(debugEnabled){
                log.debug("获取到单位[{}]待运行队列任务 ==> {}", orgCode, waitingTaskElement);
            }
            if(Objects.nonNull(waitingTaskElement)){
                return waitingTaskElement;
            }
        }
        return null;
    }

    SystemAIAnalysisTask(IAITaskProcess aiTaskProcess, ApplicationEventPublisher publisher) {
        super(aiTaskProcess);
        if(!(aiTaskProcess instanceof ISystemAITaskProcess)){
            throw new IllegalArgumentException("SystemAIAnalysisTask accept only SystemAITaskProcess");
        }
        if(Objects.isNull(publisher)){
            throw new IllegalArgumentException("ApplicationEventPublisher is null");
        }
        this.orgCode = ((ISystemAITaskProcess) aiTaskProcess).getOrgCode();
        this.publisher = publisher;
    }

    public static int getSystemTaskCounts(String orgCode){
        if(StringUtils.hasText(orgCode)){
            return ORG_AI_ANALYSIS_TASKS.getOrDefault(orgCode, Collections.emptySet()).size();
        }
        return 0;
    }

    @Override
    protected void postInitialized() {
        String taskId = this.getTaskId();
        // 初始化账号-任务信息
        Set<String> accountTaskCounts = ORG_AI_ANALYSIS_TASKS.computeIfAbsent(orgCode, key -> new CopyOnWriteArraySet<>());
        accountTaskCounts.add(taskId);
        if(log.isDebugEnabled()){
            log.debug("系统AI识别任务[{}]初始化完成，单位[{}]运行中任务信息：{}", taskId, orgCode, ORG_AI_ANALYSIS_TASKS.getOrDefault(orgCode, null));
        }
    }

    @Override
    protected void postDestruction() {
        String taskId = this.getTaskId();
        // 清理账号运行中任务信息
        ORG_AI_ANALYSIS_TASKS.computeIfPresent(orgCode, (k, v) -> {
            v.remove(taskId);
            return v;
        });
        if(log.isDebugEnabled()){
            log.debug("系统AI识别任务[{}]清理完成，单位[{}]运行中任务信息：{}", taskId, orgCode, ORG_AI_ANALYSIS_TASKS.getOrDefault(orgCode, null));
        }
        // 发布AI识别任务完成事件
        publisher.publishEvent(new AIAnalysisTaskHandleCompletedEvent(this));
    }

    @Override
    public IAITaskProcess getTaskProcess() {
        return new SystemAITaskProcessSnapshot(this);
    }

    @Override
    public synchronized void paused(Consumer<IAITaskProcess> postOperation) {
        throw new BizException("系统AI识别分析任务不允许人工暂停");
    }

    @Override
    public void resume(Consumer<IAITaskProcess> postOperation) {
        throw new BizException("系统AI识别分析任务不允许人工继续");
    }

}
