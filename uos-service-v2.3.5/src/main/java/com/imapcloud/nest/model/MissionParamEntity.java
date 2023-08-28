package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 架次参数表
 * </p>
 *
 * @author wmin
 * @since 2020-08-27
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("mission_param")
public class MissionParamEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 自动飞行速度
     */
    private Integer autoFlightSpeed;

    /**
     * 航线飞行速度
     */
    private Integer speed;

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
     * 正常航点的通用速度，例如点云所给的航线航点没有速度
     */
    private Double waypointSpeed;

    /**
     * 复制次数
     */
    private Integer copyCount;

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

    /**
     * 架次划分时间长短
     */
    private Integer deltaTime;

    /**
     * 是否开启协调转弯
     */
    private Integer isCoorTurning;

    /**
     * 由于老数据没有Speed这个字段，因此老数据可能都是0，所以在更新的时候需要通过sql语句进行数据同步，
     * 但是由于有好多的内网环境因此，做一下处理
     *
     * @return
     */
    public Integer getSpeed() {
        if (speed == null || speed == 0) {
            return autoFlightSpeed;
        }
        return speed;
    }

    public MissionParamEntity setSpeed(Integer speed) {
        this.speed = speed;
        return this;
    }
}
