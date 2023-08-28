package com.imapcloud.nest.pojo;

import com.imapcloud.nest.utils.mongo.pojo.MqttLogEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain = true)
@Data
public class ParseMqttRes {
    private List<MqttLogEntity> logList;
    private String md5;
    private String path;
}
