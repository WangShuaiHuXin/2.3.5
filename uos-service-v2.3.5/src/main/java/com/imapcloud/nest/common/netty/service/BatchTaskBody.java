package com.imapcloud.nest.common.netty.service;

import lombok.Data;

import java.util.List;

/**
 * Created by wmin on 2020/12/9 11:50
 *
 * @author wmin
 */
@Data
public class BatchTaskBody {
    /**
     * 批量任务属于哪个机巢
     */
    private String nestUuid;
    /**
     * 停止或者开始
     * 1 -> 开始
     * 0 -> 停止
     * 2 -> 执行完成
     */
    private Integer startOrStop;
    /**
     * 暂停或继续
     * 1 -> 继续
     * 0 -> 暂停
     */
    private Integer pauseOrContinue;
    /**
     * 当前执行到第几个架次
     */
    private Integer taskCurrentIndex;

    /**
     * 总任务架次
     */
    private Integer totalTaskCount;

    /**
     * 获取图片的方式
     */
    private Integer gainDataMode;

    /**
     * 获取视频的方式
     */
    private Integer gainVideo;

    /**
     * 无人机飞行策略
     */
    private Integer flightStrategy;

    /**
     * 批量任务列表
     */
    private List<TaskBody> taskBodyList;

    /**
     * 批量架次飞行总时长
     */
    private Long flyTotalTime;

    public enum RunStateEnum {
        STOP(0),
        START(1),
        COMPLETE(2),
        ;
        private final int value;

        RunStateEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum MiddleStateEnum {
        PAUSE(0),
        CONTINUE(1)
        ;
        private final int value;

        MiddleStateEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
