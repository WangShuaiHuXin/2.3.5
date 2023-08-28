package com.imapcloud.nest.pojo.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 气象数据传输类
 *
 * @author wmin
 */
@Data
@Accessors(chain = true)
public class AerographyInfoDto {

    /**
     * 气象站设备产品类型
     * 0：无气象设备
     * 1：分体式气象站
     * 2：一体式气象站
     * 255：未知
     */
    private Integer productType = 255;

    /**
     * 风向
     * 0-北风
     * 1-东北风
     * 2-东风
     * 3-东南风
     * 4-南风
     * 5-西南风
     * 6-西风
     * 7-西北风
     */
    private Integer direction = 0;
    private Double humidity = 0.0;
    private Double pressure = 0.0;
    /**
     * 一体式气象站
     */
    private Double rainfall = 0.0;
    private Double speed = 0.0;
    private Double temperature = 0.0;
    /**
     * 分体式气象站
     * 是否有雨水/雪覆盖
     * 0:没有
     * 1:有
     */
    private Integer rainsnow;
    /**
     * 光照强度
     */
    private Integer illuminationIntensity;
}
