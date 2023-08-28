package com.imapcloud.nest.v2.service.dto.out;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class AIAutoDiscernSettingsOutDTO {
    private boolean enabled;

    private String lastModifierId;

    private String lastModifierName;

    private LocalDateTime lastModifiedTime;
}
