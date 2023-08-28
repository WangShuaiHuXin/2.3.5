package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.dao.po.in.PowerInspcetionReportInfoPO;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerInspectionReportInfoInDO;
import com.imapcloud.nest.v2.manager.dataobj.out.PowerInspectionReportOutDO;

import java.util.List;

public interface PowerInspectionReportManager {
    void saveBatch(List<PowerInspectionReportInfoInDO> reportInfoInDOS);

    PowerInspectionReportOutDO queryByCondition(PowerInspcetionReportInfoPO build);

    void deleteRelBatch(List<String> batchIds);

    void deleteBatch(List<String> batchIds);

    void fixUrl(String reportId, String pathUrl);

    /**
     * 查询报告id是否在单位下
     *
     * @param inspcetionReportIdList 磁浮子报告id列表
     * @param orgCode                组织代码
     * @return 数量
     */
    int selectNum(List<String> inspcetionReportIdList, String orgCode);
}
