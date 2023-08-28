package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class NhOrderInfoInDTO {
    private String orderId;
    private String title;
    private String orgCode;
    private String degree;
    private String orderType;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private String verificationMethod;
    private String frequency;
    private String desc;
    private String orderStatus;
    private List<NhOrderInfoInDTO.VectorsInDto> vectors;

    @Data
    public static class VectorsInDto {
        private String name;
        private List<String> points;
        private Integer order;
        private Integer type;
    }
}
