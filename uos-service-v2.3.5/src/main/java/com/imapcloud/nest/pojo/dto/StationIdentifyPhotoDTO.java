package com.imapcloud.nest.pojo.dto;

import com.imapcloud.nest.model.DefectInfoEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 表记读数DTO类
 *
 * @author: zhengxd
 * @create: 2021/1/4
 **/
@Data
public class StationIdentifyPhotoDTO {

    /**
     * 图片id
     */
    private Long id;
    /**
     * 图片名称
     */
    private String picName;
    /**
     * 原图url
      */
    private String photoUrl;
    /**
     * 缩略图url
     */
    private String thumbnailUrl;
    /**
     * 最后一次修改时间
     */
    private LocalDateTime timeCreated;
    /**
     * 图片从生成时间
     */
    private LocalDateTime createTime;

    /**
     * 表记读书的值
     */
    private Double meterNum;

    /**
     * 表计读数的图片路径
     */
    private String meterPhoto;

    /**
     * 缺陷的图片路径
     */
    private String defectPhotoPath;

    /**
     * 是否有问题
     */
    private Integer flag;

    /**
     * 标签id
     */
    private Integer tagId;
    /**
     * station_identify_record的id
     */
    private Integer sid;

    /**
     * 缺陷详细信息List
     */
    private List<DefectInfoEntity> defectInfoEntityList;

    /**
     * 缺陷、其他类型的状态
     */
    private Integer status;
    
    /**
     * 表计读数的值1
     */
    private Double meterNum1;
    
    /**
     * 表计类型
     */
    private String meterType;
}
