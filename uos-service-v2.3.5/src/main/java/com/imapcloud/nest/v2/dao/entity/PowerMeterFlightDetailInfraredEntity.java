package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 红外测温-飞行数据详情表
 * @author wmin
 */

@Data
@TableName("power_meter_flight_detail_infrared")
public class PowerMeterFlightDetailInfraredEntity  extends GenericEntity {
    private static final long serialVersionUID = 1L;


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
     * 红外照片url
     */
    private String infratedUrl;

    /**
     * 可见光图片缩略图url
     */
    private String thumbnailUrl;

    /**
     * 设备状态【取字典 geoai_dial_device_state 数据项值】
     */
    private int deviceState;

    /**
     * 测温状态 【取字典 geoai_temperature _state 数据项值】
     */
    private int temperatureState;

    /**
     * 核实状态 【去字典 ‘【geoai_verification_ state】’】
     */
    private int verificationState;

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
     * AI任务识别状态
     */
    private int taskState;

    /**
     * 任务开始时间
     */
    private LocalDateTime taskStartTime;

    /**
     * 任务照片开始时间
     */
    private LocalDateTime taskPictureStartTime;

    /**
     * 电力pms_id
     */
    private String pmsId;

    /**
     * 设备台账名称
     */
    private String equipmentName;

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
}
