package com.imapcloud.nest.v2.manager.ai;

import com.geoai.common.core.exception.BizException;
import com.geoai.common.web.util.MessageUtils;
import com.imapcloud.nest.v2.common.enums.AIAnalysisTaskStateEnum;
import com.imapcloud.nest.v2.common.enums.MessageEnum;
import com.imapcloud.nest.v2.service.dto.out.AIAnalysisPicResultDataOutDTO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 抽象AI分析任务
 * @author Vastfy
 * @date 2022/11/30 16:28
 * @since 2.1.5
 */
@Slf4j
public abstract class AbstractAIAnalysisTask implements IAIAnalysisTask, IAITaskProcess {

    /**
     * 全量AI分析任务
     * key: 分析任务ID
     * value: 分析任务ID详情
     */
    private static final Map<String, IAIAnalysisTask> ALL_AI_ANALYSIS_TASKS = new ConcurrentHashMap<>();

    /**
     * 任务ID
     */
    @Getter
    private final String taskId;

    /**
     * 任务名称
     */
    @Getter
    private final String taskName;

    /**
     * 飞行成功基础数据ID（冗余）
     */
    @Getter
    private final String dataId;

    /**
     * 航线任务ID（冗余）
     */
    @Getter
    private final String flightTaskId;

    /**
     * 航线标签名称（冗余）
     */
    @Getter
    private final String flightTaskTag;

    /**
     * 任务进度通知人列表
     */
    @Getter
    private final List<String> notifierIds = new ArrayList<>();

    /**
     * 任务图片总数
     */
    @Getter
    private final int totalPic;

    /**
     * 任务图片识别成功数
     */
    @Getter
    private int successPic;

    /**
     * 任务图片识别失败数
     */
    @Getter
    private int failedPic;

    /**
     * 任务状态
     */
    @Getter
    private Integer taskState;

    /**
     * 累计执行时间（单位：毫秒）
     */
    @Getter
    private long executionTime;

    /**
     * 任务类型：[0：人工创建；0：自动创建]
     */
    @Getter
    private final Integer taskType;

    /**
     * 启动时间戳（每次任务启动时会重置为最新时间戳）
     */
    private long startTimestamp;

    /**
     * 最后一次更新时间戳
     */
    private long lastModified;

    /**
     * 结束时间戳
     * 1. 每次任务启动时会重置为-1，标识结束时间未知
     * 2. 每次暂停、完成、终止任务时会重置为当前时间戳，标识当前任务已结束
     */
    private long endTimestamp;

    AbstractAIAnalysisTask(IAITaskProcess aiTaskProcess) {
        this.taskId = aiTaskProcess.getTaskId();
        this.taskName = aiTaskProcess.getTaskName();
        this.taskType = aiTaskProcess.getTaskType();
        this.dataId = aiTaskProcess.getDataId();
        this.flightTaskId = aiTaskProcess.getFlightTaskId();
        this.flightTaskTag = aiTaskProcess.getFlightTaskTag();
        this.taskState = aiTaskProcess.getTaskState();
        this.executionTime = aiTaskProcess.getExecutionTime();
        this.totalPic = aiTaskProcess.getTotalPic();
        this.successPic = aiTaskProcess.getSuccessPic();
        this.failedPic = aiTaskProcess.getFailedPic();
        this.lastModified = System.currentTimeMillis();
        this.startTimestamp = this.lastModified;
        this.endTimestamp = -1L;
    }

    protected abstract void postInitialized();

    protected abstract void postDestruction();

    @Override
    public void initialize(){
        if(log.isDebugEnabled()){
            log.debug("AI识别任务初始化，【运行中队列】信息 ==> taskCount={}", getAllTaskCounts());
        }
        // 初始化任务队列
        ALL_AI_ANALYSIS_TASKS.put(taskId, this);
        postInitialized();
    }

    @Override
    public void destroy() {
        if(log.isDebugEnabled()){
            log.debug("AI识别任务初始化，【运行中队列】信息 ==> taskCount={}", getAllTaskCounts());
        }
        // 清理全部任务队列
        ALL_AI_ANALYSIS_TASKS.remove(taskId);
        postDestruction();
    }

