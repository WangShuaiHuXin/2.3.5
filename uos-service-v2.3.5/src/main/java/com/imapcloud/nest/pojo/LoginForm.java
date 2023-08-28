package com.imapcloud.nest.pojo;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author wmin
 * 登录表单实体
 */
@Data
public class LoginForm {

    /**
     * 密码登录时：用户名、手机号
     */
    private String account;

    /**
     * 密码（密文）
     */
    private String password;

    /**
     * 验证码登录时，手机号参数
     *
     * @deprecated at 2022/05/23
     */
    @Deprecated
    private String phone;

    /**
     * 验证码登录时，验证码参数
     *
     * @deprecated at 2022/05/23
     */
    @Deprecated
    private String checkCode;

    /**
     * 固定token登录，用于第三方免登录
     */
    private String fixToken;

    @NotNull(message = "{geoai_uos_wrong_login_type}")
    @Min(value = 1, message = "{geoai_uos_only_support_account_or_fixed_login}")
    @Max(value = 2, message = "{geoai_uos_only_support_account_or_fixed_login}")
    private Integer type;

    /**
     * 登录平台
     *
     * @deprecated at 2022/05/23
     */
    @Deprecated
    private Integer platform;

}
