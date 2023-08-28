package com.imapcloud.nest.v2.web.vo.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

@Data
public class DataEquipmentPointListRespVO implements Serializable {

    private String equipmentId;
    private String equipmentName;
    private boolean affiliateStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double lng;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double lat;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Double height;
}
