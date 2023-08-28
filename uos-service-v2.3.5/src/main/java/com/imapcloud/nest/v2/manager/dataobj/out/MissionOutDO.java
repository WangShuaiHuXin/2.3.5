package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

/**
 * 架次
 *
 * @author boluo
 * @date 2022-11-01
 */
@Data
public class MissionOutDO {

    private Long missionId;

    /**
     * 名称
     */
    private String name;

    /**
     * mission的uuid
     */
    private String uuid;

    /**
     * 顺序号，任务的第几个架次
     */
    private Integer seqId;

    /**
     * 航线ID
     */
    private Integer airLineId;

    /**
     * 任务ID
     */
    private Integer taskId;

    @Data
    public static class TaskMissionOutDO {

        private Integer missionId;

        private String orgCode;

        private String nestId;
    }
}
