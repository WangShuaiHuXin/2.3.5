package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 表计读数-飞行数据表实体
 * @author vastfy
 * @date 2022-11-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("power_meter_flight_data")
public class PowerMeterFlightDataEntity extends GenericEntity {

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
}
