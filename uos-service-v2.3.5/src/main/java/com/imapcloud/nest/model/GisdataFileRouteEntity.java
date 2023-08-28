package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 地理信息文件路径表
 * </p>
 *
 * @author root
 * @since 2020-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("gisdata_file_route")
public class GisdataFileRouteEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 区域名称-中文
     */
    private String name;

    /**
     * 区域名称-英文
     */
    private String alias;

    /**
     * 路径+文件名
     */
    private String route;


    /**
     *
     */
    @TableField(exist = false)
    private Map<Integer, String> nestInfo;

    @TableField(exist = false)
    private List<TaskEntity> taskEntityList;

    @TableField(exist = false)
    private Integer source;

    /**
     * 文件的类型, 0:影像,1:地形,2:模型,3其他
     */
    private Integer type;

    /**
     * 从属于哪个省市县，是对name的
     */
    private Integer parentId;

    /**
     * @deprecated 2.1.0，后续版本会移除
     */
    @Deprecated
    private Integer checkStatus;

    @Deprecated
    private Integer nestId;
    
    /**
     * 单位id
     * @deprecated 2.0.0，由orgCode字段替代
     */
    @Deprecated
    private Integer unitId;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 是否删除, 0:否,1:是
     */
    private Boolean deleted;

    /**
     * 创建用户id
     */
    @Deprecated
    private Integer createUserId = 0;
    private Long creatorId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;
    /**
     * 经度
     */
    private Double latitude;

    /**
     * 纬度
     */
    private Double longitude;

    /**
     * 海拔
     */
    private Double altitude;

    /**
     * @deprecated 2.1.0，后续版本会移除
     */
    @Deprecated
    private Integer isPreload;

    private Double geometricError;

    /**
     * 图层范围
     */
    private String layerRange;

    /**
     * 图层层级
     */
    private int hierarchy;


    //显示高度
    private Double viewAltitude;
    /**
     * 偏移高度
     */
    private Integer offsetHeight;
    /**
     * 安全检测
     */
    private Integer safeCheck;
}
