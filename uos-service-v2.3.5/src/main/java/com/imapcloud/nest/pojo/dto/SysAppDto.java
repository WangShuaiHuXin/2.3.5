package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.model.SysAppEntity;
import lombok.Data;


/**
 * <p>
 * 权限信息表
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
@Data
public class SysAppDto extends SysAppEntity {
    /**
     * 无人机型号值
     */
    private String aircraftTypeValue;

    /**
     * 无人机型号
     */
    private String aircraftCode;

    /**
     * 相机类型
     */
    private String cameraName;

    /**
     *
     */
    private String aircraftId;
}
