package com.imapcloud.nest.v2.manager.ai;

/**
 * 人工AI识别任务进度默认实现
 * @author Vastfy
 * @date 2022/11/30 18:33
 * @since 2.1.5
 */
public class ManualAITaskProcessSnapshot implements IManualAITaskProcess {

    private final String taskId;
    private final String taskName;
    private final Integer taskState;
    private final Integer taskType;
    private final int totalPic;
    private final int successPic;
    private final int failedPic;
    private final long executionTime;

    private final String dataId;
    private final String flightTaskId;
    private final String flightTaskTag;

    private final String accountId;

    public ManualAITaskProcessSnapshot(ManualAIAnalysisTask task){
        this.taskId = task.getTaskId();
        this.taskName = task.getTaskName();
        this.taskState = task.getTaskState();
        this.taskType = task.getTaskType();
        this.totalPic = task.getTotalPic();
        this.successPic = task.getSuccessPic();
        this.failedPic = task.getFailedPic();
        this.executionTime = task.getExecutionTime();
        this.dataId = task.getDataId();
        this.flightTaskId = task.getFlightTaskId();
        this.flightTaskTag = task.getFlightTaskTag();
        this.accountId = task.getAccountId();
    }

    public ManualAITaskProcessSnapshot(AITaskProcessBuilder builder){
        this.taskId = builder.getTaskId();
        this.taskName = builder.getTaskName();
        this.taskState = builder.getTaskState();
        this.taskType = builder.getTaskType();
        this.totalPic = builder.getTotalPic();
        this.successPic = builder.getSuccessPic();
        this.failedPic = builder.getFailedPic();
        this.executionTime = builder.getExecutionTime();
        this.dataId = builder.getDataId();
        this.flightTaskId = builder.getFlightTaskId();
        this.flightTaskTag = builder.getFlightTaskTag();
        this.accountId = builder.getAccountId();
    }

    @Override
    public String getTaskId() {
        return this.taskId;
    }

    @Override
    public String getTaskName() {
        return this.taskName;
    }

    @Override
    public Integer getTaskState() {
        return this.taskState;
    }

    @Override
    public Integer getTaskType() {
        return this.taskType;
    }

    @Override
    public int getTotalPic() {
        return this.totalPic;
    }

    @Override
    public int getSuccessPic() {
        return this.successPic;
    }

    @Override
    public int getFailedPic() {
        return this.failedPic;
    }

    @Override
    public long getExecutionTime() {
        return this.executionTime;
    }

    @Override
    public String getDataId() {
        return this.dataId;
    }

    @Override
    public String getFlightTaskId() {
        return this.flightTaskId;
    }

    @Override
    public String getFlightTaskTag() {
        return this.flightTaskTag;
    }

    @Override
    public String getAccountId() {
        return this.accountId;
    }

}
