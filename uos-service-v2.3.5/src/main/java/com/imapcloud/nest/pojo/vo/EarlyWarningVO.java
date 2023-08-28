package com.imapcloud.nest.pojo.vo;

import com.imapcloud.nest.v2.service.dto.UnitEntityDTO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 气象预警规则VO
 */
@Data
public class EarlyWarningVO {

    private static final long serialVersionUID=1L;

    private Integer id;

    /**
     * 预警期起始日期
     */
    private Date earlyWarningDate;

    /**
     * 预警期结束日期
     */
    private Date earlyWarningDateEnd;

    /**
     * 预警说明
     */
    private String warningText;

    /**
     * 预警名称
     */
    private String name;

    /**
     * 状态  0-未启用  1-启用
     */
    private Byte state;

    /**
     * 警告范围(左)
     */
    private Double waringLeft;

    /**
     * 警告范围(右)
     */
    private Double waringRight;

    /**
     * 报警天气类型 1-降水, 2-风速, 3-温度
     */
    private Byte waringType;

    /**
     * 关联单位列表
     */
    private List<UnitEntityDTO> unitList;

    /**
     * 是否为每年生效 1-每年生效  0-仅一次
     */
    private Byte isEveryYear;

}
