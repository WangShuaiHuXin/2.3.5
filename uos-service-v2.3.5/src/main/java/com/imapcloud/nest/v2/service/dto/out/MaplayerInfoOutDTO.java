package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 地图图层数据
 * @author Vastfy
 * @date 2022/9/27 13:44
 * @since 2.1.0
 */
@Data
public class MaplayerInfoOutDTO implements Serializable {

    /**
     * 图层ID
     */
    private String id;

    /**
     * 图层名称
     */
    private String name;

    /**
     * 所属单位编码
     */
    private String orgCode;

    /**
     * 所属单位名称
     */
    private String orgName;

    /**
     * 发布地址
     */
    private String route;

    /**
     * 图层经度
     */
    private Double longitude;

    /**
     * 图层纬度
     */
    private Double latitude;

    /**
     * 定位高度
     */
    private Double altitude;

    /**
     * 显示高度
     */
    private Double viewAltitude;

    /**
     * 【本单位】是否展示
     */
    private Boolean display;

    /**
     * 【本单位】是否预加载
     */
    private Boolean preload;

    /**
     * 影像层级
     */
    private Integer hierarchy;

    /**
     * 碰撞检测【地形专有】
     */
    private Integer safeCheck;

    /**
     * 几何误差【模型专有】
     */
    private Double geometricError;

    /**
     * 类型
     */
    private Integer type;

    /**
     * 【图层单位】是否展示
     */
    private Boolean display0;

    /**
     * 【图层单位】是否预加载
     */
    private Boolean preload0;

    /**
     * 偏移高度
     */
    private Integer offsetHeight;

}
