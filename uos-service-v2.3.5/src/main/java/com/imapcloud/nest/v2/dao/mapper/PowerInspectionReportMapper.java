package com.imapcloud.nest.v2.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imapcloud.nest.v2.dao.entity.PowerInspectionReportInfoEntity;
import com.imapcloud.nest.v2.dao.po.in.PowerInspcetionReportInfoPO;
import org.apache.ibatis.annotations.Param;
import org.mapstruct.Mapper;

import java.util.List;

public interface PowerInspectionReportMapper extends BaseMapper<PowerInspectionReportInfoEntity> {
    void saveBatch(@Param("list") List<PowerInspectionReportInfoEntity> collect);

}
