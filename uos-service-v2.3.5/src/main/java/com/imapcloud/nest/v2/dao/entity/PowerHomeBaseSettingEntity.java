package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;

@Data
@TableName("power_home_base_setting")
public class PowerHomeBaseSettingEntity extends GenericEntity {

    private Long id;
    private Integer coverageArea;
    private Integer inspectionPoints;
    private Integer generalInspection;
    private Integer todayInspection;
    private Integer cumulativePhotography;
    private Integer inspectionNormal;
    private Integer inspectionGeneralDefects;
    private Integer inspectionSeriousDefects;
    private Integer inspectionCriticalDefects;
    private Integer alarmStatisticsProcessed;
    private Integer alarmStatisticsPending;
    private String orgCode;
    private Boolean deleted;
}