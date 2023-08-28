package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

@Data
public class NhOrderPlanInDTO {
    /**
     * 工单ID
     */
    private String orderId;

    /**
     * 计划ID
     */
    private String planId;
}
