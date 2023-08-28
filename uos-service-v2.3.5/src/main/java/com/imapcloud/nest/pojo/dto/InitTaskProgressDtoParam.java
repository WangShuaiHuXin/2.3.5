package com.imapcloud.nest.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class InitTaskProgressDtoParam {
    private Integer missionId;
    private Boolean multiTask;
    private Integer gainDataMode;
    private Integer gainVideo;
    private LocalDateTime startTime;
    private Integer flyIndex;
    private Boolean takeOffRecord;
    private Boolean switchZoomCamera;
    private Integer uavWhich;
}
