package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.dao.po.out.NhOrderMissionOptionOutPO;
import com.imapcloud.nest.v2.dao.po.out.NhOrderPlanOptionOutPO;
import com.imapcloud.nest.v2.service.dto.in.NhOrderPlanInDTO;

import java.util.List;

public interface WorkOrderPlanManager {
    void save(String orderId, Integer planId);

    void deletePlan(NhOrderPlanInDTO planInDTO);

    List<Integer> selectPlanIds(String orderId);

    List<NhOrderPlanOptionOutPO> listOrderPlanOption(String orderId);

}
