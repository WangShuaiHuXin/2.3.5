package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.enums.FocalModeEnum;
import com.imapcloud.sdk.pojo.constant.FlightPathModeEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author wmin
 */
@Data
@Accessors(chain = true)
public class TaskDetailsDto {
    /**
     * 任务Id
     */
    private Integer id;
    /**
     * 任务名称
     */
    @Length(min = 1, max = 64, message = "{geoai_uos_name_length_1_64_characters}")
    private String name;

    /**
     * 任务类型
     */
    @NotNull(message = "{geoai_uos_mission_type_cannot_be_empty}")
    private Integer taskType;


    /**
     * 航点数量
     */
    private Integer pointCount;

    /**
     * 航线总长
     */
    private Double predictMiles;

    /**
     * 预计飞行时间,单位是秒
     */
    private Long predictFlyTime;

    /**
     * 录频时长
     */
    private Long videoLength;

    /**
     * 视频个数
     */
    private Integer videoCount;

    /**
     * 拍照数
     */
    private Integer photoCount;

    /**
     * key -> 航线架次id
     * val -> 航线的JSON
     */
    private Map<Integer, String> airLineMap;

    /**
     * 航线类型：1-> 航点航线，2->易飞，点云航线，3->基站的航线
     */
    private Integer airLineType;
    /**
     * 飞机类型
     */
    private String aircraftType;

    /**
     * 飞行器偏航角
     */
    @NotNull(message = "{geoai_uos_Yaw_angle_mode_cannot_empty}")
    private String headingMode;

    /**
     * 起降行高
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

    private Integer tagId;

    /**
     * 航线种类
     */
    private Integer mold;

    private List<DynamicAirLineDto> lineList;

    /**
     * 变电站规划的次级类别0是本地任务，1是动态任务
     */
    private Integer subType;

    /**
     * 识别类型
     */
    private Integer identificationType;

    /**
     * 是否是绝对航高，海拔
     */
    private Boolean absolute;

    /**
     * 变焦模式
     */
    private FocalModeEnum focalMode;

    /**
     * 显示参数
     */
    private String showInfo;

    /**
     * 飞行模式
     */
    private FlightPathModeEnum flightPathMode;

    /**
     * taskFileId 大疆文件数据Id
     */
    private String taskFileId;

    /**
     * 单位code 用于接收前端参数
     */
    private List<String> orgCodeList;

    private String unitId;

    private String accountId;

    /**
     * 航线保存
     */
    private Map<Integer,String> djiAirLineMap;


}
