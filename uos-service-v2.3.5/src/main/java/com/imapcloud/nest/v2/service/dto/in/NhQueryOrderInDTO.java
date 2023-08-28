package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
public class NhQueryOrderInDTO {

    /**
     * 工单状态  字典：GEOAI_WORD_ORDER_STATUS
     */
    private Integer status;

    /**
     * 优先程度   字典：GEOAI_PRIORITY_DEGREE
     */
    private Integer degree;

    /**
     * 工单标题
     */
    @Size(max = 50, min = 1, message = "title over length")
    private String title;

    /**
     * 开始时间
     */
    private String beginTime;


    /**
     * 开始时间
     */
    private String endTime;

    /**
     * 页大小
     */
    private Long pageSize;

    /**
     * 页码
     */
    private Long pageNo;

    private String orgCode;

}
