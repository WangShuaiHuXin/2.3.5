package com.imapcloud.nest.pojo.dto.reqDto;

import lombok.Data;

import java.util.List;

@Data
public class RecordDto {
    /**
     * 识别类型
     */
    private Integer recordType;

    /**
     * 图片ids
     */
    private List<Long> photoIds;

    /**
     * 图片ids
     */
    private Integer defectStatus;

}
