package com.imapcloud.nest.pojo.dto.unifyAirLineDto;

import lombok.Data;

/**
 * @Classname GridData
 * @Description 网格参数
 * @Date 2022/12/15 14:05
 * @Author Carnival
 */
@Data
public class GridData {

    private Double east;

    private Double west;

    private Double north;

    private Double south;

    private Integer row;

    private Integer column;

    private String gridBound;
}
