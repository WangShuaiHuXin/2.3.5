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
 * 架次表
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("region")
public class RegionEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 区域名称
     */
    private String name;

    /**
     * 简单描述
     */
    private String description;

    /**
     * 创建用户id
     */
    @Deprecated
    private Integer createUserId = 0;
    private Long creatorId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;

    @TableField(exist = false)
    private List<NestEntity> nestEntityList;

    /*
    * 机巢数
    * */
    @TableField(exist = false)
    private Integer nestCount;

}
