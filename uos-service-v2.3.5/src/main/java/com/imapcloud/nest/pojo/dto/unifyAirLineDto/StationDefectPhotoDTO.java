package com.imapcloud.nest.pojo.dto.unifyAirLineDto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.imapcloud.nest.model.DefectInfoEntity;
import com.imapcloud.nest.model.MissionPhotoEntity;
import com.imapcloud.nest.model.StationIdentifyRecordEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 设备-图片—读数相关字段DTO
 *
 * @author: zhengxd
 * @create: 2021/1/4
 **/
@Data
public class StationDefectPhotoDTO extends MissionPhotoEntity {
    /**
     * 缺陷的状态，0为有缺陷，1为无缺陷。所以暂时用的是boolean
     * 后面可能会变成下面的四个：0为未识别，1为无缺陷，2为有缺陷，3为已消缺
     */
    private Integer status;
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
     * 月份
     */
    private String pMonth;

    /**
     * 缺陷详细信息List
     */
    private List<DefectInfoEntity> defectInfoEntityList;

    private Integer sid;

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
     * 最高温度
     */
    private Double maxTemperature;

    /**
     * 平均温度
     */
    private Double avgTemperature;

    /**
     * 最低温度
     */
    private Double minTemperature;

    /**
     * 可见光地址
     */
    private String sunUrl;

    /**
     * 融合地址
     */
    private String pipUrl;


    /**
     * 识别地址
     */
    private String recordUrl;

    /**
     * 标签id
     */
    private Integer tagId;

    /**
     * 1有问题0没问题
     */
    private Integer flag;

    private Integer flagInfrared;

    private Integer type;

    private String typeName;

    private String photoUrlVisible;

    private String thumbnailUrlVisible;

    private String taskName;

    /**
     * 点云文件id1
     */
    private Integer beforeFileId;

    /**
     * 点云文件id2
     */
    private Integer afterFileId;

    private Integer source;
}
