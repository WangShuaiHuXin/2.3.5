package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.io.Serializable;

/**
 * 密码重置信息
 * @author Vastfy
 * @date 2022/09/02 10:35
 * @since 2.0.0
 */
@Data
public class PasswordResetInDO implements Serializable {

    private String newPwd;

}
