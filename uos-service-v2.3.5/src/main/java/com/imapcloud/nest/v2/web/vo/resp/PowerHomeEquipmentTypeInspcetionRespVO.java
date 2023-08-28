package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;

import java.io.Serializable;

@Data
public class PowerHomeEquipmentTypeInspcetionRespVO implements Serializable {

    private String equipmentType;

    private Long totalInspcetion;

    private Long errInspection;
}
