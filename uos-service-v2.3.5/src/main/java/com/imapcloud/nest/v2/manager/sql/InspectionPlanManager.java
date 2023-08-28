package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.dao.po.out.NhOrderPlanOutPO;
import com.imapcloud.nest.v2.manager.dataobj.out.BaseNestAccountOutDO;

import java.util.List;

public interface InspectionPlanManager {
    NhOrderPlanOutPO queryPlans(List<Integer> ids, Long pageNum, Long pageSize, List<String> outDO);
}
