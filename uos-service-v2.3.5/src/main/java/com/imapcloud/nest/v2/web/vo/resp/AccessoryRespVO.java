package com.imapcloud.nest.v2.web.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.checkerframework.checker.formatter.qual.Format;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AccessoryRespVO implements Serializable {
    @Data
    public static class AccessoryAuthStatusRespVO {
        /**
         * 认证是否可用
         */
        private Boolean isLteaAvailable;
        /**
         * 是否已认证
         */
        private Boolean isLteaAuthenticated;
        /**
         * 地区编码
         */
        private String lteaAreaCode;

        /**
         * 电话号码
         */
        private String lteaPhoneNumber;

        /**
         * 上一次认证时间
         */
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime lteaAuthenticatedTime;

        /**
         * 认证剩余时间
         */
        private String lteaRemainingTime;

    }

    @Data
    public static class AccessoryTransmissionRespVO {

        /**
         * 图传增强设置状态
         */
        private Boolean enable;
    }
}
