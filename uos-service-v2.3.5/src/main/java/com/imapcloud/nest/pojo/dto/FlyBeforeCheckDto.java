package com.imapcloud.nest.pojo.dto;

import lombok.Data;

/**
 * @author wmin
 */
@Data
public class FlyBeforeCheckDto {
    private String nestStatus;
    /**
     * 0-电池不在位
     * 1-电池在位
     */
    private Integer battery;

    /**
     * 0 - 天线不打开
     * 1 - 天线打开
     */
    private Integer antenna;

    /**
     * 0 - 天气不能飞行
     * 1 - 天气可以飞行
     */
    private Integer weather;

    /**
     * 0 - 未检查完毕
     * 1 - 已检查完毕
     */
    private Integer completed;
}
