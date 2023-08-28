package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;

/**
 * AI视频流识别信息
 * @author Vastfy
 * @date 2022/12/24 16:38
 * @since 2.1.7
 */
@Data
public class NestAiStreamingInfoOutDTO implements Serializable {

    private String nestId;

    private Integer which;

    private String missionRecordId;

    private String functionId;

    private boolean enableAlarm;

    /**
     * UDA视频AI任务ID
     */
    private String processId;

    private String aiStreamPullUrl;

    private String aiStreamPushUrl;

    /**
     * 操作人ID
     */
    private String accountId;

    /**
     * 操作人用户名
     */
    private String username;

    /**
     * 操作人单位
     */
    private String orgCode;

}
