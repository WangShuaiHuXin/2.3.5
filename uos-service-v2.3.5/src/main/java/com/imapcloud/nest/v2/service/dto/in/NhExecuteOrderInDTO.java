package com.imapcloud.nest.v2.service.dto.in;

import lombok.Data;

@Data
public class NhExecuteOrderInDTO {
    private int status;
    private String remark;
    private String orderId;

}
