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
 * 
 * </p>
 *
 * @author hc
 * @since 2021-04-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mission_air")
public class MissionAirEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer missionId;

    private Integer missionRecordsId;

    /**
     * json数据
     */
    private String data;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    private String execId;
    /**
     * 单位ID
     * @deprecated 2.0.0，使用orgCode字典替代
     */
    @Deprecated
    private String unitId;

    /**
     * 单位编码
     */
    private String orgCode;
}
