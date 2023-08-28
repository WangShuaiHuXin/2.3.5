package com.imapcloud.nest.v2.dao.po.in;

import lombok.*;

import java.util.List;

@ToString
@Builder
@Data
@AllArgsConstructor
public class PowerInspcetionReportInfoPO {
    private PowerInspcetionReportInfoPO() {
    }
    private String InspcetionReportId;
    private String equipmentId;
    private String orgCode;
    private String beginTime;
    private String endTime;
    private String inspcetionType;
    private String spacingUnitName;
    private String equipmentType;
    private String inspectionConclusion;
    private String equipmentName;
    private String componentName;
    private String voltageName;
    private Integer pageNo;
    private Integer pageSize;
    private List<String> ids;
    private List<String> equipmentIds;
}
