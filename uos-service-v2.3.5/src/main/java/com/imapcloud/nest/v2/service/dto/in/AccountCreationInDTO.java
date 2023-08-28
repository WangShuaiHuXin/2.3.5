package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 账号新建信息
 * @author Vastfy
 * @date 2022/5/18 17:35
 * @since 1.0.0
 */
@Data
public class AccountCreationInDTO implements Serializable {

    private String account;

    private String password;

    private String mobile;

    private String unitId;

    private String realName;

    private List<String> roleIds;

    private Boolean isOperation = Boolean.FALSE;

    private List<String> inchargeNestList;

}