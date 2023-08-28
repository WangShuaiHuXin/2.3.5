package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author zheng
 * @since 2021-09-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("nest_rtk")
public class NestRtkEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * rtk信息id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 机巢id
     *
     * @deprecated 2.0.0，使用baseNestId替代
     */
    private String nestId;


    /**
     * 是否开启（0-开启；1-关闭）
     */
    private Integer enable;

    /**
     * rtk类型（0-未知；1-网咯RTK；2-自定义网络RTK；3-D-RTK基站 ）
     */
    private Integer type;

    /**
     * IP地址或域名
     */
    private String ip;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 源节点
     */
    private String mountPoint;

    /**
     * 账号
     */
    @TableField("userName")
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 到期时间
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private LocalDate expireTime;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 需修改时间
     */
    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;

    @TableField(exist = false)
    private String nestUuid;
    @TableField(exist = false)
    private String nestName;

    /**
     * 基站ID
     */
    private String baseNestId;
}
