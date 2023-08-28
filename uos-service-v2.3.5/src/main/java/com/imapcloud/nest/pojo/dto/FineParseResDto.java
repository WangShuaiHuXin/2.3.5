package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author wmin
 */
@Data
public class FineParseResDto {
    private Integer zipId;
    private String zipName;
    private List<Map<String, Object>> towerList;
}
