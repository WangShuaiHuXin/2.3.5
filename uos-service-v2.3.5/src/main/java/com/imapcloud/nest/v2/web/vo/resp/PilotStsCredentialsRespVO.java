package com.imapcloud.nest.v2.web.vo.resp;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sean
 * @version 0.2
 * @date 2021/12/7
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class PilotStsCredentialsRespVO {

    private String bucket;

    private CredentialsRespVO credentials;

    private String endpoint;

    private String objectKeyPrefix;

    private String provider;

    private String region;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class CredentialsRespVO {

        private static final int DELAY = 300;

        private String accessKeyId;

        private String accessKeySecret;

        private Long expire;

        private String securityToken;

    }
}
