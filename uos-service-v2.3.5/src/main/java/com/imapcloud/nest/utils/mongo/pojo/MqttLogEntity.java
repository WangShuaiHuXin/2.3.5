package com.imapcloud.nest.utils.mongo.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MqttLogEntity {
    private String nestUuid;
    private String logTime;
    private String traceId;
    private String nodeId;
    private Object body;
    private Long timestamp;
}
