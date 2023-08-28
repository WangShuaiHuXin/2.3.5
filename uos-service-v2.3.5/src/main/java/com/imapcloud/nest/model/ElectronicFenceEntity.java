package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author zheng
 * @since 2021-09-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("electronic_fence")
public class ElectronicFenceEntity implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 电子围栏id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 单位id
     * @deprecated 2.0.0，由orgCode字段替代
     */
    @Deprecated
    private Integer unitId;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 围栏名称
     */
    private String name;

    /**
     * 开启状态（1-开启；2-关闭）
     */
    private Integer state;

    /**
     * 围栏类型（1-适飞区； 2-禁飞区）
     */
    private Integer type;

    /**
     * 缓冲范围
     */
    private Integer bufferRange;

    /**
     * 围栏的坐标信息
     */
    private String coordinate;

    /**
     * 高度
     */
    private Integer height;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;

    /**
     * 是否共享【0：专属；1：共享】
     * 共享指的是共享给子孙单位
     */
    private Boolean shared;

    /**
     * 有效期开始时间
     */
    private LocalDateTime effectiveStartTime;

    /**
     * 有效期截止时间
     */
    private LocalDateTime effectiveEndTime;

    /**
     * 是否永久有效
     */
    private Boolean neverExpired;

}
