package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.common.core.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 巡检计划记录查询信息
 * @author Vastfy
 * @date 2022/4/18 11:25
 * @since 1.8.9
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class InspectionPlanRecordQueryDto extends Pagination {

    /**
     * 关键字
     */
    private String keyword;

    /**
     * 基站ID
     */
    private String nestId;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 执行状态执行状态【0：待执行(待执行任务队列均未执行)；1：已取消(待执行任务队列已取消)；2：执行失败(待执行任务队列第一个任务失败)；3：未全部完成(待执行任务队列第一个任务成功，后续飞行任务存在失败或取消执行状态)；4：已执行(待执行任务队列全部执行成功)；
     * 多选以逗号分隔
     */
    private String execStates;

    /**
     * 是否过滤已关闭的计划记录
     */
    private boolean filterClosedPlan;

    /**
     * 排序字段
     * 0：更新时间
     * 1：计划执行时间
     * 2：实际执行时间
     */
    private int sort;

    /**
     * 是否升序，默认为FALSE（降序）<br/>
     */
    private boolean asc;

    /**
     * 基站IDs
     */
    private List<String> baseNestIds;

}
