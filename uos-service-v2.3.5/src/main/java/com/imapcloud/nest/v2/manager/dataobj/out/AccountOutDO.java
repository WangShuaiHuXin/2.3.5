package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.io.Serializable;

/**
 * 账号响应视图对象示例
 * @author Vastfy
 * @date 2022/5/20 10:12
 * @since 1.0.0
 */
@Data
public class AccountOutDO implements Serializable {

    private String accountId;

    private String account;

    private String name;

    private String mobile;

    private String email;

    private Integer status;

    private String orgCode;

    private String orgName;

}
