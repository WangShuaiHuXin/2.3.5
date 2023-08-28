package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录请求信息
 * @author Vastfy
 * @date 2022/5/13 17:55
 * @since 1.0.0
 */
@Data
public class SignInInDO implements Serializable {

    private String username;

    private String password;

}
