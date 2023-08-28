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
 * 机巢参数表
 * </p>
 *
 * @author wmin
 * @since 2020-10-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("nest_param")
public class NestParamEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 自动飞行速度
     */
    private Double autoFlightSpeed;

    /**
     * 飞到第一个航点的模式,详情请看gotoFirstWaypointMode枚举类
     */
    private Integer gotoFirstWaypointMode;

    /**
     * 完成的动作，详情请看finishAction枚举类
     */
    private Integer finishAction;

    /**
     * 航线模式，详情请看headingMode的枚举类
     */
    private Integer headingMode;

    /**
     * 飞行路径模式，详情请看flightPathMode枚举
     */
    private Integer flightPathMode;

    /**
     * 起降点高度
     */
    private Double startStopPointAltitude;

    /**
     * nest表的id
     */
    private Integer nestId;

    /**
     * 创建用户id(废弃)
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

    /**
     * 基站ID
     */
    private String baseNestId;

    /**
     * 告警循环次数
     */
    private Integer alarmCircleNum;

    /**
     * 禁用循环次数
     */
    private Integer forbiddenCircleNum;
}
