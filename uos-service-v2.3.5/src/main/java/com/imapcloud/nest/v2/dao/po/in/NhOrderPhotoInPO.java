package com.imapcloud.nest.v2.dao.po.in;

import lombok.Data;

import javax.validation.constraints.NotNull;
@Data
public class NhOrderPhotoInPO {
    @NotNull(message = "photoRecordId cannot be null")
    private Integer recordId;
    private Long pageSize;
    private Long pageNo;
    /**
     *  字典  GEOAI_LEN_TYPE
     */
    private Integer lenType;


}
