package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;

import java.io.Serializable;

@Data
public class PowerInspectionExportReqVO implements Serializable {
    private String ids;
    private String items;

}
