package com.imapcloud.nest.model.media;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 流媒体服务表
 * </p>
 *
 * @author daolin
 * @since 2022-04-25
 * @deprecated 2.3.2，废弃，已迁移至流媒体管理服务进行维护
 */
@Deprecated
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("media_service")
public class MediaServiceEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 业务id
     */
    private String bid;

    /**
     * 名称
     */
    private String name;

    /**
     * 服务器地址
     */
    private String domain;

    /**
     * 重构服务指令脚本
     */
    private String scriptCode;

    /**
     * 过期日期
     */
    private LocalDate expdate;

    /**
     * 请求token
     */
    private String token;

    /**
     * ssh连接密钥
     */
    private String sskey;

    /**
     * 类型，0-推，1-拉，2-中间转发，3-第三方
     */
    private Integer serviceType;


}
