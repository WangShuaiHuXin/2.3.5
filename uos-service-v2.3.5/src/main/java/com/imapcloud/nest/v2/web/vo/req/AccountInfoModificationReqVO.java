package com.imapcloud.nest.v2.web.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 账号修改信息
 * @author Vastfy
 * @date 2022/5/18 17:35
 * @since 1.0.0
 */
@ApiModel("账号修改信息")
@Data
public class AccountInfoModificationReqVO implements Serializable {

    @ApiModelProperty(value = "姓名", example = "dev@geoai")
    @NotBlank(message = "{geoai_uos_cannot_empty_name}")
    private String realName;

    @ApiModelProperty(value = "账号关联手机号", example = "131xxxx8888")
    @NotBlank(message = "{geoai_uos_cannot_empty_phone_number}")
    @Pattern(regexp = "^[1]\\d{10}$", message = "{geoai_uos_wrong_format_phone_number}")
    private String mobile;

    @ApiModelProperty(value = "邮箱地址", example = "test@geoai.com")
    private String email;

}