    @Override
    public synchronized void paused(Consumer<IAITaskProcess> postOperation) {
        // 刷新耗时
        refreshCostTimes();
        // 排队中/执行中 ==> 已暂停
        if(!AIAnalysisTaskStateEnum.QUEUING.matchEquals(this.taskState)
                && !AIAnalysisTaskStateEnum.EXECUTING.matchEquals(this.taskState)){
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_SUSPEND_TASK_OPERATION_IS_NOT_ALLOWED.getContent()));
//            throw new BizException("当前状态不允许暂停AI分析任务");
        }
        this.taskState = AIAnalysisTaskStateEnum.PAUSED.getType();
        // 终止计时器
        this.endTimestamp = lastModified;
        if(log.isDebugEnabled()){
            log.debug("AI识别任务人工暂停 ==> endTimestamp:{}, startTimestamp:{}, costTimes:{}s.", endTimestamp, startTimestamp, executionTime/1000);
        }
        doPostOperation(postOperation);
    }

    @Override
    public synchronized void resume(Consumer<IAITaskProcess> postOperation) {
        // 已暂停 ==> 排队中
        if(!AIAnalysisTaskStateEnum.PAUSED.matchEquals(this.taskState)){
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_RESUME_TASK_OPERATION_IS_NOT_ALLOWED.getContent()));
//            throw new BizException("当前状态不允许继续AI分析任务");
        }
        this.taskState = AIAnalysisTaskStateEnum.QUEUING.getType();
        this.lastModified = System.currentTimeMillis();
        // 重启计时器
        this.startTimestamp = lastModified;
        this.endTimestamp = -1L;
        if(log.isDebugEnabled()){
            log.debug("AI识别任务人工重启 ==> endTimestamp:{}, startTimestamp:{}, costTimes:{}s.", endTimestamp, startTimestamp, executionTime/1000);
        }
        doPostOperation(postOperation);
    }

    @Override
    public synchronized void terminated(Consumer<IAITaskProcess> postOperation) {
        // 刷新耗时
        refreshCostTimes();
        // 排队中、识别中、已暂停 ==> 已终止
        if(!AIAnalysisTaskStateEnum.QUEUING.matchEquals(this.taskState)
                && !AIAnalysisTaskStateEnum.PAUSED.matchEquals(this.taskState)
                && !AIAnalysisTaskStateEnum.EXECUTING.matchEquals(this.taskState)){
            throw new BizException(MessageUtils.getMessage(MessageEnum.GEOAI_UOS_TERMINATE_TASK_OPERATION_IS_NOT_ALLOWED.getContent()));
//            throw new BizException("当前状态不允许终止AI分析任务");
        }
        this.taskState = AIAnalysisTaskStateEnum.TERMINATED.getType();
        this.endTimestamp = lastModified;
        if(log.isDebugEnabled()){
            log.debug("AI识别任务人工终止 ==> endTimestamp:{}, startTimestamp:{}, costTimes:{}s.", endTimestamp, startTimestamp, executionTime/1000);
        }
        // 后续图片全部失败
        this.failedPic = this.totalPic - this.successPic;
        doPostOperation(postOperation);
        // 人工终止任务，清理任务队列
        destroy();
    }

    @Override
    public synchronized void refreshing(AIAnalysisPicResultDataOutDTO aiData, Consumer<IAITaskProcess> postOperation) {
        // 刷新耗时
        refreshCostTimes();
        int successPic = this.successPic;
        int failedPic = this.failedPic;
        int taskState = this.taskState;
        if(aiData.isResult()){
            this.successPic ++;
        }else{
            this.failedPic ++;
        }
        // 初次执行时：更新状态为执行中
        if(AIAnalysisTaskStateEnum.QUEUING.matchEquals(taskState)){
            this.taskState = AIAnalysisTaskStateEnum.EXECUTING.getType();
        }
        // 更新缓存任务状态：已完成（总数=成功+失败）
        boolean isCompleted = this.totalPic - this.successPic - this.failedPic <= 0;
        if(isCompleted){
            this.taskState = AIAnalysisTaskStateEnum.COMPLETED.getType();
            this.endTimestamp = lastModified;
        }
        if(log.isDebugEnabled()){
            log.debug("AI识别任务刷新进度 ==> endTimestamp:{}, startTimestamp:{}, costTimes:{}s.", endTimestamp, startTimestamp, executionTime/1000);
        }
        try {
            doPostOperation(postOperation);
            if(isCompleted){
                destroy();
            }
        }catch (Exception e){
            log.error("业务消息处理异常", e);
            // 异常回滚
            this.successPic = successPic;
            this.failedPic = failedPic;
            this.taskState = taskState;
            throw e;
        }
    }

    @Override
    public synchronized void completed(Consumer<IAITaskProcess> postOperation) {
        this.taskState = AIAnalysisTaskStateEnum.COMPLETED.getType();
        // 刷新耗时
        refreshCostTimes();
        this.endTimestamp = lastModified;
        if(log.isDebugEnabled()){
            log.debug("任务完成 ==> endTimestamp:{}, startTimestamp:{}, costTimes:{}s.", endTimestamp, startTimestamp, executionTime/1000);
        }
        doPostOperation(postOperation);
        destroy();
    }

    public synchronized void refreshCostTimes(){
        long currentTimeMillis = System.currentTimeMillis();
        this.executionTime = currentTimeMillis - lastModified + executionTime;
        this.lastModified = System.currentTimeMillis();
    }

    private void doPostOperation(Consumer<IAITaskProcess> postOperation) {
        if(Objects.nonNull(postOperation)){
            postOperation.accept(this.getTaskProcess());
        }
    }

    /**
     * 获取AI分析任务实例
     * @param taskId    任务ID
     * @return  AI分析任务实例
     */
    static IAIAnalysisTask getInstance(String taskId){
        if(StringUtils.hasText(taskId)){
            return ALL_AI_ANALYSIS_TASKS.get(taskId);
        }
        return null;
    }

    /**
     * 获取AI分析任务总数量
     * @return  AI分析任务总数量
     */
    static int getAllTaskCounts(){
        return ALL_AI_ANALYSIS_TASKS.size();
    }

}
