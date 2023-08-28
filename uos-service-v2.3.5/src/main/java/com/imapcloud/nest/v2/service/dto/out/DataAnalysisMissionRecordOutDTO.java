package com.imapcloud.nest.v2.service.dto.out;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 架次记录筛选项
 *
 * @author boluo
 * @date 2022-10-11
 */
@Data
public class DataAnalysisMissionRecordOutDTO {

    private String missionRecordId;

    private String name;

    private LocalDateTime createTime;;
}
