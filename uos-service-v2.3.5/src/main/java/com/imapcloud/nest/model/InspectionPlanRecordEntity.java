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

/**
 * <p>
 * 巡检计划记录表
 * </p>
 *
 * @author Vastfy
 * @since 2022-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("inspection_plan_record")
public class InspectionPlanRecordEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 巡检计划id
     */
    private Integer planId;

    private String baseNestId;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 计划执行日期
     */
    private LocalDate scheduleExecDate;

    /**
     * 计划执行时间
     */
    private LocalDateTime scheduleExecTime;

    /**
     * 实际执行时间
     */
    private LocalDateTime actualExecTime;

    /**
     * 执行状态【0：待执行(待执行任务队列均未执行)；1：已取消(待执行任务队列已取消)；2：执行失败(待执行任务队列第一个任务失败)；3：未全部完成(待执行任务队列第一个任务成功，后续飞行任务存在失败或取消执行状态)；4：已执行(待执行任务队列全部执行成功)；】
     */
    private Integer execState;

    /**
     * 飞行时长（单位：s）
     */
    private Integer flightDuration;

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
