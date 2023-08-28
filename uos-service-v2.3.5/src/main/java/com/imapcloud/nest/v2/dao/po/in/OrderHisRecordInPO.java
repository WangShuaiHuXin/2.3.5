package com.imapcloud.nest.v2.dao.po.in;

import lombok.Data;

@Data
public class OrderHisRecordInPO {
    private String orderId;
    private String recordId;
    private boolean flag;
    private String description;
    private String mark;
    private int orderStatus;
    private boolean deleted;
    private int processCode;
    private boolean processDir;
    private String creatorId;
}
