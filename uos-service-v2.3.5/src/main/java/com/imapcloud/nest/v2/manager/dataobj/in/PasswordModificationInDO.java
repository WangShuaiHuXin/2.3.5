package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.io.Serializable;

/**
 * 密码修改信息
 * @author Vastfy
 * @date 2022/5/18 16:35
 * @since 1.0.0
 */
@Data
public class PasswordModificationInDO implements Serializable {

    private String oldPassword;

    private String newPassword;

}
