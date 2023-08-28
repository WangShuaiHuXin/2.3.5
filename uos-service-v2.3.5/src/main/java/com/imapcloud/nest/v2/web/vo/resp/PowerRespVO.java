package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 台账
 *
 * @author boluo
 * @date 2022-11-28
 */
@ToString
public class PowerRespVO {

    private PowerRespVO() {}

    @Data
    public static class ComponentInfoRespVO implements Serializable {

        /**
         * 部件库id
         */
        private String componentId;

        /**
         * 部件名称
         */
        private String componentName;

        /**
         * 部件图片
         */
        private String componentPicture;

        /**
         * 部件图片名称
         */
        private String componentPictureName;

        /**
         * 单位code
         */
        private String orgCode;

        /**
         * 设备类型
         */
        private String equipmentType;

        /**
         * 备注
         */
        private String description;

        /**
         * 操作人
         */
        private String operator;

        private LocalDateTime createdTime;

        private LocalDateTime modifiedTime;

        /**
         * 巡检类型
         */
        private List<Integer> analysisTypeList;

        private List<ComponentRuleInfoRespVO> ruleInfoList;

        private List<InfraredRuleInfoRespVO> infraredRuleInfoList;
    }

    @Data
    public static class InfraredRuleInfoRespVO implements Serializable {

        /**
         * 部件库规则id
         */
        private String componentRuleId;

        /**
         * 设备状态 字典 0未知 1正常 2一般缺陷 3严重缺陷 4危机缺陷
         */
        private int deviceState;

        /**
         * 字典 1大于 2大于等于
         */
        private int infraredRuleState;

        /**
         * 阈值
         */
        private Long threshold;

        /**
         * 排序号
         */
        private Integer seq;
    }

    @Data
    public static class ComponentRuleInfoRespVO implements Serializable {

        /**
         * 部件库规则id
         */
        private String componentRuleId;

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

        /**
         * 排序号
         */
        private Integer seq;
    }
}
