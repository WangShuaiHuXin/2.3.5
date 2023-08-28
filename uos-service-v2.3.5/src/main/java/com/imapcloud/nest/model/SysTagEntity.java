package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 系统标签表
 * </p>
 *
 * @author kings
 * @since 2020-12-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_tag")
public class SysTagEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称，排序
     */
    private String name;

    /**
     * 单位id，单位必须经过检验。查询单位的接口也要校验
     * @deprecated at 2.0.0，使用{@link SysTagEntity#orgCode}替代
     */
    @Deprecated
    private Integer unitId;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 排序号
     */
    private Integer seq;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    /**
     * 是否存在成果的图片
     */
    @TableField(exist = false)
    private Boolean hasMissionPhoto;

    /**
     * 问题数量
     */
    @TableField(exist = false)
    private Long exceptions;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    private int tagType;

    @TableField(exist = false)
    private int isTask;

    @TableField(exist = false)
    private int isMission;

    @TableField(exist = false)
    private Integer count;

    @TableField(exist = false)
    private List<MissionRecordsEntity> missionRecordsEntities;

}
