package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 电力分析
 *
 * @author boluo
 * @date 2023-03-09
 */
@Data
public class PowerMeterFlightDataOutDO {

    /**
     * 数据ID（业务主键）
     */
    private String dataId;

    /**
     * 飞行任务ID
     */
    private Long taskId;

    /**
     * 飞行任务名称（冗余）
     */
    private String taskName;

    /**
     * 架次ID
     */
    private Long missionId;

    /**
     * 架次顺序号
     */
    private Long missionSeqId;

    /**
     * 任务架次记录id
     */
    private Long missionRecordId;

    /**
     * 架次飞行次数
     */
    private Integer flyIndex;

    /**
     * 架次飞行时间
     */
    private LocalDateTime flightTime;

    /**
     * 标签ID（冗余）
     */
    private Long tagId;

    /**
     * 标签名称（冗余）
     */
    private String tagName;

    /**
     * 基站ID
     */
    private String nestId;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 识别类型
     */
    private Integer idenValue;

    public String getTaskFlyIndexName() {
        return String.format("%s-架次%s#%s"
                , taskName
                , missionSeqId == null ? "" : missionSeqId
                , flyIndex == null ? "1" : flyIndex);
    }
}
