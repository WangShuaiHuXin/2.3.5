package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 账号修改信息
 * @author Vastfy
 * @date 2022/5/18 17:35
 * @since 1.0.0
 */
@Data
public class AccountModificationInDO implements Serializable {

    private String mobile;

    private String realName;

    private String orgCode;

    private List<String> incRoleIds;

    private List<String> decRoleIds;

}
