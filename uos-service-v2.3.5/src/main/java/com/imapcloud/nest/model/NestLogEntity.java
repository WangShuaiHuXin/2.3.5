package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 基站日志表
 * </p>
 *
 * @author wmin
 * @since 2021-06-22
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("nest_log")
public class NestLogEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 基站id
     */
    @Deprecated
    private Integer nestId;

    private String baseNestId;
    /**
     * 基站日志名称
     */
    private String name;

    /**
     * 基站日志名称
     */
    private String folder;

    /**
     * 基站日志路径
     */
    private String url;

    /**
     * 无人机标识
     */
    private Integer uavWhich;

    private LocalDateTime uploadTime;


}
