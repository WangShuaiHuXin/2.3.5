package com.imapcloud.nest.pojo.dto.autoMissionQueueDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class StartEventParam {
    private String nestUuid;
    private Integer which;

}
