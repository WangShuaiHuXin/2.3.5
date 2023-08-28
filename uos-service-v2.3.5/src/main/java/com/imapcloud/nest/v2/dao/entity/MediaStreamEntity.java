package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 流媒体信息表
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 * @deprecated 2.3.2，废弃，已迁移至流媒体管理服务进行维护
 */
@Deprecated
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("media_stream")
public class MediaStreamEntity extends GenericEntity {

    private static final long serialVersionUID=1L;

    /**
     * 流ID
     */
    private String streamId;

    /**
     * 推流地址
     */
    private String streamPushUrl;

    /**
     * 拉流地址
     */
    private String streamPullUrl;

    /**
     * 协议类型 RTMP/RTSP/HTTP/SRT/RTC/GB28181
     */
    private String protocol;

}
