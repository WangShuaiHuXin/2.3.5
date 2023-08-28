package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 表计读数-飞行数据详情表实体
 * @author vastfy
 * @date 2022-11-29
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("power_meter_flight_detail")
public class PowerMeterFlightDetailEntity extends GenericEntity {

    /**
     * 数据详情ID（业务主键）
     */
    private String detailId;

    /**
     * 数据详情名称
     */
    private String detailName;

    /**
     * 数据ID
     */
    private String dataId;

    /**
     * 飞行数据图片ID
     */
    private Long photoId;

    /**
     * 飞行数据图片名称
     */
    private String photoName;

    /**
     * 飞行数据原图URL
     */
    private String originalPicUrl;

    /**
     * 识别结果URL（minio文件服务器路径）
     */
    private String discernPicUrl;

    /**
     * 算法识别结果URL（内网地址）
     */
    private String algorithmPicUrl;

    /**
     * 设备状态【取字典`GEOAI_DIAL_DEVICE_STATE`数据项值】
     * @see com.imapcloud.nest.v2.common.enums.DialDeviceTypeEnum
     */
    private Integer deviceState;

    /**
     * 读数状态【取字典`GEOAI_DIAL_READING_STATE`数据项值】
     * @see com.imapcloud.nest.v2.common.enums.DialReadingTypeEnum
     */
    private Integer readingState;

    /**
     * 告警原因【取字典`GEOAI_DIAL_ALARM_REASON`数据项值】
     * @see com.imapcloud.nest.v2.common.enums.DialAlarmReasonTypeEnum
     */
    private Integer alarmReason;

    /**
     * 拍摄时间
     */
    private LocalDateTime shootingTime;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 电力PMS_ID
     */
    private String pmsId;

    /**
     * 设备台账名称
     */
    private String equipmentName;

    /**
     * 区域层ID
     */
    private String areaLayerId;

    /**
     * 区域层名称
     */
    private String areaLayerName;

    /**
     * 子区域层ID
     */
    private String subAreaLayerId;

    /**
     * 子区域层名称
     */
    private String subAreaLayerName;

    /**
     * 单元层ID
     */
    private String unitLayerId;

    /**
     * 单元层名称
     */
    private String unitLayerName;

    /**
     * 设备层ID
     */
    private String deviceLayerId;

    /**
     * 设备层名称
     */
    private String deviceLayerName;

    /**
     * 部件ID
     */
    private String componentId;

    /**
     * 部件名称
     */
    private String componentName;

    /**
     * 航点ID
     */
    private String waypointId;

    /**
     * 核实状态
     */
    private String verificationStatus;
}
