package com.imapcloud.nest.v2.manager.dataobj.out;

import com.imapcloud.nest.v2.service.dto.out.DataEquipmentPointQueryOutDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DataEquipmentPointQueryOutDO {
    private Long total;
    private List<DataEquipmentPointQueryOutDO.DataEquipmentPointQueryInfoOutDO> dtos;
    @Data
    public static class DataEquipmentPointQueryInfoOutDO implements Serializable {
        private String pointId;
        private String pointName;
        private Double lng;
        private Double lat;
        private Double height;
        private String orgCode;
        private Double panoramaDis;
        private Double groundDis;
        private String tagId;
        List<DataEquipmentPointQueryOutDO.DataEquipmentInfoOutDO> equipmentList;
        private String brief;
    }
    @Data
    public static class DataEquipmentInfoOutDO {
        private String equipmentId;
        private String equipmentName;
    }
}
