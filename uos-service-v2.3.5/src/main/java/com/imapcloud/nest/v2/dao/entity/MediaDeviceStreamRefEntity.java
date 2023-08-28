package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 媒体设备与媒体流关系表
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 * @deprecated 2.3.2，废弃，已迁移至流媒体管理服务进行维护
 */
@Deprecated
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("media_device_stream_ref")
public class MediaDeviceStreamRefEntity extends GenericEntity {

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 流ID
     */
    private String streamId;
}
