package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 缺陷、表计的详细信息表
 * </p>
 *
 * @author liao
 * @since 2021-08-20
 */
@Data
public class DefectInfoElectricEntity implements Serializable{

    private static final long serialVersionUID=1L;

    /**
     * 缺陷信息id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 识别记录id
     */
    private Integer recordId;

    /**
     * 图片id
     */
    private Long photoId;

    /**
     * 坐标x
     */
    private String x;

    /**
     * 坐标y
     */
    private String y;

    /**
     * 坐标x1
     */
    private String x1;

    /**
     * 坐标y1
     */
    private String y1;

    /**
     * 缺陷类型
     */
    private String type;

    /**
     * 缺陷类型id
     */
    private String typeId;

    /**
     * 标记来源（0:缺陷,1:表计,2:红外,3:交通,4:河道,5:违建,6:定点取证）
     */
    private Integer source;

    /**
     * 备注
     */
    private String note;
    /**
     * 创建用户id
     */
    private Integer createUserId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    @TableLogic
    private Boolean deleted;

    /**
     * 保存图片框的x坐标
     */
    @TableField(exist = false)
    private int pixelX;
    /**
     * 保存图片框的y坐标
     */
    @TableField(exist = false)
    private int pixelY;
    /**
     * 框的宽度
     */
    @TableField(exist = false)
    private int width;
    /**
     * 框的长度
     */
    @TableField(exist = false)
    private int height;
    /**
     * 问题类型code
     */
    @TableField(exist = false)
    private String code;
}
