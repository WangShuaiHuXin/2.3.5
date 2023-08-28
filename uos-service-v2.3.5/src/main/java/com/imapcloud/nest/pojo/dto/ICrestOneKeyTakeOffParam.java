package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.common.annotation.NestId;
import lombok.Data;

@Data
public class ICrestOneKeyTakeOffParam {

    @NestId
    private String nestId;
    private Float height;
    private Boolean confirm;
}
