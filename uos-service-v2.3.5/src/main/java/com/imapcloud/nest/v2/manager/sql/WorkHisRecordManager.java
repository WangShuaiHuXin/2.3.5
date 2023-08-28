package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.dao.po.in.OrderHisRecordInPO;
import com.imapcloud.nest.v2.dao.po.out.NhQueryHisRecordOutPO;

import java.util.List;

public interface WorkHisRecordManager {
    List<NhQueryHisRecordOutPO> queryExecutingRecords(String orderId);

    boolean checkHisExist(OrderHisRecordInPO inPO);

    void updateDisuseRecord(OrderHisRecordInPO inPO);

    void addRecord(OrderHisRecordInPO inPO);

    List<NhQueryHisRecordOutPO> queryHistRecords(String orderId);

    void updateProcess(OrderHisRecordInPO inPO,Integer process);
}
