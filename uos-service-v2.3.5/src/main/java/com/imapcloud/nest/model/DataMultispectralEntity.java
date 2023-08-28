package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 多光谱数据表
 * </p>
 *
 * @author hc
 * @since 2021-06-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("data_multispectral")
public class DataMultispectralEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 数据名字
     */
    private String name;

    /**
     * 关键文件的路径({z}/{x}/{y}.png)
     */
    private String keyUrl;

    /**
     * meta.json路径
     */
    private String jsonUrl;
    /**
     * tif路劲
     *
     */
    private String tifUrl;

    /**
     * 封面路径
     */
    private String coverUrl;

    /**
     * 数据拍摄的时间
     */
    private LocalDate recordTime;

    /**
     * 文件的拍摄年份
     */
    private String yearTime;

    /**
     * 文件的拍摄年月日
     */
    private String dateTime;

    private Integer fileInfoId;

    /**
     * 架次id
     */
    private Integer missionId;

    /**
     * 记录表id
     */
    private Integer missionRecordsId;

    /**
     * 架次记录时间
     */
    private LocalDateTime missionRecordTime;
    /**
     * 标签id
     */
    private Integer tagId;
    /**
     * 任务id
     */
    private Integer taskId;
    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 关联照片的架次记录id
     */
    private Integer photoRecordId;

    /**
     * 点云分析状态(0-终止分析；1-正常)
     */
    private Integer state;

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
     * 关联的架次的任务名称
     */
    @TableField(exist = false)
    private String relatedTaskName;

    private String shpUrl;

    @TableField(exist = false)
    private List<DataMultispectralDirectorEntity> recordUrls;
}
