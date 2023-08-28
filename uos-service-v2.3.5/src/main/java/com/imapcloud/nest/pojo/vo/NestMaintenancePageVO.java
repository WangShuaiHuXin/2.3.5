package com.imapcloud.nest.pojo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 维保记录分页VO
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class NestMaintenancePageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 维保类型：0-保养，1-软件故障, 2-无人机故障, 3-基站硬件故障
     */
    private Integer type;

    /**
     * 维保项目
     */
    private String maintainName;

    /**
     * 维保人员
     */
    private String staff;

    /**
     * 备注
     */
    private String remarks;


}
