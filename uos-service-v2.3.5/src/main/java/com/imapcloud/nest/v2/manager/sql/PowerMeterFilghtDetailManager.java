package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.in.PowerHomeAlarmStatisticsInDO;
import com.imapcloud.nest.v2.service.dto.out.MeterDataDetailInfoOutDTO;

import java.util.List;

public interface PowerMeterFilghtDetailManager {
    List<MeterDataDetailInfoOutDTO> queryByCondition(PowerHomeAlarmStatisticsInDO build);
}
