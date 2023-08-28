package com.imapcloud.nest.v2.web.vo.req;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DataEquipmentPointReqVO implements Serializable {
    @NotNull(message = "设备点名称不能为空")
    private String pointName;
    @NotNull(message = "设备点名称不能为空")
    @Max(value = 180,message = "经度不能超过180")
    private Double lng;
    @Max(value = 90,message = "纬度不能超过90")
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
