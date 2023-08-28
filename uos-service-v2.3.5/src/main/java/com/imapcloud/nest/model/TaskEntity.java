package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 任务表
 * </p>
 *
 * @author wmin
 * @since 2020-08-27
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("task")
public class
TaskEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 任务名称
     */
    private String name;

    /**
     * 描述，巡检作业周期
     */
    private String description;

    /**
     * 任务类型Id
     */
    private Integer type;

    /**
     * 机巢id，只有机巢航线才有（mold=0）
     */
    @Deprecated
    private Integer nestId;

    private String baseNestId;

    /**
     * 该条记录被复制的次数
     */
    private Integer copyCount;

    /**
     * 航线种类，0 - 机巢航线，1 - 易飞终端航线
     */
    private Integer mold;

    /**
     * 单位Id,只有易飞终端的航线才有(mold=1)
     * @deprecated 2.0.0，使用orgCOde字段替代
     */
    @Deprecated
    private String unitId;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 变电站规划的次级类别0是本地任务，1是动态任务
     */
    private Integer subType;

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

    private Integer dataType;

    private Integer tagId;


}
