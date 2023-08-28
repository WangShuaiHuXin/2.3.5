package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

@Data
public class PowerHomeEquipmentTypeInspcetionOutDTO {

    private String equipmentType;

    private Long totalInspcetion;

    private Long errInspection;
}
