package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.io.Serializable;

/**
 * 账号修改信息
 * @author Vastfy
 * @date 2022/5/18 17:35
 * @since 1.0.0
 */
@Data
public class AccountInfoModificationInDO implements Serializable {

    private String name;

    private String mobile;

}
