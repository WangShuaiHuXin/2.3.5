package com.imapcloud.nest.pojo.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName NestMaintenanceExportVO.java
 * @Description 设备维保
 * @createTime 2022年05月13日 14:05:00
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NestMaintenanceExportVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ExcelIgnore
    private String nestId;
    /**
     * 序号
     */
    @ExcelProperty(value = "序号", index = 0)
    private Integer num;

    /**
     * 机巢地址
     */
    @ExcelProperty(value = "机巢地址", index = 1)
    private String nestAddress;

    /**
     * 单位
     */
    @ExcelProperty(value = "单位", index = 2)
    private String orgName;

    /**
     * 设备类型
     */
    @ExcelProperty(value = "设备类型", index = 3)
    private String devType;

    /**
     * 设备编号
     */
    @ExcelProperty(value = "设备编号", index = 4)
    private String devNo;

    /**
     * 基站名称
     */
    @ExcelProperty(value = "基站名称", index = 5)
    private String nestName;

    /**
     * 维保类型：0-保养，1-软件故障, 2-无人机故障, 3-基站硬件故障
     */
    @ExcelProperty(value = "维保类型", index = 6)
    private String type;

    /**
     * 维修时间
     */
    @ExcelProperty(value = "维修时间", index = 7)
    private String maintainTime;


    /**
     * 维保项目
     */
    @ExcelProperty(value = "维保项目", index = 8)
    private String projectName;

    /**
     * 损坏部件
     */
    @ExcelProperty(value = "损坏部件", index = 9)
    private String damagePart;

    /**
     * 维保人员
     */
    @ExcelProperty(value = "维保人员", index = 10)
    private String staff;

}