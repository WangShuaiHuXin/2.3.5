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
 * 维护项目
 * </p>
 *
 * @author hc
 * @since 2021-05-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("nest_maintenance_project")
public class NestMaintenanceProjectEntity implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 自动递增id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 维保项目名称
     */
    private String name;

    /**
     * 维保项目类型
     */
    private Integer type;

    /**
     * 排序字段
     */
    private Integer seq;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    private Boolean deleted;


}


