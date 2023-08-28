package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;

import java.io.Serializable;

@Data
public class EquipmentOptionsListRespVO implements Serializable {
    private String equipmentId;
    private String equipmentName;
}
