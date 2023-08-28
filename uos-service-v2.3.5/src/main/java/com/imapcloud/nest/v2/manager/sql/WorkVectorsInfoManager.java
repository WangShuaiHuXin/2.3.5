package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.dao.po.in.OrderInfoInPO;
import com.imapcloud.nest.v2.dao.po.out.NhQueryVectorsOutPO;

import java.util.List;

public interface WorkVectorsInfoManager {
    void saveVectors(List<OrderInfoInPO.VectorsInPO> vectorsInfos);

    void deleVectors(String orderId,String vectorId);

    List<NhQueryVectorsOutPO> queryVertorsByOrderId(String orderId);
}
