package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;

import java.io.Serializable;

@Data
public class PowerEquipmentListRespVO implements Serializable {

    private String equipmentId;
    private String operationTime;
    private String lastOperator;
    private String spacingUnit;
    private String stationName;
    private String voltageLevel;
    private String equipmentType;
    private String equipmentPmsId;
    private String equipmentName;
    private String createdTime;
}
