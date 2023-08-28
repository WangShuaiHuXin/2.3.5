package com.imapcloud.nest.pojo.dto.reqDto;

import lombok.Data;

import java.util.List;

@Data
public class DataReqDto {
    private Integer dataType;
    private List<Integer> ids;
    private List<Integer> missionRecordsIds;
    private Boolean isDelFile;
}
