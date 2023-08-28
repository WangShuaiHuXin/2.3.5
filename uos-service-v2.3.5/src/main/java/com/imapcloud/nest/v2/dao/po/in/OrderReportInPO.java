package com.imapcloud.nest.v2.dao.po.in;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class OrderReportInPO {

    private String orderId;
    /**
     * 单位CODE
     */
    private String orgCode;
    /**
     * 报告名称
     */
    private String name;

    private String userId;

    private String path;

    private String reportId;
}
