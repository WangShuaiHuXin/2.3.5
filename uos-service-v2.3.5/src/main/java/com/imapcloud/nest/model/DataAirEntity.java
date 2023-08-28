package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author hc
 * @since 2021-08-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("data_air")
public class DataAirEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private Integer missionId;

    /**
     * 架次记录表的id
     */
    private Integer missionRecordsId;

    private String realPath;

    /**
     * 是否删除, 0:否,1:是
     */
    @TableLogic
    private Boolean deleted;

    /**
     * @deprecated 2.0.0，由orgCode字段替代
     */
    @Deprecated
    private String unitId;

    /**
     * 单位编码
     */
    private String orgCode;

}
