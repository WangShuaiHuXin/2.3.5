package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;
import lombok.ToString;

/**
 * 任务
 *
 * @author boluo
 * @date 2022-09-29
 */
@ToString
public class TaskRespVO {

    private TaskRespVO() {}

    @Data
    public static class ConditionOrgInfoRespVO {

        private String orgCode;

        private String orgName;
    }
}
