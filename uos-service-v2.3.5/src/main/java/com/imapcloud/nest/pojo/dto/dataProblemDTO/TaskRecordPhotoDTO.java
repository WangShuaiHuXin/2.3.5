package com.imapcloud.nest.pojo.dto.dataProblemDTO;

import lombok.Data;

import java.time.LocalDateTime;

/**
 *
 * @author: zhengxd
 * @create: 2021/6/17
 **/
@Data
public class TaskRecordPhotoDTO {
    /**
     * 任务id
     */
    private Integer taskId;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 任务时间
     */
    private LocalDateTime taskTime;

    /**
     * 架次记录id
     */
    private Integer missionRecordId;
    /**
     * 架次记录时间
     */
    private LocalDateTime missionRecordTime;
    /**
     * 整个架次下，是否有问题
     */
    private Boolean hasProblem;
}
