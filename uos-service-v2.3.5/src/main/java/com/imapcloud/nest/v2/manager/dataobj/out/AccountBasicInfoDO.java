package com.imapcloud.nest.v2.manager.dataobj.out;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 账号简要响应视图对象示例
 * @author Vastfy
 * @date 2022/4/25 15:12
 * @since 1.0.0
 */
@Data
public class AccountBasicInfoDO implements Serializable {

    private String accountId;

    private String username;

    private String accountName;

    private Boolean hasDefaultAdminRole;

    private List<String> accessibleApiResources;

}
