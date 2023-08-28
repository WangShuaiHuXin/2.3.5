package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author hc
 * @since 2021-05-20
 * @deprecated 2.0.0，使用{@link com.imapcloud.nest.v2.dao.entity.BaseNestOrgRefEntity}替代
 */
@Deprecated
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("nest_unit")
public class NestUnitEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 机巢id
     */
    private Integer nestId;

    /**
     * 单位id
     * @deprecated 2.0.0，使用orgCode字段替代
     */
    @Deprecated
    private Integer unitId;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 机巢名
     */
    @TableField(exist=false)
    private String nestName;


}
