package com.imapcloud.nest.pojo.vo;

import com.imapcloud.nest.model.PubMessageBodyEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.simpleframework.xml.Transient;

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
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"logContent","messageContent"})
public class PubMessageVO implements Serializable {

    private static final long serialVersionUID=1L;

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
     * 更新日志
     */
    private String logContent;

    /**
     * 系统版本
     */
    private String sysVersion;

    /**
     * 创建用户id
     */
    private Long createUserId;

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

    @Transient
    private List<PubMessageBodyEntity> pubMessageBodyEntityList;

    private LocalDateTime beginTime;

    private Long userId;

//    private Integer companyId;

    private Integer readState;

    private LocalDateTime pushTime;



}
