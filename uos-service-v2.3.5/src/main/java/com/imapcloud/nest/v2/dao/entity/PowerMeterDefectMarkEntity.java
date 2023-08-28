package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 缺陷识别标注
 *
 * @author boluo
 * @date 2023-03-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("power_meter_defect_mark")
public class PowerMeterDefectMarkEntity extends GenericEntity {

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
     * 裁剪宽度
     */
    private Double cutWidth;

    /**
     * 裁剪高度
     */
    private Double cutHeight;

    /**
     * 放大缩小比例
     */
    private Double picScale;
}
