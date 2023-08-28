package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.enums.FocalModeEnum;
import com.imapcloud.nest.utils.airline.PointCloudWaypoint;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;

/**
 * @author wmin
 */
@Data
@Accessors(chain = true)
public class FineInspTaskDto {
    /**
     * 基站id
     */
    private String nestId;

    private String unitId;

    /**
     * 航线种类，0-> 机巢，1->移动终端
     */
    @NotNull
    private Integer mold;

    /**
     * 任务id
     * 新增的时候不需要传，编辑的时候必须全
     */
    private Integer taskId;

    /**
     * zip包选择
     */
    @NotNull(message = "{geoai_uos_route_packet_cant_be_null}")
    private Integer zipId;

    /**
     * 任务名称
     */
    @Length(min = 1, max = 64, message = "{geoai_uos_mission_name_length_is_1_64_characters}")
    private String taskName;

    @NotNull(message = "{geoai_uos_task_type_cannot_be_null}")
    private Integer taskType;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 拍照点
     */
    @NotNull(message = "{geoai_uos_number_of_route_points_cannot_be_null}")
    private Integer pointCount;

    /**
     * 拍照动作数
     */
    @NotNull(message = "{geoai_uos_number_of_photo_actions_cannot_be_null}")
    private Integer photoActionCount;

    /**
     * 航线总长
     */
    @NotNull(message = "{geoai_uos_estimated_flight_distance_cannot_be_null}")
    private Double predictMiles;

    /**
     * 预计飞行时间,单位是秒
     */
    @NotNull(message = "{geoai_uos_estimated_flight_time_cannot_be_null}")
    private long predictFlyTime;


    /**
     * 起降行高
     */
    @NotNull(message = "{geoai_uos_takeoff_and_landing_altitude_cannot_be_null}")
    private Double takeOffLandAlt;

    /**
     * 自动飞行速度，塔间速度
     */
    @Min(value = 1, message = "{geoai_uos_minimum_speed_of_automatic_flight_is_1m}")
    @Max(value = 15, message = "{geoai_uos_maximum_speed_of_automatic_flight_is_15m}")
    private Integer autoFlightSpeed;

    /**
     * 航线飞行速度,绕塔速度
     */
    @Min(value = 1, message = "{geoai_uos_minimum_speed_of_automatic_flight_is_1m}")
    @Max(value = 15, message = "{{geoai_uos_maximum_speed_of_automatic_flight_is_15m}}")
    private Integer speed;

    /**
     * key -> 杆塔id
     * value ->
     */
    @NotEmpty(message = "{geoai_uos_Route_data_cant_be_null}")
    private Map<Integer, List<PointCloudWaypoint>> routeMap;

    /**
     * 选择的杆塔
     */
    @NotEmpty(message = "{geoai_uos_Tower_id_cant_be_null}")
    private List<Integer> towerIdList;

    /**
     * 标签id
     */
    private Integer tagId;

    private String showInfo;

    /**
     * 变焦模式
     */
//    private FocalModeEnum focalMode;

    private List<String> orgCodeList;

    private String accountId;
}
