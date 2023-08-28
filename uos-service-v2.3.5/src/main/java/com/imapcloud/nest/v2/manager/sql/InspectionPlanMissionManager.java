package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.dao.po.out.NhOrderPlanMisOutPO;

import java.util.List;
import java.util.Map;

public interface InspectionPlanMissionManager {
    Map<Integer, List<NhOrderPlanMisOutPO>> queryMissionIds(List<Integer> ids);
}
