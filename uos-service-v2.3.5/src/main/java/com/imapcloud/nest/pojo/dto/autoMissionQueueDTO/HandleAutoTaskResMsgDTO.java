package com.imapcloud.nest.pojo.dto.autoMissionQueueDTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HandleAutoTaskResMsgDTO {
    private String nestId;
    private Integer missionId;
    private String msgTitle;
    private Object msgContent;
    private Long userId;
}
