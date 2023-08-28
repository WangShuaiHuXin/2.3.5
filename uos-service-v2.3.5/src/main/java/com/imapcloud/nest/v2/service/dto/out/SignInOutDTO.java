package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求信息
 * @author Vastfy
 * @date 2022/5/13 17:55
 * @since 1.0.0
 */
@Data
public class SignInOutDTO implements Serializable {

    /**
     * 用户登录成功后token信息
     */
    private String token;

    /**
     * 是否弱密码
     */
    private Boolean isWeak;

}
