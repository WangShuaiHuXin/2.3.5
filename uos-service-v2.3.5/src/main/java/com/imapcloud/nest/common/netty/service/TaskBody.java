package com.imapcloud.nest.common.netty.service;

import lombok.Data;

import java.util.List;

/**
 * Created by wmin on 2020/12/9 11:59
 *
 * @author wmin
 */
@Data
public class TaskBody {
    /**
     * 任务id
     */
    private Integer taskId;
    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务状态
     * 0 -> 未执行
     * 1 -> 执行中
     * 2 -> 执行完成
     * 3 -> 取消执行
     * 4 -> 执行异常
     */
    private Integer taskState;

    /**
     * 当前运行到第几个架次
     */
    private Integer missionCurrentIndex;

    /**
     * 架次列表
     */
    private List<MissionBody> missionBodyList;

    /**
     * 飞行时间
     */
    private Long flyTime;

    /**
     * 任务类型
     */
    private Integer taskType;

    public enum TaskStateEnum {
        EXECUTED_UN(0),
        EXECUTING(1),
        EXECUTION_COMPLETE(2),
        EXECUTION_CANCEL(3),
        EXECUTION_ERROR(4),
        ;
        private final int value;

        TaskStateEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
}
