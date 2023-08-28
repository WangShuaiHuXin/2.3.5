package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DataInterestPointOutDTO.java
 * @Description DataInterestPointOutDTO
 * @createTime 2022年09月16日 11:30:00
 */
@Data
public class DataInterestPointOutDTO implements Serializable {

    /**
     * 兴趣点主键
     */
    private String pointId;

    /**
     * 兴趣点名称
     */
    private String pointName;

    /**
     * 兴趣点高度
     */
    private BigDecimal pointHeight;

    /**
     * 兴趣点经度
     */
    private BigDecimal pointLongitude;

    /**
     * 兴趣点纬度
     */
    private BigDecimal pointLatitude;

    /**
     * 兴趣点类型
     */
    private Integer pointType;

    /**
     * 全景点地址信息
     */
    private String address;

    /**
     * 简介
     */
    private String brief;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 基站id
     */
    private String baseNestId;

    /**
     * 兴趣点地图范围
     */
    private Integer mapDistance;

    /**
     * 兴趣的全景范围
     */
    private Integer panoramaDistance;

    /**
     * 标签
     */
    private String tagId;
}