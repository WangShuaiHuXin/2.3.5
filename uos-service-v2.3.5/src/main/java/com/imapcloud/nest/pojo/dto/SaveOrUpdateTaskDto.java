package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 新增或者修改任务的Task传输类
 *
 * @author wmin
 */
@Data
public class SaveOrUpdateTaskDto {

    /**
     * 任务名称
     */
    @NotBlank(message = "{geoai_uos_task_name_cannot_be_empty}")
    private String name;

    /**
     * 机巢Id
     */
    @NotNull(message = "{geoai_uos_nest_id_cannot_be_empty}")
    private Integer nestId;

    /**
     * 任务类型
     */
    @NotNull(message = "{geoai_uos_mission_type_cannot_be_empty}")
    private Integer taskType;

    /**
     * 无人机型号
     */
    private String aircraftType;

    /**
     * 飞行器偏航角
     */
    private String headingMode;

    /**
     * 自动飞行速度，如果航点没有设置速度的时候
     */
    private Integer autoFlightSpeed;

    /**
     * 起降航高
     */
    @NotNull(message = "{geoai_uos_the_takeoff_and_landing_altitude_cannot_be_empty}")
    private Double startStopAlt;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 航线Id
     */
    @NotEmpty(message = "{geoai_uos_route_id_cannot_be_empty}")
    private List<Integer> airLineIdList;

    /**
     * 航线JSON
     */
    @NotBlank(message = "{geoai_uos_Route_data_cant_be_null}")
    private String waypointJson;

    /**
     * 是否是多架次航线
     */
    private boolean multiMission;

}
