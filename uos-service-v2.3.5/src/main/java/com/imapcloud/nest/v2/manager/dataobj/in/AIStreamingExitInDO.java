package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.io.Serializable;

/**
 * AI流退出信息
 * @author Vastfy
 * @date 2022/12/26 11:57
 * @since 2.1.7
 */
@Data
public class AIStreamingExitInDO implements Serializable {

    private String orgCode;

    private String processId;

}
