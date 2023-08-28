package com.imapcloud.nest.v2.web.vo.resp;

import com.imapcloud.sdk.manager.DjiTslSnParam;
import lombok.*;

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
public class DJIPilotCommonResultRespVO implements Serializable {
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PilotCommonResultRespVO {
        private String appId;

        private String appSecret;

        private String appLicense;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PilotLicenseResultRespVO{
        private String appInfo;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PilotMqttInfoRespVO{
        private String mqttBrokerId;
        private String mqttBrokerInnerUrl;
        private String username;
        private String password;
        private DjiTslSnParam djiTslSnParam;
    }

}
