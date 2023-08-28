package com.imapcloud.nest.v2.service.dto.in;

import com.imapcloud.nest.enums.NestFlowTypeEnum;
import com.imapcloud.nest.enums.NestShowStatusEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NestFlowUrlInDTO {
    private List<String> nestIdList;
    private Integer showStatus;
    private NestFlowTypeEnum nestFlowTypeEnum;
}
