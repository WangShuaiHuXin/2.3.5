package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.io.Serializable;

/**
 * 账号修改信息
 * @author Vastfy
 * @date 2022/5/18 17:35
 * @since 1.0.0
 */
@Data
public class AccountInfoModificationInDTO implements Serializable {

    private String realName;

    private String mobile;

}
