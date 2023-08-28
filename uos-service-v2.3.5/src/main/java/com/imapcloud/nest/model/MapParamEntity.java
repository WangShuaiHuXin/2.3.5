package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.*;
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
 * @since 2021-08-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("map_param")
public class MapParamEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * @deprecated at 2.0.0，使用{@link MapParamEntity#orgCode}替代
     */
    @Deprecated
    private Integer unitId;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 空间误差
     */
    @TableField("systemSpaceError")
    private String systemSpaceError;

    /**
     * 飞渡服务地址
     */
    @TableField("feiDuServerUrl")
    private String feiDuServerUrl;

    /**
     * 飞渡Signalling端口
     */
    @TableField("feiDuSignallingPort")
    private String feiDuSignallingPort;

    /**
     * 飞渡API端口
     */
    @TableField("feiDuApiPort")
    private String feiDuApiPort;

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


}
