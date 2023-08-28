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
@EqualsAndHashCode(of = {"accountId","messageId"}, callSuper = false)
@TableName("pub_user_message")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PubUserMessageEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Deprecated
    private Integer userId = 0;

    /**
     * 用户id 替换userId
     */
    private Long accountId;

    private Integer messageId;

    /**
     * 是否已读：0-未读；1-已读
     */
    private Boolean readState;

    /**
     * 创建用户id
     */
    @Deprecated
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
