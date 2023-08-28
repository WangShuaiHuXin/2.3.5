package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;

import java.io.Serializable;

@Data
public class SysOrgParamRespVO implements Serializable {

    private String coverageArea;
    private String inspectPoint;
    private Integer pageType;
}
