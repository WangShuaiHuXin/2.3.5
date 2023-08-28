package com.imapcloud.sdk.pojo.djido;

import lombok.Data;

@Data
public class DjiCommonStrDO {
    private String tid;
    private String bid;
    private Long timestamp;
    private String gateway;
    private String method;
    /**
     * 是否需要回复
     */
    private Integer needReply;
    private String data;
}
