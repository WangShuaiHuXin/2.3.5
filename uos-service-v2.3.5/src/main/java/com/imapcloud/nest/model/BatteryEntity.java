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
 * 电池信息表
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
@Deprecated
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("battery")
public class BatteryEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 电池序列号
     */
    private String batterySerialNumber;

    /**
     * 电池充电次数
     */
    private Integer dischargeTimes;

    /**
     * 机巢ID
     */
    private Integer nestId;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;


}
