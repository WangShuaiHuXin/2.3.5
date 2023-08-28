package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

/**
 * 数据分析算法问题类型做裁判
 *
 * @author boluo
 * @date 2023-03-09
 */
@Data
public class DataAnalysisAlgoProblemTypeRefOutDO {

    /**
     * uos问题类型ID
     *
     */
    private Long typeId;

    /**
     * uda场景ID
     */
    private String storageId;

    /**
     * uda场景名称
     */
    private String storageName;

    /**
     * uda识别功能ID
     */
    private String functionId;

    /**
     * uda识别功能名称
     */
    private String functionName;

    /**
     * uda问题类型配置ID
     */
    private String typeRelationId;

    /**
     * uda问题类型配置名称
     */
    private String typeRelationName;
}
