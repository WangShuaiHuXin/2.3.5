package com.imapcloud.sdk.pojo.djido;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DjiConfigDO {
    private String ntpServerHost ;
    private String appId;
    private String appKey;
    private String appLicense;
    private String environment;


}

