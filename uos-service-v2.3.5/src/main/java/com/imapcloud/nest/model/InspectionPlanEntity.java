package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * <p>
 * 巡检计划表
 * </p>
 *
 * @author Vastfy
 * @since 2022-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("inspection_plan")
public class InspectionPlanEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 巡检计划业务ID（暂时只用到quartz定时任务名称）
     */
    private String bizId;

    /**
     * 巡检计划名称
     */
    private String name;

    /**
     * 基站ID
     */
    private String baseNestId;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 是否自动任务【1：自动任务；0：手动任务】
     */
    private Integer auto;

    /**
     * 计划开启状态【1：开启；0：关闭】
     */
    private Integer state;

    /**
     * 计划执行类型【0：定期计划；1：周期计划】
     */
    private Integer type;

    /**
     * 周期执行单位【0：每日；1：每周；2：每月】
     */
    private Integer cycleExecUnit;

    /**
     * 周期执行间隔（周期执行单位为周/月时，该值只允许为1）
     */
    private Integer cycleExecInterval;

    /**
     * 指定日历值（日历值 周期执行以周为维度时，该值只支持周一（1）到周天（7）； 周期执行以月为维度时，该值只支持1号到31号；多个以英文逗号分隔）
     */
    private String calendarValues;

    /**
     * 计划执行日期（周期计划时该值为空）
     */
    private LocalDate scheduleDate;

    /**
     * 计划执行时间
     */
    private LocalTime scheduleTime;

    /**
     * cron表达式（按规则生成）
     */
    private String cronExpression;

    /**
     * 保存状态：0-暂不保存，1-保存到机巢（飞机保存到机巢），2-保存到服务器（飞机保存到机巢并且保存到图片服务器）
     */
    private Integer gainDataMode;

    /**
     * 是否录制无人机图传视频【0：否；1：是】
     */
    private Integer gainVideo;

    /**
     * 创建用户id
     */
    private Long creatorId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime modifyTime;

    /**
     * 是否删除【0：否；1：是】
     */
    @TableLogic
    private Boolean deleted;

}
