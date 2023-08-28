package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.io.Serializable;

/**
 * AI视频流退出请求信息
 * @author Vastfy
 * @date 2022/12/24 17:38
 * @since 2.1.7
 */
@Data
public class AiStreamingExitInDTO implements Serializable {

    private String nestId;

    private Integer which;

}
