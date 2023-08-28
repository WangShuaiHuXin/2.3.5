package com.imapcloud.nest.pojo.dto.autoMissionQueueDTO;

import com.alibaba.fastjson.annotation.JSONField;
import com.imapcloud.nest.common.netty.service.BeforeStartCheckService;
import com.imapcloud.nest.enums.WebSocketTopicEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class AutoTaskCountDownDTO {
    private Integer type;
    private Integer countDown;
    private String missionName;
    private String nestId;
    private String nestUuid;
    private String nestName;
    private String title;
    private String account;
    private List<String> problems;
    @JSONField(serialize = false)
    private BeforeStartCheckService.CheckRes checkRes;
    private Integer planRecordId;
    private WebSocketTopicEnum webSocketTopicEnum;
    private boolean planAuto = false;
}
