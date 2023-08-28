package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PowerEquipmentJsonLeafInDTO {
    private String name;
    private String number;
    private int style;
    private int heightSpace;
    private String voltageLevel;
    private double safeDistance;
    private Map<String,String> photolist;
    private List<PowerEquipmentJsonLeafInDTO> List;
}
