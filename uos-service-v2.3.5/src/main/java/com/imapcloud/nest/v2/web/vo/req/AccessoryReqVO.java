package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
public class AccessoryReqVO implements Serializable {

    @Data
    public static class AccessoryCaptchaReqVO {
        @NotNull(message = "发送LTE认证验证码,电话号码不能为空")
        @Pattern(regexp = "^[1]\\d{10}$", message = "请检查手机号是否填写正确")
        private String lteaPhoneNumber;
        private String lteaAreaCode;
        private String verificationCode;
    }

    @Data
    public static class AccessoryTransmissionReqVO {
        @NotNull(message = "设置图传状态,参数异常")
        private Boolean enable;
    }
}
