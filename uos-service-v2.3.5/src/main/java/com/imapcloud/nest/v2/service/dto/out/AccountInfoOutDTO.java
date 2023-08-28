package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 帐户信息
 *
 * @author boluo
 * @date 2022-05-24
 */
@Data
public class AccountInfoOutDTO implements Serializable {

    private String id;

    private String account;

    private String name;

    private String mobile;

    private String email;

    private Integer status;

    private String unitId;

    private String unitName;

    private List<String> roleNameList;

    private List<String> nestNameList;

}
