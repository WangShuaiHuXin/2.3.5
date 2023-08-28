package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class PlanDetailReqDto {

    private String taskNameLike;

    private Integer page;

    private Integer limit;

    private String startTime;

    private String endTime;

    private Integer status;

    private Integer year;

    private Integer id;

    private Integer statu;

    private Integer nestId;

    private Boolean startTimeASC = true;

    private List<Integer> nestIdList;

    List<Integer> unitList;
}
