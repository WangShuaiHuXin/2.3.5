package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("power_waypoint_ledger_info")
public class PowerWaypointLedgerInfoEntity extends GenericEntity {

    /**
     * 航点台账主键
     */
    private String waypointStationId;

    /**
     * 部件库id
     */
    private String componentId;


    /**
     * 设备层名称
     */
    private String deviceLayerName;

    /**
     * 设备层id
     */
    private String deviceLayerId;

    /**
     * 单元名称
     */
    private String unitLayerName;

    /**
     * 单元id
     */
    private String unitLayerId;

    /**
     * 子区域名称
     */
    private String subRegionName;

    /**
     * 子区域id
     */
    private String subRegionId;

    /**
     * 区域名称
     */
    private String equipmentAreaName;

    /**
     * 区域id
     */
    private String equipmentAreaId;

    /**
     * 变电站名称
     */
    private String substationName;

    /**
     * 航点部件名称
     */
    private String waypointName;
    /**
     * 航点ID
     */
    private String waypointId;

    /**
     * 关联PMSid
     */
    private String equipmentId;


    private String mapsJsonPath;
    private String substationRoutelistJsonPath;
    private String substationTreeJsonPath;
    private String wholeUnitJsonPath;
    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 经度
     */
    private Double latitude;

    /**
     * 纬度
     */
    private Double longitude;

    /**
     * 海拔
     */
    private Double altitude;

}
