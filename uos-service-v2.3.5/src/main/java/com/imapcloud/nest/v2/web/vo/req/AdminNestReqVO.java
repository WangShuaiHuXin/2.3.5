package com.imapcloud.nest.v2.web.vo.req;

import com.geoai.common.core.bean.PageInfo;
import com.imapcloud.nest.common.annotation.LimitVal;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理鸟巢请求签证官
 *
 * @author boluo
 * @date 2022-08-19
 */
@ToString
public class AdminNestReqVO {
    private AdminNestReqVO() {

    }

    @Data
    public static class ListReqVO extends PageInfo implements Serializable {

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
    public static class NestBaseReqVO implements Serializable {

        /**
         * 基站业务ID
         */
        @ApiModelProperty(value = "基站ID", position = 1, example = "")
        private String nestId;

        /**
         * 机巢名称
         */
        @ApiModelProperty(value = "基站名称", position = 2, required = true, example = "")
        @NotBlank(message = "{geoai_uos_cannot_empty_nest_name}")
        @Size(min = 2, max = 32, message = "{geoai_uos_limited_2_16_nest_name}")
        private String name;

        /**
         * 单位ID
         */
        @ApiModelProperty(value = "所属单位", position = 3, required = true, example = "")
        @NotNull(message = "{geoai_uos_cannot_empty_affiliation_unit}")
        private List<String> deleteOrgCodeList;

        @ApiModelProperty(value = "所属单位", position = 3, required = true, example = "")
        @NotNull(message = "{geoai_uos_cannot_empty_affiliation_unit}")
        private List<String> addOrgCodeList;

        /**
         * 区域id
         */
        @ApiModelProperty(value = "所属区域", position = 4, required = true, example = "")
        @NotBlank(message = "{geoai_uos_cannot_empty_region}")
        private String regionId;

        /**
         * 海拔
         */
        @ApiModelProperty(value = "基站海拔", position = 5, required = true, example = "")
        private BigDecimal altitude;

        /**
         * 经度
         */
        @ApiModelProperty(value = "基站经度", position = 6, required = true, example = "")
        private BigDecimal longitude;

        /**
         * 纬度
         */
        @ApiModelProperty(value = "基站纬度", position = 7, required = true, example = "")
        private BigDecimal latitude;

        /**
         * 对地高度
         */
        @ApiModelProperty(value = "基站对地高度", position = 8, required = true, example = "")
        private BigDecimal aglAltitude;

        /**
         * 部署时间
         */
        @ApiModelProperty(value = "部署时间", position = 9, required = true, example = "")
        @NotNull(message = "{geoai_uos_deployment_time_cannot_be_empty}")
        private LocalDateTime deployTime;

        /**
         * 机巢地址，例如，广东省韶关市芙蓉变电站
         */
        @ApiModelProperty(value = "详细地址", position = 10, example = "")
        @Size(max = 200, message = "{geoai_uos_address_details_are_limited_to_200_characters}")
        private String address;
    }

    @Data
    public static class NestNestReqVO implements Serializable {

        @ApiModelProperty(value = "基站业务ID", position = 1, example = "")
        @NotBlank(message = "{geoai_uos_base_station_service_ID_cannot_be_empty}")
        private String nestId;

        @ApiModelProperty(value = "基站ID", position = 2, example = "")
        @NotBlank(message = "{geoai_uos_base_station_ID_cannot_be_empty}")
        private String uuid;

        @ApiModelProperty(value = "基站型号", position = 3, example = "")
        @NotBlank(message = "{geoai_uos_base_station_model_cannot_be_empty}")
        private String type;

        @ApiModelProperty(value = "基站编号", position = 4, example = "")
        @NotBlank(message = "{geoai_uos_base_station_number_cannot_be_empty}")
        private String number;

        @ApiModelProperty(value = "连接地址", position = 5, example = "")
        @NotBlank(message = "{geoai_uos_connection_address_cannot_be_empty}")
        private String mqttBrokerId;
    }

    @Data
    public static class NestUavReqVO implements Serializable {

        @ApiModelProperty(value = "基站业务ID", position = 1, example = "")
        @NotBlank(message = "{geoai_uos_base_station_service_ID_cannot_be_empty}")
        private String nestId;

        @ApiModelProperty(value = "无人机ID", position = 2, example = "")
        private String uavId;

        @ApiModelProperty(value = "无人机型号", position = 3, example = "")
        @NotBlank(message = "{geoai_uos_drone_model_cannot_be_empty}")
        private String type;

        @ApiModelProperty(value = "相机型号", position = 4, example = "")
        @NotBlank(message = "{geoai_uos_camera_model_cannot_be_empty}")
        private String cameraName;

        @ApiModelProperty(value = "无人机序列号", position = 5, example = "")
        private String uavNumber;

        @ApiModelProperty(value = "遥控器序列号", position = 6, example = "")
        private String rcNumber;

        @ApiModelProperty(value = "RTK过期时间的状态", position = 7, example = "")
//        @NotNull(message = "{geoai_uos_rtk_expiration_time_status_can_not_be_empty}")
        private Boolean rtkEnable;

        @ApiModelProperty(value = "RTK过期时间", position = 8, example = "")
        private LocalDate expireTime;

        @ApiModelProperty(value = "图传播放地址", position = 9, example = "")
        //@NotBlank(message = "{geoai_uos_map_dissemination_address_cannot_be_empty}")
        private String uavPullUrl;

        @ApiModelProperty(value = "图传推流地址", position = 10, example = "")
        private String uavPushUrl;

        @ApiModelProperty(value = "streamId")
        private String streamId;

