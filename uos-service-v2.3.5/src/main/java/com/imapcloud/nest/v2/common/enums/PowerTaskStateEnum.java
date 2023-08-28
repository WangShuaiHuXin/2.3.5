package com.imapcloud.nest.v2.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务状态
 *
 * @author boluo
 * @date 2023-02-28
 */
@Getter
@AllArgsConstructor
public enum PowerTaskStateEnum {

    /**
     * 识别状态 【-1：已暂停；0：识别中；1：已完成；2：已终止】
     */
    TASK_INIT(3, "待识别 默认值"),
    TASK_PRE(4, "排队中"),
    TASK_ING(0, "识别中"),
    TASK_END(1, "识别完成"),
    TASK_PAUSE(-1, "暂停识别"),
    TASK_STOP(2, "终止识别"),
    TASK_TIMEOUT(5, "识别超时"),

    TASK_NO_AUTH(6, "算法未授权"),
    ;
    private final int code;

    private final String msg;
}
