package com.imapcloud.nest.pojo.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author zhongtb
 * @version 1.0.0
 * @ClassName FlightMissionVO.java
 * @Description FlightMissionVO
 * @createTime 2022年03月24日 10:53:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightMissionExportVO {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @ExcelIgnore
    private Integer id;

    @ExcelProperty(value = "基站名称" , index = 0)
    private String nestName;

    @ExcelProperty(value = "基站型号" , index = 1)
    private String nestType;

    private Integer nestTypeId;
    /**
     * 任务执行id
     */
    @ExcelProperty(value = "任务执行Id", index = 2)
    private String execMissionId;

    /**
     * 任务id
     */
//    @ExcelProperty(value = "任务Id", index = 2)
    @ExcelIgnore
    private String missionId;

    /**
     * 任务名称
     */
    @ExcelProperty(value = "任务名", index = 3)
    private String missionName;

    /**
     * 开始时间
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "开始时间", index = 4)
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    @ExcelProperty(value = "结束时间", index = 5)
    private LocalDateTime endTime;

    /**
     * 里程
     */
    @ExcelProperty(value = "里程", index = 6)
    private Double missionDistance;

    /**
     * 时长
     */
    @ExcelProperty(value = "时长", index = 7)
    private Double missionDate;

    /**
     * 创建用户id
     */
//    @ExcelProperty(value = "创建人", index = 8)
    @ExcelIgnore
    private Integer createUserId;

    /**
     * 创建时间
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
//    @ExcelProperty(value = "创建时间", index = 9)
    @ExcelIgnore
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
//    @ExcelProperty(value = "修改时间", index = 10)
    @ExcelIgnore
    private LocalDateTime modifyTime;

    /**
     * 删除标识
     */
    @ExcelIgnore
    private Boolean deleted;

}
