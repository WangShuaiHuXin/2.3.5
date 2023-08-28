package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

@Data
public class AccessoryInDTO {
    @Data
    public static class AccessoryCaptchaInDTO {
        private String nestId;
        private String lteaPhoneNumber;
        private String lteaAreaCode;
        private String verificationCode;
    }
}
