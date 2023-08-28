package com.imapcloud.nest.v2.manager.dataobj.in;

import com.geoai.common.core.bean.PageInfo;
import com.imapcloud.nest.v2.manager.dataobj.BaseInDO;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 基站
 *
 * @author boluo
 * @date 2022-08-25
 */
@ToString
public class BaseNestInDO {
    private BaseNestInDO() {}

    @Data
    public static class BaseNestEntityInDO extends BaseInDO {
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
    public static class ListInDO extends PageInfo {

        /**
         * 用户所在的单位code
         */
        private String userOrgCode;

        /**
         * 单位code
         */
        private String orgCode;

        /**
         * 基站名称
         */
        private String name;

        /**
         * 基站编号
         */
        private String number;

        /**
         * 基站uuid
         */
        private String uuid;

        /**
         * 基站型号
         */
        private Integer type;

        /**
         * 区域id
         */
        private String regionId;

        /**
         * 关键字
         */
        private String keyword;

        /**
         * @since 2.3.2
         */
        private List<Integer> types;

        /**
         * @since 2.3.2
         */
        private Integer showStatus;


        /**
         * 飞行器类型
         */
        private Integer uavType;

        /**
         * 飞行器型号
         */
        private Integer uavModel;

        /**
         * 飞行器类别
         */
        private Integer uavCate;
    }
}
