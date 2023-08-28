package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求信息
 * @author Vastfy
 * @date 2022/5/13 17:55
 * @since 1.0.0
 */
@Data
public class SignInInDTO implements Serializable {

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密）
     */
    private String password;

}
