package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.PowerInspectionReportValueRelEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PowerInspectionReportValueRelMapper extends BaseMapper<PowerInspectionReportValueRelEntity> {
    void saveBatch( @Param("list") List<PowerInspectionReportValueRelEntity> powerInspectionReportValueRelEntities);
}
