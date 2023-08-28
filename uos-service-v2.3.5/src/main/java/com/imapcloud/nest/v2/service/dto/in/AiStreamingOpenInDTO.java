package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.io.Serializable;

/**
 * AI视频流开启请求信息
 * @author Vastfy
 * @date 2022/12/24 17:38
 * @since 2.1.7
 */
@Data
public class AiStreamingOpenInDTO implements Serializable {

    private String nestId;

    private Integer which;

    private String missionRecordId;

    private String functionId;

    private boolean enableAlarm;

}
