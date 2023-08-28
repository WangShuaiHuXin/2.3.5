package com.imapcloud.nest.pojo.dto;

import lombok.Data;

@Data
public class CommonButtonShowDTO {

    /**
     * 执行任务按钮
     */
    private Integer executeMissionBtn;

    /**
     * 删除任务按钮
     */
    private Integer deleteMissionBtn;

    /**
     * 终止任务启动流程按钮
     */
    private Integer stopMissionStartBtn;

    /**
     * 终止任务结束流程按钮
     */
    private Integer stopMissionEndBtn;

    /**
     * 重发任务按钮
     */
    private Integer reExecuteMissionBtn;

    /**
     * 云台控制按钮
     */
    private Integer gimbalControlBtn;

    /**
     * 终止飞行按钮
     */
    private Integer stopFlyingBtn;

    /**
     * 一键返航按钮
     */
    private Integer startReturnToHomeBtn;

    /**
     * 暂停任务按钮
     */
    private Integer pauseMissionBtn;

    /**
     * 继续任务按钮
     */
    private Integer continueMissionBtn;

    /**
     * 飞行作业按钮
     */
    private Integer flightOperationBtn;

    public CommonButtonShowDTO() {
        this.executeMissionBtn = 0;
        this.deleteMissionBtn = 0;
        this.stopMissionStartBtn = 0;
        this.stopMissionEndBtn = 0;
        this.reExecuteMissionBtn = 0;
        this.gimbalControlBtn = 0;
        this.stopFlyingBtn = 0;
        this.startReturnToHomeBtn = 0;
        this.pauseMissionBtn = 0;
        this.continueMissionBtn = 0;
        this.flightOperationBtn = 0;
    }
}
