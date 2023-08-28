package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 航线参数表
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("air_line_param")
public class AirLineParamEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 起飞点纬度
     */
    private Double takeOffLatitude;

    /**
     * 起飞点经度
     */
    private Double takeOffLongitude;

    /**
     * 起飞点海拔
     */
    private Double takeOffAltitude;

    /**
     * 降落点纬度
     */
    private Double landLatitude;

    /**
     * 降落点经度
     */
    private Double landLongitude;

    /**
     * 降落点海拔
     */
    private Double landAltitude;

    /**
     * 航点速度
     */
    private Double waypointSpeed;

    /**
     * 起降点速度
     */
    private Double takeOffLandSpeed;

    /**
     * 降落点机头朝向角度
     */
    private Integer landHeadAngle;

    /**
     * 辅助点的停留时间，单位是毫秒
     */
    private Long stayTime;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;


}
