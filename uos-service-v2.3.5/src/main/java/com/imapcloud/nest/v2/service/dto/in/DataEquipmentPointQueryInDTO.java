package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

@Data
public class DataEquipmentPointQueryInDTO {
    private String keyWord;
    private String tagId;
    private Long pageNo;
    private Long pageSize;
    private String orgCode;
    private String pointId;
}
