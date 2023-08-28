package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

@Data
public class PowerInspectionEquipmentListOutDTO {
    /**
     * 分析结论
     */
    private String inspectionConclusion;
    /**
     * 巡检报告主键ID
     */
    private String inspectionReportId;
    /**
     * 设备名称
     */
    private String equipmentName;
    /**
     * 拍摄时间
     */
    private String photographyTime;
    /**
     * 分析结果
     */
    private String inspectionResults;

    private String inspectionType;
}
