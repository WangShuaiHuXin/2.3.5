package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 缺陷识别
 *
 * @author boluo
 * @date 2023-03-08
 */
@Data
public class PowerMeterFlightDetailDefectOutDO {

    /**
     * 飞行数据详情id（业务主键）
     */
    private String detailId;

    /**
     * 详情名称
     */
    private String detailName;

    /**
     * 飞行数据id
     */
    private String dataId;

    /**
     * 飞行数据图片id
     */
    private Long photoId;

    /**
     * 图片名称
     */
    private String photoName;

    /**
     * 可见光图片url
     */
    private String pictureUrl;

    /**
     * 缩略图url
     */
    private String thumbnailUrl;

    /**
     * 设备状态【取字典 geoai_dial_device_state 数据项值】
     */
    private int deviceState;

    /**
     * 缺陷识别状态
     */
    private int defectState;

    /**
     * 核实状态 【去字典 ‘【geoai_verification_ state】’】
     */
    private int verificationState;

    /**
     * 任务状态
     */
    private int taskState;

    /**
     * 告警原因
     */
    private String reason;

    /**
     * 拍摄时间
     */
    private LocalDateTime shootingTime;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 电力pms_id
     */
    private String pmsId;

    /**
     * 任务开始时间
     */
    private LocalDateTime taskStartTime;

    /**
     * 任务照片开始时间
     */
    private LocalDateTime taskPictureStartTime;

    /**
     * 区域层id
     */
    private String areaLayerId;

    /**
     * 区域层名称
     */
    private String areaLayerName;

    /**
     * 子区域层id
     */
    private String subAreaLayerId;

    /**
     * 子区域层名称
     */
    private String subAreaLayerName;

    /**
     * 单元层id
     */
    private String unitLayerId;

    /**
     * 单元层名称
     */
    private String unitLayerName;

    /**
     * 设备层id
     */
    private String deviceLayerId;

    /**
     * 设备层名称
     */
    private String deviceLayerName;

    /**
     * 部件id
     */
    private String componentId;

    /**
     * 部件名称
     */
    private String componentName;

    /**
     * 航点id
     */
    private String waypointId;

    /**
     * 设备名称
     */
    private String equipmentName;

    @Data
    public static class StatisticsOutDO {

        private int deviceState;

        private Long num;
    }
}
