package com.imapcloud.nest.v2.service.dto.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class DataEquipmentPointListOutDTO {
    private String equipmentId;
    private String equipmentName;
    private boolean affiliateStatus;
    private Double lng;
    private Double lat;
    private Double height;
}
