package com.imapcloud.nest.v2.service;

import com.imapcloud.nest.v2.service.dto.in.InspectionEquipmentInDTO;
import com.imapcloud.nest.v2.service.dto.in.InspectionStatisticsInDTO;
import com.imapcloud.nest.v2.service.dto.out.*;
import com.imapcloud.nest.v2.web.vo.req.PowerHomeInspectionQueryByReqVO;
import com.imapcloud.nest.v2.web.vo.resp.PowerHomeInspectionStatisticsRespVO;

import java.util.List;

public interface PowerHomeService {
    PowerHomeSettingOutDTO queryDataStatic(String orgCode);

    List<PowerInspectionEquipmentListOutDTO> inspectionEquipmentList(InspectionEquipmentInDTO dto);

    PowerEquipmentTreeOutDTO equipmentTree(String orgCode,String keyWord);

    PowerHomeInspectionQueryByOutDTO inspectionQueryBy(PowerHomeInspectionQueryByReqVO vo);

    List<PowerHomeInspectionStatisticsOutDTO> inspcetionStatistics(String orgCode,String beginTime,String endTime);

    PowerHomeInspectionStatisticsOutDTO.PowerHomeAlarmStatisticsOutDTO alarmStatistics(String orgCode,String beginTIme,String endTime);

    List<PowerHomeEquipmentTypeInspcetionOutDTO> equipmentTypeInspectionStatistics(String orgCode,String beginTime,String endTime);

    boolean inspectionStatisticsEdit(InspectionStatisticsInDTO build);

    List<PowerHomePointQueryOutDTO> homePointQuery(String orgCode);

    List<PowerHomePointQueryByOutDTO> homePointQueryBy(String pointId, String orgCode);

    Integer homeInspectionAlarmEventsCount(String orgCode);

    List<PowerHomeInspectionAlarmEventsOutDTO> homeInspectionAlarmEvents(String orgCode);

    List<PowerHomeInspectionStatisticsOutDTO> inspcetionStatisticsDefault();
}
