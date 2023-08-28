package com.imapcloud.nest.pojo.dto.unifyAirLineDto;

import lombok.Data;

import java.util.List;

/**
 * @Classname GridPoint
 * @Description GridPoint
 * @Date 2022/12/15 15:42
 * @Author Carnival
 */
@Data
public class GridPoint {

    /**
     * 航线位置
     */
    private UnifyLocation location;

    /**
     * 自定义航点动作
     */
    private List<ActionCustoms> actionCustoms;

    /**
     * 旋转角
     */
    private Double cornerRadiusInMeters;
}
