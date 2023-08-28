package com.imapcloud.nest.utils.mongo.pojo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class AppAirMsgEntity {
    private String deviceId;
    private Integer missionRecordsId;
    private LocalDateTime saveDateTime;
    private Object msg;
}
