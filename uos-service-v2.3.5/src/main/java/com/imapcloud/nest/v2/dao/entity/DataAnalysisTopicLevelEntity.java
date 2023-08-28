package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 分析统计-专题级别表
 *
 * @author boluo
 * @date 2022-07-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("data_analysis_topic_level")
public class DataAnalysisTopicLevelEntity extends GenericEntity {

    /**
     * 专题级别id
     */
    private Long topicLevelId;

    /**
     * 专题级别名称
     */
    private String topicLevelName;

    /**
     * 专题级别code
     */
    private String topicLevelCode;

    /**
     * 专题key
     */
    private String topicKey;
}
