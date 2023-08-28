package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 电子围栏信息
 * @author Vastfy
 * @date 2022/9/23 17:27
 * @since 2.1.0
 */
@Data
public class ElecfenceInfoOutDTO implements Serializable {

    /**
     * 电子围栏ID
     */
    private String id;

    /**
     * 电子围栏名称
     */
    private String name;

    /**
     * 电子围栏类型【1：适飞区； 2：禁飞区】
     */
    private Integer type;

    /**
     * 电子围栏状态【1：开启；2：关闭】
     */
    private Integer state;

    /**
     * 电子围栏坐标点
     */
    private String coordinates;

    /**
     * 电子围栏高度【单位：m】
     */
    private Integer height;

    /**
     * 是否共享给子孙单位
     */
    private Boolean shared;

    /**
     * 电子围栏所属单位编码
     */
    private String orgCode;

    /**
     * 有效期开始时间
     */
    private LocalDateTime effectiveStartTime;

    /**
     * 有效期截止时间
     */
    private LocalDateTime effectiveEndTime;

    /**
     * 是否永久有效
     */
    private Boolean neverExpired;

}
