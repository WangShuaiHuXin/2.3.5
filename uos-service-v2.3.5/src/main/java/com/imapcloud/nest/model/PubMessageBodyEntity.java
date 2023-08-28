package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

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
@TableName("pub_message_b")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PubMessageBodyEntity implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 组织id
     * @deprecated 2.0.0，由{@link PubMessageBodyEntity#orgCode}替代
     */
    @Deprecated
    private Integer companyId;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 消息id
     */
    private Integer pkMessage;

    /**
     * 创建用户id
     */
    private Integer createUserId;

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


}
