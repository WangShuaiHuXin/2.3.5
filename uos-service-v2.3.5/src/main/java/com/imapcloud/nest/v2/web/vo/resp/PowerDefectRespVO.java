package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 缺陷识别
 *
 * @author boluo
 * @date 2023-03-08
 */
@ToString
public final class PowerDefectRespVO {

    private PowerDefectRespVO() {}

    @Data
    public static class ListRespVO implements Serializable {

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
         * 缩略图url
         */
        private String thumbnailUrl;

        /**
         * 设备状态【取字典 geoai_dial_device_state 数据项值】
         */
        private int deviceState;

        /**
         * 缺陷识别状态
         */
        private int defectState;

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
         * 电力pms_id
         */
        private String pmsId;

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

        /**
         * 设备名称
         */
        private String equipmentName;
    }

    @Data
    public static class DetailRespVO implements Serializable {

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
         * 缩略图url
         */
        private String thumbnailUrl;

        /**
         * 设备状态【取字典 geoai_dial_device_state 数据项值】
         */
        private int deviceState;

        /**
         * 缺陷识别状态
         */
        private int defectState;

        /**
         * 核实状态 【去字典 ‘【geoai_verification_ state】’】
         */
        private int verificationState;

        /**
         * 任务状态
         */
        private int taskState;

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
         * 电力pms_id
         */
        private String pmsId;

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

        /**
         * 设备名称
         */
        private String equipmentName;

        private List<DefectMarkInfoRespVO> defectMarkInfoList;
    }

    @Data
    public static class DefectMarkInfoRespVO implements Serializable {

        /**
         * 标注id
         */
        private String defectMarkId;

        /**
         * 飞行数据详情id（业务主键）
         */
        private String detailId;

        /**
         * 坐标x1
         */
        private BigDecimal siteX1;

        /**
         * 坐标y1
         */
        private BigDecimal siteY1;

        /**
         * 坐标x2
         */
        private BigDecimal siteX2;

        /**
         * 坐标y2
         */
        private BigDecimal siteY2;

        /**
         * 是否ai标注，0-否，1-是
         */
        private int aiMark;

        /**
         * 设备状态【取字典 geoai_dial_device_state 数据项值】
         */
        private int deviceState;

        /**
         * 行业类型【取字典 geoai_industry_type 值】
         */
        private Integer industryType;

        /**
         * 问题类型
         */
        private String topicProblemId;

        /**
         * 问题类型名称，ai识别没有匹配时，显示ai问题名称
         */
        private String topicProblemName;

        /**
         * 裁剪高度
         */
        private Double relX;

        /**
         * 裁剪寬度
         */
        private Double relY;

        /**
         * 放大縮小比例
         */
        private Double picScale;
    }
}
