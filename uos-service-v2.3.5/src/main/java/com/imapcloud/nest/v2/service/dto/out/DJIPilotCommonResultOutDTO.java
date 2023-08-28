package com.imapcloud.nest.v2.service.dto.out;

import com.imapcloud.sdk.manager.DjiTslSnParam;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName DJIPilotCommonResultOutDTO.java
 * @Description DJIPilotCommonResultOutDTO
 * @createTime 2022年07月08日 15:41:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DJIPilotCommonResultOutDTO implements Serializable {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PilotCommonResultOutDTO {
        private String appId;

        private String appSecret;

        private String appLicense;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PilotLicenseResultOutDTO {
        private String appInfo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class PilotMqttResultOutDTO {
        private String mqttBrokerId;
        private String mqttBrokerInnerUrl;
        private String username;
        private String password;
        private DjiTslSnParam djiTslSnParam;
    }


}
