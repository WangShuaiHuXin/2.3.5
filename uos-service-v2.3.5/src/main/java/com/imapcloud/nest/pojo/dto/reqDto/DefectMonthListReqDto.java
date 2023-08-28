package com.imapcloud.nest.pojo.dto.reqDto;

import lombok.Data;

import java.util.List;

@Data
public class DefectMonthListReqDto {

    private static final long serialVersionUID=1L;

    private List<Integer> missionRecordsIds;

    private Integer missionRecordsId;

    private Integer type;

    private Integer defectStatus;

    private List<Integer> tagIds;

    private Integer tagId;

    private List<Integer> taskIds;

    private String startTime;

    private String endTime;

    private List<String> names;

    private String name;

    private Integer page;

    private Integer limit;
}