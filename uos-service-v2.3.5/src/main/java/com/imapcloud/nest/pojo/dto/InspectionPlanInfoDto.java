package com.imapcloud.nest.pojo.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 巡检计划信息
 * @author Vastfy
 * @date 2022/4/18 14:25
 * @since 1.8.9
 */
@Data
public class InspectionPlanInfoDto implements Serializable {

    /**
     * 巡检计划ID
     */
    private Integer id;

    /**
     * 巡检计划名称
     */
    @NotNull(message = "{geoai_uos_name_of_the_inspection_plan_cannot_be_empty}")
    @Length(min = 1, max = 50, message = "{geoai_uos_inspection_plan_name_between_1_and_50_characters}")
    private String name;

    /**
     * 基站ID
     */
    @NotNull(message = "{geoai_uos_base_station_ID_cannot_be_empty}")
    private String nestId;

    /**
     * 数据传输方式
     * 0-暂不保存，1-保存到机巢（飞机保存到机巢），2-保存到服务器（飞机保存到机巢并且保存到图片服务器）
     */
    @NotNull(message = "{geoai_uos_must_specify_the_data_transfer_method}")
    @Min(value = 0, message = "{geoai_uos_data_transmission_method_between_0_and_2}")
    @Max(value = 2, message = "{geoai_uos_data_transmission_method_between_0_and_2}")
    private Integer gainDataMode;

    /**
     * 是否自动任务
     * 0：否， 1：是
     */
    @NotNull(message = "{geoai_uos_must_specify_whether_the_task_is_automatic}")
    @Min(value = 0, message = "{geoai_uos_automatic_task_is_limited_to_optional_values_between_0_and_1}")
    @Max(value = 1, message = "{geoai_uos_automatic_task_is_limited_to_optional_values_between_0_and_1}")
    private Integer auto;
    /**
     * 是否录频
     * false：否， true：是
     */
    @NotNull(message = "{geoai_uos_must_specify_whether_to_record_the_screen}")
    @Min(value = 0, message = "{geoai_uos_record_the_frequency_optional_values_between_0_and_1}")
    @Max(value = 1, message = "{geoai_uos_record_the_frequency_optional_values_between_0_and_1}")
    private Integer gainVideo;


    /**
     * 飞行架次列表
     */
    @NotNull(message = "{geoai_uos_must_specify_the_number_of_flights}")
    @Size(min = 1, max = 8, message = "{geoai_uos_inspection_plan_Only_1_8_sorties_are_allowed}")
    private List<Integer> missionIds;

    /**
     * 计划触发时间规则
     */
    @Valid
    @NotNull(message = "{geoai_uos_must_specify_the_inspection_plan_rules}")
    private InspectionPlanTriggerDto inspectionPlanTrigger;

}
