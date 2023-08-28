package com.imapcloud.nest.enums;

import lombok.Getter;

/**
 * 计划执行状态定义
 * @author Vastfy
 * @date 2022/4/21 14:23
 * @since 1.8.9
 */
@Getter
public enum InspectionPlanExecStateEnum {

    /**
     * -1：执行中
     */
    IN_EXECUTION("执行中"),
    /**
     * 0：待执行
     */
    TO_BE_EXECUTED("待执行"),
    /**
     * 1：已取消
     */
    CANCELLED("已取消"),
    /**
     * 2：执行失败
     */
    EXECUTION_FAILED("执行失败"),
    /**
     * 3：未全部完成
     */
    NOT_ALL_COMPLETED("未全部成功"),
    /**
     * 4：已执行
     */
    EXECUTED("已执行"),
    ;

    /**
     * 状态中文描述
     */
    private final String cnName;

    InspectionPlanExecStateEnum(String cnName) {
        this.cnName = cnName;
    }

    public int getState(){
        return ordinal() - 1;
    }

}
