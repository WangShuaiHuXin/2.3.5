package com.imapcloud.nest.v2.service.dto.out;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AppFlowPageOutDTO {
    private List<AppFlowOutDTO> records;
    private Long pages;
    private Long size;
    private Long total;
    private Long current;
}
