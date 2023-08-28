package com.imapcloud.nest.v2.dao.po.in;

import lombok.Data;

@Data
public class AccessoryInPO {
    @Data
    public static class AccessoryCaptchaInPO {
        private String nestId;
        private String lteaPhoneNumber;
        private String lteaAreaCode;
        private String verificationCode;
    }
}
