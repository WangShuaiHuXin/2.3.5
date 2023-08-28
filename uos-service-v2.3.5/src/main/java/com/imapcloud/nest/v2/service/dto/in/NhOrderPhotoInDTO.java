package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import javax.validation.constraints.NotNull;
@Data
public class NhOrderPhotoInDTO {
    private Integer orderId;

    private Integer planId;
    private Integer recordId;
    private Long pageSize;
    private Long pageNo;

    /**
     *  字典  GEOAI_LEN_TYPE
     */
    private Integer lenType;
}
