package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 基站
 *
 * @author boluo
 * @date 2022-08-25
 */
@ToString
public class BaseNestOutDO {

    private BaseNestOutDO() {}

    @Data
    public static class BaseNestEntityOutDO implements Serializable {
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
         * 对地高度
         */
        private BigDecimal aglAltitude;

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
    }


    @Data
    public static class ListOutDO implements Serializable {
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
         * 区域id
         */
        private String regionId;

        /**
         * 维度
         */
        private Double latitude;

        /**
         * 经度
         */
        private Double longitude;

        /**
         * 高度
         */
        private Double altitude;
    }
    @Data
    public static  class BaseNestUavInfoOutDO{

        private String nestId;

        /**
         * 无人机ID
         */
        private String uavId;

        /**
         * 无人机序列号
         */
        private String uavNumber;

        /**
         * 遥控器序列号
         */
        private String rcNumber;

        /**
         * 相机名称
         */
        private String cameraName;

        /**
         * 推拉流信息
         */
        private String streamId;

        /**
         * 无人机型号值
         */
        private String type;

        /**
         * 无人机标识
         */
        private Integer which;

        /**
         * appid
         */
        private Integer appId;

        /**
         * 登记码
         */
        private String registerCode;

        /**
         * 起飞全重
         */
        private Double takeoffWeight;

        /*******************中科天网*/
        /*中科天网-生产厂家名称*/
        private Integer uavPro;

        /*中科天网-飞行器名称*/
        private Integer uavName;

        /*中科天网-飞行器类别*/
        private Integer uavType;

        /*中科天网-飞行器类型*/
        private Integer uavPattern;

        /*中科天网-飞行器序列号*/
        private String uavSn;

    }
}
