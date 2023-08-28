package com.imapcloud.nest.pojo.dto.app;

import lombok.Data;

/**
 * @author wmin
 */
@Data
public class AppMissionFinishDTO {
    /**
     * 架次记录ID
     */
    private Integer missionRecordId;
    /**
     * 任务执行状态：0-未执行完成，1-执行中，2-执行完成，3-执行异常
     */
    private Integer status;

    /**
     * 飞行时间,单位秒
     */
    private Long flyTime;

    /**
     * 飞行距离
     */
    private Double flyDistance;

    /**
     * 飞机飞行到哪个点
     */
    private Integer reachIndex;
}
