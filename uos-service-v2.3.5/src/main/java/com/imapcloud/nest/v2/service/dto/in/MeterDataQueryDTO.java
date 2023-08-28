package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 表计数据查询请求信息
 * @author Vastfy
 * @date 2022/12/04 15:59
 * @since 2.1.5
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MeterDataQueryDTO extends PageInfo {

    private String orgCode;

    private LocalDate fromTime;

    private LocalDate toTime;

    private Integer idenValue;

    /**
     * 关键字搜索
     */
    private String keyword;
}
