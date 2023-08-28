package com.imapcloud.nest.v2.service.dto.in;

import com.geoai.common.core.bean.PageInfo;
import com.imapcloud.nest.v2.web.vo.resp.AdminNestRespVO;
import lombok.Data;
import lombok.ToString;
import scala.Int;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 基站相关参数
 *
 * @author boluo
 * @date 2022-08-19
 */
@ToString
public class BaseNestInDTO {
    private BaseNestInDTO() {
    }

    @Data
    public static class ListInDTO extends PageInfo {

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

    @Data
    public static class NestBaseInDTO {

        private String accountId;

        /**
         * 基站业务ID
         */
        private String nestId;

        /**
         * 机巢名称
         */
        private String name;

        /**
         * 单位ID
         */
        private List<String> deleteOrgCodeList;

        private List<String> addOrgCodeList;

        /**
         * 区域id
         */
        private String regionId;

        /**
         * 海拔
         */
        private BigDecimal altitude;

        /**
         * 经度
         */
        private BigDecimal longitude;

        /**
         * 纬度
         */
        private BigDecimal latitude;

        /**
         * 对地高度
         */
        private BigDecimal aglAltitude;

        private LocalDateTime deployTime;

        /**
         * 机巢地址，例如，广东省韶关市芙蓉变电站
         */
        private String address;
    }

    @Data
    public static class NestNestInDTO {

        private String accountId;

        /**
         * 基站业务ID
         */
        private String nestId;

        /**
         * 机巢uuid
         */
        private String uuid;

        /**
         * 机巢编号
         */
        private String number;

        /**
         * 类型(0-P4R固定基站；1-mini1代；2-M300；3-简易基站；4-车载基站；5-mini2代)
         */
        private Integer type;

        /**
         * mqttBrokerID
         */
        private String mqttBrokerId;
    }

    @Data
    public static class BatteryInDTO {

        /**
         * 基站业务ID
         */
        private String nestId;

        /**
         * 告警循环次数
         */
        private Integer alarmCircleNum;

        /**
         * 禁用循环次数
         */
        private Integer forbiddenCircleNum;
    }

    @Data
    public static class NestTypeInDTO {

        private String accountId;

        /**
         * 基站型号 字典
         */
        private Integer nestType;

        /**
         * 巡检半径 单位m
         */
        private BigDecimal patrolRadius;
    }

    @Data
    public static class AdminNestTypeCountOutDto {
        private Integer nestCount= Integer.valueOf(0);
        private Integer uavCount= Integer.valueOf(0);
        private List<AdminNestTypeMapOutDTO> infos;
    }

    @Data
    public static class AdminNestTypeMapOutDTO {
        private String uavType;
        private Integer value;
    }
}
