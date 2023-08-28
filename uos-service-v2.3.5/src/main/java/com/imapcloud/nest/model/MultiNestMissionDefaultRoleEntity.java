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
 * 多基站任务默认架次表
 * </p>
 *
 * @author wmin
 * @since 2021-02-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("multi_nest_mission_default_role")
@Accessors(chain = true)
public class MultiNestMissionDefaultRoleEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 基站id
     */
    private Integer nestId;

    private String baseNestId;

    /**
     * 基站uuid
     */
    private String nestUuid;

    /**
     * 架次id
     */
    private Integer missionId;

    /**
     * 架次名称
     */
    private String missionName;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 用户id(待废弃)
     */
    @Deprecated
    private Integer sysUserId = 0;

    /**
     * 用户id（新）
     */
    private Long creatorId;

    /**
     * 是否录频
     */
    private Integer gainVideo;

    /**
     * 获取数据方式
     */
    private Integer gainDataMode;


    /**
     * 任务id
     */
    private Integer taskId;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;


}
