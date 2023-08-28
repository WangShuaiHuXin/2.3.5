package com.imapcloud.nest.pojo.DO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Vastfy
 * @date 2022/04/20 11:24
 * @since 1.8.9
 */
@Getter
@Builder
public class InspectionPlanRecordCriteriaDo extends QueryCriteriaDo<InspectionPlanRecordCriteriaDo> {

    /**
     * 巡检计划名称
     */
    private String planName;

    /**
     * 基站ID
     */
    private String baseNestId;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 执行状态
     */
    @Setter
    private List<Integer> execStates;

    /**
     * 过滤已关闭的计划记录
     */
    private boolean filterClosedPlan;

    /**
     * 排序字段
     * 0：更新时间
     * 1：计划执行时间
     * 2：实际执行时间
     */
    private Integer sort;

    /**
     * 是否升序，默认为FALSE（降序）</>
     */
    private boolean asc;

    /**
     * 单位编码-用于过滤权限数据
     */
    private String orgCode;

    /**
     * 基站业务id
     */
    private List<String> baseNestIds;
}
