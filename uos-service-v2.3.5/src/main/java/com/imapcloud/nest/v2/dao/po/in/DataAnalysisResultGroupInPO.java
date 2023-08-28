package com.imapcloud.nest.v2.dao.po.in;

import com.geoai.common.mp.entity.QueryCriteriaDo;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

/**
 * @Classname DataAnalysisResultGroupInPO
 * @Description 数据分析问题统计结果分组标准PO
 * @Date 2022/10/11 11:22
 * @Author Carnival
 */
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DataAnalysisResultGroupInPO extends QueryCriteriaDo<DataAnalysisResultGroupInPO> {

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 专题key
     */
    private String topicKey;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 过滤权限
     */
    private String visibleOrgCode;

    /**
     * 专题行业类型
     */
    private Integer industryType;

    /**
     * 专题级别id
     */
    private Long topicLevelId;

    /**
     * 问题类型
     */
    private Long topicProblemId;

    /**
     * 标签名
     */
    private String tagName;
}
