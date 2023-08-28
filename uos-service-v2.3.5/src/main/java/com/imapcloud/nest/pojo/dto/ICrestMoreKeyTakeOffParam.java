package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.common.annotation.NestId;
import com.imapcloud.sdk.manager.icrest.entity.DistancParam;
import lombok.Data;

@Data
public class ICrestMoreKeyTakeOffParam {
    @NestId
    private String nestId;
    private DistancParam distancParam;
}
