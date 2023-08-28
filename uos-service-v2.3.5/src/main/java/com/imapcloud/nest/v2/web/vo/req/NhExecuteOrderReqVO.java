package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;

import java.io.Serializable;

@Data
public class NhExecuteOrderReqVO implements Serializable {


    private int status;

    private String remark;

}
