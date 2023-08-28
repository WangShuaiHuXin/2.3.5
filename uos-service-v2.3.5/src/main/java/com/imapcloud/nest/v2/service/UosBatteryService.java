package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.out.BatteryEnableOutDTO;
import com.imapcloud.nest.v2.service.dto.out.BatteryOutDTO;

import java.util.List;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName UosBatteryService.java
 * @Description UosBatteryService
 * @createTime 2022年08月19日 10:03:00
 */
public interface UosBatteryService {
    /**
     * 停用、启用电池组
     * @param nestId
     * @param batteryGroupId
     * @param enable
     * @return
     */
    Boolean enableBatteryGroup(String nestId,  Integer batteryGroupId, Integer enable);

    /**
     * 获取电池组的停用、启用状态
     * @param nestId
     * @return
     */
    List<BatteryEnableOutDTO> getBatteryGroupEnable(String nestId);


    /**
     * 获取
     * @param nestId
     * @return
     */
    List<BatteryOutDTO> getBatteryUseNums(String nestId);

}
