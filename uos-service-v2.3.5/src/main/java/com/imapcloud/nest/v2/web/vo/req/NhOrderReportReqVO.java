package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;

import java.io.Serializable;

@Data
public class NhOrderReportReqVO implements Serializable {
    /**
     * 关联的工单ID
     */
    private String orderId;
    /**
     * 单位CODE
     */
    private String orgCode;
    /**
     * 报告名称
     */
    private String name;

    /**
     * 巡检报告存储路径
     * @since 2.2.3
     */
    private String filePath;

}
