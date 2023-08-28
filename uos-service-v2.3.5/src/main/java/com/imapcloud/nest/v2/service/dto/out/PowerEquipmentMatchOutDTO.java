package com.imapcloud.nest.v2.service.dto.out;

import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class PowerEquipmentMatchOutDTO {
    private Integer successCount;
    private Integer failureCount;
}
