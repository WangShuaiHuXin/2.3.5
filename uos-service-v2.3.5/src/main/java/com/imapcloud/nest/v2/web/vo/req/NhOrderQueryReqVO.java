package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;
import scala.Int;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Data
public class NhOrderQueryReqVO implements Serializable {


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
    @Size(max = 50, min = 0, message = "title over length")
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
    @NotNull(message = "pageSize cannot be null")
    private Long pageSize;

    /**
     * 页码
     */
    @NotNull(message = "pageNo cannot be null")
    private Long pageNo;

    private String orgCode;
}
