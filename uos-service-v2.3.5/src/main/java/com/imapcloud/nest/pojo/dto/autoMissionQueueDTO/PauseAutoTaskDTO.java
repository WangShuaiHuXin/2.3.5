package com.imapcloud.nest.pojo.dto.autoMissionQueueDTO;

import com.imapcloud.nest.common.netty.service.BeforeStartCheckService;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PauseAutoTaskDTO {
    private String nestUuid;
    private String account;
    private String nestId;
    private String nestName;
    private Integer missionId;
    private String missionName;
    private String planName;
    private BeforeStartCheckService.CheckRes checkRes;
}
