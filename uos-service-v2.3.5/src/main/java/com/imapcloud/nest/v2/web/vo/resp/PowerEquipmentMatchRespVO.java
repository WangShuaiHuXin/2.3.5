package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;

import java.io.Serializable;

@Data
public class PowerEquipmentMatchRespVO implements Serializable {
    private String successCount;
    private String failureCount;
}
