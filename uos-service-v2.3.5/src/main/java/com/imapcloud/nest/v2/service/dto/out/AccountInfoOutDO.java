package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 账号响应视图对象示例
 * @author Vastfy
 * @date 2022/4/25 15:12
 * @since 1.0.0
 */
@Data
public class AccountInfoOutDO implements Serializable {

    private String accountId;

    private String accountName;

    private String name;

    private String mobile;

    private String email;

    private Integer status;

}
