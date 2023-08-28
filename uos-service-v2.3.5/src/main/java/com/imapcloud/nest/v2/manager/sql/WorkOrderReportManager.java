package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.dao.po.in.OrderReportInPO;
import com.imapcloud.nest.v2.dao.po.out.OrderReportOutPO;

import java.util.List;

public interface WorkOrderReportManager {
    void saveOne(OrderReportInPO build);

    boolean updateToDel(String reportId);

    List<OrderReportOutPO> selectByCondition(OrderReportInPO build);
}
