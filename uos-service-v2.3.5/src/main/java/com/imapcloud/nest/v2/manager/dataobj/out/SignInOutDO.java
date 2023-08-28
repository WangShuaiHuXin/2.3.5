package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录输出信息
 * @author Vastfy
 * @date 2022/5/13 17:55
 * @since 1.0.0
 */
@Data
public class SignInOutDO implements Serializable {

    /**
     * 用户token
     */
    private String token;

    /**
     * 密码强度是否较弱
     */
    private Boolean isWeak;

}
