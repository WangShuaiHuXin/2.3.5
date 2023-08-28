package com.imapcloud.nest.v2.dao.po.in;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderInfoInPO {
    private String orderId;
    private String title;
    private String orgCode;
    private int degree;
    private int orderType;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
    private String verificationMethod;
    private String frequency;
    private String desc;
    private int orderStatus;
    private String userId;
    private int versionId;

    @Data
    public static class VectorsInPO {
        private String name;
        private List<String> points;
        private Integer order;
        private Integer type;
        private String orderId;
        private String userId;
    }
}
