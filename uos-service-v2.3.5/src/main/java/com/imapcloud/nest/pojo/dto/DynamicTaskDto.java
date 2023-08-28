package com.imapcloud.nest.pojo.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author daolin
 * 动态任务传参
 */
@Data
public class DynamicTaskDto {

    /**
     * 任务名称
     */
    @Length(min = 1, max = 64,message = "{geoai_uos_name_length_1_64_characters}")
    private String name;

    /**
     * 任务类型
     */
    @NotNull(message = "{geoai_uos_mission_type_cannot_be_empty}")
    private Integer taskType;

    /**
     * 航线类型：1-> 航点航线，2->易飞，点云航线，3->基站的航线
     */
    private Integer airLineType;

    /**
     * 飞行器偏航角
     */
    @NotNull(message = "{geoai_uos_Yaw_angle_mode_cannot_empty}")
    private String headingMode;

    /**
     * 起降航高
     */
    @Min(value = 0)
    private Double startStopAlt;

    /**
     * 自动飞行速度
     */
    @Min(value = 0)
    @Max(value = 15)
    private Integer autoFlightSpeed;

    /**
     * 航线飞行速度
     */
    @Min(value = 0)
    @Max(value = 15)
    private Integer speed;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 机巢id
     */
    @NotNull(message = "{geoai_uos_nest_id_cannot_be_empty}")
    private String nestId;

    /**
     * 标签id
     */
    private Integer tagId;

    /**
     * 航线种类，0-机巢，1-易飞
     */
    private Integer mold;

    /**
     * 单位id
     */
    private String unitId;

    /**
     * 识别类型
     */
    private Integer identificationType;


    /**
     * 航线列表
     */
    private List<DynamicAirLineDto> lineList;

    /**
     * 变电站规划的次级类别0是本地任务，1是动态任务
     */
    private Integer subType;

    private List<String> orgCodeList;

    private String accountId;
}
