package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.io.Serializable;

/**
 * AI流开启信息
 * @author Vastfy
 * @date 2022/12/26 11:57
 * @since 2.1.7
 */
@Data
public class AIStreamingOpenInDO implements Serializable {

    private String functionId;

    private String aiStreamPullUrl;

    private String aiStreamPushUrl;

    private Long taskTimeoutMills;

}
