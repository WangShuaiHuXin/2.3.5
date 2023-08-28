package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 飞机信息表
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("aircraft")
public class AircraftEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 机巢id
     */
    private Integer nestId;

    /**
     * 移动终端id
     */
    private Integer appId;

    /**
     * 无人机型号code
     */
    private String code;

    /**
     * 无人机型号值
     */
    private Integer typeValue;

    /**
     * 飞机序列号
     */
    private String aircraftNumber;

    /**
     * 遥控器序列号
     */
    private String controllerNumber;

    /**
     * 相机名称
     */
    private String cameraName;

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