        @ApiModelProperty(value = "传感器", position = 10, example = "")
        private List<String> sensorIdList;

        /**
         * 无人机标识
         */
        @ApiModelProperty(value = "无人机标识-0，1，2，3", position = 10, example = "0")
        private Integer uavWhich;

        @ApiModelProperty(value = "登记码", position = 11, example = "2052147410")
        private String registerCode;

        @ApiModelProperty(value = "起飞全重", position = 12, example = "25")
        private Double takeoffWeight;

        /*民航局优化版本新增字段*/
        @ApiModelProperty(value = "中科天网-生产厂家名称", position = 13, example = "深圳市大疆创新科技有限公司")
        private Integer uavPro;

        @ApiModelProperty(value = "中科天网-飞行器名称", position = 14, example = "1")
        private Integer uavName;

        @ApiModelProperty(value = "中科天网-飞行器类别", position = 15, example = "1")
        private Integer uavType;

        @ApiModelProperty(value = "中科天网-飞行器类型", position = 16, example = "1")
        private Integer uavPattern;

        @ApiModelProperty(value = "中科天网-飞行器序列号", position = 16, example = "1")
        private String uavSn;
    }

    @Data
    public static class NestDeviceReqVO implements Serializable {

        @ApiModelProperty(value = "基站业务ID", position = 1, example = "")
        @NotBlank(message = "{geoai_uos_base_station_service_ID_cannot_be_empty}")
        private String nestId;

        @ApiModelProperty(value = "巢内监控播放地址", position = 2, example = "")
        private String innerPullUrl;

        @ApiModelProperty(value = "巢内监控mac地址", position = 3, example = "")
        private String innerMac;

        @ApiModelProperty(value = "巢外监控播放地址", position = 4, example = "")
        private String outerPullUrl;

        @ApiModelProperty(value = "巢外监控mac地址", position = 5, example = "")
        private String outerMac;

        @ApiModelProperty(value = "巢外监控推流地址", position = 6, example = "")
        private String outerPushUrl;
    }

    @Data
    public static class UavPushUrlReqVO implements Serializable {
        @ApiModelProperty(value = "基站业务ID", position = 1, example = "")
        @NotBlank(message = "{geoai_uos_base_station_service_ID_cannot_be_empty}")
        private String nestId;

        @ApiModelProperty(value = "无人机标识", position = 2, example = "0")
        private Integer uavWhich;
    }

    @Data
    public static class DjiUavPushUrlReqVO implements Serializable {

        /**
         * 基站id
         */
        @NotBlank(message = "{geoai_uos_base_station_service_ID_cannot_be_empty}")
        private String nestId;

        /**
         * 无人机sn
         */
        private String uavSn;

        /**
         * 1 - 基站sn
         * 2 - 无人机sn
         * //@LimitVal(values = {"1", "2"}, message = "sn取值不正确")
         */
        private Integer snType;

        private String serverId;
    }

    @Data
    public static class GetUavPushUrlReqVO implements Serializable {

        /**
         * uavId
         */
        @ApiModelProperty(value = "uavId" , position = 1, required = false , example = "")
        private String uavId;

        /**
         * uavSn
         */
        @ApiModelProperty(value = "uavSn" , position = 1, required = true , example = "")
        private String uavSn;
    }

    @Data
    public static class SetUavPushUrlReqVO implements Serializable {
        @ApiModelProperty(value = "基站业务ID", position = 1, example = "")
        @NotBlank(message = "{geoai_uos_base_station_service_ID_cannot_be_empty}")
        private String nestId;

        @ApiModelProperty(value = "推流地址", position = 2, example = "")
        @NotBlank(message = "{geoai_uos_push_stream_address_cannot_be_empty}")
        private String pushUrl;

        @ApiModelProperty(value = "无人机标识", position = 3, example = "0")
        private Integer uavWhich;
    }

    @Data
    public static class SetDeviceReqVO implements Serializable {

        @ApiModelProperty(value = "推流地址", position = 1, example = "")
        @NotBlank(message = "{geoai_uos_push_stream_address_cannot_be_empty}")
        private String pushUrl;

        /**
         * 接入账号
         */
        @ApiModelProperty(value = "登录用户名", position = 2, example = "")
        @NotBlank(message = "{geoai_uos_login_user_name_cannot_be_empty}")
        private String accessKey;

        /**
         * 接入密钥
         */
        @ApiModelProperty(value = "登录密码", position = 3, example = "")
        @NotBlank(message = "{geoai_uos_login_password_cannot_be_empty}")
        private String accessSecret;

        /**
         * 设备地址
         */
        @ApiModelProperty(value = "摄像头IP地址", position = 4, example = "")
        @NotBlank(message = "{geoai_uos_camera_IP_address_cannot_be_empty}")
        private String deviceDomain;

        /**
         * 是否推流
         */
        @ApiModelProperty(value = "推流功能开关", position = 5, example = "")
        @NotNull(message = "{geoai_uos_push_function_switch_cannot_be_empty}")
        private Boolean videoEnable;

        /**
         * true：巢内 false：巢外
         */
        @ApiModelProperty(value = "巢内巢外类型", position = 6, example = "")
        @NotNull(message = "{geoai_uos_type_of_nest_inside_and_outside_cannot_be_empty}")
        private Boolean nestInner;
    }

    @Data
    public static class BatteryReqVO implements Serializable {

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
    public static class NestTypeReqVO {

        /**
         * 基站型号 字典
         */
        @NotNull(message = "param nestType is null")
        private Integer nestType;

        /**
         * 巡检半径 单位m
         */
        @NotNull(message = "param patrolRadius is null")
        private BigDecimal patrolRadius;
    }
}
