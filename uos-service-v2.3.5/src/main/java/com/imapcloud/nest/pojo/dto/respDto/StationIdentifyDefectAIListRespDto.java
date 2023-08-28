package com.imapcloud.nest.pojo.dto.respDto;

import com.imapcloud.nest.model.StationIdentifyRecordEntity;
import lombok.Data;

import java.util.List;

@Data
public class StationIdentifyDefectAIListRespDto {
    //返回的结果的实体数组
    private List<StationIdentifyRecordEntity> list;
    private int defectNum;
    private float excTime;
}