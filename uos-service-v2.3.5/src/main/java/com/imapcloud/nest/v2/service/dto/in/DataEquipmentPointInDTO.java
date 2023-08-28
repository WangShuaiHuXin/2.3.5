package com.imapcloud.nest.v2.service.dto.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataEquipmentPointInDTO {
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
    private String pointId;

}
