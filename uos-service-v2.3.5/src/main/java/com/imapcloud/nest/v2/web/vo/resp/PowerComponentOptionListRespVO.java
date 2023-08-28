package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;

import java.io.Serializable;

@Data
public class PowerComponentOptionListRespVO implements Serializable {
    private String componentId;
    private String componentName;
}
