package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 表计数据信息
 *
 * @author Vastfy
 * @date 2022/11/29 15:38
 * @since 2.1.5
 */
@Data
public class PowerMeterFlightDataInDTO {

    /**
     * 飞行任务ID
     */
    private Long taskId;

    /**
     * 飞行任务名称（冗余）
     */
    private String taskName;

    /**
     * 架次ID
     */
    private Long missionId;

    /**
     * 架次顺序号
     */
    private Long missionSeqId;

    /**
     * 任务架次记录id
     */
    private Long missionRecordId;

    /**
     * 架次飞行次数
     */
    private Integer flyIndex;

    /**
     * 架次飞行时间
     */
    private LocalDateTime flightTime;

    /**
     * 基站ID
     */
    private String nestId;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 标签ID（冗余）
     */
    private Long tagId;

    /**
     * 标签名称（冗余）
     */
    private String tagName;

    /**
     * 识别类型
     */
    private Integer idenValue;
    /**
     * 飞行数据详情信息列表
     */
    private List<MeterFlightDetailInfo> detailInfos;

    /**
     * 红外测温
     */
    private List<MeterFlightDetailInfraredDTO> detailInfraredDTOList;

    /**
     * 缺陷识别
     */
    private List<MeterFlightDetailDefectInfo> meterFlightDetailDefectInfoList;

    @Data
    public static class MeterFlightDetailInfo extends DetailInfo {

        /**
         * 飞行数据原图URL
         */
        private String originalPicUrl;
    }

    @Data
    public static class MeterFlightDetailDefectInfo extends DetailInfo {

        /**
         * 飞行数据原图URL
         */
        private String pictureUrl;

        private String thumbnailUrl;
    }

    @Data
    public static class DetailInfo {
        /**
         * 飞行数据图片ID
         */
        private Long photoId;

        /**
         * 图片名字
         */
        private String photoName;

        /**
         * 拍摄时间
         */
        private LocalDateTime shootingTime;

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
    }

    @Data
    public static class MeterFlightDetailInfraredDTO {
        /**
         * 飞行数据详情id（业务主键）
         */
        private String detailId;

        /**
         * 详情名称
         */
        private String detailName;

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

}
