package com.imapcloud.nest.pojo.dto;

import java.time.LocalDate;

/**
 * @author wmin
 */
public class TaskEntityPartDto {

    /**
     * 任务id
     */
    private Integer id;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 任务类型Id
     */
    private Integer taskTypeId;


    /**
     * 最近修改时间
     */
    private LocalDate lastModifyTime;
}
