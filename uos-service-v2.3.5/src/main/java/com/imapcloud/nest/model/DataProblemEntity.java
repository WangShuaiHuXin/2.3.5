package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author zhengxd
 * @since 2021-06-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("data_problem")
public class DataProblemEntity implements Serializable {

    private static final long serialVersionUID=1L;

    /**
     * 问题表id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 源数据id
     */
    private Long dataId;

    /**
     * 任务id
     */
    private Integer taskId;
    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 架次记录id
     */
    private Integer missionRecordId;
    /**
     * 架次记录时间
     */
    private LocalDateTime missionRecordTime;

    /**
     * 所属标签
     */
    private Integer tagId;

    /**
     * 数据来源（0-照片；1-视频；2-正射；3-点云；4-倾斜；5-矢量；6-全景）
     */
    private Integer dataSource;

    /**
     * 问题来源（0-缺陷识别；1-表记读数；2-红外测温；3-排污检测；4-河道巡查；5-管道巡查；6-水库巡查；7-城市巡查；
     * 8-违建识别；9-非法摆摊；10-公安巡查；11-违法取证；12-治安巡逻；13-应急巡查；14-事故勘察；15-应急指挥；
     * 16-国土改造；17-违章识别；18-国土取证；19-环保巡查；20-气体监测；21-排污取证；22-交通巡查；23-故事取证）
     */
    private Integer problemSource;

    /**
     * 问题的状态（0：未识别，1：没问题，2：有问题，3：已解决）
     */
    private Integer problemStatus;

    /**
     * 原图路径
     */
    private String photoUrl;

    /**
     * 问题照片路径
     */
    @TableField(updateStrategy=FieldStrategy.IGNORED)
    private String problemUrl;
    /**
     * 问题照片路径
     */
    @TableField(updateStrategy=FieldStrategy.IGNORED)
    private String problemThumbUrl;

    /**
     * 缺陷的具体内容
     */
    @TableField(updateStrategy=FieldStrategy.IGNORED)
    private String problemName;

    /**
     * 照片时间
     */
    private LocalDateTime photoTime;
    /**
     * 照片经纬度
     */
    private Double lng;
    private Double lat;

    /**
     * 0-没问题；1-有问题
     */
    private Integer flag;

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

    @TableField(exist = false)
    private Integer orderNo;
    @TableField(exist = false)
    private String locationInfo;
    @TableField(exist = false)
    private String recordsTime;
    @TableField(exist = false)
    private String clearPhotoUrl;
    @TableField(exist = false)
    private String clearRecordsTime;
    private String alarmid;
    private String detectResult;


}
