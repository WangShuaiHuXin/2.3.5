package com.imapcloud.nest.v2.service.dto.out;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NestFlowUrlOutDTO {
    private String nestId;
    private String nestName;
    private String nestUuid;
    private Integer type;
    /**
     * @deprecated 2.3.2
     */
    @Deprecated
    private String innerStreamUrl;
    /**
     * @deprecated 2.3.2
     */
    @Deprecated
    private String outerStreamUrl;
    /**
     * @deprecated 2.3.2
     */
    @Deprecated
    private String uavStreamUrl;
}
