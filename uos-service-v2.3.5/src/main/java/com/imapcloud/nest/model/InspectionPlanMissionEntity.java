package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 巡检计划-飞行架次关联表
 * </p>
 *
 * @author Vastfy
 * @since 2022-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("inspection_plan_mission")
public class InspectionPlanMissionEntity implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 表自增主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 巡检计划ID
     */
    private Integer planId;

    /**
     * 计划关联任务架次ID（mission表）
     */
    private Integer missionId;

    /**
     * 执行顺序
     */
    private Integer execOrder;

    /**
     * 是否删除【0：否；1：是】
     */
    @TableLogic
    private Boolean deleted;

}
