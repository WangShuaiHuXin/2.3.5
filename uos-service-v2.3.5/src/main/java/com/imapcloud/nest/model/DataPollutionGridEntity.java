package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author zheng
 * @since 2021-09-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("data_pollution_grid")
public class DataPollutionGridEntity implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 污染网格表id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 文件大小
     */
    private String size;

    /**
     * 压缩包的路劲
     */
    private String zipUrl;
    /**
     * 关键数据路径
     */
    private String keyUrl;

    /**
     * 类型（1-NH3-N; 2-DO; 3-TN; 4-TP; 5-COD; 6-BOD5）
     */
    private Integer type;

    /**
     * 上传时间
     */
    private LocalDateTime recordTime;

    private Integer fileInfoId;

    /**
     * 标签id
     */
    private Integer tagId;

    private Integer missionId;

    /**
     * 架次记录表的Id
     */
    private Integer missionRecordsId;

    /**
     * 架次记录时间
     */
    private LocalDateTime missionRecordTime;

    /**
     * 任务id
     */
    private Integer taskId;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 点云分析状态(0-终止分析；1-正常)
     */
    private Boolean state;

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
