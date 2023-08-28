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
 * 用户token表
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_token")
public class SysTokenEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户Id
     */
    private Integer userId;

    /**
     * token
     */
    private String token;

    /**
     * token过期时间
     */
    private LocalDateTime expireTime;

    /**
     * token更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;


}
