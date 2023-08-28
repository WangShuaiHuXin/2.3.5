package com.imapcloud.sdk.pojo.djido;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class DjiCommonDO<T> {

    private String tid;
    private String bid;
    private Long timestamp;
    private String gateway;
    private String method;
    /**
     * 是否需要回复
     */
    private Integer needReply;
    private T data;
}
