package com.imapcloud.nest.pojo.vo;

import lombok.Data;

import java.util.List;

@Data
public class MapPlottingVO {

    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 坐标点数组
     */
    private List<String> pointList;

    /**
     * 坐标点数组字符串
     */
    private String pointStr;

    /**
     * 类型 1.点 2.线 3.面
     */
    private Byte type;

    /**
     * 用户名
     */
    private String userName;

}
