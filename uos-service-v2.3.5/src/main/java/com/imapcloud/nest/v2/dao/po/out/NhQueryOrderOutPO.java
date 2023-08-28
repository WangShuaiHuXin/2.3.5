package com.imapcloud.nest.v2.dao.po.out;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
@Data
public class NhQueryOrderOutPO {

    /**
     * 总数
     */
    private Long total;
    private List<OrderInfo> infoList;
    @Data
    public static class OrderInfo {
        private Long id;
        private String title;
        private String orgCode;
        private LocalDateTime inspectionBeginTime;
        private LocalDateTime inspectionEndTime;
        private String orderId;
        private LocalDateTime createdTime;
        private int orderStatus;
        private int degree;
        private int orderType;
        private int versionId;
        private String creatorId;
        private String verificationMethod;
        private String frequency ;
        private String desc ;
    }
}
