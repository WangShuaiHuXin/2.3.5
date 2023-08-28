package com.imapcloud.nest.v2.dao.po.out;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NhQueryHisRecordOutPO {
    private String orderId;
    private String recordId;
    private boolean flag;
    private String desc;
    private String mark;
    private int orderStatus;
    private boolean deleted;
    private String creatorId;
    private LocalDateTime createdTime;
}

