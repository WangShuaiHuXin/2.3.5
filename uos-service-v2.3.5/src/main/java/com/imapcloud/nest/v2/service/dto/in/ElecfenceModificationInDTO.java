package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 电子围栏新建信息
 * @author Vastfy
 * @date 2022/9/26 16:27
 * @since 2.1.0
 */
@Data
public class ElecfenceModificationInDTO implements Serializable {

    /**
     * 电子围栏名称
     */
    private String name;

    /**
     * 电子围栏状态【1：开启；2：关闭】
     */
    private Integer state;

    /**
     * 是否共享
     */
    private Boolean shared;

    /**
     * 电子围栏坐标点
     */
    private String coordinates;

    /**
     * 电子围栏高度【单位：m】
     */
    private Integer height;


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
