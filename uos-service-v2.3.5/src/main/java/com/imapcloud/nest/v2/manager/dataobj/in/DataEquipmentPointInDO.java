package com.imapcloud.nest.v2.manager.dataobj.in;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DataEquipmentPointInDO implements Serializable {
    private String pointId;
    private String pointName;
    private Double lng;
    private Double lat;
    private Double height;
    private Double panoramaDis;
    private Double groundDis;
    private String tagId;
    private String orgCode;
    private String brief;
    private List<String> equipmentList;
}
