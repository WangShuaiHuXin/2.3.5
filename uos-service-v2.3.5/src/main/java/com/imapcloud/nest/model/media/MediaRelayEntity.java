package com.imapcloud.nest.model.media;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author daolin
 * @since 2022-04-22
 * @deprecated 2.3.2，废弃，已迁移至流媒体管理服务进行维护
 */
@Deprecated
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("media_relay")
public class MediaRelayEntity implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 基站id，对应nest:uuid
     */
    private String bsId;

    /**
     * 流媒体业务id，media_service:bid
     */
    private String msId;

    /**
     * 转发名称
     */
    private String name;

    /**
     * 流app
     */
    private String streamApp;

    /**
     * 流名称
     */
    private String streamName;

    /**
     * 转发id，删除后每次创建id不同,已存在相同流名称的不会重复创建
     */
    private String relayId;


}