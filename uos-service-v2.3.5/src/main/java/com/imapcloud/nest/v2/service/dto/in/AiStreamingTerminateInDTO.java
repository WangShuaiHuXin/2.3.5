package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * AI视频流退出请求信息
 * @author Vastfy
 * @date 2022/12/24 17:38
 * @since 2.1.7
 */
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class AiStreamingTerminateInDTO extends AiStreamingExitInDTO {

    private String processTaskId;

    private String orgCode;

}
