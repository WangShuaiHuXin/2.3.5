package com.imapcloud.nest.pojo.dto;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class SelectSysUserPageParam {
    private String account;
    private String mobile;
    private Integer unitId;
    private Integer status;
    private Set<Long> userQueryIds;
    private Set<Integer> exceptUserQueryIds;
    private List<Integer> unitList;
}
