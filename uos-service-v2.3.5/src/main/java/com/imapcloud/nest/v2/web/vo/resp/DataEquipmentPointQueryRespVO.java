package com.imapcloud.nest.v2.web.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DataEquipmentPointQueryRespVO implements Serializable {
    private String pointId;
    private String pointName;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double lng;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double lat;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double height;
    private String orgCode;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double panoramaDis;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double groundDis;

    private String tagId;
    List<DataEquipmentInfo> equipmentList;

    private String brief;

    @Data
    public static class DataEquipmentInfo {
        private String equipmentId;
        private String equipmentName;
    }
}
