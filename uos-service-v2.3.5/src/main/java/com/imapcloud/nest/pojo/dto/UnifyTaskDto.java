package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.enums.FocalModeEnum;
import com.imapcloud.nest.pojo.dto.unifyAirLineDto.MultiMissionExtra;
import com.imapcloud.nest.pojo.dto.unifyAirLineDto.SlopePhotoExtra;
import com.imapcloud.sdk.pojo.constant.FlightPathModeEnum;
import com.imapcloud.sdk.pojo.constant.HeadingModeEnum;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * Created by wmin on 2020/11/17 11:39
 *
 * @author wmin
 */
@Data
public class UnifyTaskDto {


    @NotNull(message = "{geoai_uos_nest_id_cannot_be_empty}")
    private String nestId;

    private Integer taskId;

    private Integer tagId;

    private String tagName;

    @Length(min = 1, max = 64, message = "{geoai_uos_name_length_1_64_characters}")
    private String taskName;

    /**
     * 任务类型
     */
    @NotNull(message = "{geoai_uos_mission_type_cannot_be_empty}")
    private Integer taskType;
    /**
     * 任务描述
     */
    private String description;

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
    private long predictFlyTime;

    /**
     * 拍照数
     */
    private Integer photoCount;

    private Long videoLength;

    private Integer videoCount;

    /**
     * 统一航线格式json
     */
    @NotBlank(message = "{geoai_uos_route_data_cannot_be_empty}")
    private String unifyAirLineJson;

    /**
     * 飞行速度
     */
    @Min(value = 0)
    @Max(value = 15)
    private Integer speed;

    /**
     * 自动飞行速度
     */
    @Min(value = 0)
    @Max(value = 15)
    private Integer autoFlightSpeed;

    /**
     * 起降航高
     */
    private Integer takeOffLandAlt;

    /**
     * 航线类型
     */
    private Integer airLineType;

    /**
     * 航线种类,0->机巢，1->移动终端
     */
    private Integer mold;

    private List<SlopePhotoExtra> slopePhotoExtraList;

    /**
     * 多架次任务的额外参数
     */
    private List<MultiMissionExtra> multiMissionExtraList;

    /**
     * 架次划分时间长短
     */
    private Integer deltaTime;

    /**
     * 变焦模式
     */
    private FocalModeEnum focalMode;

    /**
     *
     */
    private String showInfo;

    /**
     * 飞行模式
     */
    private FlightPathModeEnum flightPathMode;

    /**
     * 单位code 用于接收前端参数
     */
    private List<String> orgCodeList;

    private String unitId;

    private String accountId;

    /**
     * 大疆格式航线
     */
    private String djiAirLineJson;

    private HeadingModeEnum headingMode;

    /**
     * 网格边界
     */
    private String gridBounds;

    private Map<Integer,String> djiAirLineMap;
}
