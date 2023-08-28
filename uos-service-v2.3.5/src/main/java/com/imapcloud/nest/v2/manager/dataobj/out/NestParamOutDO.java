package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

/**
 * nest_param
 *
 * @author boluo
 * @date 2023-03-28
 */
@Data
public class NestParamOutDO {

    /**
     * 基站ID
     */
    private String baseNestId;

    /**
     * 告警循环次数
     */
    private Integer alarmCircleNum;

    /**
     * 禁用循环次数
     */
    private Integer forbiddenCircleNum;
}
