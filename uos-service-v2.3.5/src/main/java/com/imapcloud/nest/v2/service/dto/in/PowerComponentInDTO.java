package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 部件库
 *
 * @author boluo
 * @date 2022-11-25
 */
@ToString
public class PowerComponentInDTO {

    private PowerComponentInDTO() {

    }

    @Data
    public static class SaveOrUpdateInDTO {

        private String orgCode;

        private String componentId;

        private String componentName;

        private String equipmentType;

        private List<Integer> analysisTypeList;

        /**
         * 部件图片
         */
        private String componentPicture;

        /**
         * 部件图片名称
         */
        private String componentPictureName;

        private String description;

        private String accountId;
    }

    @Data
    public static class ComponentRuleInDTO {

        private String componentId;

        private String accountId;

        private List<RuleInDTO> ruleInDTOList;
    }

    @Data
    public static class RuleInDTO {

        /**
         * 部件库规则id
         */
        private String componentRuleId;

        /**
         * 部件库id
         */
        private String componentId;

        /**
         * 部件规则名称
         */
        private String componentRuleName;

        /**
         * 告警判断--1：是 0：否
         */
        private int alarmStatus;

        /**
         * 告警最小值
         */
        private BigDecimal alarmMin;

        /**
         * 告警最大值
         */
        private BigDecimal alarmMax;
    }

    @Data
    public static class ComponentListInDTO extends PageInfo {

        private String orgCode;

        private Integer analysisType;

        private String equipmentType;

        private String componentName;

        private LocalDateTime start;

        private LocalDateTime end;
    }

    @Data
    public static class ComponentRuleInfraredInDTO {

        private String componentId;

        private String accountId;

        private List<InfraredRuleInDTO> infraredRuleInDTOList;
    }

    @Data
    public static class InfraredRuleInDTO {

        /**
         * 部件库规则id
         */
        private String componentRuleId;

        private Integer deviceState;

        private Integer infraredRuleState;

        private Long threshold;
    }

}
