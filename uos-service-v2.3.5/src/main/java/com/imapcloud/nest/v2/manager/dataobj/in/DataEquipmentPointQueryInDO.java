package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class DataEquipmentPointQueryInDO implements Serializable {

    private String pointId;

    private String keyWord;

    private String tagId;

    private Long pageSize;

    private Long pageNo;

    private String orgCode;
}
