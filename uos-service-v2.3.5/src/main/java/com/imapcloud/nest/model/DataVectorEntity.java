package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 矢量数据表
 * </p>
 *
 * @author hc
 * @since 2021-06-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("data_vector")
public class DataVectorEntity implements Serializable {

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
     * json文件路径
     */
    private String jsonUrl;

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
