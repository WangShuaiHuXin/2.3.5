package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;

@Data
@TableName("data_equipment_point_rel")
public class DataEquipmentPointRelEntity extends GenericEntity {

    private String pointId;
    private String equipmentId;
    private String orgCode;
}
