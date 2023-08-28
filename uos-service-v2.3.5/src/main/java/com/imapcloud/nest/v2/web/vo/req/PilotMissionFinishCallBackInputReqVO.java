package com.imapcloud.nest.v2.web.vo.req;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PilotMissionFinishCallBackInputReqVO {

    private String fileGroupId;

    private Integer fileCount;

    private Integer fileUploadedCount;



}
