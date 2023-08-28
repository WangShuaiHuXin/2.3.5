package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 变电站的设备出现的缺陷记录
 * </p>
 *
 * @author wmin
 * @since 2020-12-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("station_identify_record")
public class StationIdentifyRecordEntity implements Serializable {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * mission_photo的id
     */
    private Long photoId;

    /**
     * 缺陷的状态，0为有缺陷，1为无缺陷。所以暂时用的是boolean
     * 后面可能会变成下面的四个：0为未识别，1为无缺陷，2为有缺陷，3为已消缺
     */
    private Integer status;

    /**
     * 表计读数的值
     */
    private Double meterNum;

    /**
     * 表计读数状态（1-识别成功；2-识别失败）
     */
    private Integer meterStatus;

    /**
     * 表计读数截取的图片
     */
    private String meterPhoto;

    /**
     * 状态的数目
     */
    @TableField(exist = false)
    private Integer statusCount;

    /**
     * 累计缺陷数目
     */
    @TableField(exist = false)
    private Integer totalDefectNum;

    /**
     * 现有缺陷部件数目
     */
    @TableField(exist = false)
    private Integer deviceInDefectNum;

    /**
     * 累计消缺
     */
    @TableField(exist = false)
    private Integer totalDefectRemovedNum;

    /**
     * 创建用户id
     */
    private Long createUserId;

    /**
     * 是否删除, 0:否,1:是
     */
    @TableLogic
    private Boolean deleted;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    /**
     * 缺陷内容
     */
    private String defectContent;

    /**
     * 有缺陷的图片路径
     */
    private String defectPhotoPath;

    /**
     * 有缺陷的缩略图
     */
    private String defectPhotoThumbPath;

    /**
     * 联表拿到的missionId
     */
    @TableField(exist = false)
    private Integer missionId;

    /**
     * 联表拿到的标签id，是用户上传的
     */
    @TableField(exist = false)
    private Integer tagId;

    /**
     * 缺陷详细信息List
     */
    @TableField(exist = false)
    private List<DefectInfoEntity> defectInfoEntityList;

    /**
     * 1有问题0没问题
     */
    private Integer flag;

    /**
     * 标记来源（0:缺陷,1:表计,2:红外,3:交通,4:河道,5:违建,6:定点取证）
     */
    private Integer source;

    /**
     * 识别后的照片（除缺陷、表记外的其他类型）
     */
    private String photoPath;
    
    /**
     * 表计读数的值1
     */
    private Double meterNum1;
    
    /**
     * 表计类型
     */
    private String meterType;
}
