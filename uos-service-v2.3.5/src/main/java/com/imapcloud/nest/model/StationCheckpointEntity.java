package com.imapcloud.nest.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 巡检点，对应拍照点坐标、角度等信息
 * </p>
 *
 * @author daolin
 * @since 2020-11-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("station_checkpoint")
public class StationCheckpointEntity implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 唯一id
     */
    private String uuid;

    /**
     * 巡检点对应的设备id
     */
    private Integer deviceId;

    /**
     * 巡检点对应的设备uuid
     */
    private String deviceUuid;

    /**
     * 变电站id，此处绑定机巢id
     */
    @Deprecated
    private Integer nestId;

    private String baseNestId;
    /**
     * 经度
     */
    private BigDecimal longitude;

    /**
     * 纬度
     */
    private BigDecimal latitude;

    /**
     * 海拔
     */
    private BigDecimal altitude;

    /**
     * 作业点属性：
        0:航拍点, 1:辅助点，2：无动作点
     */
    private Integer jobAttr;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime modifyTime;

    /**
     * 删除标记，0-未删除，1-删除
     */
    private Boolean deleted;


}
