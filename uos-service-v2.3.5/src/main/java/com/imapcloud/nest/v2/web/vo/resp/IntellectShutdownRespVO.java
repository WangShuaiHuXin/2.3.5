package com.imapcloud.nest.v2.web.vo.resp;

import lombok.Data;

import java.io.Serializable;

@Data
public class IntellectShutdownRespVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 开启/关闭低电量智能
     */
    private Boolean enable;
    /**
     * 电量值（百分比）
     */
    private Integer threshold;
}
