package com.imapcloud.nest.pojo.dto.autoMissionQueueDTO;

import com.imapcloud.nest.enums.WebSocketTopicEnum;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Builder
public class AutoTaskResDTO {
    private WebSocketTopicEnum topic;
    private String title;
    private String missionName;
    private String nestName;
    private List<String> problems;
}
