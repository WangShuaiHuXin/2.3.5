package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

/**
 * 航线任务
 *
 * @author boluo
 * @date 2022-11-30
 */
@Data
public class TaskOutDO {

    private Long taskId;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 描述，巡检作业周期
     */
    private String description;

    /**
     * 任务类型Id
     */
    private Integer type;

    private String baseNestId;

    /**
     * 单位编码
     */
    private String orgCode;

    private Long tagId;

    private String tagName;
}
