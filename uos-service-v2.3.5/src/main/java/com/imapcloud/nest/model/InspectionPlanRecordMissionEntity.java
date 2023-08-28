package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 巡检计划记录飞行架次表
 * </p>
 *
 * @author Vastfy
 * @since 2022-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("inspection_plan_record_mission")
public class InspectionPlanRecordMissionEntity implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 表自增主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 巡检计划记录ID
     */
    private Integer planRecordId;

    /**
     * 巡检计划ID
     */
    private Integer planId;

    /**
     * 飞行架次ID
     */
    private Integer missionId;

    /**
     * 架次飞行记录ID，只有执行成功该字段才有值
     */
    private Integer missionRecordId;

    /**
     * 计划执行时间
     */
    private LocalDateTime scheduleExecTime;

    /**
     * 实际执行时间
     */
    private LocalDateTime actualExecTime;

    /**
     * 巡检记录状态【0：未执行；1：执行成功；2：执行失败；3：取消执行】
     */
    private Integer execState;

    /**
     * 飞行时长（单位：s）
     */
    private Integer flightDuration;

    /**
     * 失败原因
     */
    private String failureCause;

    /**
     * 创建用户ID(废弃)
     */
    @Deprecated
    private Integer createUserId = 0;

    /**
     * 创建用户ID
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
     * 是否删除【0：未删除；1：已删除】
     */
    @TableLogic
    private Boolean deleted;


}
