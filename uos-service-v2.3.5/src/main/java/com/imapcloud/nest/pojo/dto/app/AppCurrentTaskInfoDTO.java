package com.imapcloud.nest.pojo.dto.app;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AppCurrentTaskInfoDTO {
    private String deviceId;
    private Integer missionId;
    private Integer missionRecordId;
    private Integer taskId;
    private Integer taskType;
    private String taskName;
    private String tagName;
    private Integer appLocalRoute;
    private String airCode;

}
