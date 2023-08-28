package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName AudioRespVO.java
 * @Description AudioRespVO
 * @createTime 2022年08月16日 14:25:00
 */
@Data
public class DataAnalysisAlgoProblemTypeRefInDTO {

    // uos问题类型ID
    private Long typeId;

    // uda场景ID
    private String storageId;

    // uda场景名称
    private String storageName;

    // uda识别功能ID
    private String functionId;

    // uda识别功能名称
    private String functionName;

    // uda问题类型配置ID
    private String typeRelationId;

    // uda问题类型配置名称
    private String typeRelationName;
}
