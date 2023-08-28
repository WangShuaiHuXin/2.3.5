package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 架次记录，任务信息DTO类
 *
 * @author: zhengxd
 * @create: 2021/6/28
 **/
@Data
public class MissionRecordTaskInfoDTO {
    private Integer missionRecordId;
    private LocalDateTime missionRecordTime;
    private Integer taskId;
    private String taskName;

    private Integer flyIndex;

    private String missionName;
}
