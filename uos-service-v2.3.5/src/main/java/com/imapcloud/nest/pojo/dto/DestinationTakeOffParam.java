package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.common.annotation.NestId;
import com.imapcloud.sdk.manager.icrest.entity.DestinationParam;
import lombok.Data;

@Data
public class DestinationTakeOffParam {
    @NestId
    private String nestId;
    private DestinationParam destinationParam;
}
