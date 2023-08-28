package com.imapcloud.nest.pojo.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.imapcloud.nest.common.annotation.TrimStr;
import com.imapcloud.nest.model.NestEntity;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 新增或者修改任务的Task传输类
 *
 * @author wmin
 */
@Data
public class GisdataFileRouteDto extends NestEntity {


    private static final long serialVersionUID=1L;

    private Integer id;

    /**
     * 区域名称-中文
     */
    @NotBlank(message = "{geoai_uos_layer_name_cannot_be_empty}")
    private String name;

    /**
     * 区域名称-英文
     */
    private String alias;

    /**
     * 路径+文件名
     */
    @NotBlank(message = "{geoai_uos_the_layer_publish_path_cannot_be_empty}")
    @TrimStr
    private String route;

    /**
     * 图层范围
     */
    private String layerRange;

    /**
     * 文件的类型, 0:影像,1:地形,2:模型,3其他
     */
    @NotNull(message = "{geoai_uos_the_layer_type_cannot_be_empty}")
    private Integer type;

    /**
     * 从属于哪个省市县，是对name的
     */
    private Integer parentId;

    private Integer nestId;

    @TableField(exist = false)
    private List<Integer> routeIds;


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
     * @deprecated 2.1.0
     */
    @Deprecated
    private Integer isPreload;

    private Double geometricError;

    private int hierarchy;

    //显示高度
    private Double viewAltitude;

    @Deprecated
    private String unitId;

    @NotBlank(message = "{geoai_uos_cannot_empty_unitcode}")
    private String orgCode;
    /**
     * 偏移高度
     */
    private Integer offsetHeight;
    /**
     * 安全检测
     */
    private Integer safeCheck;
}
