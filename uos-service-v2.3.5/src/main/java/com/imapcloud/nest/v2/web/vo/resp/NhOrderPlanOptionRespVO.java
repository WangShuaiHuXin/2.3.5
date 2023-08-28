package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;

import java.io.Serializable;

@Data
public class NhOrderPlanOptionRespVO implements Serializable {

    private Integer planId;

    private String planName;

    private String baseNestId;
}
