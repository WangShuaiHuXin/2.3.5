package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.io.Serializable;

/**
 * 视频录像任务参数
 * @author Vastfy
 * @date 2023/04/25 15:12
 * @since 2.3.2
 */
@Data
public class LiveRecordingParamInDO implements Serializable {

    /**
     * 推流ID
     */
    private String pushStreamId;

    /**
     * 录像请求ID
     */
    private String requestId;

}
