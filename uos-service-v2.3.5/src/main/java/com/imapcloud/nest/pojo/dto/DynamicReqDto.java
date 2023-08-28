package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.util.List;

/**
 * 获取动态航线请求实体
 */
@Data
public class DynamicReqDto {

    private String nestId;

    private List<Integer> deviceIds;
}
