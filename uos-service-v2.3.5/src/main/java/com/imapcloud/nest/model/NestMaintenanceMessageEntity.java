package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 机巢维保表与维保项目的关联表
 * </p>
 *
 * @author hc
 * @since 2021-05-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("nest_maintenance_message")
public class NestMaintenanceMessageEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 信息保养提醒
     */
    private Integer type;

    /**
     * 信息保养
     */
    private String body;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    private Integer deleted;


}
