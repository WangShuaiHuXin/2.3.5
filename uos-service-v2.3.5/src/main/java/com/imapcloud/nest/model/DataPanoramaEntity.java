package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 全景数据表
 * </p>
 *
 * @author hc
 * @since 2021-06-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("data_panorama")
public class DataPanoramaEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 数据名字
     */
    private String name;

    /**
     * 关键文件的路径
     */
    private String keyUrl;

    /**
     * 缩略图url
     */
    private String thumbnailUrl;

    /**
     * 经度
     */
    private Double log;

    /**
     * 纬度
     */
    private Double lat;

    /**
     * 架次id
     */
    private Integer missionId;

    /**
     * 记录表id
     */
    private Integer missionRecordsId;

    private Integer fileInfoId;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 是否删除, 0:否,1:是
     */
    @TableLogic
    private Boolean deleted;

    /**
     * @deprecated 2.0.0，由orgCode字段替代
     */
    @Deprecated
    private String unitId;

    /**
     * 单位编码
     */
    private String orgCode;

}
