package com.imapcloud.nest.v2.manager.dataobj.in;

import com.imapcloud.nest.v2.manager.dataobj.BaseInDO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 缺陷标注
 *
 * @author boluo
 * @date 2023-03-08
 */
@Data
public class PowerMeterDefectMarkInDO extends BaseInDO {

    private String defectMarkId;

    /**
     * 飞行数据详情id（业务主键）
     */
    private String detailId;

    /**
     * 是否ai标注，0-否，1-是
     */
    private int aiMark;

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
     * 设备状态【取字典 geoai_dial_device_state 数据项值】
     */
    private Integer deviceState;

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
    private Double relY;

    /**
     * 裁剪高度
     */
    private Double relX;

    /**
     * 放大缩小比例
     */
    private Double picScale;
}
