package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

/**
 * 标注合并
 *
 * @author boluo
 * @date 2022-10-11
 */
@Data
public class DataAnalysisMarkMergeOutDO {

    /**
     * 问题结果分组id
     */
    private String resultGroupId;

    /**
     * 标注id
     */
    private Long markId;
}
