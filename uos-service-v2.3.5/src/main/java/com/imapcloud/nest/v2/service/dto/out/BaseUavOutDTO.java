package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.ToString;

/**
 * 无人机
 *
 * @author boluo
 * @date 2022-08-25
 */
@ToString
public class BaseUavOutDTO {

    private BaseUavOutDTO() {}

    @Data
    public static class PushInfoOutDTO {

        private String pullHttp;

        private String pushRtmp;

        private String streamId;
    }
}
