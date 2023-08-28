package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 巡检计划飞行架次信息
 * @author Vastfy
 * @date 2022/4/19 15:39
 * @since 1.8.9
 */
@Data
public class InspectionPlanMissionInfoDto implements Serializable {

    private Integer planId;

    private Integer missionId;

    private String missionName;

}
