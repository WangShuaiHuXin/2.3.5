package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InspectionEquipmentInDTO {
    private Integer pageNo;
    private Integer pageSize;
    private String equipmentType;
    private String beginTime;
    private String endTime;
    private String orgCode;
}
