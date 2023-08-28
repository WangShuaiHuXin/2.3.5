package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;

import java.io.Serializable;

@Data
public class PowerHomeInspectionAlarmEventsRespVO implements Serializable {
    /**
     * 设备状态-缺陷状态
     */
    private String defectStatus;
    /**
     * 设备名称
     */
    private String equipmentName;
    /**
     * 分析结果
     */
    private String analysisResult;
    /**
     * 告警原因
     */
    private String alarmReson;
    /**
     * 拍摄时间
     */
    private String photographyTime;
    /**
     * 告警主键id  ==detailId
     */
    private String alarmId;
    /**
     * 分析截图
     */
    private String url;
    /**
     * 告警类型
     * BIAOJI("101", "表计识别"),
     * QUEXIAN("102", "缺陷识别"),
     * HONGWAI("103", "热红测温");
     */
    private String analysisType;

    /**
     * 单位编号
     */
    private String orgCode;

    /**
     * 架次ID
     */
    private String dataId;
}
