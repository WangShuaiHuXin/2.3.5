package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

@Data
public class MissionMediaErrLogOutDTO {

    private Long id;

    /**
     * 任务ID
     */
    private Integer missionId;
    /**
     * 架次ID
     */
    private Integer missionRecordId;
    /**
     * 基站UUID
     */
    private String nestUuid;

    /**
     * 参考 MissionMediaErrStatus
     */
    private String errorStep;
    private String errorCode;
    private String errorFile;
    private String errorInfo;
    private String errorSolution;
}
