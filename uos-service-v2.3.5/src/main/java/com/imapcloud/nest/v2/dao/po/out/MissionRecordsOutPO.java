package com.imapcloud.nest.v2.dao.po.out;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaRecordsCriteriaPO.java
 * @Description DataPanoramaRecordsCriteriaPO
 * @createTime 2022年09月22日 15:04:00
 */
@Data
public class MissionRecordsOutPO {

    private String tagId;

    private String orgCode;

    private String missionName;

    private String missionRecordsId;

    private String baseNestId;

    private String flyIndex;

    private LocalDateTime startTime;

}
