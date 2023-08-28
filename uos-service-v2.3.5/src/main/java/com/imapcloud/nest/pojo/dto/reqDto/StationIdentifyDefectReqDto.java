package com.imapcloud.nest.pojo.dto.reqDto;

import com.imapcloud.nest.pojo.dto.unifyAirLineDto.StationIdentifyDefectDtO;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class StationIdentifyDefectReqDto {
    //提交检测的成果id
    private Integer photoId;
    private Integer defectModelType;
    private Integer tagId;
}
