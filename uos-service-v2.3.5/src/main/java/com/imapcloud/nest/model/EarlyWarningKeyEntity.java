package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

/**
 * 天气预报接口key列表
 */
@TableName("early_warning_key")
@Data
public class EarlyWarningKeyEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(value = "name", jdbcType = JdbcType.VARCHAR)
    private String name;

    @TableField(value = "nest_id", jdbcType = JdbcType.INTEGER)
    private Integer nestId;

    @TableField(value = "token", jdbcType = JdbcType.VARCHAR)
    private String token;

}
