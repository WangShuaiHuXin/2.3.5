package com.imapcloud.nest.v2.service.dto.out;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
public class UosNestDeviceRefOutDTO {

    private String nestId;

    private String deviceId;

    private Integer deviceUse;

    private String channelId;

}
