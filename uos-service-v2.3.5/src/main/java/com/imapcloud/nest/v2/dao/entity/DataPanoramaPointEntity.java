package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataPanoramaPointEntity.java
 * @Description DataPanoramaPointEntity
 * @createTime 2022年09月16日 11:23:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("data_panorama_point")
public class DataPanoramaPointEntity extends GenericEntity {

    /**
     * 全景点主键
     */
    private String pointId;

    /**
     * 全景点名
     */
    private String pointName;

    /**
     * 全景点高度
     */
    private BigDecimal pointHeight;

    /**
     * 全景点经度
     */
    private BigDecimal pointLongitude;

    /**
     * 全景点纬度
     */
    private BigDecimal pointLatitude;

    /**
     * 全景点类型-0自动创建、1手工创建
     */
    private Integer pointType;

    /**
     * 全景点地址信息
     */
    private String address;

    /**
     * 标签id
     */
    private String tagId;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 航线id
     */
    private String airLineId;

    /**
     * 航点id
     */
    private String airPointId;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 基站id
     */
    private String baseNestId;

}
