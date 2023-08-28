package com.imapcloud.nest.common.netty.ws;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UriParam {
    /**
     * 连接账号
     */
    private String account;
    /**
     * 客户端连接类型
     */
    private String type;

    /**
     * 基站uuid
     */
    private String uuid;

    private String token;
}
