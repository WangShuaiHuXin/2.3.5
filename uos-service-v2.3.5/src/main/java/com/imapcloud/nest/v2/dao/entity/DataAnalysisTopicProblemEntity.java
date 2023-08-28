package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分析统计-专题行业问题表
 *
 * @author boluo
 * @date 2022-07-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("data_analysis_topic_problem")
public class DataAnalysisTopicProblemEntity extends GenericEntity {
    /**
     * 专题行业问题id
     */
    private Long topicProblemId;

    /**
     * 专题行业问题名称
     */
    private String topicProblemName;

    /**
     * 专题行业id
     * @deprecated 2.1.4，将在后续版本移除，使用industryType替换
     */
    @Deprecated
    private Long topicIndustryId;

    /**
     * 行业类型，取字典`GEOAI_INDUSTRY_TYPE`数据项项
     */
    private Integer industryType;

    /**
     * 单位id
     * @deprecated 2.0.0，使用orgCode替代
     */
    @Deprecated
    private Long orgId;

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

    /**
     * 问题类型配置，区分系统默认（-1）、单位独有（0）
     */
    private Integer source;

}
