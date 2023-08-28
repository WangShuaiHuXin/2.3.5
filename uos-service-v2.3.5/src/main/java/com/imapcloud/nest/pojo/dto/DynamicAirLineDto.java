package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.util.List;


/**
 * 动态规划的架次任务
 *
 * @author daolin 2020/11/4 16:54
 */

@Data
public class DynamicAirLineDto {

    /**
     * Id
     */
    private Integer id;

    /**
     * 架次名称
     */
    private String name;

    /**
     * 航点数量
     */
    private Integer pointCount;

    /**
     * 航线总长
     */
    private Double predictMiles;

    /**
     * 预计飞行时间,单位是秒
     */
    private long predictFlyTime;

    /**
     * 拍照数
     */
    private Integer photoCount;

    /**
     * 航线json
     */
    private String airLineStr;

    /**
     * 航点列表
     */
    private List<FlyPoint> airLineList;

    private List<Integer> propList;
}
