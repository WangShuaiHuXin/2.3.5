package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.util.List;

@Data
public class PowerEquipmentTreeOutDTO {

    private Long total;

    private List<PowerEquipmentInfo> infoList;

    @Data
    public static class PowerEquipmentInfo {
        private String equipmentId;
        private String lastOperator;
        private String spacingUnit;
        private String stationName;
        private String voltageLevel;
        private String equipmentType;
        private String equipmentName;
        private boolean voltageFlag;
        private boolean spacingFlag;
        private boolean typeFlag;
    }
}
