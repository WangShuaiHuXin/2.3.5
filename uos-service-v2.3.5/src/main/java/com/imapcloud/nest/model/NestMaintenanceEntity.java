package com.imapcloud.nest.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 机巢维保记录表
 * </p>
 *
 * @author hc
 * @since 2021-05-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("nest_maintenance")
//序列化到前端忽略一下几个字段
@JsonIgnoreProperties({"createTime", "modifyTime", "deleted", "nestId"})
public class NestMaintenanceEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 开始时间
     */
    private LocalDate startTime;
    /**
     * 结束时间
     */
    private LocalDate endTime;

    /**
     * 维保类型：0-保养，1-软件故障, 2-无人机故障, 3-基站硬件故障
     */
    private Integer type;

    public static String getTypeName(Integer value) {
        switch (value) {
            case 0:
                return "保养";
            case 1:
                return "软件故障";
            case 2:
                return "无人机故障";
            case 3:
                return "基站硬件故障";
            default:
                return null;
        }
    }

    /**
     * 维保项目id
     */
    private String project;

    /**
     * 附件地址
     */
    private String attachmentPath;

    /**
     * 维保人员
     */
    private String staff;

    private String remarks;

    /**
     * 保养项目名
     */
    private String maintainName;

    /**
     * 电池序号
     */
    private String batteryIndex;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    private Integer deleted;

    @Deprecated
    private Integer nestId;

    private String baseNestId;
}
