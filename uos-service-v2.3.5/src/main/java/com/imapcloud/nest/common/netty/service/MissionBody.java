package com.imapcloud.nest.common.netty.service;

import lombok.Data;

/**
 * Created by wmin on 2020/12/9 14:12
 *
 * @author wmin
 */
@Data
public class MissionBody {

    /**
     * 架次Id
     */
    private Integer missionId;

    /**
     * 架次名称
     */
    private String missionName;

    /**
     * 架次状态
     * -1 - 未执行
     * 0 - 执行中
     * 1 - 执行完毕
     * 2 - 执行异常
     * 3 - 暂停
     * 4 - 终止
     */
    private Integer missionState;


    private Double missionPercentage = 0.0;

    /**
     * 架次飞行时间
     */
    private Long flyTime = 0L;

    public enum MissionStateEnum {
        EXECUTED_UN(-1),
        EXECUTING(0),
        EXECUTION_COMPLETE(1),
        EXECUTION_ERROR(2),
        PAUSE(3),
        STOP(4),

        ;
        private final int value;

        MissionStateEnum(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}
