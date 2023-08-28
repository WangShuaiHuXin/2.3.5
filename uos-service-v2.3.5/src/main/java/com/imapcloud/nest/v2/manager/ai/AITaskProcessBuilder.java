package com.imapcloud.nest.v2.manager.ai;

import lombok.Getter;

/**
 * 人工AI识别任务进度默认实现
 * @author Vastfy
 * @date 2022/11/30 18:33
 * @since 2.1.5
 */
@Getter
public final class AITaskProcessBuilder {

    private String taskId;
    private String taskName;
    private Integer taskType;
    private Integer taskState;
    private int totalPic;
    private int successPic;
    private int failedPic;
    private long executionTime;

    private String dataId;
    private String flightTaskId;
    private String flightTaskTag;

    private String accountId;

    private String orgCode;

    private boolean auto;

    private AITaskProcessBuilder(){
    }

    public AITaskProcessBuilder taskId(String taskId){
        this.taskId = taskId;
        return this;
    }
    public AITaskProcessBuilder taskName(String taskName){
        this.taskName = taskName;
        return this;
    }
    public AITaskProcessBuilder taskType(Integer taskType){
        this.taskType = taskType;
        return this;
    }
    public AITaskProcessBuilder taskState(Integer taskState){
        this.taskState = taskState;
        return this;
    }
    public AITaskProcessBuilder totalPic(int totalPic){
        this.totalPic = totalPic;
        return this;
    }
    public AITaskProcessBuilder failedPic(int failedPic){
        this.failedPic = failedPic;
        return this;
    }
    public AITaskProcessBuilder successPic(int successPic){
        this.successPic = successPic;
        return this;
    }
    public AITaskProcessBuilder executionTime(long executionTime){
        this.executionTime = executionTime;
        return this;
    }
    public AITaskProcessBuilder dataId(String dataId){
        this.dataId = dataId;
        return this;
    }
    public AITaskProcessBuilder flightTaskId(String flightTaskId){
        this.flightTaskId = flightTaskId;
        return this;
    }
    public AITaskProcessBuilder flightTaskTag(String flightTaskTag){
        this.flightTaskTag = flightTaskTag;
        return this;
    }
    public AITaskProcessBuilder auto(boolean auto){
        this.auto = auto;
        return this;
    }
    public AITaskProcessBuilder orgCode(String orgCode){
        this.orgCode = orgCode;
        return this;
    }
    public AITaskProcessBuilder accountId(String accountId){
        this.accountId = accountId;
        return this;
    }

    public static AITaskProcessBuilder builder(){
        return new AITaskProcessBuilder();
    }

    public IAITaskProcess build(){
        if(this.auto){
            return new SystemAITaskProcessSnapshot(this);
        }
        return new ManualAITaskProcessSnapshot(this);
    }
    
}
