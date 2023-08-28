package com.imapcloud.nest.enums;

import lombok.Getter;

/**
 * 计划任务执行状态定义
 * @author Vastfy
 * @date 2022/4/21 14:23
 * @since 1.8.9
 */
@Getter
public enum InspectionPlanMissionExecStateEnum {

    /**
     * -1：执行中
     */
    IN_EXECUTION("执行中"),
    /**
     * 0：未执行
     */
    TO_BE_EXECUTED("未执行"),
    /**
     * 1：已执行
     */
    EXECUTED("已执行"),
    /**
     * 2：执行失败
     */
    EXECUTION_FAILED("执行失败"),
    /**
     * 3：已取消
     */
    CANCELLED("已取消"),
    ;

    /**
     * 状态中文描述
     */
    private final String cnName;

    InspectionPlanMissionExecStateEnum(String cnName) {
        this.cnName = cnName;
    }

    public int getState(){
        return ordinal() - 1;
    }
}
