package com.imapcloud.nest.v2.service.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import scala.Int;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PowerEquipmentListOutDTO {

    private List<PowerEquipmentObj> powerEquipmentLists;

    private Long total;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PowerEquipmentObj {
        private String equipmentId;
        private LocalDateTime operationTime;
        private String lastOperator;
        private String spacingUnit;
        private String stationName;
        private String voltageLevel;
        private String equipmentType;
        private String equipmentPmsId;
        private String equipmentName;
        private LocalDateTime createdTime;
    }
}

