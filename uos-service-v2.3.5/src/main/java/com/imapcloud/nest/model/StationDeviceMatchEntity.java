package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设备-图片经纬度匹配的误差范围值
 *
 * @author: zhengxd
 * @create: 2021/1/15
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("station_device_match")
public class StationDeviceMatchEntity {
    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 误差值
     */
    private Double errorRange;
}
