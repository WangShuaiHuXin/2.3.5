package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class NhOrderDataPhotoReqVO implements Serializable {


    @NotNull(message = "photoRecordId cannot be null")
    private Integer recordId;
    @NotNull(message = "pageSize cannot be null")
    private Long pageSize;
    @NotNull(message = "pageNum cannot be null")
    private Long pageNo;

    /**
     *  字典  GEOAI_LEN_TYPE
     */
    private Integer lenType;
}
