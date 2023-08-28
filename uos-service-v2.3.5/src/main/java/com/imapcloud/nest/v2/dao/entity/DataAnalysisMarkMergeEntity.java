package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * data_analysis_mark_merge
 *
 * @author boluo
 * @date 2022-10-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("data_analysis_mark_merge")
public class DataAnalysisMarkMergeEntity extends GenericEntity {

    /**
     * 问题结果分组id
     */
    private String resultGroupId;

    /**
     * 标注id
     */
    private Long markId;
}
