package com.imapcloud.nest.v2.service.dto.in;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

/**
 * 基站无人机信息
 *
 * @author boluo
 * @date 2022-08-23
 */
@ToString
public class BaseUavInDTO {

    private BaseUavInDTO() {

    }

    @Data
    public static class UavInDTO {

        private String accountId;

        /**
         * 基站ID
         */
        private String nestId;

        /**
         * 无人机id
         */
        private String uavId;

        /**
         * 无人机型号
         */
        private String type;

        /**
         * 相机型号
         */
        private String cameraName;

        /**
         * 无人机序列号
         */
        private String uavNumber;

        /**
         * 遥控器序列号
         */
        private String rcNumber;

        /**
         * RTK过期时间启用状态 true：启用
         */
        private Boolean rtkEnable;

        /**
         * RTK过期时间
         */
        private LocalDate expireTime;

        /**
         * 无人机拉流地址 图传播放地址
         * @deprecated 2.3.2，将在后续版本删除
         */
        @Deprecated
        private String uavPullUrl;

        /**
         * 无人机推流地址 图传推流地址
         * @deprecated 2.3.2，将在后续版本删除
         */
        @Deprecated
        private String uavPushUrl;


        private String streamId;

        /**
         * 传感器id列表
         */
        private List<String> sensorIdList;

        /**
         * 无人机标识
         */
        private Integer uavWhich;

        /**
         * 登记码
         */
        private String registerCode;

        /**
         * 起飞全重
         */
        private Double takeoffWeight;

        /***********************************中科天网***********************/
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
