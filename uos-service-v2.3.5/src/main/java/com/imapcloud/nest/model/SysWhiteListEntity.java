package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 系统白名单表
 * </p>
 *
 * @author hc
 * @since 2022-03-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_white_list")
public class SysWhiteListEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 键
     */
    private String url;

    /**
     * 白名单类型
     */
    private Integer type;

    /**
     * 说明
     */
    private String description;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;


}
