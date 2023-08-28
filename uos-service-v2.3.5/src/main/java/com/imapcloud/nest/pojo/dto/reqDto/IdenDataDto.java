package com.imapcloud.nest.pojo.dto.reqDto;

import lombok.Data;

import java.util.List;

@Data
public class IdenDataDto {
    private Integer tagId;
    private List<Integer> idenIds;
    private List<Integer> photoIds;
    private List<Integer> dataIds;
    private Integer dataType;
    private Integer infraredType;
}
