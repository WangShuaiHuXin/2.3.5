package com.imapcloud.nest.v2.service.dto.in;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class PowerEquipmentInDTO {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PowerEquipmentSaveOrUpdateDTO {
        private String equipmentId;

        private String equipmentName;

        private String stationName;

        private String spacingUnit;

        private String equipmentType;

        private String voltageLevel;

        private String equipmentPmsId;

        private String orgCode;
    }
}
