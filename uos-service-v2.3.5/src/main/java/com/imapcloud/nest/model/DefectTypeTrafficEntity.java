package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("defect_type_traffic")
public class DefectTypeTrafficEntity implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 缺陷类型名称
     */
    private String name;
    /**
     * 缺陷类型code
     */
    private String code;

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

    /**
     * @deprecated 2.0.0，由orgCode字段替代
     */
    @Deprecated
    private Integer unitId;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 0:缺陷,1:表计,2:红外,3:交通
     */
    private Integer type;
    /**
     * 缺陷等级,0:一般,1:严重,2:紧急
     */
    private int defectLevel;
}
