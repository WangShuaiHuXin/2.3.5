package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.util.List;

@Data
public class PlanBuildConfigDto {
    //间隔类型：day天，week周，month月
    private String intervalType;
    //间隔值,根据不同类型，该值有不同的含义
    private Integer interval;
    //根据不同类型，选择不同的组合间隔点
    private List<Integer> nextIntervals;
    //具体的时间
    private String time;
    //开始时间，对定时计划使用的
    private String startTime;

}
