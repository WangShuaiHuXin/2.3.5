package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * 架次表
 * </p>
 *
 * @author wmin
 * @since 2020-08-27
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("mission")
public class MissionEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * mission的uuid
     */
    private String uuid;

    /**
     * 顺序号，任务的第几个架次
     */
    private Integer seqId;

    /**
     * 航线ID
     */
    private Integer airLineId;

    /**
     * 任务ID
     */
    private Integer taskId;

    /**
     * missionId,对应的标签名称
     */
    @TableField(exist = false)
    private String tagName;

    /**
     * missionId,对应的标签名称
     */
    @TableField(exist = false)
    private Integer tagId;

    /**
     * 航次参数Id
     */
    private Integer missionParamId;

    /**
     * 任务复制的次数
     */
    private Integer copyCount;

    /**
     * 上一次飞行策略
     */
    private Integer lastFlightStrategy;

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
     * 单位ID
     * @deprecated 2.0.0，使用orgCode字典替代
     */
    @Deprecated
    private String unitId;

    /**
     * 单位编码
     */
    private String orgCode;

}
