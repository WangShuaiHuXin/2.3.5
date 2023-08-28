package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;

import java.io.Serializable;

@Data
public class PowerInspectionEquipmentListRespVO implements Serializable {
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

    /**
     * 报告类型
     */
    private String inspectionType;
}
