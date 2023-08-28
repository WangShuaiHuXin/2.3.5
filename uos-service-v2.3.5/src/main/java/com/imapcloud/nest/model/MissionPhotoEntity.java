package com.imapcloud.nest.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author wmin
 * @since 2020-07-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("mission_photo")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MissionPhotoEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 图片名字
     */
    private String name;

    /**
     * 图片别名
     */
    private String OriginalFilename;

    /**
     * 图片链接
     */
    private String photoUrl;

    /**
     * 图片大小
     */
    private Long photoSize;

    /**
     * 缩略图名称
     */
    private String thumbnailName;

    /**
     * 缩略图url
     */
    private String thumbnailUrl;

    /**
     * 架次id
     */
    private Integer missionId;

    /**
     * 记录表id
     */
    private Integer missionRecordsId;

    /**
     * 任务执行的execId
     */
    private String execId;


    /**
     * 图片在机巢的id
     */
    private String fileId;

    /**
     * 图片在机巢的名称
     */
    private String fileName;

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
     * 设备的uuid
     */
    private String deviceUuid;

    /**
     * 照片来源（1-机巢同步；2-分析中台上传 3-抽帧推送的）
     */
    private Integer source;
    /**
     * 任务id
     */
    private Integer taskId;

    /**
     * 架次id的集合
     */
    @TableField(exist = false)
    private String missionIds;

    /**
     * 标签名称
     */
    @TableField(exist = false)
    private String sysTagName;

    /**
     * 图片在机巢的文件类型
     */
    private Integer mediaType;

    /**
     * 图片在机巢的文件创建时间
     */
    private LocalDateTime timeCreated;

    /**
     * 图片是否已经下载到机巢(0：否； 1：是)
     */
    private Integer downloaded;

    /**
     * 创建用户id
     */
    private Integer createUserId;

    private LocalDateTime createTime;

    private LocalDateTime modifyTime;

    @TableField(exist = false)
    private String createTimeStr;

    /**
     * 是否删除, 0:否,1:是
     */
    @TableLogic
    private Boolean deleted;

    /**
     * 是否有缺陷，0为无缺陷，1为有缺陷
     */
    @TableField(exist = false)
    private Integer defectStatus;


    /**
     * 缺陷内容
     */
    @TableField(exist = false)
    private String defectContent;
    /**
     * 缺陷内容List
     */
    @TableField(exist = false)
    private List<DefectInfoEntity> defectInfoEntityList;
    /**
     * 红外手动标记详细信息List
     */
    @TableField(exist = false)
    private List<StationInfraredRecordRectangleEntity> stationInfraredRecordRectangleEntities;
    /**
     * 缺陷图片路径
     */
    @TableField(exist = false)
    private String defectPhotoPath;

    @Deprecated
    private Integer nestId;

    private String baseNestId;

    /**
     * 通过上传的照片对应的压缩包名
     */
    private String photoZipName;

    /**
     * 照片类型
     * 0 -> 可见光
     * 1 -> 红外光
     * 2 -> 可见红外合成
     */
    private Integer photoType;

    /**
     * 照片标识，如果相同则为同一个拍照动作拍的多张照片
     */
    private Integer photoMark;

    @TableField(exist = false)
    private List<Integer> propList;

    @TableField(exist = false)
    private String byname;

    /**
     * 拍照的航点序号
     */
    private Integer waypointIndex;

//    @TableField(exist = false)
//    private Integer visibleIndex;

    @TableField(exist = false)
    private Integer tagId;

    private Integer clearId;

    private Integer videoPhotoId;

    /**
     * @deprecated 2.0.0，由orgCode字段替代
     */
    @Deprecated
    private String unitId;

    /**
     * 单位编码
     */
    private String orgCode;

    /**
     * 镜头类型
     */
    private Integer lenType;

    /**
     * 标签版本
     */
    private Integer tagVersion;

    private Integer physicsDelete;
}
