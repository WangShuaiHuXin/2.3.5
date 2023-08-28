package com.imapcloud.nest.pojo.dto.reqDto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DistanceDto {
    @NotBlank
    private Integer nestId;

    /**
     * 飞行高度
     */
    private Float altitude;

    /**
     * 速度
     */
    private Float speed;

    /**
     * 平移距离
     */
    private Float distance;

    /**
     * 方向
     */
    private Integer direction;

    /**
     * 机头朝向
     */
    private Float heading;


}
