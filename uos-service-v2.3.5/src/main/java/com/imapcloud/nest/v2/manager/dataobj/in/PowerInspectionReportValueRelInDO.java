package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PowerInspectionReportValueRelInDO {
    private String inspectionReportId;

    private String valueId;

    private String creatorId;
    private String modifierId;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private Boolean deleted;
}
