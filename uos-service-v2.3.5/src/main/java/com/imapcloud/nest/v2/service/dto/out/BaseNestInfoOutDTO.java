package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseNestInfoOutDTO {
    /**
     * 基站业务ID
     */
    private String nestId;

    /**
     * 机巢uuid
     */
    private String uuid;

    /**
     * 机巢名称
     */
    private String name;

    /**
     * 机巢编号
     */
    private String number;

    /**
     * 类型(0-P4R固定基站；1-mini1代；2-M300；3-简易基站；4-车载基站；5-mini2代)
     */
    private Integer type;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 海拔
     */
    private Double altitude;

    /**
     * 机巢地址，例如，广东省韶关市芙蓉变电站
     */
    private String address;

    /**
     * 区域id
     */
    private String regionId;

    /**
     * 备注
     */
    private String description;

    /**
     * 查看监控的状态，0为不展示，默认为1展示
     */
    private Integer showStatus;

    /**
     * 0->没有维保，1->维保中,2->CPS更新中
     */
    private Integer maintenanceStatus;

    /**
     * 部署时间
     */
    private LocalDateTime deployTime;

    /**
     * mqttBrokerID
     */
    private String mqttBrokerId;

    /**
     * 巢内流信息
     */
    private String innerStreamId;

    /**
     * 巢外流信息
     */
    private String outerStreamId;

    private String planCode;
}
