package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 精细巡检杆塔数据表
 * </p>
 *
 * @author hc
 * @since 2021-05-25
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("fine_insp_tower")
public class FineInspTowerEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * fine_insp_zip表id
     */
    private Integer fineInspZipId;

    /**
     * 杆塔名称
     */
    @TableField("towerName")
    private String towerName;

    /**
     * 杆塔纬度
     */
    @TableField("towerLat")
    private Double towerLat;

    /**
     * 杆塔经度
     */
    @TableField("towerLng")
    private Double towerLng;

    /**
     * 杆塔海拔
     */
    @TableField("towerAlt")
    private Double towerAlt;

    /**
     * 点云文件路径
     */
    private String pointCloudFileUrl;

    /**
     * 航线文件路径
     */
    private String routeFileUrl;

    /**
     * 更改过的航线数据
     */
    private String updateRouteData;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;


}
