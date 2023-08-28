package com.imapcloud.nest.v2.service.dto.out;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.imapcloud.nest.v2.web.vo.resp.DataEquipmentPointQueryRespVO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DataEquipmentPointQueryOutDTO {

    private Long total;
    private List<DataEquipmentPointQueryInfoDTO> dtos;

    @Data
    public static class DataEquipmentPointQueryInfoDTO implements Serializable {
        private String pointId;
        private String pointName;
        private Double lng;
        private Double lat;
        private Double height;
        private String orgCode;
        private Double panoramaDis;
        private Double groundDis;
        private String tagId;
        List<DataEquipmentQueryInfoDTO> equipmentList;
        private String brief;
    }

    @Data
    public static class DataEquipmentQueryInfoDTO {
        private String equipmentId;
        private String equipmentName;
    }
}