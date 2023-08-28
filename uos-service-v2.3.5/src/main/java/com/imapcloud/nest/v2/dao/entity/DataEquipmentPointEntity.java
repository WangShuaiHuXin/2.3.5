package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;

@Data
@TableName("data_equipment_point")
public class DataEquipmentPointEntity extends GenericEntity {

    private String pointId;
    private String pointName;
    private String tagId;
    private Double pointLongitude;
    private Double pointLatitude;
    private Double pointHeight;
    private Double panoramaDistance;
    private Double groundDistance;
    private String brief;
    private String orgCode;
}
