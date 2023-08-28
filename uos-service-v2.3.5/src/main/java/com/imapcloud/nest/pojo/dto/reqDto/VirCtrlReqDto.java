package com.imapcloud.nest.pojo.dto.reqDto;

import lombok.Data;

import javax.validation.constraints.*;

/**
 * <p>
 * 虚拟摇杆传参
 * </p>
 *
 * @author daolin
 * @since 2020-12-08
 */
@Data
public class VirCtrlReqDto {

    @NotBlank
    private Integer nestId;

    /**
     * （【俯仰】）杆，用于控制无人机往机头方向前进/后退，正数为前进，负数为后退。使用虚拟摇杆控制时，最大速度为15m/s，最小速度为-15m/s。
     */
    @DecimalMax("15.0")
    @DecimalMin("-15.0")
    private Float pitch;

    /**
     * 【横滚】）杆，用于控制无人机相对机头方向向左平移/向右平移，正数为向右平移，负数为向左平移。使用虚拟摇杆控制时，最大速度为15m/s，最小速度为-15m/s。
     */
    @DecimalMax("15.0")
    @DecimalMin("-15.0")
    private Float roll;

    /**
     * 【偏航】）杆，用于控制无人机机头旋转，正数为顺时针，负数为逆时针。使用虚拟摇杆控制时，最大速度为100°/s，最小速度为-100°/s。
     */
    @DecimalMax("100.0")
    @DecimalMin("-100.0")
    private Float yaw;

    /**
     * 【升降】/【油门】）杆，用于控制无人机升降，正数为上升，负数为下降。使用虚拟摇杆控制时，最大垂直速度为4 m/s。最小垂直速度为-4 m/s。
     */
    @DecimalMax("4.0")
    @DecimalMin("-4.0")
    private Float throttle;

}
