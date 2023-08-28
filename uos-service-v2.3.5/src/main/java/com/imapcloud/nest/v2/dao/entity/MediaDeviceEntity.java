package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 媒体设备表
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 * @deprecated 2.3.2，废弃，已迁移至流媒体管理服务进行维护
 */
@Deprecated
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("media_device")
public class MediaDeviceEntity extends GenericEntity {

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备mac
     */
    private String deviceMac;

    /**
     * 设备地址
     */
    private String deviceDomain;

    /**
     * 设备品牌
     */
    private String deviceBrand;

    /**
     * 设备类型 0->摄像头，1->无人机，2->其他
     */
    private Integer deviceType;

    /**
     * 接入账号
     */
    private String accessKey;

    /**
     * 接入密钥
     */
    private String accessSecret;

    /**
     * 是否推流
     */
    private Integer videoEnable;
}
