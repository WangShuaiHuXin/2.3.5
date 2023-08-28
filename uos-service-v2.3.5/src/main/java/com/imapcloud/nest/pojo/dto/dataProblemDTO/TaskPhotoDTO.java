package com.imapcloud.nest.pojo.dto.dataProblemDTO;

import com.imapcloud.nest.model.DefectInfoEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 分析应用、分析中台的 任务-问题数据DTO
 *
 * @author: zhengxd
 * @create: 2021/6/17
 **/
@Data
public class TaskPhotoDTO {
    /**
     * 源数据id
     */
    private Long dataId;
    /**
     * 照片id
     */
    private Long photoId;
    /**
     * 照片名称
     */
    private String photoName;
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
     * 原图路径、问题照片路径
     */
    private String photoUrl;
    private String photoThumbUrl;
    private String problemUrl;
    private String problemThumbUrl;

    /**
     * 可见光地址
     */
    private String sunUrl;

    /**
     * 可见光路径（排污监测用）
     */
    private String photoUrlVisible;
    private String thumbnailUrlVisible;

    /**
     * 问题id
     */
    private Integer problemId;
    /**
     * 问题名称
     */
    private String problemName;
    /**
     * 问题时间
     */
    private LocalDateTime problemTime;

    /**
     * 任务id
     */
    private Integer taskId;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 任务时间
     */
    private LocalDateTime taskTime;

    /**
     * 架次记录id
     */
    private Integer missionRecordId;
    /**
     * 架次记录时间
     */
    private LocalDateTime missionRecordTime;

    /**
     * 标签id
     */
    private Integer tagId;
    private String tagName;

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
     * 是否有问题（0-没问题；1-有问题）
     */
    private Integer flag;



    /**
     * 问题的具体坐标等信息
     */
    private List<DefectInfoEntity> defectInfoEntityList;

}
