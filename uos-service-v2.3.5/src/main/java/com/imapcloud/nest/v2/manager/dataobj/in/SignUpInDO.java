package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 注册请求信息
 * @author Vastfy
 * @date 2022/5/18 14:35
 * @since 1.0.0
 */
@Data
public class SignUpInDO implements Serializable {
    private String sourceType;
    private String username;

    private String password;

    private String mobile;

    private String realName;

    private String orgCode;

    private List<String> roleIds;

}
