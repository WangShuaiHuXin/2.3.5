package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 备降功能表
 * </p>
 *
 * @author wmin
 * @since 2021-06-30
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("back_land_fun")
public class BackLandFunEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String backLandFunId;

    /**
     * 备降点名称
     */
    private String name;

    /**
     * 基站id
     */
    private String baseNestId;

    /**
     * 默认备降点经度
     */
    private Double backLandPointLng;

    /**
     * 默认备降点纬度
     */
    private Double backLandPointLat;

    /**
     * 飞往默认备降点相对高度
     */
    private Double gotoBackLandPointAlt;


}
