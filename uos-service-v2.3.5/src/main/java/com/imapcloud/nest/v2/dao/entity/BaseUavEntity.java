package com.imapcloud.nest.v2.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

import com.geoai.common.mp.entity.GenericEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 无人机信息表
 * </p>
 *
 * @author wmin
 * @since 2022-08-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("base_uav")
public class BaseUavEntity extends GenericEntity {

    private static final long serialVersionUID=1L;

    /**
     * 无人机ID
     */
    private String uavId;

    /**
     * 无人机序列号
     */
    private String uavNumber;

    /**
     * 遥控器序列号
     */
    private String rcNumber;

    /**
     * 相机名称
     */
    private String cameraName;

    /**
     * 推拉流信息
     */
    private String streamId;

    /**
     * 无人机型号值
     */
    private String type;

    /**
     * 无人机标识
     */
    private Integer which;

    /**
     * 登记码
     */
    private String registerCode;

    /**
     * 起飞全重
     */
    private Double takeoffWeight;


    /***********************************中科天网***********************/
    /*中科天网-生产厂家名称*/
    private Integer uavPro;

    /*中科天网-飞行器名称*/
    private Integer uavName;

    /*中科天网-飞行器类别*/
    private Integer uavType;

    /*中科天网-飞行器类型*/
    private Integer uavPattern;

    /*中科天网-飞行器序列号*/
    private String uavSn;
}
