package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.Serializable;

@Data
public class DataEquipmentPointQueryReqVO implements Serializable {
    private String keyWord;
    private String tagId;
    private Long pageNo;
    private Long pageSize;
    private String pointId;
}
