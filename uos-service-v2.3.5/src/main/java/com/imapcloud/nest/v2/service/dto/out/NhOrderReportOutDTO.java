package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NhOrderReportOutDTO {
    private String name;
    private String reportId;
    private String url;
    private String creatorName;
    private LocalDateTime createdTime;
}
