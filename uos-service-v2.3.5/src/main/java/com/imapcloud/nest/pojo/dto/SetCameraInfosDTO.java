package com.imapcloud.nest.pojo.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class SetCameraInfosDTO {

    @NotNull
    private String nestId;

    @NotNull
    private String ip;

    @Length(max = 50)
    @NotNull
    private String username;

    @Length(max = 50)
    @NotNull
    private String password;

    @Length(max = 255)
    @NotNull
    private String rtmpUrl;

    @NotNull
    private Boolean enable;
}
