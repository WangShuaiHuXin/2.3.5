package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;

@Data
@TableName("power_inspection_report_value_rel")
public class PowerInspectionReportValueRelEntity extends GenericEntity {

    private String inspectionReportId;

    private String valueId;
}
