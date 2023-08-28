package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;

import java.io.Serializable;

@Data
public class NestDetailsRespDataVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long nestId;
    private Long aircraftId;

}
