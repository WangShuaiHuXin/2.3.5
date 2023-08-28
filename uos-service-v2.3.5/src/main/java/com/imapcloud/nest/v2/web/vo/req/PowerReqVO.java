package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 电力相关
 *
 * @author boluo
 * @date 2022-11-25
 */
@ToString
public class PowerReqVO {
    private PowerReqVO() {

    }

    @Data
    public static class ComponentSaveReqVO {

        @NotEmpty(message = "{geoai_uos_cannot_empty_unitcode}")
        private String orgCode;

        /**
         * 部件库ID 新增时为空
         */
        private String componentId;

        @NotEmpty(message = "{geoai_uos_power_component_param_001}")
        @Size(min = 1, max = 50, message = "{geoai_uos_power_component_param_002}")
        private String componentName;

        /**
         * 设备类型
         */
        @NotEmpty(message = "{geoai_uos_power_component_param_003}")
        @Size(min = 1, max = 50, message = "{geoai_uos_power_component_param_004}")
        private String equipmentType;

        /**
         * 巡检类型
         */
        @NotNull(message = "{geoai_uos_power_component_param_005}")
        @Size(min = 1, message = "{geoai_uos_power_component_param_005}")
        private List<Integer> analysisTypeList;

        /**
         * 部件图片
         */
        private String componentPicture;

        /**
         * 部件图片名称
         */
        private String componentPictureName;


        /**
         * 备注
         */
        @Size(max = 100, message = "{geoai_uos_power_component_param_006}")
        private String description;
    }

    @Data
    public static class ComponentRuleReqVO implements Serializable {

        /**
         * 部件库ID 新增时为空
         */
        @NotNull(message = "{geoai_uos_power_component_param_007}")
        private String componentId;

        @Valid
        private List<RuleInfoReqVO> ruleInfoList;
    }

    @Data
    public static class RuleInfoReqVO {
        /**
         * 部件库规则id
         */
        private String componentRuleId;

        /**
         * 部件规则名称
         */
        @NotBlank(message = "{geoai_uos_power_component_param_008}")
        @Size(max = 10, message ="{geoai_uos_power_component_param_009}")
        private String componentRuleName;

        /**
         * 告警判断--1：是 0：否
         */
        @NotNull(message = "{geoai_uos_power_component_param_010}")
        private Integer alarmStatus;

        /**
         * 告警最小值
         */
        @NotNull(message = "{geoai_uos_power_component_param_011}")
        private BigDecimal alarmMin;

        /**
         * 告警最大值
         */
        @NotNull(message = "{geoai_uos_power_component_param_012}")
        private BigDecimal alarmMax;
    }

    @Data
    public static class BaseReq extends PageInfo {
        // yyyy-MM-dd
        private String startTime;

        private String endTime;

        private LocalDateTime start;

        private LocalDateTime end;
    }

    @Data
    public static class ComponentListReqVO extends BaseReq {

        @NotEmpty(message = "{geoai_uos_cannot_empty_unitcode}")
        private String orgCode;

        private Integer analysisType;

        private String equipmentType;

        private String componentName;
    }

    @Data
    public static class ComponentInfraredRuleReqVO implements Serializable {

        /**
         * 部件库ID 新增时为空
         */
        @NotNull(message = "{geoai_uos_power_component_param_007}")
        private String componentId;

        @Valid
        private List<InfraredRuleInfoReqVO> ruleInfoList;
    }

    @Data
    public static class InfraredRuleInfoReqVO {

        /**
         * 部件库规则id
         */
        private String componentRuleId;

        /**
         * 设备状态 字典 0未知 1正常 2一般缺陷 3严重缺陷 4危机缺陷
         */
        @NotNull(message = "{geoai_common_Illegal_request_parameters}")
        private Integer deviceState;

        /**
         * 字典 1大于 2大于等于
         */
        @NotNull(message = "{geoai_common_Illegal_request_parameters}")
        private Integer infraredRuleState;

        /**
         * 阈值
         */
        @NotNull(message = "{geoai_common_Illegal_request_parameters}")
        private Long threshold;
    }
}
