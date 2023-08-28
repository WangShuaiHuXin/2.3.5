package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class PlanBuildReqDto {

    private Integer id;

    private List<Integer> ids;

    private Integer page;

    private Integer limit;

    private List<Integer> nestIdList;

}
