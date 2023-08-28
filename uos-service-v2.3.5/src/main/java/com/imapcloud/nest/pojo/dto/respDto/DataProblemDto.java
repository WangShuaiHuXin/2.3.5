package com.imapcloud.nest.pojo.dto.respDto;

import com.imapcloud.nest.model.DataProblemEntity;
import lombok.Data;

import java.util.List;

@Data
public class DataProblemDto {
    //返回的结果的实体数组
    private List<DataProblemEntity> list;
    private int defectNum;
    private float excTime;
}
