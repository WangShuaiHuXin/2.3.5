package com.imapcloud.nest.v2.manager.sql;

import com.imapcloud.nest.v2.dao.entity.PowerInspectionReportValueRelEntity;
import com.imapcloud.nest.v2.manager.dataobj.in.PowerInspectionReportValueRelInDO;

import java.util.List;

public interface PowerInspectionReportValueRelManager {
    void saveBatch(List<PowerInspectionReportValueRelInDO> reportValueRelEntities);

    List<PowerInspectionReportValueRelEntity> selectByReportIds(List<String> reportIds);

    void deleteBatch(List<String> ids);
}
