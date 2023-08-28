package com.imapcloud.nest.pojo.vo;

import lombok.Data;

import java.io.File;

/**
 * 导出报告的VO类
 *
 * @author: zhengxd
 * @create: 2021/3/24
 **/
@Data
public class IllegalPointVO {

    private Integer id;

    /**
     * 点云文件id1
     */
    private String beforeFileName;

    /**
     * 点云文件id2
     */
    private String afterFileName;

    /**
     * 违建点名称
     */
    private String name;

    /**
     * 违建点面积
     */
    private Double area;

    /**
     * 经度
     */
    private Double latitude;

    /**
     * 纬度
     */
    private Double longitude;

    /**
     * 高度
     */
    private Double height;

    /**
     * 备注
     */
    private String note;

    /**
     * 类型（1-手动标记；2-智能分析）
     */
    private Integer type;

    /**
     * excel导出的图片
     */
    private File[] file;

    /**
     * 违建点图片路径
     */
    private String photo;
}
