package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据分析主题产业实体
 *
 * @author boluo
 * @date 2022-07-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("data_analysis_topic_industry")
public class DataAnalysisTopicIndustryEntity extends GenericEntity {

    /**
     * 专题行业id
     * @deprecated 2.1.4，将在后续版本移除，使用industryType替换
     */
    @Deprecated
    private Long topicIndustryId;

    /**
     * 专题行业名称
     * @deprecated 2.1.4，将在后续版本移除，使用industryType替换
     */
    @Deprecated
    private String topicIndustryName;

    /**
     * 行业类型，取字典`GEOAI_INDUSTRY_TYPE`数据项项
     */
    private Integer industryType;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 专题key
     */
    private String topicKey;

    /**
     * 排序号
     */
    private Integer seq;

}
