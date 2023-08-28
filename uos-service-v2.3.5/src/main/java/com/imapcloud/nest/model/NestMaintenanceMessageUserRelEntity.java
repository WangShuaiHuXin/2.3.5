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
@TableName("nest_maintenance_message_user_rel")
public class NestMaintenanceMessageUserRelEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 维保消息表id
     */
    private Integer messageId;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 0-未读，1-已读
     */
    private Integer readed;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;


}
