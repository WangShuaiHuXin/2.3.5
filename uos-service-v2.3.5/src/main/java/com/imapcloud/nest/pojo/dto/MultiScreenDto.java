package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author wmin
 */
@Data
public class MultiScreenDto {
    private String flowUrl;
    private List<Map<String, Object>> nestNameAndStatusList;
    private String nestName;
    private String nestUuid;
}
