package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务列表接口传输类
 *
 * @author wmin
 */
@Data
public class TaskListDto {
    private Integer id;
    private String name;
    private String remarks;
    private Integer type;
    private List<Vehicles> vehicles;
    private LocalDateTime modifyTime;
    private Integer subType;

}
