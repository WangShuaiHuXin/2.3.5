package com.imapcloud.nest.utils.mongo.pojo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 基站和无人机的信息
 *
 * @Author: wmin
 * @Date: 2021/3/22 14:13
 */
@Data
public class NestAndAirEntity {
    private String nestUuid;
    private Integer missionRecordsId;
    private Integer nestType;
    private LocalDateTime saveDateTime;
    private Map<String, Object> msg;
}
