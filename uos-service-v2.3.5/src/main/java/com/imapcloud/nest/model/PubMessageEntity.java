package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author double
 * @since 2022-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("pub_message")
@ToString(exclude = {"logContent","messageContent"})
public class PubMessageEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 消息编码
     */
    private String messageCode;

    /**
     * 消息内容
     */
    private String messageContent;

    /**
     * 消息标题
     */
    private String messageTitle;

    /**
     * 消息状态:0 草稿；1未推送；2已推送
     */
    private Integer messageState;

    /**
     * 消息类型0更新公告；1其他公告
     */
    private Integer messageType;

    /**
     * 消息种类：0公告；1任务
     */
    private Integer messageClass;

    /**
     * 更新日志-富文本
     */
    private String logContent;

    /**
     * 系统版本
     */
    private String sysVersion;

    /**
     * 创建用户id(废弃)
     */
    @Deprecated
    private Integer createUserId = 0;

    /**
     * 创建用户id
     */
    private Long creatorId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;

    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime beginTime;

    @TableField(exist = false)
    private List<PubMessageBodyEntity> pubMessageBodyEntityList;

    @TableField(exist = false)
    private String nestId;

}
