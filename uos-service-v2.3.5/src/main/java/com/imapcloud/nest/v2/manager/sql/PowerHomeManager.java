package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.manager.dataobj.in.PowerHomeBaseSettingInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerEquipmentInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerHomeBaseSettingInfoOutDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerInspectionReportOutDO;
import com.imapcloud.nest.v2.service.dto.in.InspectionStatisticsInDTO;
import com.imapcloud.nest.v2.web.vo.req.PowerHomeInspectionQueryByReqVO;

import java.util.List;

public interface PowerHomeManager {
    PowerHomeBaseSettingInfoOutDO queryByOrg(String orgCode);

    PowerInspectionReportOutDO inspectionEquipmentList(String orgCode, Integer pageNo, Integer pageSize,String equipmentType);

    List<PowerEquipmentInfoOutDO> equipmentTree(String orgCode);

    PowerInspectionReportOutDO inspectionQueryBy(PowerHomeInspectionQueryByReqVO vo);

    boolean saveStatisticsOne(PowerHomeBaseSettingInDO build);

    boolean updateStatisticsOne(PowerHomeBaseSettingInDO build);
}
