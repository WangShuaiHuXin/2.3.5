package com.imapcloud.nest.pojo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author wmin
 */

@Data
public class RegisterForm {
    @NotBlank(message = "{geoai_uos_cannot_empty_username}")
    private String account;
    @NotBlank(message = "{geoai_uos_cannot_empty_password}")
    private String password;
    @NotBlank(message = "{geoai_uos_cell_phone_number_cannot_be_empty}")
    private String mobile;
    @NotBlank(message = "{geoai_uos_user_name_cannot_be_empty}")
    private String name;
}
