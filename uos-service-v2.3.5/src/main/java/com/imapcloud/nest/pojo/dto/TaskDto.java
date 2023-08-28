package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


/**
 * @author wmin
 */

@Data
public class TaskDto {
    /**
     * 任务名称
     */
    @NotBlank(message = "{geoai_uos_task_name_cannot_be_empty}")
    private String taskName;
    /**
     * 任务类型类型Id
     */
    @NotNull(message = "{geoai_uos_mission_type_cannot_be_empty}")
    private Integer taskTypeId;
    /**
     * 机巢Id
     */
    @NotNull(message = "{geoai_uos_nest_id_cannot_be_empty}")
    private Integer nestId;
    /**
     * 航线Id,如果是航线新增航线，改值为null,否则是修改航线
     */
    @NotNull(message = "{geoai_uos_route_id_cannot_be_empty}")
    private Integer airLineId;
    /**
     * 任务描述
     */
    private String description;
    /**
     * 架次名字
     */
    @NotBlank(message = "{geoai_uos_the_name_of_the_sortie_cannot_be_empty}")
    private String missionName;
    /**
     * 详细看枚举值
     */
    private Integer autoFlightSpeed;
    private Integer gotoFirstWaypointMode;
    private Integer finishAction;
    private Integer headingMode;
    private Integer flightPathMode;

    /**
     * 起降点高度
     */
    @Min(0)
    private Double startStopPointAltitude;

    /**
     * 航点数
     */
    private Integer waypointCount;

    /**
     * 飞行距离
     */
    private Integer miles;

    /**
     * 预计飞行时长
     */
    private Integer seconds;

    /**
     * 保存的任务航线
     */
    @NotBlank(message = "{geoai_uos_route_information_cannot_be_empty}")
    private String airLineJson;

    /**
     * 航线类型，1-航点航线，2-点云，3-易飞
     */
    @NotNull(message = "{geoai_uos_route_type_cannot_be_empty}")
    private Integer airLineType;

    /**
     * 航线名字
     */
    @NotBlank(message = "{geoai_uos_route_name_cannot_be_empty}")
    private String airLineName;
}
