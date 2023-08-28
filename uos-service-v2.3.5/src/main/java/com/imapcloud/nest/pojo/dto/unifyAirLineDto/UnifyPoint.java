package com.imapcloud.nest.pojo.dto.unifyAirLineDto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by wmin on 2020/11/17 15:46
 *
 * @author wmin
 */
@Data
public class UnifyPoint {
    /**
     * 航线位置
     */
    private UnifyLocation location;
    /**
     * 是否显示杆塔图标
     */
    private Boolean towerIcon;
    /**
     * 是否显示航线的原点
     */
    private Boolean cirlclePoint;
    /**
     * 旋转半径
     */
    private Double cornerRadiusInMeters;

    /**
     * 自定义航点动作
     */
    private List<UnifyAction> customActions;

    private Double focalLength;

    /**
     * 航点速度
     */
    private Double speed;
}
