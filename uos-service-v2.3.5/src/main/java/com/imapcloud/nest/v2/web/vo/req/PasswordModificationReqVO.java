package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 密码修改信息
 * @author Vastfy
 * @date 2022/5/18 16:35
 * @since 1.0.0
 */
@ApiModel("密码修改信息")
@Data
public class PasswordModificationReqVO implements Serializable {

    @ApiModelProperty(value = "旧密码（RSA公钥加密后密文）", example = "a201MjAxMzE0Y3hk")
    @NotBlank(message = "{geoai_uos_cannot_old_password}")
    private String originPassword;

    @ApiModelProperty(value = "新密码（RSA公钥加密后密文）", example = "d201MjAxMzE0Y3hk")
    @NotBlank(message = "{geoai_uos_cannot_empty_new_password}")
    private String destPassword;

}
