package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 表计数据信息
 * @author Vastfy
 * @date 2022/12/04 15:59
 * @since 2.1.5
 */
@Data
public class MeterDataInfoOutDTO implements Serializable {

    private String dataId;

    private String taskId;

    private String taskName;

    private String missionId;

    private String missionSeqId;

    private String missionRecordId;

    private String flyIndex;

    private LocalDateTime flightTime;

    private String orgCode;

}
