package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.dao.po.in.OrderInfoInPO;
import com.imapcloud.nest.v2.dao.po.in.QueryOrderInPO;
import com.imapcloud.nest.v2.dao.po.out.NhQueryOrderOutPO;
import com.imapcloud.nest.v2.service.dto.in.NhOrderInfoInDTO;

public interface WorkOrderInfoManager {
    NhQueryOrderOutPO queryListByCondition(QueryOrderInPO build);

    boolean checkNameConfilct(String title,String orgCode,String orderId);

    void saveOrder(OrderInfoInPO infoInPO);

    NhQueryOrderOutPO.OrderInfo queryOneById(String orderId);

    void editOrder(OrderInfoInPO infoInPO);

}
