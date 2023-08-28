package com.imapcloud.nest.pojo.dto.respDto;

import com.imapcloud.nest.model.StationIdentifyRecordEntity;
import com.imapcloud.nest.pojo.dto.MissionRecordsStatisticsDto;
import lombok.Data;

import java.util.List;

@Data
public class MissionRecordsStatisticsRespDto {
    //返回的结果的实体数组
    private List<MissionRecordsStatisticsDto> list;
}