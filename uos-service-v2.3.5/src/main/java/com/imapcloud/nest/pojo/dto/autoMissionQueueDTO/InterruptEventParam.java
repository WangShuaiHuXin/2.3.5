package com.imapcloud.nest.pojo.dto.autoMissionQueueDTO;

import lombok.Data;

@Data
public class InterruptEventParam {
    private String nestUuid;
    /**
     * 0 - 1
     */
    private InterruptTypeEnum type;

    private String errorMessage;

    private Integer which;

    public enum InterruptTypeEnum {
        ERROR_ENCOUNTERED,
        ABORTED;
    }
}
