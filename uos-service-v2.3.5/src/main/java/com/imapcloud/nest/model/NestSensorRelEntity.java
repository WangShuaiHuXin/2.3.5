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
 * 传感器表
 * </p>
 *
 * @author wmin
 * @since 2020-09-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("nest_sensor_rel")
public class NestSensorRelEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 机巢id
     */
    @Deprecated
    private Integer nestId;

    private String baseNestId;
    /**
     * 传感器id
     */
    private Integer sensorId;
    private String name;
    /**
     * 喊话器音量
     */
    private Integer volume;
    /**
     * 喊话器是否重复播放（0-不重复； 1-重复）
     */
    private Integer repeat;


    /**
     * 创建用户id(旧，废弃)
     */
    @Deprecated
    private Integer createUserId = 0;

    private Long creatorId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;

}
