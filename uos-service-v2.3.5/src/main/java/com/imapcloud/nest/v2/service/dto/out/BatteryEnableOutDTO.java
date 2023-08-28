package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName BatteryEnableRespVO.java
 * @Description BatteryEnableRespVO
 * @createTime 2022年08月19日 11:20:00
 */
@Data
@Accessors(chain = true)
public class BatteryEnableOutDTO {

    private Integer groupId;

    private Integer enable;

    private Integer layout;
}
