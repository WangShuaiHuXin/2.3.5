package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.common.core.Pagination;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 巡检计划查询信息
 * @author Vastfy
 * @date 2022/4/18 11:25
 * @since 1.8.9
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class InspectionPlanQueryDto extends Pagination {
}
